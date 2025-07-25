import axios from "../utils/CustomizeAxios";

export const notificationCurrentLogin = async () => {
    try {
        const response = await axios.get(`api/v1/notification-current`)
        return response.data;
    } catch (error) {
        console.log('Failed to getNotification By Current Login', error);
        throw error;
    }
}

export const markAsReadNotification = async (notificationId) => {
    try {
        const response = await axios.put(`api/v1/is-read/${notificationId}`);
        console.log(response)
        return response;
    } catch (error) {
        console.log('Failed to markAsReadNotification', error);
        throw error;
    }
}