import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ToastContainer, toast } from 'react-toastify';

import AuthContext from '~/context/AuthContext';
import { LoginForm } from './LoginForm';
import { OAuthConfig } from '~/config/OAuthConfig';
import LoadingSpinner from '~/components/LoadingSpinner';
import { introspect, login } from '~/services/AuthenticationService';

import './Login.module.scss';

export const Login = () => {
    const navigate = useNavigate();
    const authContext = useContext(AuthContext);

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(true);

    // Kiểm tra token nếu đã đăng nhập
    useEffect(() => {
        document.title = 'Login Page';

        const checkToken = async () => {
            try {
                const data = await introspect();
                if (data?.valid) {
                    navigate('/home');
                }
            } catch (err) {
                // Token không hợp lệ hoặc hết hạn
            } finally {
                setLoading(false);
            }
        };

        const token = localStorage.getItem('token');
        if (token) checkToken();
        else setLoading(false);
    }, [navigate]);

    const handleLogin = async (event) => {
        event.preventDefault();

        try {
            const res = await login(email, password);
            const token = res.token;
            localStorage.setItem('token', token);
            authContext.refresh();

            const introspectData = await introspect();

            if (introspectData?.valid) {
                const role = introspectData.scope;
                if (role === 'ADMIN') navigate('/admin');
                else if (role === 'TEACHER') navigate('/manager-courses');
                else navigate('/home');
            } else {
                throw new Error('Token không hợp lệ');
            }
        } catch (error) {
            toast.error(error.message || 'Đăng nhập thất bại');
        }
    };

    const handleGoogleLogin = () => {
        const { authUri, clientId, redirectUri } = OAuthConfig.google;
        const url = `${authUri}?redirect_uri=${encodeURIComponent(
            redirectUri
        )}&response_type=code&client_id=${clientId}&scope=openid%20email%20profile`;
        window.location.href = url;
    };

    const handleFacebookLogin = () => {
        window.location.href = 'http://localhost:8080/oauth2/authorization/facebook';
    };

    const handleGithubLogin = () => {
        window.location.href = 'http://localhost:8080/oauth2/authorization/github';
    };

    if (loading) return <LoadingSpinner />;

    return (
        <motion.div
            className="content-page"
            initial={{ opacity: 0, x: -100 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: 100 }}
            transition={{ duration: 0.5 }}
        >
            <section className="py-3 py-md-5 py-xl-8">
                <LoginForm
                    email={email}
                    setEmail={setEmail}
                    password={password}
                    setPassword={setPassword}
                    handleLogin={handleLogin}
                    handleGoogleLogin={handleGoogleLogin}
                    handleFacebookLogin={handleFacebookLogin}
                    handleGithubLogin={handleGithubLogin}
                />
                <ToastContainer position="top-right" autoClose={3000} />
            </section>
        </motion.div>
    );
};

export default Login;
