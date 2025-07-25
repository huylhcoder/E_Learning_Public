import axios from '../utils/CustomizeAxios';

export const getMyInfo = async () => {
    try {
        const response = await axios.get(`api/v1/my-info`)
        return response.data;
    } catch (error) {
        console.error('Error get my info', error);
        throw error;
    }
}

export const getPointsByCurrentLogin = async () => {
    try {
        const response = await axios.get(`api/v1/get-points-user-current`)
        return response.data;
    } catch (error) {
        console.error('Error get point by current login', error);
        throw error;
    }
}

export const sendOtp = async (email) => {
    try {
        const response = await axios.post(`api/v1/send-otp`, { email: email });
        console.log(response);

        return response.data;
    } catch (error) {
        console.error('Error forgot password', error);
        throw error;
    }
}

export const verifyOtp = async (email, otp) => {
    try {
        const response = await axios.post(`api/v1/verify-otp`, {
            email: email,
            otp: otp
        })
        return response.data;
    } catch (error) {
        console.error('Error verify otp', error);
        throw error;
    }
}

export const resetPassword = async (email, otp, newPassword) => {
    try {
        const response = await axios.post('api/v1/reset-password', {
            email: email,
            otp: otp,
            password: newPassword
        });

        console.log('Response:', response);
        return response.data;

    } catch (error) {
        console.error('Error resetting password:', error.response ? error.response.data : error.message);
        throw error;
    }
};

export const checkUserExists = async (email) => {
    try {
        const response = await axios.post(`api/v1/check-exists-user`, { email: email })
        console.log(response);
        return response.data;
    } catch (error) {
        console.error('User not existed', error);
        throw error;
    }
}

export const sendOtpRegister = async (email) => {
    try {
        const response = await axios.post(`api/v1/send-otp-register`, {
            email: email
        });
        console.log(response);
        return response.data;
    } catch (error) {
        console.error('Error sendOtpRegister', error);
        throw error;
    }
};

export const registerUser = async (otp, userData) => {
    try {
        const response = await axios.post(`api/v1/register`, userData, {
            params: {
                otp: otp
            }
        });
        console.log(response);
        return response.data;
    } catch (error) {
        console.error('Error create user', error);
        throw error;
    }
};

export const registerTeacher = async (formDataToSend) => {
    try {
        const response = await axios.post(`api/v1/register-teacher`, formDataToSend, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        return response.data;
    } catch (error) {
        console.error('Error in registerTeacher API:', error);
        throw error;
    }
};


export const createPasswordForFirst = async (password) => {
    try {
        const response = await axios.post(`api/v1/create-password`, { password });
        return response.data;
    } catch (error) {
        console.log(error);
        throw error;
    }
};

export const myInfo = async() => {
    const response = await axios.get(`api/v1/my-info`);
    return response.data;
}


