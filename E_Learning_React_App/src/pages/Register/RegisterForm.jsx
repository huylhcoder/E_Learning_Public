import { Link } from 'react-router-dom';
import classNames from 'classnames/bind';
import { FaEye, FaEyeSlash } from 'react-icons/fa';
import { useState } from 'react';

import styles from './Register.module.scss'; // 🔹 dùng chung style Login
const cx = classNames.bind(styles);

export default function RegisterForm({ form, handleChange, handleCheckUser, loading }) {
    const [passwordVisible, setPasswordVisible] = useState(false);

    const togglePasswordVisibility = () => {
        setPasswordVisible(!passwordVisible);
    };

    return (
        <div className={cx('login-container')}>
            <div className={cx('login-card')}>
                <h2 className="text-center fw-bold mb-2">Đăng ký</h2>
                <p className="text-center mb-4">
                    Đã có tài khoản? <Link to="/login">Đăng nhập ngay</Link>
                </p>

                <form onSubmit={handleCheckUser}>
                    {/* Fullname */}
                    <div className={cx('input-outlined')}>
                        <input
                            type="text"
                            id="fullname"
                            name="fullname"
                            placeholder=" "
                            value={form.fullname}
                            onChange={handleChange}
                            required
                        />
                        <label htmlFor="fullname">Họ và tên</label>
                    </div>

                    {/* Email */}
                    <div className={cx('input-outlined')}>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            placeholder=" "
                            value={form.email}
                            onChange={handleChange}
                            required
                        />
                        <label htmlFor="email">Email</label>
                    </div>

                    {/* Password */}
                    <div className={cx('input-outlined')}>
                        <input
                            type={passwordVisible ? 'text' : 'password'}
                            id="password"
                            name="password"
                            placeholder=" "
                            value={form.password}
                            onChange={handleChange}
                            required
                        />
                        <label htmlFor="password">Mật khẩu</label>
                        <span
                            onClick={togglePasswordVisibility}
                            style={{ position: 'absolute', right: '12px', top: '50%', transform: 'translateY(-50%)', cursor: 'pointer' }}
                        >
                            {passwordVisible ? <FaEyeSlash /> : <FaEye />}
                        </span>
                    </div>

                    {/* Retype Password */}
                    <div className={cx('input-outlined')}>
                        <input
                            type={passwordVisible ? 'text' : 'password'}
                            id="retypePassword"
                            name="retypePassword"
                            placeholder=" "
                            value={form.retypePassword}
                            onChange={handleChange}
                            required
                        />
                        <label htmlFor="retypePassword">Xác nhận mật khẩu</label>
                    </div>

                    <div className="d-grid mb-3">
                        <button type="submit" className="btn btn-dark btn-lg" disabled={loading}>
                            {loading ? 'Đang xử lý...' : 'Đăng ký ngay'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
