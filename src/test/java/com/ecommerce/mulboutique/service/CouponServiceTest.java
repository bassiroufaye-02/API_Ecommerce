package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.entity.Coupon;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.repository.CouponRepository;
import com.ecommerce.mulboutique.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    void validateCoupon_requiresMatchingStoreAndMinimumAmount() {
        Store store = new Store();
        store.setId(1L);

        Coupon coupon = new Coupon("SAVE", Coupon.DiscountType.FIXED_AMOUNT, new BigDecimal("10.00"), store,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        coupon.setMinimumAmount(new BigDecimal("50.00"));
        coupon.setIsActive(true);

        when(couponRepository.findByCode("SAVE")).thenReturn(Optional.of(coupon));

        Coupon validated = couponService.validateCoupon("save", 1L, new BigDecimal("100.00"));

        assertEquals("SAVE", validated.getCode());
    }

    @Test
    void validateCoupon_wrongStoreThrows() {
        Store store = new Store();
        store.setId(1L);

        Coupon coupon = new Coupon("SAVE", Coupon.DiscountType.FIXED_AMOUNT, new BigDecimal("10.00"), store,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        coupon.setIsActive(true);

        when(couponRepository.findByCode("SAVE")).thenReturn(Optional.of(coupon));

        assertThrows(RuntimeException.class, () -> couponService.validateCoupon("save", 2L, new BigDecimal("100.00")));
    }

    @Test
    void calculateDiscount_appliesCaps() {
        Coupon coupon = new Coupon();
        coupon.setDiscountType(Coupon.DiscountType.PERCENTAGE);
        coupon.setDiscountValue(new BigDecimal("50"));
        coupon.setMaximumDiscount(new BigDecimal("30"));

        BigDecimal discount = couponService.calculateDiscount(coupon, new BigDecimal("100.00"));

        assertEquals(new BigDecimal("30"), discount);
    }

    @Test
    void incrementUsage_nullCouponDoesNothing() {
        couponService.incrementUsage(null);
        verifyNoInteractions(couponRepository);
    }

    @Test
    void incrementUsage_updatesUsageCount() {
        Coupon coupon = new Coupon();
        coupon.setUsageCount(1);

        couponService.incrementUsage(coupon);

        assertEquals(2, coupon.getUsageCount());
        verify(couponRepository).save(any(Coupon.class));
    }
}
