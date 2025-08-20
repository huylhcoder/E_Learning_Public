import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faClock, faQuestionCircle, faStar } from '@fortawesome/free-solid-svg-icons';

import axios from '~/utils/CustomizeAxios';
import SecureVideo from './components/SecureVideo/SecureVideo';
import './Learning.module.scss';
import { formatDuration } from '~/utils/format';
import images from '~/assets/images';

const Learning = () => {
    const [course, setCourse] = useState(null);
    const [sections, setSections] = useState([]);
    const [currentLesson, setCurrentLesson] = useState(null);
    const [videoProgress, setVideoProgress] = useState(0);
    const [progressPercentage, setProgressPercentage] = useState(0);
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');
    const [rating, setRating] = useState(0);
    const [loading, setLoading] = useState(true);

    const navigate = useNavigate();
    const token = localStorage.getItem('token');
    const courseId = new URLSearchParams(window.location.search).get('courseId');

    useEffect(() => {
        if (!token) {
            alert('Bạn cần đăng nhập để tiếp tục.');
            navigate('/login');
            return;
        }

        loadCourse();
        loadCourseProgress();
        loadCurrentLesson();
        loadComments();
    }, [courseId]);

    const loadCourse = async () => {
        try {
            const response = await axios.get(`/course/learning/course-detail/${courseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            console.log('Course:', response.data);

            setCourse(response.data);

            // Tổ chức lại dữ liệu sections
            if (response.data.listSection) {
                const formattedSections = response.data.listSection.map((section, sIndex) => ({
                    ...section,
                    sectionNumber: sIndex + 1,
                    listLesson:
                        section.listLesson?.map((lesson, lIndex) => ({
                            ...lesson,
                            lessonNumber: lIndex + 1,
                        })) || [],
                    listTest: section.listTest || [],
                }));
                setSections(formattedSections);
            }
        } catch (error) {
            toast.error('Lỗi khi tải khóa học:', error);
        }
    };

    const loadCurrentLesson = async () => {
        try {
            const response = await axios.get(`/course-progress/current-lesson?courseId=${courseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.data) {
                console.log('Current Lesson: ' + response.data);
                setCurrentLesson(response.data);
            } else {
                // Nếu chưa có current lesson, lấy bài học đầu tiên
                const firstSection = sections[0];
                if (firstSection && firstSection.listLesson.length > 0) {
                    console.log('Current Lesson: ' + firstSection.listLesson[0]);
                    setCurrentLesson(firstSection.listLesson[0]);
                }
            }
        } catch (error) {
            toast.error('Lỗi khi tải bài học hiện tại:', error);
        } finally {
            setLoading(false);
        }
    };

    const updateVideoProgress = async (progress) => {
        try {
            await axios.put(
                '/video-progress',
                {
                    lessonId: currentLesson.lessonId,
                    videoProgress: progress,
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                },
            );
            setVideoProgress(progress);
        } catch (error) {
            console.error('Lỗi khi cập nhật tiến độ video:', error);
        }
    };

    const completeLesson = async () => {
        try {
            await axios.post(
                '/lesson-complete',
                {
                    lessonId: currentLesson.lessonId,
                    courseId,
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                },
            );
            alert('Bài học đã được hoàn thành!');
            loadCourse(); // Reload course progress
        } catch (error) {
            console.error('Lỗi khi hoàn thành bài học:', error);
        }
    };

    const submitComment = async () => {
        if (!rating || !newComment) {
            alert('Vui lòng nhập đánh giá và nhận xét.');
            return;
        }

        try {
            await axios.post(
                `/comment`,
                {
                    courseId,
                    content: newComment,
                    starRating: rating,
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                },
            );
            alert('Đánh giá của bạn đã được gửi.');
            setNewComment('');
            setRating(0);
            loadComments();
        } catch (error) {
            console.error('Lỗi khi gửi đánh giá:', error);
        }
    };

    const loadComments = async () => {
        try {
            const response = await axios.get(`/comment/course/${courseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            console.log('Comments :' + response.data);
            setComments(response.data);
        } catch (error) {
            console.error('Lỗi khi tải đánh giá:', error);
        }
    };

    const handleVideoProgress = (progress) => {
        setVideoProgress(progress);
        // Lưu tiến độ khi video đạt mốc nhất định (ví dụ: mỗi 10%)
        if (progress % 10 < 1) {
            updateVideoProgress(progress);
        }
    };

    const handleLessonClick = (lesson) => {
        console.log('Lesson onclick:', lesson);
        setCurrentLesson(lesson);
        // Cuộn lên đầu trang khi chuyển bài học
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const loadCourseProgress = async () => {
        try {
            const response = await axios.get(`/course-progress/progress?courseId=${courseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            console.log('Course progress response:', response.data);
            setProgressPercentage(response.data.progressPercentage || 0);
        } catch (error) {
            console.error('Lỗi khi tải tiến độ khóa học:', error);
        }
    };

    const handleTestClick = (test) => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
        navigate(`/quiz?testId=${test.testId}`);
    };

    return (
        <div className="container mt-4">
            <div className="row">
                {/* Video Section */}
                <div className="col-md-8">
                    {loading ? (
                        <div className="text-center">
                            <div className="spinner-border text-primary" role="status">
                                <span className="visually-hidden">Đang tải...</span>
                            </div>
                        </div>
                    ) : currentLesson ? (
                        <>
                            <div className="video-container">
                                {/* <SecureVideo
                                    lessonId={currentLesson.lessonId}
                                    onProgress={handleVideoProgress}
                                    onComplete={completeLesson}
                                    className="mb-4"
                                /> */}
                            </div>
                            <div className="mt-3">
                                <span className="h3 text-primary fw-bold mt-3">Bài học: {currentLesson.name}</span>
                                <p className="text-muted mt-3">Mô tả ngắn: {currentLesson.description}</p>
                            </div>
                        </>
                    ) : (
                        <div className="alert alert-info">Khóa học này chưa có bài học nào.</div>
                    )}
                </div>

                {/* Course Progress and Sections */}
                <div className="col-md-4">
                    {/* Progress Card */}
                    <div className="card mb-3">
                        <div className="card-body">
                            <span className="h3 text-primary">
                                Tiến độ khóa học: {progressPercentage.toFixed(2)}% complete
                            </span>
                            <div className="progress">
                                <div
                                    className="progress-bar bg-primary"
                                    role="progressbar"
                                    style={{ width: `${progressPercentage}%` }}
                                    aria-valuenow={progressPercentage}
                                    aria-valuemin="0"
                                    aria-valuemax="100"
                                ></div>
                            </div>
                        </div>
                    </div>

                    {/* Sections Accordion */}
                    <div className="accordion" id="sectionsAccordion">
                        {sections.map((section, index) => (
                            <div className="accordion-item" key={section.sectionId}>
                                <h2 className="accordion-header">
                                    <button
                                        className="accordion-button collapsed fw-bold fs-4 mt-3"
                                        type="button"
                                        data-bs-toggle="collapse"
                                        data-bs-target={`#section${section.sectionId}`}
                                    >
                                        Phần {section.sectionNumber}: {section.name}
                                    </button>
                                </h2>
                                <div
                                    id={`section${section.sectionId}`}
                                    className="accordion-collapse collapse"
                                    data-bs-parent="#sectionsAccordion"
                                >
                                    <div className="accordion-body p-0">
                                        <ul className="list-group list-group-flush">
                                            {section.listLesson.map((lesson) => (
                                                <li
                                                    key={lesson.lessonId}
                                                    className={`list-group-item p-2 d-flex flex-column ${
                                                        currentLesson?.lessonId === lesson.lessonId ? 'active' : ''
                                                    }`}
                                                    onClick={() => handleLessonClick(lesson)}
                                                    style={{ cursor: 'pointer' }}
                                                >
                                                    <div className="d-flex align-items-center">
                                                        <i className="fas fa-play-circle me-2 text-primary"></i>
                                                        <span className="fw-bold">
                                                            Bài {lesson.lessonNumber}: {lesson.name}
                                                        </span>
                                                    </div>
                                                    <div className="d-flex align-items-center ms-4 small">
                                                        <FontAwesomeIcon icon={faClock} className="me-2" />
                                                        <span>{formatDuration(lesson.lessonDuration)}</span>
                                                    </div>
                                                </li>
                                            ))}
                                            {section.listTest.map((test) => (
                                                <li
                                                    key={test.testId}
                                                    className="list-group-item mt-2"
                                                    onClick={() => handleTestClick(test)}
                                                    style={{ cursor: 'pointer' }}
                                                >
                                                    <FontAwesomeIcon icon={faQuestionCircle} className="ms-2 pe-2" />
                                                    {test.title}
                                                </li>
                                            ))}
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Comments Section */}
            <div className="mt-4">
                <p className="text-primary fs-4 fw-bold mb-3">Đánh giá khóa học</p>

                {/* Chọn sao */}
                <div className="mb-3">
                    {[1, 2, 3, 4, 5].map((star) => (
                        <FontAwesomeIcon
                            key={star}
                            icon={faStar}
                            className={`me-1 fs-4 ${rating >= star ? 'text-warning' : 'text-secondary'}`}
                            onClick={() => setRating(star)}
                            style={{ cursor: 'pointer' }}
                        />
                    ))}
                </div>

                {/* Nhập nhận xét */}
                <div className="mb-3">
                    <textarea
                        className="form-control rounded-3 p-3 fs-4"
                        rows="3"
                        placeholder="Chia sẻ cảm nhận của bạn về khóa học..."
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                    ></textarea>
                </div>

                <button className="btn btn-primary px-3 fs-4 mb-3" onClick={submitComment}>
                    Gửi đánh giá
                </button>

                {/* Danh sách bình luận */}
                <div className="mt-4">
                    {comments.map((comment) => (
                        <div key={comment.id} className="border rounded-3 p-3 mb-3 bg-light">
                            <div className="d-flex align-items-start">
                                {/* Avatar */}
                                <img
                                    src={comment.user.avatar || images.avatarDefault}
                                    alt={comment.user.name}
                                    className="rounded-circle me-3"
                                    width="40"
                                    height="40"
                                />

                                {/* Nội dung */}
                                <div className="flex-grow-1">
                                    <span className="fs-5 fw-bold mt-1">{comment.user.name}</span>
                                    <p className="text-muted mb-0">{comment.createAt}</p>
                                    <div className="mt-1">
                                        {[...Array(comment.starRating)].map((_, i) => (
                                            <FontAwesomeIcon key={i} icon={faStar} className="text-warning fs-5" />
                                        ))}
                                    </div>
                                    <p className="text-muted mt-1 mb-0">{comment.content}</p>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default Learning;
