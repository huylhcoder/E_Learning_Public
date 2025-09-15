import SecureVideo from './SecureVideo/SecureVideo';

const VideoSection = ({ currentLesson, loading, onProgress, onComplete }) => {
  if (loading) {
    return (
      <div className="text-center">
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
