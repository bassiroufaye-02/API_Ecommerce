package com.ecommerce.mulboutique.model;

import com.ecommerce.mulboutique.dto.CategoryDto;
import com.ecommerce.mulboutique.dto.ProductDto;
import com.ecommerce.mulboutique.dto.StoreDto;
import com.ecommerce.mulboutique.dto.analytics.AnalyticsResponse;
import com.ecommerce.mulboutique.dto.analytics.TopProductDto;
import com.ecommerce.mulboutique.dto.auth.JwtResponse;
import com.ecommerce.mulboutique.dto.auth.LoginRequest;
import com.ecommerce.mulboutique.dto.auth.SignUpRequest;
import com.ecommerce.mulboutique.dto.cart.AddToCartRequest;
import com.ecommerce.mulboutique.dto.cart.CartDto;
import com.ecommerce.mulboutique.dto.cart.CartItemDto;
import com.ecommerce.mulboutique.dto.cart.UpdateCartItemRequest;
import com.ecommerce.mulboutique.dto.coupon.CouponDto;
import com.ecommerce.mulboutique.dto.coupon.CreateCouponRequest;
import com.ecommerce.mulboutique.dto.order.CreateOrderRequest;
import com.ecommerce.mulboutique.dto.order.OrderDto;
import com.ecommerce.mulboutique.dto.order.OrderItemDto;
import com.ecommerce.mulboutique.dto.order.UpdateOrderStatusRequest;
import com.ecommerce.mulboutique.dto.order.UpdateShippingRequest;
import com.ecommerce.mulboutique.dto.payment.PaymentRequest;
import com.ecommerce.mulboutique.dto.payment.PaymentResponse;
import com.ecommerce.mulboutique.dto.user.AddressDto;
import com.ecommerce.mulboutique.dto.user.AddressRequest;
import com.ecommerce.mulboutique.dto.user.PaymentMethodDto;
import com.ecommerce.mulboutique.dto.user.PaymentMethodRequest;
import com.ecommerce.mulboutique.dto.user.UpdateProfileRequest;
import com.ecommerce.mulboutique.dto.user.UserProfileDto;
import com.ecommerce.mulboutique.entity.Address;
import com.ecommerce.mulboutique.entity.CartItem;
import com.ecommerce.mulboutique.entity.Category;
import com.ecommerce.mulboutique.entity.Coupon;
import com.ecommerce.mulboutique.entity.Order;
import com.ecommerce.mulboutique.entity.OrderItem;
import com.ecommerce.mulboutique.entity.PaymentMethod;
import com.ecommerce.mulboutique.entity.Product;
import com.ecommerce.mulboutique.entity.Review;
import com.ecommerce.mulboutique.entity.ShoppingCart;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.entity.User;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelCoverageTest {

    @Test
    void modelGettersAndSettersExecute() throws Exception {
        List<Class<?>> types = List.of(
                CategoryDto.class, ProductDto.class, StoreDto.class,
                AnalyticsResponse.class, TopProductDto.class,
                JwtResponse.class, LoginRequest.class, SignUpRequest.class,
                AddToCartRequest.class, CartDto.class, CartItemDto.class, UpdateCartItemRequest.class,
                CouponDto.class, CreateCouponRequest.class,
                CreateOrderRequest.class, OrderDto.class, OrderItemDto.class, UpdateOrderStatusRequest.class, UpdateShippingRequest.class,
                PaymentRequest.class, PaymentResponse.class,
                AddressDto.class, AddressRequest.class, PaymentMethodDto.class, PaymentMethodRequest.class,
                UpdateProfileRequest.class, UserProfileDto.class,
                Address.class, CartItem.class, Category.class, Coupon.class, Order.class, OrderItem.class,
                PaymentMethod.class, Product.class, Review.class, ShoppingCart.class, Store.class, User.class
        );

        Map<Class<?>, Object> cache = new HashMap<>();

        for (Class<?> type : types) {
            Object instance = type.getDeclaredConstructor().newInstance();
            cache.put(type, instance);

            for (Method method : type.getMethods()) {
                if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                    Object value = sampleValue(method.getParameterTypes()[0], cache);
                    if (value != null) {
                        method.invoke(instance, value);
                    }
                }
            }

            for (Method method : type.getMethods()) {
                if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                    method.invoke(instance);
                }
                if (method.getName().startsWith("is") && method.getParameterCount() == 0) {
                    method.invoke(instance);
                }
            }
        }
    }

    @Test
    void entityBehaviorBasics() {
        User user = new User();
        user.setId(1L);

        Store store = new Store();
        store.setId(2L);
        store.setName("Store");

        Product product = new Product();
        product.setId(3L);
        product.setName("Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setStore(store);

        ShoppingCart cart = new ShoppingCart(user, store);
        CartItem item = new CartItem(cart, product, 2, new BigDecimal("10.00"));
        cart.addCartItem(item);

        assertEquals(new BigDecimal("20.00"), cart.getTotalAmount());
        assertEquals(2, cart.getItemCount());

        item.incrementQuantity();
        assertEquals(new BigDecimal("30.00"), item.getTotalPrice());

        item.decrementQuantity();
        assertEquals(new BigDecimal("20.00"), item.getTotalPrice());

        cart.clearCart();
        assertEquals(0, cart.getItemCount());

        Coupon coupon = new Coupon("SAVE", Coupon.DiscountType.FIXED_AMOUNT, new BigDecimal("5.00"), store,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        coupon.setIsActive(true);
        assertTrue(coupon.isValid());
        coupon.incrementUsageCount();
        assertEquals(1, coupon.getUsageCount());
    }

    private Object sampleValue(Class<?> type, Map<Class<?>, Object> cache) {
        if (type == String.class) {
            return "value";
        }
        if (type == Long.class || type == long.class) {
            return 1L;
        }
        if (type == Integer.class || type == int.class) {
            return 1;
        }
        if (type == Boolean.class || type == boolean.class) {
            return true;
        }
        if (type == BigDecimal.class) {
            return new BigDecimal("10.00");
        }
        if (type == LocalDateTime.class) {
            return LocalDateTime.of(2026, 3, 6, 12, 0);
        }
        if (type == LocalDate.class) {
            return LocalDate.of(2026, 3, 6);
        }
        if (List.class.isAssignableFrom(type)) {
            return new ArrayList<>();
        }
        if (Set.class.isAssignableFrom(type)) {
            return new HashSet<>();
        }
        if (Map.class.isAssignableFrom(type)) {
            return new HashMap<>();
        }
        if (type.isEnum()) {
            Object[] values = type.getEnumConstants();
            return values.length > 0 ? values[0] : null;
        }
        if (cache.containsKey(type)) {
            return cache.get(type);
        }
        try {
            Object nested = type.getDeclaredConstructor().newInstance();
            cache.put(type, nested);
            return nested;
        } catch (Exception ignored) {
            return null;
        }
    }
}
