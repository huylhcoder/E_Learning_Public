package com.fpoly.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailManagerDTO {
    private int courseId;
    private String name;
    private int status;
    private String description;
    private String avatar;
    private float price;
    private String topic;
//    private int categoryId;
    private List<Integer> categoryIds;
    private int levelId;
    private String updateAt;
    
}