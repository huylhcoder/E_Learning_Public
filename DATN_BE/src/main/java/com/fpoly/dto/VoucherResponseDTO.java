package com.fpoly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class VoucherResponseDTO {
    private int voucherId;
    private String name;
    private String description;
    private String voucherCode;
    private float percentSale;
    private Date startDate;
    private Date endDate;
}
