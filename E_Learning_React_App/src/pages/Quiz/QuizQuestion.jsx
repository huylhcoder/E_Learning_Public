import React from 'react';
import './QuizQuestion.scss';

const QuizQuestion = ({ 
    question, 
    currentIndex, 
    totalQuestions, 
    answers, 
    onAnswerSelect,
    onNavigate 
}) => {
    return (
        <div className="quiz-question">
            <div className="question-content">
                <span className="fs-3 fw-bold text-primary mb-3">
                    Câu hỏi {currentIndex + 1}/{totalQuestions}
                </span>
                <p className="question-text mt-3">{question.contents}</p>
                
                <div className="answers-list">
                    {question.listAnswerDTO?.map((answer) => {
                        const inputId = `answer_${question.questionId}_${answer.answerId}`;
                        return (
                            <div 
                                className={`answer-option ${answers[question.questionId] === answer.answerId ? 'selected' : ''}`}
                                key={answer.answerId}
                                onClick={() => onAnswerSelect(question.questionId, answer.answerId)}
                            >
                                <input
                                    id={inputId}
                                    type="radio"
                                    name={`question_${question.questionId}`}
                                    checked={answers[question.questionId] === answer.answerId}
                                    onChange={() => {}}
                                    className="form-check-input"
                                />
                                <label htmlFor={inputId} className="form-check-label">
                                    {answer.text}
                                </label>
                            </div>
                        );
                    })}
                </div>
            </div>

            <div className="navigation-buttons">
                <button
                    className="btn btn-outline-primary fs-5 fw-bold p-3"
                    onClick={() => onNavigate('prev')}
                    disabled={currentIndex === 0}
                >
                    Câu trước
                </button>
                <button
                    className="btn btn-outline-primary fs-5 fw-bold"
                    onClick={() => onNavigate('next')}
                    disabled={currentIndex === totalQuestions - 1}
                >
                    Câu tiếp theo
                </button>
            </div>
        </div>
    );
};

export default QuizQuestion;
