import { FaVideo } from 'react-icons/fa';
import { Button } from 'react-bootstrap';
import styles from './LessonOffcanvas.module.scss'; // bạn có thể tạo file SCSS để style thêm

const LessonOffcanvas = ({ lessonDetail, setLessonDetail, videoSrc, handleFileChange, saveLesson, loading }) => {
    console.log('Video tại LessonOffcanvas: ' + videoSrc);

    return (
        <div className="offcanvas offcanvas-end" id="lessonOffcanvas" tabIndex="-1">
            <div className="offcanvas-header">
                <h5 className="offcanvas-title fs-3 text-primary fw-bold">
                    {lessonDetail.lessonId === 0 ? 'Thêm bài học mới' : 'Chỉnh sửa bài học'}
                </h5>
                <button className="btn-close" data-bs-dismiss="offcanvas"></button>
            </div>
            <div className="offcanvas-body">
                <form onSubmit={saveLesson}>
                    {/* Tên bài học */}
                    <div className="mb-3">
                        <label className="form-label">Tên bài học</label>
                        <input
                            type="text"
                            className="form-control fs-4"
                            value={lessonDetail.name}
                            onChange={(e) => setLessonDetail({ ...lessonDetail, name: e.target.value })}
                        />
                    </div>

                    {/* Mô tả */}
                    <div className="mb-3">
                        <label className="form-label">Mô tả</label>
                        <textarea
                            className="form-control  fs-4"
                            rows="4"
                            value={lessonDetail.description}
                            onChange={(e) => setLessonDetail({ ...lessonDetail, description: e.target.value })}
                        />
                    </div>

                    {/* Video upload */}
                    <div className="mb-3">
                        <label className="form-label">Video bài học</label>
                        {!videoSrc && !lessonDetail.pathVideo ? (
                            <div
                                className={`${styles.uploadBox}`}
                                onClick={() => document.getElementById('videoInput').click()}
                            >
                                <FaVideo className="me-2" />
                                <p>Chọn video cho bài học</p>
                            </div>
                        ) : (
                            <div className={`${styles.previewBox}`}>
                                {/* <video className="w-100 rounded" controls>
                                    <source src={lessonDetail.pathVideo || videoSrc} type="video/mp4" />
                                </video> */}
                                <video key={videoSrc} className="w-100 rounded" controls>
                                    <source src={videoSrc} type="video/mp4" />
                                </video>

                                <Button
                                    size="sm"
                                    className={`${styles.changeBtn} mt-2 ms-3 fs-5 p-3`}
                                    variant="outline-secondary"
                                    onClick={() => document.getElementById('videoInput').click()}
                                >
                                    <FaVideo className="me-2" /> Đổi video
                                </Button>
                            </div>
                        )}
                        <input
                            id="videoInput"
                            type="file"
                            className="d-none"
                            accept="video/*"
                            onChange={handleFileChange}
                        />
                    </div>

                    {/* Nút lưu */}
                    <Button type="submit" className="btn btn-primary w-100 fs-5" disabled={loading}>
                        {loading ? (
                            <span className="spinner-border spinner-border-sm me-2"></span>
                        ) : (
                            <i className="fas fa-save me-2"></i>
                        )}
                        Lưu bài học
                    </Button>
                </form>
            </div>
        </div>
    );
};

export default LessonOffcanvas;
