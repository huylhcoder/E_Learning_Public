import React, { useRef, useEffect } from 'react';
import useSecureVideo from '~/hooks/useSecureVideo';
import './SecureVideo.scss';

const SecureVideo = ({ lessonId, onProgress, onComplete, className }) => {
    const videoRef = useRef(null);
    const { videoBlob, loading, error } = useSecureVideo(lessonId);

    // Ngăn chặn context menu
    useEffect(() => {
        const video = videoRef.current;
        if (video) {
            video.addEventListener('contextmenu', (e) => e.preventDefault());

            // Disable picture-in-picture
            video.disablePictureInPicture = true;
        }
    }, []);

    // Xử lý tiến độ video
    const handleTimeUpdate = () => {
        const video = videoRef.current;
        if (video) {
            const progress = (video.currentTime / video.duration) * 100;
            onProgress?.(progress);

            // Kiểm tra hoàn thành video
            if (progress >= 95) {
                onComplete?.();
            }
        }
    };

    if (loading) {
        return <div className="video-loading">Đang tải video...</div>;
    }

    if (error) {
        return <div className="video-error">{error}</div>;
    }

    return (
        <div className={`secure-video-container ${className}`}>
            <video
                ref={videoRef}
                src={videoBlob}
                controls
                className="secure-video"
                onTimeUpdate={handleTimeUpdate}
                controlsList="nodownload"
                onContextMenu={(e) => e.preventDefault()}
            >
                Trình duyệt của bạn không hỗ trợ video
            </video>
        </div>
    );
};

export default SecureVideo;
