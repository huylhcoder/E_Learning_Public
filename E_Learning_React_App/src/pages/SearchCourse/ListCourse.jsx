import { Link } from 'react-router-dom';
import classNames from 'classnames/bind';
import { FaStar } from 'react-icons/fa';

import styles from './SearchCourse.module.scss';

const cx = classNames.bind(styles);

const formatCurrency = (price: number): string => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    minimumFractionDigits: 0,
  }).format(price);
};

function ListCourse({ loading, courses }) {
  if (loading) {
    return <h4 className="text-center">Đang tải dữ liệu...</h4>;
  }

  if (!courses || courses.length === 0) {
    return (
      <div className="text-center">
        <img
          src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTZLhV29HaDKqmUqIqveXIBCyMdcaINrOyLmA&s"
          alt="Empty"
          width="250"
        />
        <h3>Không có khóa học nào cần tìm</h3>
      </div>
    );
  }

  return (
    <div className="row">
      {courses.map((course) => (
        <div className="col-12 col-sm-6 col-lg-4 mb-4" key={course.courseId}>
          <div className={cx('courses-item', 'position-relative', 'h-100')}>
            <Link to={`/course/course-detail/${course.courseId}`} className={cx('link')}>
              <div className={cx('courses-item-inner')}>
                <div className={cx('img-box')}>
                  <img
                    src={course.avatar}
                    alt={course.name}
                    className="img-fluid w-100"
                  />
                </div>

                <span className={cx('title', 'text-truncate-cell', 'h5')}>
                  {course.name}
                </span>

                <div className={cx('rating')}>
                  <div className={cx('average-stars')}>
                    {[...Array(course.averageRating)].map((_, i) => (
                      <FaStar key={`filled-${i}`} className="text-warning" />
                    ))}
                    {[...Array(5 - course.averageRating)].map((_, i) => (
                      <FaStar key={`empty-${i}`} className="text-secondary" />
                    ))}
                  </div>
                </div>
              </div>

              <div className="badge bg-danger text-white fs-6 position-absolute top-0 end-0 m-2">
                Giá: {course.price === 0 ? 'Miễn phí' : formatCurrency(course.price)}
              </div>
            </Link>
          </div>
        </div>
      ))}
    </div>
  );
}

export default ListCourse;
