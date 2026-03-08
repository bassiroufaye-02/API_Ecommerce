package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.payment.PaymentResponse;
import com.ecommerce.mulboutique.entity.Order;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.exception.ForbiddenException;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private OrderRepository orderRepository;

    public PaymentResponse initiatePayment(Long orderId, String paymentMethod, User user) {
        Order order = getOrderForUser(orderId, user);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setPaymentReference(generatePaymentReference());
        orderRepository.save(order);

        logger.info("event=payment_initiated orderId={} paymentReference={}", orderId, order.getPaymentReference());
        return toResponse(order);
    }

    public PaymentResponse confirmPayment(Long orderId, User user) {
        Order order = getOrderForUser(orderId, user);
        order.setPaymentStatus(Order.PaymentStatus.PAID);
        order.setStatus(Order.OrderStatus.CONFIRMED);
        orderRepository.save(order);

        logger.info("event=payment_confirmed orderId={} paymentReference={}", orderId, order.getPaymentReference());
        return toResponse(order);
    }

    public PaymentResponse failPayment(Long orderId, User user) {
        Order order = getOrderForUser(orderId, user);
        order.setPaymentStatus(Order.PaymentStatus.FAILED);
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        logger.info("event=payment_failed orderId={} paymentReference={}", orderId, order.getPaymentReference());
        return toResponse(order);
    }

    public PaymentResponse refundPayment(Long orderId, User user) {
        Order order = getOrderForUser(orderId, user);
        order.setPaymentStatus(Order.PaymentStatus.REFUNDED);
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        logger.info("event=payment_refunded orderId={} paymentReference={}", orderId, order.getPaymentReference());
        return toResponse(order);
    }

    private Order getOrderForUser(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Commande non trouvee"));
        if (!order.getCustomer().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("Acces refuse");
        }
        return order;
    }

    private PaymentResponse toResponse(Order order) {
        PaymentResponse response = new PaymentResponse();
        response.setOrderId(order.getId());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentReference(order.getPaymentReference());
        return response;
    }

    private String generatePaymentReference() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}

