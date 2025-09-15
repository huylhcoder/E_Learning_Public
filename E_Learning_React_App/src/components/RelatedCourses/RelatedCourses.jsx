import { useRef } from 'react';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import { Link } from 'react-router-dom';
import styles from './RelatedCourses.module.scss';

function RelatedCourses({ courses }) {
    const sliderRef = useRef(null);

    const scroll = (direction) => {
        if (direction === 'left') {
            sliderRef.current.scrollBy({ left: -300, behavior: 'smooth' });
        } else {
            sliderRef.current.scrollBy({ left: 300, behavior: 'smooth' });
        }
    };

    return (
        <div className="mt-5 mb-5">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <p className="fs-3 fw-bold">Khóa học liên quan</p>
                <div>
                    <button className="btn btn-light me-2" onClick={() => scroll('left')}>
                        <FaChevronLeft />
                    </button>
                    <button className="btn btn-light" onClick={() => scroll('right')}>
                        <FaChevronRight />
                    </button>
                </div>
            </div>

            <div
                ref={sliderRef}
                className={`${styles.slider} d-flex overflow-auto`}
                style={{ scrollBehavior: 'smooth' }}
            >
                {courses.map((course) => (
                    <Link
                        key={course.courseId}
                        to={`/course/course-detail/${course.courseId}`}
                        className={`${styles.card} card me-3 text-decoration-none`}
                    >
                        <img src={course.avatar} className="card-img-top" alt={course.name} />
                        <div className="card-body">
                            <h6 className="card-title text-dark">{course.name}</h6>
                            <p className="card-text text-muted mb-1">{course.user?.name}</p>
                            <p className="fw-bold text-danger mb-0">
                                {course.price === 0 ? 'Miễn phí' : course.price.toLocaleString() + ' VND'}
                            </p>
                            <small className="text-muted">
                                ⭐ {course.averageRating?.toFixed(1) || 0} ({course.listComment?.length || 0})
                            </small>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
}

export default RelatedCourses;
