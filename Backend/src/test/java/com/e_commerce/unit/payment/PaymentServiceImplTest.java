package com.e_commerce.unit.payment;

import com.e_commerce.configuration.VNPAYConfig;
import com.e_commerce.dto.payment.PaymentDTO.PaymentDTO;
import com.e_commerce.entity.order.Orders;
import com.e_commerce.entity.payment.Payment;
import com.e_commerce.entity.payment.PaymentMethod;
import com.e_commerce.enums.PaymentStatus;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.repository.payment.PaymentRepository;
import com.e_commerce.service.email.EmailService;
import com.e_commerce.service.invoice.InvoiceService;
import com.e_commerce.service.order.OrderService;
import com.e_commerce.service.payment.PaymentMethodService;
import com.e_commerce.service.payment.impl.PaymentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private VNPAYConfig vnPayConfig;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMethodService paymentMethodService;

    @Mock
    private EmailService emailService;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Orders testOrder;
    private Payment testPayment;
    private PaymentMethod cashPaymentMethod;
    private PaymentMethod vnpayPaymentMethod;

    @BeforeEach
    void setUp() {
        testOrder = new Orders();
        testOrder.setId(1);
        testOrder.setTotalPrice(BigDecimal.valueOf(1000));

        cashPaymentMethod = new PaymentMethod();
        cashPaymentMethod.setId(2);
        cashPaymentMethod.setName("Cash on Delivery");

        vnpayPaymentMethod = new PaymentMethod();
        vnpayPaymentMethod.setId(1);
        vnpayPaymentMethod.setName("VNPay");

        testPayment = new Payment();
        testPayment.setId(1);
        testPayment.setOrder(testOrder);
        testPayment.setAmount(BigDecimal.valueOf(1000));
        testPayment.setStatus(PaymentStatus.PENDING);
        testPayment.setPaymentMethod(vnpayPaymentMethod);
    }

    @Test
    void getPaymentEntityById_Success() {
        // Arrange
        when(paymentRepository.findById(anyInt())).thenReturn(Optional.of(testPayment));

        // Act
        Payment result = paymentService.getPaymentEntityById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(paymentRepository).findById(1);
    }

    @Test
    void getPaymentEntityById_NotFound_ThrowsException() {
        // Arrange
        when(paymentRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> paymentService.getPaymentEntityById(999));
    }

    @Test
    void createPayment_CashPayment_Success() {
        // Arrange
        when(orderService.getOrder()).thenReturn(testOrder);
        when(request.getParameter("paymentType")).thenReturn("CASH");
        when(paymentMethodService.getPaymentMethodEntityById(2)).thenReturn(cashPaymentMethod);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        doNothing().when(orderService).confirmOrderAfterPayment(any(Orders.class));

        // Act
        PaymentDTO result = paymentService.createPayment(request);

        // Assert
        assertNotNull(result);
        assertEquals("ok", result.getCode());
        assertEquals("success", result.getMessage());
        assertNull(result.getPaymentUrl());
        verify(paymentRepository).save(any(Payment.class));
        verify(orderService).confirmOrderAfterPayment(testOrder);
    }

    @Test
    void createPayment_VNPayPayment_ReturnsPaymentUrl() {
        // Arrange
        Map<String, String> vnpayConfig = new HashMap<>();
        vnpayConfig.put("vnp_Version", "2.1.0");
        vnpayConfig.put("vnp_Command", "pay");

        when(orderService.getOrder()).thenReturn(testOrder);
        when(request.getParameter("paymentType")).thenReturn("VNPAY");
        when(request.getParameter("bankCode")).thenReturn(null);
        when(paymentMethodService.getPaymentMethodEntityById(1)).thenReturn(vnpayPaymentMethod);
        when(vnPayConfig.getVNPayConfig()).thenReturn(vnpayConfig);
        when(vnPayConfig.getVnp_SecretKey()).thenReturn("test_secret_key");
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // Act
        PaymentDTO result = paymentService.createPayment(request);

        // Assert
        assertNotNull(result);
        assertEquals("ok", result.getCode());
        assertNotNull(result.getPaymentUrl());
        verify(paymentRepository).save(any(Payment.class));
    }
}
