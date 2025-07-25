package com.fpoly.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fpoly.dto.CourseResponseDTO;
import com.fpoly.dto.CourseSearchRequest;
import com.fpoly.entity.Course;
import com.fpoly.entity.User;

public interface CourseRepositoryCustom {
    Page<Course> searchCourses(
        String categorySlug,
        String courseName,
        Boolean free,
        Float minPrice,
        Float maxPrice,
        Integer ratedStar,
        Integer levelId,
        Pageable pageable
    );
}

