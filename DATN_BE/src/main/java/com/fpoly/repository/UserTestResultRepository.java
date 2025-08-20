package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.fpoly.entity.RegisteredCourse;
import com.fpoly.entity.Test;
import com.fpoly.entity.User;
import com.fpoly.entity.UserTestResult;

import com.fpoly.dto.MonthlyRevenueDTO;

public interface UserTestResultRepository extends JpaRepository<UserTestResult, Integer> {

    // Lấy kết quả mới nhất
    Optional<UserTestResult> findTopByUser_UserIdAndTest_TestIdOrderByCreateAtDesc(int userId, int testId);

    // Lấy theo testId và userId
    Optional<UserTestResult> findByTest_TestIdAndUser_UserId(Integer testId, Integer userId);

    // Tìm kết quả test của 1 user trong 1 bài test
    Optional<UserTestResult> findByUser_UserIdAndTest_TestId(int userId, int testId);
    

    // Nếu bạn muốn lấy luôn list (trường hợp user làm lại nhiều lần, nhưng bạn chỉ lưu 1 record)
    List<UserTestResult> findAllByUser_UserIdAndTest_TestId(int userId, int testId);
    
    // Cập nhật score
    @Modifying
    @Transactional
    @Query("UPDATE UserTestResult u SET u.score = 0 WHERE u.user.userId = :userId AND u.test.testId = :testId")
    void updateScoreToZero(@Param("userId") int userId, @Param("testId") int testId);

    // Hiển thị test đã làm
    @Query("SELECT ur FROM UserTestResult ur JOIN ur.test t WHERE ur.user.userId = :userId")
    List<UserTestResult> findByUser(@Param("userId") int userId);

    // Tìm theo entity
    UserTestResult findByUserAndTest(User user, Test test);
}

