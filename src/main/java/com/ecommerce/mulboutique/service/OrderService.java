package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.order.OrderDto;
import com.ecommerce.mulboutique.dto.order.OrderItemDto;
import com.ecommerce.mulboutique.dto.order.CreateOrderRequest;
import com.ecommerce.mulboutique.dto.order.UpdateOrderStatusRequest;
import com.ecommerce.mulboutique.dto.order.UpdateShippingRequest;
import com.ecommerce.mulboutique.entity.*;
import com.ecommerce.mulboutique.exception.BadRequestException;
import com.ecommerce.mulboutique.exception.ForbiddenException;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.repository.OrderRepository;
import com.ecommerce.mulboutique.repository.ProductRepository;
import com.ecommerce.mulboutique.repository.ShoppingCartRepository;
import com.ecommerce.mulboutique.repository.StoreRepository;
import com.ecommerce.mulboutique.entity.*;
import com.ecommerce.mulboutique.repository.*;
import com.ecommerce.mulboutique.util.TextSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private ShoppingCartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CouponService couponService;

    @Value("${stock.low-threshold:5}")
    private int lowStockThreshold;

    public OrderDto createOrder(CreateOrderRequest request, User user) {
        ShoppingCart cart = cartRepository.findByCustomerIdAndStoreId(user.getId(), request.getStoreId())
                .orElseThrow(() -> new NotFoundException("Panier non trouve"));

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Panier vide");
        }

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new NotFoundException("Boutique non trouvee"));

        BigDecimal totalAmount = cart.getTotalAmount();
        Coupon coupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            coupon = couponService.validateCoupon(TextSanitizer.clean(request.getCouponCode()), store.getId(), totalAmount);
            discountAmount = couponService.calculateDiscount(coupon, totalAmount);
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(user);
        order.setStore(store);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setShippingStatus(Order.ShippingStatus.PENDING);
        order.setShippingAddress(TextSanitizer.clean(request.getShippingAddress()));
        order.setBillingAddress(TextSanitizer.clean(request.getBillingAddress()));
        order.setPaymentMethod(TextSanitizer.clean(request.getPaymentMethod()));
        order.setShippingMethod(request.getShippingMethod() == null ? Order.ShippingMethod.STANDARD : request.getShippingMethod());
        order.setCoupon(coupon);
        order.setOrderDate(LocalDateTime.now());

        cart.getCartItems().forEach(item -> {
            Product product = item.getProduct();
            int newStock = product.getStockQuantity() - item.getQuantity();
            if (newStock < 0) {
                throw new BadRequestException("Stock insuffisant pour " + product.getName());
            }
            product.setStockQuantity(newStock);
            productRepository.save(product);
            if (newStock <= lowStockThreshold) {
                logger.warn("event=low_stock productId={} sku={} remaining={}", product.getId(), product.getSku(), newStock);
            }
            OrderItem orderItem = new OrderItem(order, product, item.getQuantity(), item.getUnitPrice());
            order.addOrderItem(orderItem);
        });

        Order savedOrder = orderRepository.save(order);
        couponService.incrementUsage(coupon);

        cart.clearCart();
        cartRepository.save(cart);

        logger.info("event=order_created orderId={} orderNumber={} userId={} storeId={} total={}",
                savedOrder.getId(), savedOrder.getOrderNumber(), user.getId(), store.getId(), finalAmount);

        return toDto(savedOrder);
    }

    public List<OrderDto> getOrdersByCustomer(User user) {
        return orderRepository.findByCustomerId(user.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getOrdersByStore(Long storeId) {
        return orderRepository.findByStoreId(storeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getOrdersByStoreForOwner(Long storeId, User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Boutique non trouvee"));
        if (!store.getOwner().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("Acces refuse");
        }
        return getOrdersByStore(storeId);
    }

    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Commande non trouvee"));

        if (!order.getStore().getOwner().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("Acces refuse");
        }

        order.setStatus(request.getStatus());
        if (request.getStatus() == Order.OrderStatus.DELIVERED) {
            order.setShippingStatus(Order.ShippingStatus.DELIVERED);
            order.setDeliveredAt(LocalDateTime.now());
        }

        Order saved = orderRepository.save(order);
        logger.info("event=order_status_updated orderId={} status={}", orderId, request.getStatus());
        return toDto(saved);
    }

    public OrderDto updateShipping(Long orderId, UpdateShippingRequest request, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Commande non trouvee"));

        if (!order.getStore().getOwner().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("Acces refuse");
        }

        order.setShippingStatus(request.getShippingStatus());
        order.setShippingProvider(TextSanitizer.clean(request.getShippingProvider()));
        order.setTrackingNumber(TextSanitizer.clean(request.getTrackingNumber()));
        if (request.getShippingStatus() == Order.ShippingStatus.IN_TRANSIT) {
            order.setShippedAt(LocalDateTime.now());
        }
        if (request.getShippingStatus() == Order.ShippingStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        Order saved = orderRepository.save(order);
        logger.info("event=shipping_updated orderId={} status={}", orderId, request.getShippingStatus());
        return toDto(saved);
    }

    public Order getOrderEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Commande non trouvee"));
    }

    public OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStoreId(order.getStore().getId());
        dto.setStoreName(order.getStore().getName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setShippingStatus(order.getShippingStatus());
        dto.setShippingMethod(order.getShippingMethod());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentReference(order.getPaymentReference());
        dto.setShippingProvider(order.getShippingProvider());
        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setBillingAddress(order.getBillingAddress());
        dto.setOrderDate(order.getOrderDate());
        dto.setItems(order.getOrderItems().stream().map(item -> {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setUnitPrice(item.getUnitPrice());
            itemDto.setTotalPrice(item.getTotalPrice());
            return itemDto;
        }).collect(Collectors.toList()));
        return dto;
    }

    private String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + date + "-" + suffix;
    }
}

