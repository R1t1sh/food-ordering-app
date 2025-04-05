package com.foodapp.model;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private String status;
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;

    public Payment(Bill bill, String razorpayOrderId) {
        this.bill = bill;
        this.razorpayOrderId = razorpayOrderId;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
}
