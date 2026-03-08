package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.order.CreateOrderRequest;
import com.ecommerce.mulboutique.dto.order.OrderDto;
import com.ecommerce.mulboutique.dto.order.UpdateOrderStatusRequest;
import com.ecommerce.mulboutique.entity.CartItem;
import com.ecommerce.mulboutique.entity.Coupon;
import com.ecommerce.mulboutique.entity.Order;
import com.ecommerce.mulboutique.entity.Product;
import com.ecommerce.mulboutique.entity.ShoppingCart;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.repository.OrderRepository;
import com.ecommerce.mulboutique.repository.ProductRepository;
import com.ecommerce.mulboutique.repository.ShoppingCartRepository;
import com.ecommerce.mulboutique.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ShoppingCartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_appliesCouponAndClearsCart() {
        ReflectionTestUtils.setField(orderService, "lowStockThreshold", 5);

        User customer = new User();
        customer.setId(7L);

        User owner = new User();
        owner.setId(9L);

        Store store = new Store();
        store.setId(1L);
        store.setName("Tech");
        store.setOwner(owner);

        Product product = new Product();
        product.setId(5L);
        product.setName("Phone");
        product.setStore(store);
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(10);

        ShoppingCart cart = new ShoppingCart(customer, store);
        CartItem cartItem = new CartItem(cart, product, 2, new BigDecimal("100.00"));
        cart.addCartItem(cartItem);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setStoreId(1L);
        request.setShippingAddress("Dakar");
        request.setBillingAddress("Dakar");
        request.setPaymentMethod("CARD");
        request.setCouponCode("SAVE10");

        Coupon coupon = new Coupon("SAVE10", Coupon.DiscountType.FIXED_AMOUNT, new BigDecimal("10.00"), store,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        when(cartRepository.findByCustomerIdAndStoreId(7L, 1L)).thenReturn(Optional.of(cart));
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(couponService.validateCoupon("SAVE10", 1L, new BigDecimal("200.00"))).thenReturn(coupon);
        when(couponService.calculateDiscount(coupon, new BigDecimal("200.00"))).thenReturn(new BigDecimal("10.00"));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderDto dto = orderService.createOrder(request, customer);

        assertEquals(new BigDecimal("190.00"), dto.getFinalAmount());
        assertEquals(1, dto.getItems().size());
        assertEquals(0, cart.getCartItems().size());
        verify(couponService).incrementUsage(coupon);
    }

    @Test
    void updateOrderStatus_deliveredUpdatesShipping() {
        User owner = new User();
        owner.setId(9L);

        Store store = new Store();
        store.setId(1L);
        store.setOwner(owner);

        Order order = new Order();
        order.setId(99L);
        order.setStore(store);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(Order.OrderStatus.DELIVERED);

        when(orderRepository.findById(99L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderDto dto = orderService.updateOrderStatus(99L, request, owner);

        assertEquals(Order.ShippingStatus.DELIVERED, dto.getShippingStatus());
        assertNotNull(order.getDeliveredAt());
    }
}
