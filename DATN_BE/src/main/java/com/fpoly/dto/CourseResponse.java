package com.fpoly.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private int courseId;
    private String name;
    private String avatar;
    private float price;
    private float averageRating;
}