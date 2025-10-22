import SecureVideo from './SecureVideo/SecureVideo';
import style from '../Learning.module.scss';

const VideoSection = ({ currentLesson, loading, onProgress, onComplete }) => {
    // 💡 THAY ĐỔI: Hiển thị placeholder giống khung video khi loading
    if (loading) {
        return (
            // Thêm class để đặt kích thước và vị trí loading
            <div className={`${style.videoPlaceholder} d-flex justify-content-center align-items-center mb-4`}>
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Đang tải...</span>
                </div>
            </div>
        );
    }

    if (!currentLesson) {
        return <div className="alert alert-info">Khóa học này chưa có bài học nào.</div>;
    }

    return (
        <>
            <div className="video-container">
                <SecureVideo
                    currentLesson={currentLesson} // 💡 Truyền cả currentLesson để SecureVideo có thể hiển thị title
                    lessonId={currentLesson.lessonId}
                    onProgress={onProgress}
                    onComplete={onComplete}
                    className="mb-4"
                />
            </div>
            <div className="mt-3">
                <span className="h3 text-primary fw-bold mt-3">Bài học: {currentLesson.name}</span>
                <p className="text-muted mt-3">Mô tả ngắn: {currentLesson.description}</p>
            </div>
        </>
    );
};

export default VideoSection;
