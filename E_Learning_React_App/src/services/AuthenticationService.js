import { toast } from 'react-toastify';

import axios from '../utils/CustomizeAxios';


export const login = async (email, password) => {
    try {
        const response = await axios.post(`/auth/login`, {
            email,
            password,
        });
        if (response.data.code === 401) {
            toast.error('Email or Password incorrect');
            return;
        }
        toast.success('Đăng nhập thành công');
        return response.data;
    } catch (error) {
        throw new Error(error);
    }
};

export const introspect = async () => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Token is missing');
        }

        const response = await axios.post(`/auth/introspect`, {
            token: token,
        });

        if (response.data && response.data.result) {
            return response.data.result;
        } else {
            throw new Error('Invalid introspect response structure');
        }
    } catch (error) {
        const errorMessage = error.response?.data?.message || 'Failed to introspect token';
        throw new Error(errorMessage);
    }
};

// export const logout = async (token) => {
//     const response = await axios.post(`api/v1/auth/logout`, { token: token });
//     return response;
// };
