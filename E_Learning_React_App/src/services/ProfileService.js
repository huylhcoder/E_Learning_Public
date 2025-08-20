import axios from "../utils/CustomizeAxios";

export const getAvatar = async () => {
    const response = await axios.get(`/user/get-avatar`)
    return response.data
}

export const getProfileInfo = async () => {
    const response = await axios.get(`/user/info-user`)
    return response.data;
}

export const updateAvatar = async (formData) => {
    try {
        const response = await axios.post(`/user/update-avatar`, formData)
        console.log(response)
        return response.data;
    } catch (error) {
        console.log('Fail to update Avatar', error)
        throw error;
    }
};

export const removeAvatar = async () => {
    try {
        const response = await axios.delete(`/user/remove-avatar`)
        return response.data;
    } catch (error) {
        console.log('Fail to remove Avatar', error)
        throw error;
    }
}

export const updateProfile = async (profileData) => {
    try {
        const response = await axios.put('/user/update-profile', profileData);
        return response.data;
    } catch (error) {
        console.log('Fail to update profile', error)
        throw error;
    }
};