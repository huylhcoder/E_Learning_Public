import { useState, useEffect } from 'react';
import axios from '~/utils/CustomizeAxios';

const useSecureVideo = (lessonId) => {
    const [videoBlob, setVideoBlob] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const token = localStorage.getItem('token');

    useEffect(() => {
        const fetchVideo = async () => {
            try {
                const response = await axios.get(`/lesson/video/${lessonId}`, {
                    responseType: 'blob',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                // Tạo Blob URL từ response
                const videoURL = URL.createObjectURL(new Blob([response.data]));
                setVideoBlob(videoURL);
                setLoading(false);
            } catch (err) {
                setError('Không thể tải video');
                setLoading(false);
            }
        };

        if (lessonId) {
            fetchVideo();
        }

        // Cleanup function
        return () => {
            if (videoBlob) {
                URL.revokeObjectURL(videoBlob);
            }
        };
    }, [lessonId]);

    return { videoBlob, loading, error };
};

export default useSecureVideo;
