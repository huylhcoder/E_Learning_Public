// Learning.jsx

import React, { useEffect, useState, useCallback, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify'; // Giữ nguyên, thư viện này đã được import
import axios from '~/utils/CustomizeAxios';

import VideoSection from './components/VideoSection';
import CourseProgress from './components/CourseProgress';
import SectionsAccordion from './components/SectionsAccordion';
import Comments from './components/Comments';
import RelatedCourses from '~/components/RelatedCourses/RelatedCourses';

import './Learning.module.scss';

const Learning = () => {
    const [course, setCourse] = useState(null);
    const [sections, setSections] = useState([]);
    const [currentLesson, setCurrentLesson] = useState(null);
    const [videoProgress, setVideoProgress] = useState(0);
    const [progressPercentage, setProgressPercentage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [relatedCourses, setRelatedCourses] = useState([]);
    const [isProcessing, setIsProcessing] = useState(false); // 💡 Trạng thái xử lý nhận chứng chỉ

    const isProcessingRef = useRef(false);

    const navigate = useNavigate();
    const token = localStorage.getItem('token');
    const courseId = new URLSearchParams(window.location.search).get('courseId');

    // ... (các useEffect và load functions khác)

    useEffect(() => {
        const fetchData = async () => {
            if (!token) {
                alert('Bạn cần đăng nhập để tiếp tục.');
                navigate('/login');
                return;
            }

            try {
                // 1️⃣ Load thông tin khóa học
                await loadCourse();

                // 2️⃣ Load tiến độ khóa học
                await loadCourseProgress();

                // 3️⃣ Sau khi load tiến độ xong mới load bài học hiện tại
                await loadCurrentLesson();

                // 4️⃣ Cuối cùng load khóa học liên quan
                await loadRelatedCourses();

                // 5️⃣ (Tùy chọn) Load comment sau cùng nếu có
                // await loadComments();
            } catch (error) {
                console.error('Lỗi khi tải dữ liệu khóa học:', error);
            }
        };

        fetchData();
    }, [courseId]);

    const loadCourseProgress = async () => {
        try {
            const response = await axios.get(`/course-progress/progress?courseId=${courseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setProgressPercentage(response.data.progressPercentage || 0);
        } catch (error) {
            console.error('Lỗi khi tải tiến độ khóa học:', error);
        }
    };

    const loadCurrentLesson = async () => {
        try {
            const response = await axios.get(`/course-progress/current-lesson?courseId=${courseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.data) {
                // console.log('Current Lesson: ' + response.data);
                setCurrentLesson(response.data);
            } else {
                // Nếu chưa có current lesson, lấy bài học đầu tiên
                const firstSection = sections[0];
                if (firstSection && firstSection.listLesson.length > 0) {
                    // console.log('Current Lesson: ' + firstSection.listLesson[0]);
                    setCurrentLesson(firstSection.listLesson[0]);
                }
            }
        } catch (error) {
            toast.error('Lỗi khi tải bài học hiện tại:', error);
        } finally {
            setLoading(false);
        }
    };

    const loadRelatedCourses = async () => {
        const relatedRes = await axios.get(`/course/${courseId}/related`, {
            headers: { Authorization: `Bearer ${token}` },
        });
        setRelatedCourses(relatedRes.data);
    };

    const loadCourse = async () => {
        try {
            const response = await axios.get(`/course/learning/course-detail/${courseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
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

    //Cập nhật tiến độ video khi dừng video
    const updateVideoProgress = async (progress) => {
        if (!currentLesson) return;

        try {
            await axios.post(
                '/video-progress/save',
                {
                    courseId, // 👈 chỉ truyền courseId
                    lessonId: currentLesson.lessonId,
                    pathVideo: currentLesson.pathVideo,
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
        if (!currentLesson) return;

        try {
            await axios.post(
                '/lesson/lesson-complete',
                {
                    lessonId: currentLesson.lessonId,
                    courseId,
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                },
            );

            toast.success('Bài học đã được hoàn thành! 🎉');
            loadCourseProgress(); // Cập nhật thanh tiến độ

            // 💡 THAY ĐỔI: Cập nhật state sections để hiển thị dấu tick ngay lập tức
            setSections((prevSections) => {
                const updatedSections = prevSections.map((section) => ({
                    ...section,
                    listLesson: section.listLesson.map((lesson) => {
                        // Tìm bài học hiện tại và cập nhật trường 'complete'
                        if (lesson.lessonId === currentLesson.lessonId) {
                            return {
                                ...lesson,
                                complete: true, // Đánh dấu là đã hoàn thành
                            };
                        }
                        return lesson;
                    }),
                }));
                return updatedSections;
            });
        } catch (error) {
            console.error('Lỗi khi hoàn thành bài học:', error);
        }
    };

    const receiveCertificate = useCallback(() => {
        if (isProcessingRef.current) {
            // console.log('⏳ Đang xử lý, bỏ qua click này');
            return;
        }

        if (!token || !courseId) {
            toast.error('Token hoặc ID khóa học không hợp lệ!');
            return;
        }

        isProcessingRef.current = true;
        setIsProcessing(true);

        // Tạo promise async cho toast.promise
        const downloadPromise = (async () => {
            try {
                const res = await axios.post(`/user/certificate/${courseId}`, null, {
                    headers: { Authorization: token.startsWith('Bearer') ? token : `Bearer ${token}` },
                    responseType: 'blob',
                });

                const blob = res.data;
                if (!(blob instanceof Blob)) {
                    throw new Error('Phản hồi không hợp lệ (không phải file chứng chỉ)');
                }

                // 💾 Tải file xuống
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `certificate_${courseId}_${Date.now()}.png`;
                a.click();
                window.URL.revokeObjectURL(url);

                return 'Chứng chỉ đã được tải thành công! 🥳';
            } catch (error) {
                console.error('❌ Lỗi khi nhận chứng chỉ:', error);

                if (error.response?.data instanceof Blob) {
                    const text = await error.response.data.text();
                    try {
                        const json = JSON.parse(text);
                        throw new Error(json.message || json.error || 'Lỗi từ server');
                    } catch {
                        throw new Error('Không thể đọc phản hồi lỗi từ server.');
                    }
                }

                throw new Error(error.response?.data?.message || 'Không thể tạo chứng chỉ. Vui lòng thử lại sau.');
            } finally {
                isProcessingRef.current = false;
                setIsProcessing(false);
            }
        })();

        // Hiển thị loading toast đẹp
        toast.promise(downloadPromise, {
            pending: 'Đang xử lý nhận chứng chỉ...',
            success: {
                render({ data }) {
                    return data; // message từ return
                },
            },
            error: {
                render({ data }) {
                    return data?.message || 'Có lỗi xảy ra khi tải chứng chỉ.';
                },
            },
        });
    }, [token, courseId]);

    const handleLessonClick = (lesson) => {
        setCurrentLesson(lesson);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleTestClick = (test) => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
        navigate(`/quiz?testId=${test.testId}`);
    };

    return (
        <div className="container mt-4">
            <div className="row">
                {/* Video */}
                <div className="col-md-8">
                    <VideoSection
                        currentLesson={currentLesson}
                        loading={loading}
                        onProgress={updateVideoProgress}
                        onComplete={completeLesson}
                    />
                </div>

                {/* Tiến độ + Sections */}
                <div className="col-md-4">
                    <CourseProgress
                        progressPercentage={progressPercentage}
                        onReceiveCertificate={receiveCertificate}
                        // 💡 SỬA LỖI: Truyền trạng thái đang xử lý xuống component con
                        isProcessing={isProcessing}
                    />
                    <SectionsAccordion
                        sections={sections}
                        currentLesson={currentLesson}
                        onLessonClick={handleLessonClick}
                        onTestClick={handleTestClick}
                    />
                </div>
            </div>

            {/* Comments */}
            <Comments courseId={courseId} token={token} />

            {/* Related Courses */}
            {relatedCourses.length > 0 && <RelatedCourses courses={relatedCourses} />}
        </div>
    );
};

export default Learning;
