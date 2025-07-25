import classNames from 'classnames/bind';
import { Link } from 'react-router-dom';

import styles from './BannerSection.module.scss';
import bannerImage from '~/assets/images/banner/banner_img.png';

const cx = classNames.bind(styles);

const BannerSection = () => {
    return (
        <section className={cx('banner-section', 'd-flex', 'align-items-center', 'position-relative', 'mb-5')}>
            {/* Bubble animation */}
            <div className={cx('bubble-animation')}>
                {Array.from({ length: 10 }).map((_, i) => (
                    <div className={cx('bubble-animation-item')} key={i}></div>
                ))}
            </div>
            <div className="container">
                <div className="row align-items-center">
                    <div className="col-md-6">
                        <div className={cx('banner-text')}>
                            <h2 className="mb-3">
                                Mở khóa một thế giới học tập với nền tảng trực tuyến của chúng tôi, nơi giáo dục đáp ứng
                                sự đổi mới.
                            </h2>
                            <h1 className="mb-3 text-capitalize fs-2">Nền tảng trực tuyến tốt nhất</h1>
                            <p className="mb-4">
                                Một dự án LMS giúp các học sinh sinh viên học CNTT tốt hơn. Với việc theo dõi và báo cáo
                                chi tiết tiến độ học. Đáp ứng đa dạng nhu cầu của người học, tiết kiệm cơ sở vật chất.
                                Cho phép phát triển kỹ năng liên tục.{' '}
                                <Link className="text-primary" to="/register">
                                    Tìm hiểu thêm
                                </Link>
                            </p>
                            <Link className="btn btn-primary p-3 w-50 fs-4 fw-bold" to="/register">
                                Đăng ký ngay
                            </Link>
                        </div>
                    </div>
                    <div className="col-md-6 order-first order-md-last mb-5 mb-md-0">
                        <div className={cx('banner-img')}>
                            <div className={cx('circular-img')}>
                                <div className={cx('circular-img-inner')}>
                                    <div className={cx('circular-img-circle')}></div>
                                    <img src={bannerImage} alt="banner img" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
};

export default BannerSection;
