import { useEffect, useState, useContext } from 'react';
import { useParams } from 'react-router-dom';

import AuthContext from '~/context/AuthContext';
import axios from '~/utils/CustomizeAxios';
import CourseHeader from './CourseHeader';
import CourseSidebar from './CourseSidebar';
import CourseOverview from './CourseOverview';
import CourseCurriculum from './CourseCurriculum';
import LoadingSpinner from '~/components/LoadingSpinner';
import Comments from './Comments';
import RelatedCourses from './RelatedCourses'; // 👈 import mới
import images from '~/assets/images';

import './CourseDetail.module.scss';

function CourseDetail() {
    const { courseId } = useParams();
    const { authenticated } = useContext(AuthContext);
    const [course, setCourse] = useState(null);
    const [comments, setComments] = useState([]);
    const [relatedCourses, setRelatedCourses] = useState([]); // 👈 thêm state
    const [isInCart, setIsInCart] = useState(false);

    const token = localStorage.getItem('token');

    useEffect(() => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
        const fetchDetail = async () => {
            try {
                const courseRes = await axios.get(`/course/course-detail/${courseId}`, {
                    headers: token ? { Authorization: `Bearer ${token}` } : {},
                });
                const commentsRes = await axios.get(`/comment/course/${courseId}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                const relatedRes = await axios.get(`/course/${courseId}/related`, {
                    headers: { Authorization: `Bearer ${token}` },
                });

                setIsInCart(courseRes.data.inCart);
                setCourse(courseRes.data);
                setComments(commentsRes.data);
                setRelatedCourses(relatedRes.data); // 👈 set data
            } catch (error) {
                console.error('Error loading course detail', error);
            }
        };

        fetchDetail();
    }, [courseId]);

    if (!course) return <LoadingSpinner />;

    const {
        name,
        avatar,
        averageRating,
        description,
        contentDescription,
        price,
        levelName,
        totalComments,
        totalRegistered,
        paymentStatus,
        categories,
        sections,
    } = course;

    return (
        <div>
            <CourseHeader title={name} rating={averageRating} comments={totalComments} registered={totalRegistered} />

            <div className="container my-4 row">
                <div className="col-md-8">
                    <CourseOverview
                        levelName={levelName}
                        categories={categories}
                        contentDescription={contentDescription}
                        description={description}
                    />

                    <CourseCurriculum sections={sections} />
                    <Comments images={images} comments={comments} />
                </div>

                <div className="col-md-4">
                    <CourseSidebar
                        price={price}
                        setIsInCart={setIsInCart}
                        isInCart={isInCart}
                        paymentStatus={paymentStatus}
                        courseId={courseId}
                        avatar={avatar}
                        authenticated={authenticated}
                    />
                </div>
                <div className="col-12">
                    {/* 👇 Thêm Related Courses */}
                    {relatedCourses.length > 0 && <RelatedCourses courses={relatedCourses} />}
                </div>
            </div>
        </div>
    );
}

export default CourseDetail;
