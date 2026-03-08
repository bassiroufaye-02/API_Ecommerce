package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.user.AddressDto;
import com.ecommerce.mulboutique.dto.user.AddressRequest;
import com.ecommerce.mulboutique.dto.user.PaymentMethodDto;
import com.ecommerce.mulboutique.dto.user.PaymentMethodRequest;
import com.ecommerce.mulboutique.dto.user.UpdateProfileRequest;
import com.ecommerce.mulboutique.entity.Address;
import com.ecommerce.mulboutique.entity.PaymentMethod;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.exception.ForbiddenException;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.repository.AddressRepository;
import com.ecommerce.mulboutique.repository.PaymentMethodRepository;
import com.ecommerce.mulboutique.repository.UserRepository;
import com.ecommerce.mulboutique.util.TextSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public User updateProfile(User user, UpdateProfileRequest request) {
        user.setFirstName(TextSanitizer.clean(request.getFirstName()));
        user.setLastName(TextSanitizer.clean(request.getLastName()));
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(TextSanitizer.clean(request.getEmail()));
        }
        user.setPhone(TextSanitizer.clean(request.getPhone()));
        return userRepository.save(user);
    }

    public List<AddressDto> getAddresses(User user) {
        return addressRepository.findByUserId(user.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AddressDto addAddress(User user, AddressRequest request) {
        Address address = new Address();
        address.setUser(user);
        address.setLabel(TextSanitizer.clean(request.getLabel()));
        address.setLine1(TextSanitizer.clean(request.getLine1()));
        address.setLine2(TextSanitizer.clean(request.getLine2()));
        address.setCity(TextSanitizer.clean(request.getCity()));
        address.setState(TextSanitizer.clean(request.getState()));
        address.setCountry(TextSanitizer.clean(request.getCountry()));
        address.setPostalCode(TextSanitizer.clean(request.getPostalCode()));
        address.setPhone(TextSanitizer.clean(request.getPhone()));
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultAddress(user);
            address.setIsDefault(true);
        } else {
            address.setIsDefault(false);
        }
        return toDto(addressRepository.save(address));
    }

    public AddressDto updateAddress(Long addressId, User user, AddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Adresse non trouvee"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Acces refuse");
        }
        address.setLabel(TextSanitizer.clean(request.getLabel()));
        address.setLine1(TextSanitizer.clean(request.getLine1()));
        address.setLine2(TextSanitizer.clean(request.getLine2()));
        address.setCity(TextSanitizer.clean(request.getCity()));
        address.setState(TextSanitizer.clean(request.getState()));
        address.setCountry(TextSanitizer.clean(request.getCountry()));
        address.setPostalCode(TextSanitizer.clean(request.getPostalCode()));
        address.setPhone(TextSanitizer.clean(request.getPhone()));
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultAddress(user);
            address.setIsDefault(true);
        } else {
            address.setIsDefault(false);
        }
        return toDto(addressRepository.save(address));
    }

    public void deleteAddress(Long addressId, User user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Adresse non trouvee"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Acces refuse");
        }
        addressRepository.delete(address);
    }

    public List<PaymentMethodDto> getPaymentMethods(User user) {
        return paymentMethodRepository.findByUserId(user.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PaymentMethodDto addPaymentMethod(User user, PaymentMethodRequest request) {
        PaymentMethod method = new PaymentMethod();
        method.setUser(user);
        method.setMethodType(request.getMethodType());
        method.setBrand(TextSanitizer.clean(request.getBrand()));
        method.setLast4(TextSanitizer.clean(request.getLast4()));
        method.setExpMonth(request.getExpMonth());
        method.setExpYear(request.getExpYear());
        method.setToken("tok_" + user.getId() + "_" + System.currentTimeMillis());
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultPaymentMethod(user);
            method.setIsDefault(true);
        } else {
            method.setIsDefault(false);
        }
        return toDto(paymentMethodRepository.save(method));
    }

    public PaymentMethodDto updatePaymentMethod(Long id, User user, PaymentMethodRequest request) {
        PaymentMethod method = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Moyen de paiement non trouve"));
        if (!method.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Acces refuse");
        }
        method.setMethodType(request.getMethodType());
        method.setBrand(TextSanitizer.clean(request.getBrand()));
        method.setLast4(TextSanitizer.clean(request.getLast4()));
        method.setExpMonth(request.getExpMonth());
        method.setExpYear(request.getExpYear());
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultPaymentMethod(user);
            method.setIsDefault(true);
        } else {
            method.setIsDefault(false);
        }
        return toDto(paymentMethodRepository.save(method));
    }

    public void deletePaymentMethod(Long id, User user) {
        PaymentMethod method = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Moyen de paiement non trouve"));
        if (!method.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Acces refuse");
        }
        paymentMethodRepository.delete(method);
    }

    private void unsetDefaultAddress(User user) {
        List<Address> addresses = addressRepository.findByUserId(user.getId());
        addresses.forEach(addr -> addr.setIsDefault(false));
        addressRepository.saveAll(addresses);
    }

    private void unsetDefaultPaymentMethod(User user) {
        List<PaymentMethod> methods = paymentMethodRepository.findByUserId(user.getId());
        methods.forEach(m -> m.setIsDefault(false));
        paymentMethodRepository.saveAll(methods);
    }

    private AddressDto toDto(Address address) {
        AddressDto dto = new AddressDto();
        dto.setId(address.getId());
        dto.setLabel(address.getLabel());
        dto.setLine1(address.getLine1());
        dto.setLine2(address.getLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setCountry(address.getCountry());
        dto.setPostalCode(address.getPostalCode());
        dto.setPhone(address.getPhone());
        dto.setIsDefault(address.getIsDefault());
        return dto;
    }

    private PaymentMethodDto toDto(PaymentMethod method) {
        PaymentMethodDto dto = new PaymentMethodDto();
        dto.setId(method.getId());
        dto.setMethodType(method.getMethodType());
        dto.setBrand(method.getBrand());
        dto.setLast4(method.getLast4());
        dto.setExpMonth(method.getExpMonth());
        dto.setExpYear(method.getExpYear());
        dto.setIsDefault(method.getIsDefault());
        return dto;
    }
}

