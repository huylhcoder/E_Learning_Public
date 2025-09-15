import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import axios from '~/utils/CustomizeAxios';

import VideoSection from './components/VideoSection';
import CourseProgress from './components/CourseProgress';
import SectionsAccordion from './components/SectionsAccordion';
import Comments from './components/Comments';
import RelatedCourses from '~/components/RelatedCourses/RelatedCourses'; // 👈 import mới

import './Learning.module.scss';

const Learning = () => {
    const [course, setCourse] = useState(null);
    const [sections, setSections] = useState([]);
    const [currentLesson, setCurrentLesson] = useState(null);
    const [videoProgress, setVideoProgress] = useState(0);
    const [progressPercentage, setProgressPercentage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [relatedCourses, setRelatedCourses] = useState([]); // 👈 thêm state

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
        loadRelatedCourses();
        // loadComments();
    }, [courseId]);

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

    // const updateVideoProgress = async (progress) => {
    //     try {
    //         await axios.put(
    //             '/video-progress',
    //             {
    //                 lessonId: currentLesson.lessonId,
    //                 videoProgress: progress,
    //             },
    //             {
    //                 headers: { Authorization: `Bearer ${token}` },
    //             },
    //         );
    //         setVideoProgress(progress);
    //     } catch (error) {
    //         console.error('Lỗi khi cập nhật tiến độ video:', error);
    //     }
    // };

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
            alert('Bài học đã được hoàn thành!');
            loadCourse(); // Reload course progress
        } catch (error) {
            console.error('Lỗi khi hoàn thành bài học:', error);
        }
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
                    <CourseProgress progressPercentage={progressPercentage} />
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
