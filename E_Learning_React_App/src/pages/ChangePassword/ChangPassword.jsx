import { useState } from 'react';
import { motion } from 'framer-motion';
import { toast, ToastContainer } from 'react-toastify';
import classNames from 'classnames/bind';

import axios from '~/utils/CustomizeAxios';
import styles from './ChangePassword.module.scss'; // 🔹 dùng lại style của Login
const cx = classNames.bind(styles);

const ChangePassword = () => {
    const [form, setForm] = useState({
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
    });
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
        setErrors({ ...errors, [e.target.name]: '' }); // clear lỗi khi user nhập lại
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        let newErrors = {};

        if (!form.oldPassword) newErrors.oldPassword = 'Vui lòng nhập mật khẩu cũ';
        if (!form.newPassword) newErrors.newPassword = 'Vui lòng nhập mật khẩu mới';
        if (form.newPassword && form.newPassword.length < 6)
            newErrors.newPassword = 'Mật khẩu mới phải ít nhất 6 ký tự';
        if (form.newPassword !== form.confirmPassword) newErrors.confirmPassword = 'Mật khẩu xác nhận không khớp!';

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        try {
            setLoading(true);
            const res = await axios.post('/auth/change-password', {
                oldPassword: form.oldPassword,
                newPassword: form.newPassword,
            });

            if (res.status === 200) {
                setForm({ oldPassword: '', newPassword: '', confirmPassword: '' });
                toast.success('Đổi mật khẩu thành công!');
            } else {
                toast.error(res.data?.message || 'Đổi mật khẩu thất bại!');
            }
        } catch (err) {
            toast.error(err.response?.data?.message || 'Đổi mật khẩu thất bại!');
        } finally {
            setLoading(false);
        }
    };

    return (
        <motion.div
            className="content-page"
            initial={{ opacity: 0, x: -100 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: 100 }}
            transition={{ duration: 0.5 }}
        >
            <section className="py-3 py-md-5 py-xl-8">
                <div className={cx('login-container')}>
                    <div className={cx('login-card')}>
                        <h2 className="text-center fw-bold mb-4">Đổi mật khẩu</h2>

                        <form onSubmit={handleSubmit}>
                            {/* Mật khẩu cũ */}
                            <div className={cx('input-outlined')}>
                                <input
                                    type="password"
                                    id="oldPassword"
                                    name="oldPassword"
                                    placeholder=" "
                                    value={form.oldPassword}
                                    onChange={handleChange}
                                />
                                <label htmlFor="oldPassword">Mật khẩu cũ</label>
                                {errors.oldPassword && <small className="text-danger">{errors.oldPassword}</small>}
                            </div>

                            {/* Mật khẩu mới */}
                            <div className={cx('input-outlined')}>
                                <input
                                    type="password"
                                    id="newPassword"
                                    name="newPassword"
                                    placeholder=" "
                                    value={form.newPassword}
                                    onChange={handleChange}
                                />
                                <label htmlFor="newPassword">Mật khẩu mới</label>
                                {errors.newPassword && <small className="text-danger">{errors.newPassword}</small>}
                            </div>

                            {/* Xác nhận mật khẩu */}
                            <div className={cx('input-outlined')}>
                                <input
                                    type="password"
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    placeholder=" "
                                    value={form.confirmPassword}
                                    onChange={handleChange}
                                />
                                <label htmlFor="confirmPassword">Xác nhận mật khẩu mới</label>
                                {errors.confirmPassword && (
                                    <small className="text-danger">{errors.confirmPassword}</small>
                                )}
                            </div>

                            <div className="d-grid mb-3">
                                <button type="submit" className="btn btn-dark btn-lg" disabled={loading}>
                                    {loading ? 'Đang xử lý...' : 'Đổi mật khẩu'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </section>

            {/* Toast hiển thị kết quả submit */}
            <ToastContainer position="top-right" autoClose={3000} />
        </motion.div>
    );
};

export default ChangePassword;
