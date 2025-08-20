import classNames from 'classnames/bind';
import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { FaStar } from 'react-icons/fa';
import styles from './MostFollowedCourses.module.scss';
import axios from '~/utils/CustomizeAxios';

const cx = classNames.bind(styles);

const MostFollowedCourses = () => {
    const [courses, setCourses] = useState([]);

    useEffect(() => {
        const fetchCourses = async () => {
            try {
                const response = await axios.get('/course/top-registered-course');
                if (response && response.status === 200) {
                    const formattedCourses = response.data.map((course) => ({
                        courseId: course.courseId,
                        avatar: course.avatar,
                        name: course.name,
                        averageRating: Math.round(course.averageRating || 0),
                        formattedPrice: `${Number(course.price).toLocaleString('vi-VN')}đ`,
                    }));
                    setCourses(formattedCourses);
                }
            } catch (error) {
                console.error('Lỗi khi lấy danh sách khóa học:', error);
            }
        };

        fetchCourses();
    }, []);

    return (
        <section className={cx('courses-section', 'section-padding')}>
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-md-8">
                        <div className={cx('section-title', 'text-center')}>
                            <p className="fs-3 text-primary fw-bold">Khóa học được đăng ký nhiều nhất</p>
                        </div>
                    </div>
                </div>
                <div className="row">
                    {courses.map((fl) => (
                        <div className="col-md-6 col-lg-3 mb-4" key={fl.courseId}>
                            <div className={cx('courses-item', 'position-relative')}>
                                <Link to={`/course/course-detail/${fl.courseId}`} className={cx('link')}>
                                    <div className={cx('courses-item-inner')}>
                                        <div className={cx('img-box')}>
                                            <img src={fl.avatar} alt="courses img" className="img-fluid" />
                                        </div>
                                        <span className={cx('title', 'text-truncate-cell', 'h3')}>{fl.name}</span>
                                        <div className={cx('rating')}>
                                            <div className={cx('average-stars')}>
                                                {[...Array(fl.averageRating)].map((_, i) => (
                                                    <FaStar key={i} className="text-warning" />
                                                ))}
                                                {[...Array(5 - fl.averageRating)].map((_, i) => (
                                                    <FaStar key={i} className="text-secondary" />
                                                ))}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="badge bg-danger text-white fs-6 position-absolute top-0 end-0 m-2">
                                        Giá: {fl.formattedPrice}
                                    </div>
                                </Link>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
};

export default MostFollowedCourses;
