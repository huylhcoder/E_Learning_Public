import React from 'react';
import './ReviewQuiz.scss';

const ReviewQuiz = ({ reviewQuiz }) => {
    if (!reviewQuiz) return null;

    return (
        <div className="review-quiz-container">
            <h3 className="mb-4">Chi tiết:</h3>

            {reviewQuiz.questions.map((q, qIndex) => (
                <div key={q.id} className="question-block mb-4">
                    <h5>
                        Câu {qIndex + 1}: {q.content}
                    </h5>
                    <ul className="list-group">
                        {q.answers.map((a) => {
                            let className = 'list-group-item';

                            if (a.selected && a.correct) {
                                // User chọn đúng
                                className += ' list-group-item-success'; // xanh lá
                            } else if (a.selected && !a.correct) {
                                // User chọn sai
                                className += ' list-group-item-danger'; // đỏ
                            } else if (!a.selected && a.correct) {
                                // Đáp án đúng (nhưng user không chọn)
                                className += ' list-group-item-success';
                            }

                            return (
                                <li key={a.id} className={className}>
                                    {a.content}
                                    {a.selected && !a.correct && (
                                        <span className="ms-2 badge bg-danger">Bạn chọn</span>
                                    )}
                                    {a.selected && a.correct && (
                                        <span className="ms-2 badge bg-success">Bạn chọn</span>
                                    )}
                                    {!a.selected && a.correct && (
                                        <span className="ms-2 badge bg-success">Đáp án đúng</span>
                                    )}
                                </li>
                            );
                        })}
                    </ul>
                </div>
            ))}
        </div>
    );
};

export default ReviewQuiz;
