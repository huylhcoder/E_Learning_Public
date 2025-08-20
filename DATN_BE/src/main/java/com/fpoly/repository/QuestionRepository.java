package com.fpoly.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fpoly.entity.Question;
import com.fpoly.entity.Test;
import com.fpoly.entity.User;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
//Quiz Page
	// Lấy tất cả câu hỏi của test
	List<Question> findByTest_TestId(int testId);

//Khác
	List<Question> findByTest(Test test);
}
