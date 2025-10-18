import React, { useState, useEffect } from 'react';
// Giả định bạn đã cài đặt react-router-dom
import { Link } from 'react-router-dom'; 
// Giả định các import khác
import axios from '~/utils/CustomizeAxios';
import styles from './Roadmap.module.scss';
import classNames from 'classnames/bind';

const cx = classNames.bind(styles);

// Hàm giả định để format tiền tệ (nếu không có, bạn có thể tự implement)
const formatCurrency = (price) => {
    if (price === null || price === undefined) return 'N/A';
    return price.toLocaleString('en-US', { style: 'currency', currency: 'USD' }).replace('$', '');
};

const Roadmap = () => {
    const [goal, setGoal] = useState('');
    const [level, setLevel] = useState('');
    const [preferredCategories, setPreferredCategories] = useState('');
    const [loading, setLoading] = useState(false);
    const [roadmap, setRoadmap] = useState(null);

    const [levels, setLevels] = useState([]); 
    const [categories, setCategories] = useState([]); 

    const token = localStorage.getItem('token'); 
    console.log(roadmap);

    // Lấy levels + categories khi load component
    useEffect(() => {
        const fetchMetaData = async () => {
            // ... (Phần fetch data giữ nguyên)
            try {
                const [resLevels, resCategories] = await Promise.all([
                    axios.get('/course-level/list-course-level', {
                        headers: { Authorization: `Bearer ${token}` },
                    }),
                    axios.get('/category/list-category', {
                        headers: { Authorization: `Bearer ${token}` },
                    }),
                ]);

                setLevels(resLevels.data);
                setCategories(resCategories.data);
            } catch (err) {
                console.error('Error fetching levels/categories:', err);
            }
        };

        fetchMetaData();
    }, [token]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setRoadmap(null);
        // ... (Phần handleSubmit giữ nguyên)
        try {
            const response = await axios.post(
                'course/roadmap',
                { goal, level, preferredCategories },
                { headers: { Authorization: `Bearer ${token}` } },
            );
            setRoadmap(response.data);
            console.log(response.data);
        } catch (error) {
            console.error('Error fetching roadmap:', error);
            alert('Có lỗi xảy ra khi gợi ý roadmap. Vui lòng thử lại.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container py-5">
            {/* Tiêu đề chính */}
            <h1 className="display-4 fw-bold text-center mb-5">
                <i className="bi bi-compass-fill me-2"></i> Roadmap Gợi ý
            </h1>

            {/* Form nhập thông tin */}
            <div className={cx('roadmap-form-card', 'card', 'shadow-lg', 'mb-5')}>
                <div className="card-body p-4 p-md-5">
                    <h2 className="card-title h3 mb-4 text-primary">Xác định Mục tiêu</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="row g-4">
                            {/* Input Goal */}
                            <div className="col-12">
                                <label htmlFor="goalInput" className="form-label fw-semibold">
                                    Mục tiêu mong muốn:
                                </label>
                                <input
                                    type="text"
                                    id="goalInput"
                                    value={goal}
                                    onChange={(e) => setGoal(e.target.value)}
                                    placeholder="Ví dụ: Tôi muốn học Backend với Java"
                                    className="form-control form-control-lg"
                                    required
                                />
                                <div className="form-text">Mô tả rõ mục tiêu của bạn (ngành nghề, công nghệ,...)</div>
                            </div>

                            {/* Select Level */}
                            <div className="col-md-6">
                                <label htmlFor="levelSelect" className="form-label fw-semibold">
                                    Độ khó/Trình độ hiện tại:
                                </label>
                                <select
                                    id="levelSelect"
                                    value={level}
                                    onChange={(e) => setLevel(e.target.value)}
                                    className="form-select form-select-lg"
                                    required
                                >
                                    <option value="">-- Chọn độ khó --</option>
                                    {levels.map((lv) => (
                                        <option key={lv.id} value={lv.name}>
                                            {lv.name}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            {/* Select Category */}
                            <div className="col-md-6">
                                <label htmlFor="categorySelect" className="form-label fw-semibold">
                                    Danh mục yêu thích:
                                </label>
                                <select
                                    id="categorySelect"
                                    value={preferredCategories}
                                    onChange={(e) => setPreferredCategories(e.target.value)}
                                    className="form-select form-select-lg"
                                    required
                                >
                                    <option value="">-- Chọn danh mục --</option>
                                    {categories.map((cat) => (
                                        <option key={cat.id} value={cat.slug || cat.name}>
                                            {cat.name}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            {/* Button Submit */}
                            <div className="col-12 pt-3">
                                <button
                                    type="submit"
                                    className="btn btn-primary btn-lg w-100 fw-bold d-flex align-items-center justify-content-center"
                                    disabled={loading}
                                >
                                    {loading ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                            Đang gợi ý...
                                        </>
                                    ) : (
                                        <>
                                            <i className="bi bi-magic me-2"></i> Gợi ý Roadmap Ngay!
                                        </>
                                    )}
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            {/* Hiển thị kết quả */}
            {loading && !roadmap && (
                 <div className="text-center my-5 p-5 border rounded bg-light">
                    <div className="spinner-grow text-primary" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                    <p className="mt-3 fw-bold text-primary">Hệ thống đang tạo roadmap cho bạn, vui lòng chờ...</p>
                </div>
            )}
            
            {roadmap && (
                <div className={cx('roadmap-result', 'p-4', 'p-md-5', 'border', 'rounded-3', 'shadow')}>
                    {/* Thông tin Roadmap */}
                    <h2 className="mb-3 text-success fw-bold">
                        <i className="bi bi-check-circle-fill me-2"></i> {roadmap.roadmapName}
                    </h2>
                    <p className="lead border-bottom pb-3 mb-4 fst-italic text-muted">{roadmap.explanation}</p>

                    {/* Danh sách Khóa học */}
                    <h3 className="h4 fw-bold mb-4 text-primary">
                        <i className="bi bi-book-half me-2"></i> Khóa học gợi ý:
                    </h3>
                    
                    <div className="list-group">
                        {roadmap.recommendedCourses?.map((course) => (
                            // Sử dụng Link thay thế cho div và thêm path động
                            <Link 
                                key={course.courseId} 
                                // Đường dẫn tới trang chi tiết khóa học. Giả định courseId là ID của khóa học.
                                to={`/course/course-detail/${course.courseId}`}
                                className={cx('course-item', 'list-group-item', 'list-group-item-action', 'p-4', 'mb-3', 'rounded-3', 'shadow-sm')}
                            >
                                <div className="d-flex w-100 justify-content-between">
                                    <h4 className="mb-1 fw-bold text-dark">{course.name}</h4>
                                    <small className="text-muted text-nowrap">
                                        <span className="badge bg-secondary">{course.level}</span>
                                    </small>
                                </div>
                                <p className="mb-2 text-muted">{course.description}</p>
                                <div className="d-flex justify-content-between align-items-center mt-3 border-top pt-2">
                                    <div className="small">
                                        <span className="me-3">
                                            <i className="bi bi-tag-fill me-1"></i> Chủ đề: <span className="fw-semibold">{course.topic}</span>
                                        </span>
                                    </div>
                                    <div className="small fw-bold text-primary">
                                        <span className="me-3 text-warning">
                                            <i className="bi bi-star-fill"></i> {course.averageRating}
                                        </span>
                                        Giá: {formatCurrency(course.price)}$
                                    </div>
                                </div>
                            </Link>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default Roadmap;
