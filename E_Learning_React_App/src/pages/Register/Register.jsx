import { useState } from 'react';
import { motion } from 'framer-motion';
import { ToastContainer, toast } from 'react-toastify';

import axios from '~/utils/CustomizeAxios';
import OtpModal from './OtpModal';
import RegisterForm from './RegisterForm';

export default function Register() {
    const baseURL = '/auth';

    const [form, setForm] = useState({
        fullname: '',
        email: '',
        password: '',
        retypePassword: '',
        otp: '',
    });

    const [showOtpModal, setShowOtpModal] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value,
        });
    };

    const handleCheckUser = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await axios.post(`${baseURL}/check-user`, form);

            await axios.post(`${baseURL}/send-verification-code`, form.email, {
                headers: { 'Content-Type': 'text/plain' },
            });

            toast.success('Mã xác nhận đã được gửi qua email!');
            setShowOtpModal(true);
        } catch (err) {
            toast.error(err.response?.data?.message || 'Lỗi khi kiểm tra email');
        } finally {
            setLoading(false);
        }
    };

    const handleOtpSubmit = async (otp) => {
        try {
            const userRegisterDTO = { ...form, otp };
            await axios.post(`${baseURL}/register`, userRegisterDTO);
            toast.success('Đăng ký thành công!');
            setShowOtpModal(false);
        } catch (err) {
            toast.error(err.response?.data || 'OTP không hợp lệ hoặc đăng ký thất bại');
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
                <RegisterForm
                    form={form}
                    handleChange={handleChange}
                    handleCheckUser={handleCheckUser}
                    loading={loading}
                />
            </section>

            <OtpModal
                show={showOtpModal}
                onClose={() => setShowOtpModal(false)}
                onSubmit={handleOtpSubmit}
            />
            <ToastContainer position="top-right" autoClose={3000} />
        </motion.div>
    );
}
