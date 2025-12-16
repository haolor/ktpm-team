package com.e_commerce.unit.invoice;

import com.e_commerce.dto.invoice.invoiceDTO.InvoiceCreateForm;
import com.e_commerce.dto.invoice.invoiceDTO.InvoiceDTO;
import com.e_commerce.entity.account.Account;
import com.e_commerce.entity.invoice.Invoice;
import com.e_commerce.entity.invoice.InvoiceDetails;
import com.e_commerce.entity.order.Orders;
import com.e_commerce.entity.payment.PaymentMethod;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.mapper.invoice.InvoiceMapper;
import com.e_commerce.repository.invoice.InvoiceRepository;
import com.e_commerce.service.account.AccountService;
import com.e_commerce.service.invoice.InvoiceDetailsService;
import com.e_commerce.service.invoice.impl.InvoiceServiceImpl;
import com.e_commerce.service.order.OrderService;
import com.e_commerce.service.payment.PaymentMethodService;
import com.e_commerce.service.voucher.VoucherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private OrderService orderService;

    @Mock
    private AccountService accountService;

    @Mock
    private PaymentMethodService paymentMethodService;

    @Mock
    private VoucherService voucherService;

    @Mock
    private InvoiceDetailsService invoiceDetailsService;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private Orders testOrder;
    private Invoice testInvoice;
    private InvoiceDTO testInvoiceDTO;
    private Account testAccount;
    private PaymentMethod testPaymentMethod;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1);
        testAccount.setEmail("test@example.com");

        testOrder = new Orders();
        testOrder.setId(1);
        testOrder.setAccount(testAccount);
        testOrder.setTotalPrice(BigDecimal.valueOf(1000));

        testPaymentMethod = new PaymentMethod();
        testPaymentMethod.setId(1);
        testPaymentMethod.setName("Cash");

        testInvoice = new Invoice();
        testInvoice.setId(1);
        testInvoice.setOrder(testOrder);
        testInvoice.setCustomer(testAccount);
        testInvoice.setPaymentMethod(testPaymentMethod);
        testInvoice.setSubTotal(BigDecimal.valueOf(1000));
        testInvoice.setTotalAmount(BigDecimal.valueOf(1000));
        testInvoice.setDiscountAmount(BigDecimal.ZERO);
        testInvoice.setShippingFee(BigDecimal.ZERO);

        testInvoiceDTO = new InvoiceDTO();
        testInvoiceDTO.setId(1);
        testInvoiceDTO.setTotalAmount(BigDecimal.valueOf(1000));
    }

    @Test
    void getInvoiceById_Success() {
        // Arrange
        when(invoiceRepository.findById(anyInt())).thenReturn(Optional.of(testInvoice));

        // Act
        Invoice result = invoiceService.getInvoiceById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(invoiceRepository).findById(1);
    }

    @Test
    void getInvoiceById_NotFound_ThrowsException() {
        // Arrange
        when(invoiceRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> invoiceService.getInvoiceById(999));
    }

    @Test
    void createInvoice_Success() {
        // Arrange
        InvoiceCreateForm createForm = new InvoiceCreateForm();
        createForm.setOrderId(1);
        createForm.setStaffId(1);
        createForm.setPaymentMethodId(1);
        createForm.setShippingFee(BigDecimal.valueOf(50));

        InvoiceDetails invoiceDetails = new InvoiceDetails();
        invoiceDetails.setLineTotal(BigDecimal.valueOf(1000));
        List<InvoiceDetails> detailsList = Arrays.asList(invoiceDetails);

        when(orderService.getOrderEntityById(anyInt())).thenReturn(testOrder);
        when(invoiceRepository.existsByOrder(any(Orders.class))).thenReturn(false);
        when(accountService.getAccountEntityById(anyInt())).thenReturn(testAccount);
        when(paymentMethodService.getPaymentMethodEntityById(anyInt())).thenReturn(testPaymentMethod);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);
        when(invoiceDetailsService.createInvoiceDetailsFromOrder(anyInt(), any(Invoice.class))).thenReturn(detailsList);
        when(invoiceMapper.convertEntityToDTO(any(Invoice.class))).thenReturn(testInvoiceDTO);

        // Act
        InvoiceDTO result = invoiceService.createInvoice(createForm);

        // Assert
        assertNotNull(result);
        verify(invoiceRepository, times(2)).save(any(Invoice.class));
        verify(invoiceDetailsService).createInvoiceDetailsFromOrder(anyInt(), any(Invoice.class));
    }

    @Test
    void createInvoice_AlreadyExists_ThrowsException() {
        // Arrange
        InvoiceCreateForm createForm = new InvoiceCreateForm();
        createForm.setOrderId(1);
        createForm.setStaffId(1);
        createForm.setPaymentMethodId(1);

        when(orderService.getOrderEntityById(anyInt())).thenReturn(testOrder);
        when(invoiceRepository.existsByOrder(any(Orders.class))).thenReturn(true);

        // Act & Assert
        assertThrows(CustomException.class, () -> invoiceService.createInvoice(createForm));
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }
}
