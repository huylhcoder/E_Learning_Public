import { Link } from 'react-router-dom';
import styles from './Footer.module.scss';
import classNames from 'classnames/bind';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFacebook, faTwitter, faInstagram, faYoutube } from '@fortawesome/free-brands-svg-icons';

const cx = classNames.bind(styles);

function Footer() {
    return (
        <footer>
            <div className={cx('footer')}>
                <div className={cx('footer-top')}>
                    <div className="container">
                        <div className="row">
                            <div className="col-sm-6 col-lg-3">
                                <div className={cx('footer-item')}>
                                    <h3
                                        className={cx(
                                            'footer-logo',
                                            'fs-4',
                                            'text-uppercase',
                                            'text-primary',
                                            'rounded',
                                        )}
                                    >
                                        E-Learning
                                    </h3>
                                    <ul>
                                        <li>
                                            <Link to="/about">Về chúng tôi</Link>
                                        </li>
                                        <li>
                                            <Link to="/feedback">Đóng góp ý kiến</Link>
                                        </li>
                                        <li>
                                            <Link to="/faq">Hỏi đáp</Link>
                                        </li>
                                        <li>
                                            <Link to="/contact">Liên hệ</Link>
                                        </li>
                                        <li>
                                            <Link to="/blog">Blog</Link>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                            <div className="col-sm-6 col-lg-3">
                                <div className={cx('footer-item')}>
                                    <h3 className="text-light">Learning</h3>
                                    <ul>
                                        <li>
                                            <Link to="/courses">Các khóa học</Link>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                            <div className="col-sm-6 col-lg-3">
                                <div className={cx('footer-item')}>
                                    <h3 className="text-light">More</h3>
                                    <ul>
                                        <li>
                                            <span>Chi nhánh</span>
                                        </li>
                                        <li>
                                            <span>Hotline: 0389955132</span>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                            <div className="col-sm-6 col-lg-3">
                                <div className={cx('footer-item')}>
                                    <h3 className="text-light">Contact</h3>
                                    <ul>
                                        <li>
                                            <a
                                                href="https://www.facebook.com/huylhcoder"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                            >
                                                <FontAwesomeIcon icon={faFacebook} className={cx('social-icon')} />{' '}
                                                Facebook
                                            </a>
                                        </li>
                                        <li>
                                            <a href="https://twitter.com/" target="_blank" rel="noopener noreferrer">
                                                <FontAwesomeIcon icon={faTwitter} className={cx('social-icon')} />{' '}
                                                Twitter
                                            </a>
                                        </li>
                                        <li>
                                            <a
                                                href="https://www.instagram.com/?hl=en"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                            >
                                                <FontAwesomeIcon icon={faInstagram} className={cx('social-icon')} />{' '}
                                                Instagram
                                            </a>
                                        </li>
                                        <li>
                                            <a
                                                href="https://www.youtube.com/"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                            >
                                                <FontAwesomeIcon icon={faYoutube} className={cx('social-icon')} />{' '}
                                                YouTube
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className={cx('footer-bottom', 'pb-5')}>
                    <div className="container">
                        <p className="m-0 py-4 text-center">
                            copyright &copy;2025 <Link to="/" className='text-primary'>Revolutionary</Link>
                        </p>
                    </div>
                </div>
            </div>
        </footer>
    );
}

export default Footer;
