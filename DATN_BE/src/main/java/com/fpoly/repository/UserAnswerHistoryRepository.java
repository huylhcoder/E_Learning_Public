package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fpoly.entity.Question;
import com.fpoly.entity.User;
import com.fpoly.entity.UserAnswerHistory;
import com.fpoly.entity.Test;

@Repository
public interface UserAnswerHistoryRepository extends JpaRepository<UserAnswerHistory, Integer> {

// Quiz Page
    List<UserAnswerHistory> findByTest_TestIdAndUser_UserId(Integer testId, Integer userId);

    void deleteByTest_TestIdAndUser_UserId(Integer testId, Integer userId);

    // Lấy lịch sử trả lời của user cho test
    List<UserAnswerHistory> findByUser_UserIdAndTest_TestId(int userId, int testId);

    // Lấy đáp án user đã chọn cho 1 câu hỏi
    Optional<UserAnswerHistory> findByUser_UserIdAndTest_TestIdAndQuestion_QuestionId(
            int userId, int testId, int questionId);

    // Xóa toàn bộ lịch sử làm bài của user cho test
    void deleteByUser_UserIdAndTest_TestId(int userId, int testId);
    
   // Tìm kiếm đáp án người dùng đã chọn
    Optional<UserAnswerHistory> findByUserAndTestAndQuestion(
            User user,
            Test test,
            Question question
    );
    
    @Query("SELECT uah FROM UserAnswerHistory uah WHERE uah.user = :user AND uah.test = :test AND uah.question = :question")
    List<UserAnswerHistory> findAllByUserAndTestAndQuestion(@Param("user") User user,
                                                            @Param("test") Test test,
                                                            @Param("question") Question question);
    
    List<UserAnswerHistory> findByUserAndTest(User user, Test test);

// Khác
    @Query("SELECT u FROM UserAnswerHistory u WHERE u.user.userId = :userId AND u.question.questionId = :questionId")
    Optional<UserAnswerHistory> findByUserAndQuestion(@Param("userId") int userId, @Param("questionId") int questionId);

    UserAnswerHistory findByUserAndQuestion(User user, Question question);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserAnswerHistory u WHERE u.user.userId = ?1 AND u.test.testId = ?2")
    void deleteByUserAndTestId(int userId, int testId);
}
