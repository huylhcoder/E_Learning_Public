package com.fpoly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSearchRequest {
    private String categorySlug;
    private String courseName;
    private Boolean free;
    private Float minPrice;
    private Float maxPrice;
    private Integer ratedStar;
    private Integer levelId;
    private Boolean priceASC;
    private Boolean priceDESC;
}

