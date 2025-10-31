//Thư viện
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import axios from '~/utils/CustomizeAxios';

//Component
import QuizResult from './QuizResult';
import QuizQuestion from './QuizQuestion';
import QuizNavigation from './QuizNavigation';
import ReviewQuiz from './ReviewQuiz';
import LoadingSpinner from '~/components/LoadingSpinner';
import useDebounce from '~/hooks/useDebounce'; // 👈 thêm hook debounce

const Quiz = () => {
    const [quiz, setQuiz] = useState(null);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [answers, setAnswers] = useState({});
    const [pendingAnswer, setPendingAnswer] = useState(null); // 👈 lưu đáp án vừa chọn
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(true);
    const [reviewQuiz, setReviewQuiz] = useState(null);

    const token = localStorage.getItem('token');
    const testId = new URLSearchParams(window.location.search).get('testId');
    const navigate = useNavigate();

    // Debounce 500ms khi pendingAnswer thay đổi
    const debouncedAnswer = useDebounce(pendingAnswer, 500);

    useEffect(() => {
        if (!token) {
            navigate('/login');
            return;
        }
        loadQuiz();
    }, [token, testId]);

    const loadQuiz = async () => {
        try {
            const resultResponse = await axios.get(`/test/${testId}/result`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (resultResponse.data.score !== 0) {
                setResult(resultResponse.data);
                const answersResponse = await axios.get(`/test/${testId}/answers`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setReviewQuiz(answersResponse.data);
            } else {
                const questionsResponse = await axios.get(`/test/${testId}/questions`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setQuiz(questionsResponse.data);

                const userAnswers = {};
                questionsResponse.data.questions.forEach((q) => {
                    if (q.selectedAnswerId) userAnswers[q.questionId] = q.selectedAnswerId;
                });
                setAnswers(userAnswers);
            }
        } catch (error) {
            console.error('Failed to load test:', error);
        } finally {
            setLoading(false);
        }
    };

    // 👇 chỉ cập nhật UI ngay, còn API sẽ gửi sau khi debounce xong
    const handleAnswerSelect = (questionId, answerId) => {
        setAnswers((prev) => ({ ...prev, [questionId]: answerId }));
        setPendingAnswer({ questionId, answerId }); // ghi nhớ để debounce
    };

    // 👇 chỉ gọi API sau khi user ngừng chọn ~500ms
    useEffect(() => {
        if (!debouncedAnswer) return;
        const sendAnswer = async () => {
            const { questionId, answerId } = debouncedAnswer;
            try {
                await axios.post(
                    `/test/${testId}/answer`,
                    { questionId, answerId },
                    { headers: { Authorization: `Bearer ${token}` } },
                );
            } catch (error) {
                console.error('Failed to save answer:', error);
            }
        };
        sendAnswer();
    }, [debouncedAnswer]);

    const handleSubmit = async () => {
        if (!confirmSubmission()) return;
        try {
            const response = await axios.post(
                `/test/${testId}/submit`,
                {},
                { headers: { Authorization: `Bearer ${token}` } },
            );
            setResult(response.data);
            const answersResponse = await axios.get(`/test/${testId}/answers`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setReviewQuiz(answersResponse.data);
        } catch (error) {
            console.error('Failed to submit test:', error);
        }
    };

    const handleReset = async () => {
        if (!window.confirm('Bạn có chắc chắn muốn làm lại bài kiểm tra?')) return;
        try {
            await axios.delete(`/test/${testId}/reset`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            const questionsResponse = await axios.get(`/test/${testId}/questions`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setQuiz(questionsResponse.data || null);
            const userAnswers = {};
            (questionsResponse.data?.questions || []).forEach((q) => {
                if (q.selectedAnswerId != null) {
                    userAnswers[q.questionId] = q.selectedAnswerId;
                }
            });
            setAnswers(userAnswers);
            setResult(null);
            setReviewQuiz(null);
            setCurrentQuestionIndex(0);
        } catch (error) {
            console.error('Failed to reset quiz:', error);
        }
    };

    const confirmSubmission = () => {
        const total = quiz.questions.length;
        const answered = Object.keys(answers).filter((key) => answers[key] !== null).length;
        if (answered < total) {
            alert(`Bạn đã trả lời ${answered}/${total} câu hỏi. Vui lòng hoàn thành trước khi nộp.`);
            return false;
        }
        return window.confirm('Bạn có chắc chắn muốn nộp bài?');
    };

    const handleNavigation = (direction) => {
        setCurrentQuestionIndex((i) => {
            if (direction === 'next') return Math.min(i + 1, quiz.questions.length - 1);
            if (direction === 'prev') return Math.max(0, i - 1);
            return i;
        });
    };

    if (loading) return <LoadingSpinner />;

    if (result && reviewQuiz) {
        return (
            <div className="quiz-review-page">
                <QuizResult result={result} quiz={quiz} onReset={handleReset} navigate={navigate} />
                <hr className="my-4" />
                <ReviewQuiz reviewQuiz={reviewQuiz} />
            </div>
        );
    }

    return (
        <div className="quiz-container container">
            <div className="quiz-header mt-4 mb-3">
                <button
                    type="button"
                    className="btn btn-outline-primary fs-4 fw-bold"
                    onClick={() => window.history.back()}
                >
                    <FontAwesomeIcon icon={faArrowLeft} /> Quay lại trang học bài
                </button>
            </div>

            <div className="quiz-content">
                <div className="row">
                    <div className="col-md-4">
                        <QuizNavigation
                            currentIndex={currentQuestionIndex}
                            totalQuestions={quiz.questions.length}
                            setCurrentIndex={setCurrentQuestionIndex}
                            answers={answers}
                            questions={quiz.questions}
                            onSubmit={handleSubmit}
                        />
                    </div>
                    <div className="col-md-8">
                        <QuizQuestion
                            question={quiz.questions[currentQuestionIndex]}
                            currentIndex={currentQuestionIndex}
                            totalQuestions={quiz.questions.length}
                            answers={answers}
                            onAnswerSelect={handleAnswerSelect}
                            onNavigate={handleNavigation}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Quiz;
