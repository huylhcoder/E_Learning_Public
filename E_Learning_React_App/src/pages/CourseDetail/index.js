import { useEffect, useState, useContext } from 'react';
import { useParams } from 'react-router-dom';

import AuthContext from '~/context/AuthContext';
import axios from '~/utils/CustomizeAxios';
import CourseHeader from './CourseHeader';
import CourseSidebar from './CourseSidebar';
import CourseOverview from './CourseOverview';
import CourseCurriculum from './CourseCurriculum';

import './CourseDetail.module.scss';

function CourseDetail() {
    const { courseId } = useParams();
    const { authenticated } = useContext(AuthContext);
    const [course, setCourse] = useState(null);
    const [isInCart, setIsInCart] = useState(false); // ← tạo state từ props

    useEffect(() => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
        const fetchDetail = async () => {
            try {
                const token = localStorage.getItem('token');
                const res = await axios.get(`/course/course-detail/${courseId}`, {
                    headers: token ? { Authorization: `Bearer ${token}` } : {},
                });
                console.log(res.data);
                setIsInCart(res.data.inCart); // ← cập nhật state từ response
                setCourse(res.data);
            } catch (error) {
                console.error('Error loading course detail', error);
            }
        };

        fetchDetail();
    }, [courseId]);

    if (!course) return <div>Loading...</div>;

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
        inCart,
        categories,
        sections,
    } = course;

    return (
        <div>
            <CourseHeader title={name} rating={averageRating} comments={totalComments} registered={totalRegistered} />

            <div className="container my-4 row">
                <div className="col-md-8">
                    {/* Tách riêng phần overview */}
                    <CourseOverview
                        levelName={levelName}
                        categories={categories}
                        contentDescription={contentDescription}
                        description={description}
                    />

                    {/* Tách riêng phần curriculum */}
                    <CourseCurriculum sections={sections} />
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
            </div>
        </div>
    );
}

export default CourseDetail;
