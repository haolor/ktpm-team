package com.e_commerce.repository.invoice;

import com.e_commerce.entity.invoice.InvoiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetails, Integer> {
    List<InvoiceDetails> findByInvoiceId(Integer invoiceId);
}
