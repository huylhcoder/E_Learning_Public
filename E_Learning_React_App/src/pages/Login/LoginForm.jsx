import { Link } from 'react-router-dom';
import classNames from 'classnames/bind';
import { FaFacebookF, FaGithub } from 'react-icons/fa';
import GoogleLoginButton from './GoogleLoginButton';

import styles from './Login.module.scss';
const cx = classNames.bind(styles);

export const LoginForm = ({
    email,
    setEmail,
    password,
    setPassword,
    handleLogin,
    handleFacebookLogin,
    handleGithubLogin,
}) => {
    return (
        <div className={cx('login-container')}>
            <div className={cx('login-card')}>
                <h2 className="text-center fw-bold mb-2">Đăng nhập</h2>
                <p className="text-center mb-4">
                    Bạn chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link>
                </p>

                <form onSubmit={handleLogin}>
                    {/* Email */}
                    <div className={cx('input-outlined')}>
                        <input
                            type="email"
                            id="email"
                            placeholder=" "
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                        <label htmlFor="email">Email</label>
                    </div>

                    {/* Password */}
                    <div className={cx('input-outlined')}>
                        <input
                            type="password"
                            id="password"
                            placeholder=" "
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        <label htmlFor="password">Mật khẩu</label>
                    </div>

                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <div className="form-check">
                            <input className="form-check-input" type="checkbox" id="rememberMe" />
                            <label className="form-check-label" htmlFor="rememberMe">
                                Ghi nhớ đăng nhập
                            </label>
                        </div>
                        <Link to="/forgot-password" className="text-decoration-none">
                            Quên mật khẩu?
                        </Link>
                    </div>

                    <div className="d-grid mb-3">
                        <button type="submit" className="btn btn-dark btn-lg">
                            Đăng nhập
                        </button>
                    </div>
                </form>

                <div className={cx('divider')}>
                    <span>Hoặc đăng nhập bằng</span>
                </div>

                <div className="d-flex flex-column gap-3">
                    <GoogleLoginButton />
                    {/* <button className={cx('social-btn', 'google')} onClick={handleGoogleLogin}>
                        <FaGoogle className={cx('icon')} />
                        Đăng nhập bằng Google
                    </button> */}

                    {/* <button className={cx('social-btn', 'facebook')} onClick={handleFacebookLogin}>
                        <FaFacebookF className={cx('icon')} />
                        Đăng nhập bằng Facebook
                    </button>
                    <button className={cx('social-btn', 'github')} onClick={handleGithubLogin}>
                        <FaGithub className={cx('icon')} />
                        Đăng nhập bằng GitHub
                    </button> */}
                </div>
            </div>
        </div>
    );
};
