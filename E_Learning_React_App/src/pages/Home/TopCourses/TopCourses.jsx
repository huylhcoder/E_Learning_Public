import classNames from 'classnames/bind';
import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { FaStar } from 'react-icons/fa';

import styles from './TopCourse.module.scss';
import axios from '~/utils/CustomizeAxios';

const cx = classNames.bind(styles);

const BannerSection = () => {
    const [topCourses, setTopCourses] = useState([]);

    useEffect(() => {
        const fetchTopRatedCourses = async () => {
            try {
                const response = await axios.get('/course/top-rated');
                if (response && response.status === 200) {
                    const formatted = response.data.map((course) => ({
                        courseId: course.courseId,
                        avatar: course.avatar,
                        name: course.name,
                        averageRating: Math.round(course.averageRating || 0),
                        formattedPrice: `${Number(course.price).toLocaleString('vi-VN')}đ`,
                    }));
                    setTopCourses(formatted);
                }
            } catch (err) {
                console.error('Lỗi khi lấy top-rated courses:', err);
            }
        };

        fetchTopRatedCourses();
    }, []);

    return (
        <section className={cx('courses-section', 'section-padding')}>
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-md-8">
                        <div className={cx('section-title', 'text-center', 'text-primary')}>
                            <p className="fs-3 fw-bold">Khóa học được đánh giá tốt nhất</p>
                        </div>
                    </div>
                </div>

                <div className="row">
                    {topCourses.map((kh) => (
                        <div className="col-md-6 col-lg-3 mb-4 mt-4" key={kh.courseId}>
                            <div className={cx('courses-item', 'position-relative')}>
                                <Link to={`/course/course-detail/${kh.courseId}`} className={cx('link')}>
                                    <div className={cx('courses-item-inner')}>
                                        <div className={cx('img-box')}>
                                            <img src={kh.avatar} alt="courses img" className="img-fluid" />
                                        </div>
                                        <span className={cx('title', 'text-truncate-cell', 'h3')}>{kh.name}</span>
                                        <div className={cx('rating')}>
                                            <div className={cx('average-stars')}>
                                                {[...Array(kh.averageRating)].map((_, i) => (
                                                    <FaStar key={i} className="text-warning" />
                                                ))}
                                                {[...Array(5 - kh.averageRating)].map((_, i) => (
                                                    <FaStar key={i} className="text-secondary" />
                                                ))}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="badge bg-danger text-white fs-6 position-absolute top-0 end-0 m-2">
                                        Giá: {kh.formattedPrice}
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

export default BannerSection;
