package com.fpoly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private int courseId;
    private String name;
    private String description;
    private String topic;
    private String level;
    private float averageRating;
    private float price;
}

