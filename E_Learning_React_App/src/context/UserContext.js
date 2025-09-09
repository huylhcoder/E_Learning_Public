import { createContext, useState, useEffect } from 'react';
import { getAvatar, getProfileInfo } from '~/services/ProfileService';

export const UserContext = createContext();

export const UserProvider = ({ children }) => {
    const [userProfile, setUserProfile] = useState(null);
    const [avatar, setAvatar] = useState(null);

    const fetchUserData = async () => {
        try {
            const profileData = await getProfileInfo();
            const avatarUrl = await getAvatar();
            setUserProfile(profileData);
            setAvatar(avatarUrl);
        } catch (error) {
            console.error('Lỗi lấy dữ liệu user:', error);
        }
    };

    useEffect(() => {
        fetchUserData();
    }, []);

    // 👈 expose để trang Profile gọi lại sau khi update
    const updateUser = async () => {
        await fetchUserData();
    };

    return <UserContext.Provider value={{ userProfile, avatar, updateUser }}>{children}</UserContext.Provider>;
};
