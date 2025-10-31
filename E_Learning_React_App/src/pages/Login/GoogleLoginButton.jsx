import React, { useContext } from 'react';
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
import { jwtDecode } from 'jwt-decode';
import axios from '~/utils/CustomizeAxios';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import AuthContext from '~/context/AuthContext'; // chỉnh đúng path context của bạn

const GoogleLoginButton = () => {
    const navigate = useNavigate();
    const authContext = useContext(AuthContext);

    const handleSuccess = async (credentialResponse) => {
        try {
            const idToken = credentialResponse.credential;
            const decoded = jwtDecode(idToken);
            console.log('Google user info:', decoded);

            // Gửi token Google sang backend để nhận token hệ thống
            const res = await axios.post('/auth/google', { credential: idToken });

            console.log(res);
            
            const { token, user } = res.data || {};
            if (token && user) {
                // Lưu vào localStorage giống login thường
                localStorage.setItem('token', token);
                localStorage.setItem('user', JSON.stringify(user));

                // Cập nhật AuthContext
                authContext.refresh?.();

                toast.success('Đăng nhập thành công', {
                    // position: 'top-center',
                    autoClose: 2000,
                });

                // Điều hướng theo role
                const role = user.role || 'USER';
                setTimeout(() => {
                    if (role === 'ADMIN') navigate('/admin');
                    else if (role === 'TEACHER') navigate('/manager-courses');
                    else navigate('/home');
                }, 1500);
            } else {
                toast.error('Không nhận được token hoặc user từ server ❌', {
                    position: 'top-center',
                });
            }
        } catch (error) {
            console.error('Google login error:', error);
            toast.error('Đăng nhập Google thất bại ⚠️', {
                position: 'top-center',
            });
        }
    };

    const handleError = () => {
        toast.error('Đăng nhập Google thất bại ⚠️', {
            position: 'top-center',
        });
    };

    return (
        <GoogleOAuthProvider clientId="191517587755-sl1mq6vmh78knfhj15k9o3mahjml8qqj.apps.googleusercontent.com">
            <GoogleLogin
                onSuccess={handleSuccess}
                onError={handleError}
                shape="circle"
                text="signin_with"
                size="large"
            />
        </GoogleOAuthProvider>
    );
};

export default GoogleLoginButton;
