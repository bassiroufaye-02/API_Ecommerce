package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.coupon.CouponDto;
import com.ecommerce.mulboutique.dto.coupon.CreateCouponRequest;
import com.ecommerce.mulboutique.entity.Coupon;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.exception.BadRequestException;
import com.ecommerce.mulboutique.exception.ConflictException;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.repository.CouponRepository;
import com.ecommerce.mulboutique.repository.StoreRepository;
import com.ecommerce.mulboutique.util.TextSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private StoreRepository storeRepository;

    public CouponDto createCoupon(CreateCouponRequest request, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Boutique non trouvee"));

        if (couponRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Code coupon deja utilise");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(TextSanitizer.clean(request.getCode()).toUpperCase());
        coupon.setDescription(TextSanitizer.clean(request.getDescription()));
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinimumAmount(request.getMinimumAmount() == null ? BigDecimal.ZERO : request.getMinimumAmount());
        coupon.setMaximumDiscount(request.getMaximumDiscount());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setStore(store);
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidUntil(request.getValidUntil());
        coupon.setIsActive(true);

        return toDto(couponRepository.save(coupon));
    }

    public CouponDto updateCoupon(Long id, CreateCouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coupon non trouve"));

        coupon.setDescription(TextSanitizer.clean(request.getDescription()));
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinimumAmount(request.getMinimumAmount() == null ? BigDecimal.ZERO : request.getMinimumAmount());
        coupon.setMaximumDiscount(request.getMaximumDiscount());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidUntil(request.getValidUntil());

        return toDto(couponRepository.save(coupon));
    }

    public void deactivateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coupon non trouve"));
        coupon.setIsActive(false);
        couponRepository.save(coupon);
    }

    public List<CouponDto> getCouponsByStore(Long storeId) {
        return couponRepository.findByStoreId(storeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Coupon validateCoupon(String code, Long storeId, BigDecimal cartTotal) {
        Coupon coupon = couponRepository.findByCode(TextSanitizer.clean(code).toUpperCase())
                .orElseThrow(() -> new NotFoundException("Coupon non trouve"));

        if (!coupon.getStore().getId().equals(storeId)) {
            throw new BadRequestException("Coupon invalide pour cette boutique");
        }
        if (!coupon.isValid()) {
            throw new BadRequestException("Coupon invalide ou expire");
        }
        if (cartTotal != null && coupon.getMinimumAmount() != null && cartTotal.compareTo(coupon.getMinimumAmount()) < 0) {
            throw new BadRequestException("Montant minimum non atteint");
        }
        return coupon;
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal total) {
        if (coupon == null || total == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discount = total.multiply(coupon.getDiscountValue()).divide(new BigDecimal("100"));
        } else {
            discount = coupon.getDiscountValue();
        }
        if (coupon.getMaximumDiscount() != null && discount.compareTo(coupon.getMaximumDiscount()) > 0) {
            discount = coupon.getMaximumDiscount();
        }
        if (discount.compareTo(total) > 0) {
            discount = total;
        }
        return discount;
    }

    public void incrementUsage(Coupon coupon) {
        if (coupon == null) {
            return;
        }
        coupon.incrementUsageCount();
        couponRepository.save(coupon);
    }

    private CouponDto toDto(Coupon coupon) {
        CouponDto dto = new CouponDto();
        dto.setId(coupon.getId());
        dto.setCode(coupon.getCode());
        dto.setDescription(coupon.getDescription());
        dto.setDiscountType(coupon.getDiscountType());
        dto.setDiscountValue(coupon.getDiscountValue());
        dto.setMinimumAmount(coupon.getMinimumAmount());
        dto.setMaximumDiscount(coupon.getMaximumDiscount());
        dto.setUsageLimit(coupon.getUsageLimit());
        dto.setUsageCount(coupon.getUsageCount());
        dto.setIsActive(coupon.getIsActive());
        dto.setValidFrom(coupon.getValidFrom());
        dto.setValidUntil(coupon.getValidUntil());
        return dto;
    }
}

