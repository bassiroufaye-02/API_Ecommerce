package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.cart.AddToCartRequest;
import com.ecommerce.mulboutique.dto.cart.CartDto;
import com.ecommerce.mulboutique.dto.cart.CartItemDto;
import com.ecommerce.mulboutique.entity.CartItem;
import com.ecommerce.mulboutique.entity.Product;
import com.ecommerce.mulboutique.entity.ShoppingCart;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.exception.BadRequestException;
import com.ecommerce.mulboutique.exception.ForbiddenException;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.repository.CartItemRepository;
import com.ecommerce.mulboutique.repository.ProductRepository;
import com.ecommerce.mulboutique.repository.ShoppingCartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private ShoppingCartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public CartDto getCart(Long storeId, User user) {
        Optional<ShoppingCart> cartOpt = cartRepository.findByCustomerIdAndStoreId(user.getId(), storeId);
        return cartOpt.map(this::toDto).orElse(null);
    }

    public CartDto addItem(AddToCartRequest request, User user) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Produit non trouve"));
        if (!product.getIsActive()) {
            throw new BadRequestException("Produit inactif");
        }
        if (!product.getStore().getId().equals(request.getStoreId())) {
            throw new BadRequestException("Produit non disponible dans cette boutique");
        }
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Stock insuffisant");
        }

        ShoppingCart cart = cartRepository.findByCustomerIdAndStoreId(user.getId(), request.getStoreId())
                .orElseGet(() -> {
                    ShoppingCart created = new ShoppingCart(user, product.getStore());
                    return cartRepository.save(created);
                });

        CartItem existing = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            existing.setUnitPrice(product.getPrice());
            cartItemRepository.save(existing);
        } else {
            CartItem newItem = new CartItem(cart, product, request.getQuantity(), product.getPrice());
            cart.addCartItem(newItem);
            cartItemRepository.save(newItem);
        }

        cart.updateTotalAmount();
        cartRepository.save(cart);

        logger.info("event=cart_item_added userId={} cartId={} productId={} quantity={}", user.getId(), cart.getId(), product.getId(), request.getQuantity());
        return toDto(cart);
    }

    public CartDto updateItem(Long itemId, int quantity, User user) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item panier non trouve"));
        if (!item.getCart().getCustomer().getId().equals(user.getId())) {
            throw new ForbiddenException("Acces refuse");
        }
        if (quantity < 1) {
            throw new BadRequestException("Quantite invalide");
        }
        if (item.getProduct().getStockQuantity() < quantity) {
            throw new BadRequestException("Stock insuffisant");
        }

        item.setQuantity(quantity);
        item.setUnitPrice(item.getProduct().getPrice());
        cartItemRepository.save(item);

        ShoppingCart cart = item.getCart();
        cart.updateTotalAmount();
        cartRepository.save(cart);

        logger.info("event=cart_item_updated userId={} cartItemId={} quantity={}", user.getId(), itemId, quantity);
        return toDto(cart);
    }

    public CartDto removeItem(Long itemId, User user) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item panier non trouve"));
        if (!item.getCart().getCustomer().getId().equals(user.getId())) {
            throw new ForbiddenException("Acces refuse");
        }

        ShoppingCart cart = item.getCart();
        cart.removeCartItem(item);
        cartItemRepository.delete(item);
        cart.updateTotalAmount();
        cartRepository.save(cart);

        logger.info("event=cart_item_removed userId={} cartItemId={}", user.getId(), itemId);
        return toDto(cart);
    }

    public CartDto clearCart(Long storeId, User user) {
        ShoppingCart cart = cartRepository.findByCustomerIdAndStoreId(user.getId(), storeId)
                .orElseThrow(() -> new NotFoundException("Panier non trouve"));
        cart.clearCart();
        cartRepository.save(cart);

        logger.info("event=cart_cleared userId={} cartId={}", user.getId(), cart.getId());
        return toDto(cart);
    }

    private CartDto toDto(ShoppingCart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setStoreId(cart.getStore().getId());
        dto.setStoreName(cart.getStore().getName());
        dto.setTotalAmount(cart.getTotalAmount());
        dto.setItemCount(cart.getItemCount());
        dto.setUpdatedAt(cart.getUpdatedAt());
        List<CartItemDto> items = cart.getCartItems().stream()
                .sorted(Comparator.comparing(CartItem::getId))
                .map(item -> {
                    CartItemDto itemDto = new CartItemDto();
                    itemDto.setId(item.getId());
                    itemDto.setProductId(item.getProduct().getId());
                    itemDto.setProductName(item.getProduct().getName());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setUnitPrice(item.getUnitPrice());
                    itemDto.setTotalPrice(item.getTotalPrice());
                    return itemDto;
                })
                .collect(Collectors.toList());
        dto.setItems(items);
        return dto;
    }
}

