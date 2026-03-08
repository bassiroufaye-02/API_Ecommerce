package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.cart.AddToCartRequest;
import com.ecommerce.mulboutique.dto.cart.CartDto;
import com.ecommerce.mulboutique.entity.CartItem;
import com.ecommerce.mulboutique.entity.Product;
import com.ecommerce.mulboutique.entity.ShoppingCart;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.repository.CartItemRepository;
import com.ecommerce.mulboutique.repository.ProductRepository;
import com.ecommerce.mulboutique.repository.ShoppingCartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ShoppingCartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void addItem_createsCartAndAddsItem() {
        User user = new User();
        user.setId(10L);

        Store store = new Store();
        store.setId(1L);
        store.setName("Tech");

        Product product = new Product();
        product.setId(5L);
        product.setName("Phone");
        product.setStore(store);
        product.setPrice(new BigDecimal("50.00"));
        product.setStockQuantity(10);
        product.setIsActive(true);

        AddToCartRequest request = new AddToCartRequest();
        request.setStoreId(1L);
        request.setProductId(5L);
        request.setQuantity(2);

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(cartRepository.findByCustomerIdAndStoreId(10L, 1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        CartDto dto = cartService.addItem(request, user);

        assertNotNull(dto);
        assertEquals(2, dto.getItemCount());
        assertEquals(new BigDecimal("100.00"), dto.getTotalAmount());
        assertEquals(1, dto.getItems().size());
        assertEquals(5L, dto.getItems().get(0).getProductId());
    }

    @Test
    void updateItem_updatesQuantityAndTotal() {
        User user = new User();
        user.setId(10L);

        Store store = new Store();
        store.setId(1L);
        store.setName("Tech");

        Product product = new Product();
        product.setId(5L);
        product.setName("Phone");
        product.setStore(store);
        product.setPrice(new BigDecimal("20.00"));
        product.setStockQuantity(10);

        ShoppingCart cart = new ShoppingCart(user, store);
        cart.setId(100L);

        CartItem item = new CartItem(cart, product, 1, new BigDecimal("20.00"));
        item.setId(200L);
        cart.addCartItem(item);

        when(cartItemRepository.findById(200L)).thenReturn(Optional.of(item));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartDto dto = cartService.updateItem(200L, 3, user);

        assertEquals(3, dto.getItemCount());
        assertEquals(new BigDecimal("60.00"), dto.getTotalAmount());
    }

    @Test
    void removeItem_removesFromCart() {
        User user = new User();
        user.setId(10L);

        Store store = new Store();
        store.setId(1L);
        store.setName("Tech");

        Product product = new Product();
        product.setId(5L);
        product.setName("Phone");
        product.setStore(store);
        product.setPrice(new BigDecimal("20.00"));
        product.setStockQuantity(10);

        ShoppingCart cart = new ShoppingCart(user, store);
        cart.setId(100L);

        CartItem item = new CartItem(cart, product, 1, new BigDecimal("20.00"));
        item.setId(200L);
        cart.addCartItem(item);

        when(cartItemRepository.findById(200L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartDto dto = cartService.removeItem(200L, user);

        assertEquals(0, dto.getItemCount());
        assertEquals(0, dto.getTotalAmount().compareTo(BigDecimal.ZERO));
        verify(cartItemRepository).delete(item);
    }
}
