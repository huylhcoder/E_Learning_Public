package com.fpoly.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponse {
    private int paymentId;
    private String transactionNo;
    private boolean transactionStatus;
    private String banktranNo;
    private String txnRef;
    private float amount;
    private String bankCode;
    private Date createAt;
    private List<CourseResponse> courses;
}
