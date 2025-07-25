package com.fpoly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSearchResponseDTO {
    private int courseId;
    private String name;
    private String avatar;
    private float price;
    private float averageRating;
    private int follow;
    private boolean isRegistered;
}

