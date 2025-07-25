package com.fpoly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.entity.Category;
import com.fpoly.entity.Course;
import com.fpoly.entity.CourseCategory;
import com.fpoly.entity.CourseCategoryId;
import com.fpoly.entity.Voucher;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, CourseCategoryId> {
    List<CourseCategory> findByCourse(Course course);
}


