package com.springproject.auctionplatform.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)  // Ensure issueDate is mandatory
    private LocalDateTime issueDate;

    @OneToOne
    @JoinColumn(name = "promotion_id", nullable = false)  // Explicit foreign key for Payment
    private Promotion promotion;

    @Column(nullable = false)
    private BigDecimal amountExcludingTax;

    @Column(nullable = false)
    private BigDecimal vatRate;

    private BigDecimal vatAmount;

    private BigDecimal totalAmount;
}
