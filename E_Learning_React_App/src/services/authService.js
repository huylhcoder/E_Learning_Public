import axios from '../utils/CustomizeAxios';
import { toast } from 'react-toastify';

export const login = async (email, password) => {
    try {
        const response = await axios.post(`api/v1/auth/token`, { email, password });
        if (response.data.code === 401) {
            toast.error('Email or Password incorrect');
            return;
        }
        return response.data;
    } catch (error) {
        throw new Error(error);
    }
};

export const introspect = async () => {
    try {
        const token = localStorage.getItem('token');
        if (!token) throw new Error('Token is missing');

        const response = await axios.post(`api/v1/auth/introspect`, { token });
        return response.data?.result;
    } catch (error) {
        const message = error.response?.data?.message || 'Failed to introspect token';
        throw new Error(message);
    }
};

export const logout = async (token) => {
    const response = await axios.post(`api/v1/auth/logout`, { token });
    return response;
};
