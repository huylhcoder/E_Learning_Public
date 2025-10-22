// import React, { useRef, useEffect, useState } from 'react'; // 👈 import useState
// import useSecureVideo from '~/hooks/useSecureVideo';
// import './SecureVideo.scss';

// const SecureVideo = ({ lessonId, onProgress, onComplete, className }) => {
//     const videoRef = useRef(null);
//     const { videoBlob, loading, error } = useSecureVideo(lessonId);

//     // 💡 Thêm state để theo dõi đã hoàn thành bài học này chưa
//     const [isLessonCompleted, setIsLessonCompleted] = useState(false);

//     // Reset trạng thái hoàn thành khi lessonId thay đổi (chuyển bài)
//     useEffect(() => {
//         setIsLessonCompleted(false);
//     }, [lessonId]);

//     // Ngăn chặn context menu
//     useEffect(() => {
//         const video = videoRef.current;
//         if (video) {
//             video.addEventListener('contextmenu', (e) => e.preventDefault());

//             // Disable picture-in-picture
//             video.disablePictureInPicture = true;
//         }
//     }, []);

//     // Xử lý tiến độ video
//     const handleTimeUpdate = () => {
//         const video = videoRef.current;
//         if (video) {
//             const progress = (video.currentTime / video.duration) * 100;
//             onProgress?.(progress);

//             // 💡 KIỂM TRA ĐÃ HOÀN THÀNH CHƯA TRƯỚC KHI GỌI onComplete
//             if (progress >= 95 && !isLessonCompleted) { // 👈 Thêm điều kiện !isLessonCompleted
//                 onComplete?.();
//                 setIsLessonCompleted(true); // 👈 Đặt trạng thái đã hoàn thành
//             }
//         }
//     };

//     if (loading) {
//         return <div className="video-loading">Đang tải video...</div>;
//     }

//     if (error) {
//         return <div className="video-error">{error}</div>;
//     }

//     return (
//         <div className={`secure-video-container ${className}`}>
//             <video
//                 ref={videoRef}
//                 src={videoBlob}
//                 controls
//                 className="secure-video"
//                 onTimeUpdate={handleTimeUpdate}
//                 controlsList="nodownload"
//                 onContextMenu={(e) => e.preventDefault()}
//             >
//                 Trình duyệt của bạn không hỗ trợ video
//             </video>
//         </div>
//     );
// };

// export default SecureVideo;

import React, { useRef, useEffect, useState } from 'react';
import useSecureVideo from '~/hooks/useSecureVideo';
import './SecureVideo.scss';

const SecureVideo = ({ lessonId, onProgress, onComplete, className }) => {
    const videoRef = useRef(null);
    const { videoBlob, loading, error } = useSecureVideo(lessonId);
    const [isLessonCompleted, setIsLessonCompleted] = useState(false);
    const [currentProgress, setCurrentProgress] = useState(0); // ✅ chỉ lưu tạm

    // Reset khi đổi bài học
    useEffect(() => {
        setIsLessonCompleted(false);
        setCurrentProgress(0);
    }, [lessonId]);

    // Ngăn context menu + picture-in-picture
    useEffect(() => {
        const video = videoRef.current;
        if (video) {
            video.addEventListener('contextmenu', (e) => e.preventDefault());
            video.disablePictureInPicture = true;
        }
    }, []);

    // Theo dõi thời gian phát video
    const handleTimeUpdate = () => {
        const video = videoRef.current;
        if (!video || !video.duration) return;
        const progress = (video.currentTime / video.duration) * 100;
        setCurrentProgress(progress); // ✅ chỉ cập nhật state, không gọi API ở đây

        // ✅ Nếu xem >=95% và chưa hoàn thành thì gọi onComplete 1 lần
        if (progress >= 95 && !isLessonCompleted) {
            onComplete?.();
            setIsLessonCompleted(true);
        }
    };

    // ✅ Khi người dùng dừng video (pause) → lưu tiến độ
    const handlePause = () => {
        if (currentProgress > 0) {
            onProgress?.(currentProgress);
        }
    };

    // ✅ Khi người dùng tua (seeked) → lưu lại tiến độ mới
    const handleSeeked = () => {
        const video = videoRef.current;
        if (!video || !video.duration) return;
        const progress = (video.currentTime / video.duration) * 100;
        onProgress?.(progress);
    };

    // ✅ Khi video kết thúc → đảm bảo gọi hoàn thành và lưu lại 100%
    const handleEnded = () => {
        onProgress?.(100);
        if (!isLessonCompleted) {
            onComplete?.();
            setIsLessonCompleted(true);
        }
    };

    if (loading) return <div className="video-loading">Đang tải video...</div>;
    if (error) return <div className="video-error">{error}</div>;

    return (
        <div className={`secure-video-container ${className}`}>
            <video
                ref={videoRef}
                src={videoBlob}
                controls
                className="secure-video"
                controlsList="nodownload"
                onContextMenu={(e) => e.preventDefault()}
                onTimeUpdate={handleTimeUpdate}
                onPause={handlePause}     // 👈 gọi khi dừng
                onSeeked={handleSeeked}   // 👈 gọi khi tua
                onEnded={handleEnded}     // 👈 gọi khi hết
            >
                Trình duyệt của bạn không hỗ trợ video.
            </video>
        </div>
    );
};

export default SecureVideo;

