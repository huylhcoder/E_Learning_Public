import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { toast } from 'react-toastify';

//Component
import axios from '~/utils/CustomizeAxios';
import SectionOverview from './SectionOverview';
import LessonTable from './LessonTable';
import LessonOffcanvas from './LessonOffcanvas';
import QuizManager from './QuizManager';

const SectionDetailManager = () => {
    const [section, setSection] = useState({});
    const [lessonDetail, setLessonDetail] = useState({
        lessonId: 0,
        name: '',
        description: '',
    });
    const [videoSrc, setVideoSrc] = useState(null);
    const [selectedFile, setSelectedFile] = useState(null);
    const [listLesson, setListLesson] = useState([]);
    const [loading, setLoading] = useState(false);

    const { courseId, sectionId } = useParams();

    const tokenLogin = localStorage.getItem('token');

    useEffect(() => {
        showSectionDetail();
        showListLesson();
    }, []);

    // Cleanup effect for video URL
    useEffect(() => {
        return () => {
            if (videoSrc) {
                URL.revokeObjectURL(videoSrc);
            }
        };
    }, [videoSrc]);

    const showSectionDetail = async () => {
        try {
            const response = await axios.get(`/section-manager/${sectionId}`, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
            setSection(response.data);
        } catch (error) {
            toast.error('Không thể tải thông tin phần');
        }
    };

    const showListLesson = async () => {
        try {
            const response = await axios.get(`/section-manager/${sectionId}/show-list-section`, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
            setListLesson(response.data);
        } catch (error) {
            toast.error('Không thể tải danh sách bài học');
        }
    };

    const handleFileChange = (event) => {
        if (event.target.files && event.target.files[0]) {
            const file = event.target.files[0];
            const fileURL = URL.createObjectURL(file);
            setVideoSrc(fileURL);
            setSelectedFile(file);
        }
    };

    // const saveLesson = async (e) => {
    //     e.preventDefault();
    //     setLoading(true);
    //     const formData = new FormData();
    //     formData.append('name', lessonDetail.name);
    //     formData.append('description', lessonDetail.description);
    //     if (selectedFile) {
    //         formData.append('file', selectedFile);
    //     }

    //     try {
    //         if (lessonDetail.lessonId !== 0) {
    //             await axios.put(
    //                 `/section-manager/${sectionId}/lesson/${lessonDetail.lessonId}/update-lesson`,
    //                 formData,
    //                 { headers: { Authorization: `Bearer ${tokenLogin}` } },
    //             );
    //             toast.success('Cập nhật bài học thành công');
    //         } else {
    //             await axios.post(`/section-manager/${sectionId}/add-lesson`, formData, {
    //                 headers: { Authorization: `Bearer ${tokenLogin}` },
    //             });
    //             toast.success('Thêm bài học mới thành công');
    //         }
    //         showListLesson();
    //     } catch (error) {
    //         toast.error(error.response?.data || 'Có lỗi xảy ra khi lưu bài học');
    //     } finally {
    //         setLoading(false);
    //     }
    // };

    // Hàm này phải được định nghĩa bên ngoài component hoặc ở đâu đó có thể truy cập được
    const extractVideoDuration = (file) => {
        return new Promise((resolve) => {
            const video = document.createElement('video');
            video.preload = 'metadata';

            video.onloadedmetadata = function () {
                resolve(video.duration);
                URL.revokeObjectURL(video.src);
            };

            video.onerror = function () {
                console.error('Lỗi khi load metadata video.');
                resolve(0);
                URL.revokeObjectURL(video.src);
            };

            video.src = URL.createObjectURL(file);
        });
    };

    const saveLesson = async (e) => {
        e.preventDefault();
        setLoading(true);

        let duration = lessonDetail.videoDuration || 0; // Bắt đầu với giá trị cũ hoặc 0

        // 1. Trích xuất thời lượng nếu có file mới
        if (selectedFile) {
            try {
                // Dừng hàm và chờ kết quả trích xuất thời lượng
                duration = await extractVideoDuration(selectedFile);
            } catch (error) {
                console.error('Không thể lấy thời lượng video:', error);
                // Có thể hiển thị toast lỗi và dừng lại ở đây nếu thời lượng là bắt buộc
                // toast.error('Lỗi: Không thể xác định thời lượng video.');
                // setLoading(false);
                // return;
            }
        }

        // 2. Tạo FormData SAU KHI có giá trị duration cuối cùng
        const formData = new FormData();
        formData.append('name', lessonDetail.name);
        formData.append('description', lessonDetail.description);

        // Gắn thời lượng đã được tính toán (hoặc giá trị cũ)
        formData.append('videoDuration', duration);

        // Gắn file nếu có
        if (selectedFile) {
            formData.append('file', selectedFile);
        }

        // 3. Thực hiện API call
        try {
            if (lessonDetail.lessonId !== 0) {
                await axios.put(
                    `/section-manager/${sectionId}/lesson/${lessonDetail.lessonId}/update-lesson`,
                    formData,
                    { headers: { Authorization: `Bearer ${tokenLogin}` } },
                );
                toast.success('Cập nhật bài học thành công');
            } else {
                await axios.post(`/section-manager/${sectionId}/add-lesson`, formData, {
                    headers: { Authorization: `Bearer ${tokenLogin}` },
                });
                toast.success('Thêm bài học mới thành công');
            }
            showListLesson();
        } catch (error) {
            toast.error(error.response?.data || 'Có lỗi xảy ra khi lưu bài học');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteLesson = async (lessonId) => {
        try {
            await axios.delete(`/section-manager/${sectionId}/lesson/${lessonId}/remove-lesson`, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
            toast.success('Xóa bài học thành công');
            showListLesson();
        } catch (error) {
            toast.error('Xóa thất bại');
        }
    };

    const handleSectionUpdate = async (e) => {
        e.preventDefault();
        console.log('Cập nhật phần:', section);
        try {
            await axios.put(`/section-manager/${sectionId}`, section, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
            toast.success('Cập nhật phần thành công');
        } catch (error) {
            toast.error('Cập nhật phần thất bại');
        }
    };

    const handleEditLesson = (lesson) => {
        setLessonDetail({
            lessonId: lesson.lessonId,
            name: lesson.name,
            description: lesson.description,
            pathVideo: lesson.pathVideo,
        });
        setVideoSrc(lesson.pathVideo);
        console.log('handleEditLesson: ' + lesson.pathVideo);

        setSelectedFile(null); // Reset selected file when editing
    };

    return (
        <div className="container">
            {/* Breadcrumb */}
            <ul className="list-unstyled d-flex align-items-center gap-2 mb-3">
                <li>
                    <Link to="/admin/course-manager" className="fs-3">
                        Danh sách khóa học &gt;
                    </Link>
                </li>
                <li>
                    <Link to={`/admin/course-detail-manager/${courseId}`} className="fs-3">
                        Khóa học: {section?.course?.name} &gt;
                    </Link>
                </li>
                <li>
                    <Link
                        to={`/admin/course-detail-manager/${courseId}/section/${sectionId}`}
                        className="fs-3 text-primary"
                    >
                        Phần: {section?.name}
                    </Link>
                </li>
            </ul>

            {/* Tabs */}
            <ul className="nav nav-tabs" role="tablist">
                <li className="nav-item">
                    <button className="nav-link active" data-bs-toggle="tab" data-bs-target="#overview">
                        Thông tin phần
                    </button>
                </li>
                <li className="nav-item">
                    <button className="nav-link" data-bs-toggle="tab" data-bs-target="#lessons">
                        Các bài học
                    </button>
                </li>
                <li className="nav-item">
                    <button className="nav-link" data-bs-toggle="tab" data-bs-target="#quiz">
                        Bài kiểm tra của phần
                    </button>
                </li>
            </ul>

            <div className="tab-content mt-4">
                {/* Overview */}
                <div className="tab-pane fade show active" id="overview">
                    <SectionOverview
                        section={section}
                        setSection={setSection}
                        handleSectionUpdate={handleSectionUpdate}
                    />
                </div>

                {/* Lessons */}
                <div className="tab-pane fade" id="lessons">
                    <LessonTable
                        listLesson={listLesson}
                        handleEditLesson={handleEditLesson}
                        handleDeleteLesson={handleDeleteLesson}
                    />
                </div>

                <div className="tab-pane fade" id="quiz">
                    <QuizManager sectionId={sectionId} token={tokenLogin} />
                </div>
            </div>

            {/* Offcanvas */}
            <LessonOffcanvas
                lessonDetail={lessonDetail}
                setLessonDetail={setLessonDetail}
                videoSrc={videoSrc}
                handleFileChange={handleFileChange}
                saveLesson={saveLesson}
                loading={loading}
            />
        </div>
    );
};

export default SectionDetailManager;
