import React from 'react';
import classNames from 'classnames';
import './QuizNavigation.scss';

const QuizNavigation = ({ 
    currentIndex, 
    totalQuestions, 
    setCurrentIndex, 
    answers, 
    questions,
    onSubmit 
}) => {
    const isAllAnswered = questions.every(q => answers[q.questionId]);

    return (
        <div className="quiz-navigation">
            <div className="questions-grid mt-3">
                {questions.map((question, index) => (
                    <button
                        key={question.questionId}
                        className={classNames('question-number mt-2', {
                            'active': index === currentIndex,
                            'answered': answers[question.questionId]
                        })}
                        onClick={() => setCurrentIndex(index)}
                    >
                        {index + 1}
                    </button>
                ))}
            </div>
            <button 
                onClick={onSubmit} 
                className="btn btn-primary w-100 mt-5 fs-4 fw-bold"
                disabled={!isAllAnswered}
            >
                Nộp bài
            </button>
        </div>
    );
};

export default QuizNavigation;
