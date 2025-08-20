import { useContext, useEffect, useState } from 'react';
import { getAvatar } from '~/services/ProfileService';
import AuthContext from '~/context/AuthContext';

export const useUserProfile = () => {
    const authContext = useContext(AuthContext);
    const [avatar, setAvatar] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!authContext.authenticated) {
            setAvatar(null);
            setLoading(false);
            return;
        }

        const fetchUserProfile = async () => {
            try {
                const avatarData = await getAvatar();
                setAvatar(avatarData.result);

                console.log('useUserProfile:', avatarData.result); // Debugging
                
            } catch (error) {
                console.log(error);
            } finally {
                setLoading(false);
            }
        };

        fetchUserProfile();
    }, [authContext]);

    return { avatar, loading };
};
