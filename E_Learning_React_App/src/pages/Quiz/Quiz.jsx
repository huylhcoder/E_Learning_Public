//Thư viện
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';

//Component
import axios from '~/utils/CustomizeAxios';
import QuizResult from './QuizResult';
import QuizQuestion from './QuizQuestion';
import QuizNavigation from './QuizNavigation';
import ReviewQuiz from './ReviewQuiz';
import './Quiz.module.scss';
import LoadingSpinner from '~/components/LoadingSpinner';

const Quiz = () => {
    const [quiz, setQuiz] = useState(null);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [answers, setAnswers] = useState({});
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(true);
    const [reviewQuiz, setReviewQuiz] = useState(null); // thêm state để quản lý bài làm kèm đáp án

    const token = localStorage.getItem('token');
    const testId = new URLSearchParams(window.location.search).get('testId');
    const navigate = useNavigate();

    useEffect(() => {
        if (!token) {
            navigate('/login');
            return;
        }
        loadQuiz();
    }, [token, testId]);

    const loadQuiz = async () => {
        try {
            //Lấy kết quả kiểm tra nếu chưa có sẽ tạo kết quả mới và set score = 0
            const resultResponse = await axios.get(`/test/${testId}/result`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            //score khác 0 ==> Hiển thị kết quả làm bài của người dùng
            if (resultResponse.data.score !== 0) {
                console.log('(resultResponse.data.score !== 0)');

                //Hiển thị điểm
                setResult(resultResponse.data);
                //Hiển thị danh sách đáp án cho người dùng tham khảo
                const answersResponse = await axios.get(`/test/${testId}/answers`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setReviewQuiz(answersResponse.data); // để show lại khi học
            }
            //score = 0 ==> Hiển thị bài kiểm  tra cho người dùng làm
            if (resultResponse.data.score === 0 || resultResponse.data.score == null) {
                console.log('(resultResponse.data.score === 0 || resultResponse.data.score == null)');
                //Lấy danh sách câu hỏi
                const questionsResponse = await axios.get(`/test/${testId}/questions`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setQuiz(questionsResponse.data);

                //Lấy danh sách đáp án mà người dùng đã chọn
                const userAnswers = {};
                questionsResponse.data.questions.forEach((question) => {
                    if (question.selectedAnswerId) {
                        userAnswers[question.questionId] = question.selectedAnswerId;
                    }
                });
                setAnswers(userAnswers);
            }
        } catch (error) {
            console.error('Failed to load test:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleAnswerSelect = async (questionId, answerId) => {
        try {
            await axios.post(
                `/test/${testId}/answer`,
                { questionId, answerId },
                { headers: { Authorization: `Bearer ${token}` } },
            );
            setAnswers((prev) => ({ ...prev, [questionId]: answerId }));
        } catch (error) {
            console.error('Failed to save answer:', error);
        }
    };

    const handleSubmit = async () => {
        if (!confirmSubmission()) return;

        try {
            const response = await axios.post(
                `/test/${testId}/submit`,
                {},
                { headers: { Authorization: `Bearer ${token}` } },
            );
            setResult(response.data);

            // gọi thêm API để lấy dữ liệu review
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

            // Lấy lại câu hỏi sau khi reset
            const questionsResponse = await axios.get(`/test/${testId}/questions`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setQuiz(questionsResponse.data || null);

            const userAnswers = {};
            (questionsResponse.data?.questions || []).forEach((question) => {
                if (question.selectedAnswerId != null) {
                    userAnswers[question.questionId] = question.selectedAnswerId;
                }
            });
            setAnswers(userAnswers);

            // Khôi phục trạng thái UI
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
        if (direction === 'next') {
            setCurrentQuestionIndex(i => Math.min(i + 1, quiz.questions.length - 1));
        } else if (direction === 'prev') {
            setCurrentQuestionIndex(i => Math.max(0, i - 1));
        }
    };

    if (loading) {
        return <LoadingSpinner />;
    }

    if (result && reviewQuiz) {
        console.log('Result:', result);
        console.log('ReviewQuiz data:', reviewQuiz);

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
