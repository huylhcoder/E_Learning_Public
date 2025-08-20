import React, { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSearch } from '@fortawesome/free-solid-svg-icons';

import axios from '~/utils/CustomizeAxios';
import styles from './MyCourse.module.scss'; // SCSS module

const MyCourse = () => {
    const token = localStorage.getItem('token');
    const [itemsPerPage] = useState(5);
    const [currentPage, setCurrentPage] = useState(1);
    const [registeredCourses, setRegisteredCourses] = useState([]);
    const [filteredCourses, setFilteredCourses] = useState([]);
    const [listYeuThich, setListYeuThich] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [searchQuery, setSearchQuery] = useState('');

    const loadCuaToi = async () => {
        try {
            const resp = await axios.get(`/course/registered-course`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setRegisteredCourses(resp.data || []);
            setFilteredCourses(resp.data || []);
            setTotalPages(Math.ceil((resp.data?.length || 0) / itemsPerPage));
        } catch (err) {
            console.error('Lỗi không thể tải dữ liệu', err);
        }
    };

    const loadYeuThich = async () => {
        try {
            const resp = await axios.get(`/course/favorite-course`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setListYeuThich(resp.data || []);
        } catch (err) {
            console.error('Lỗi không thể tải dữ liệu', err);
        }
    };

    const deleteItem = async (favoriteCourseId) => {
        if (window.confirm('Bạn có chắc chắn muốn xóa khỏi yêu thích?')) {
            try {
                await axios.delete(`/favorite-course/delete/${favoriteCourseId}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                loadYeuThich();
            } catch (err) {
                console.error('Lỗi khi xóa khóa học', err);
            }
        }
    };

    useEffect(() => {
        if (searchQuery.trim()) {
            const query = searchQuery.toLowerCase();
            setFilteredCourses(
                registeredCourses.filter((c) => {
                    const name = c.course.name?.toLowerCase() || '';
                    const desc = c.course.description?.toLowerCase() || '';
                    const category = c.course.category?.name?.toLowerCase() || '';
                    return name.includes(query) || desc.includes(query) || category.includes(query);
                }),
            );
        } else {
            setFilteredCourses(registeredCourses);
        }
    }, [searchQuery, registeredCourses]);

    useEffect(() => {
        if (!token) {
            window.location.href = '/login';
        } else {
            loadCuaToi();
            loadYeuThich();
        }
    }, []);

    const goToPage = (page) => {
        if (page >= 1 && page <= totalPages) {
            setCurrentPage(page);
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    };

    const getPages = () => Array.from({ length: totalPages }, (_, i) => i + 1);

    const CourseCard = ({ course, date, actionBtn, linkTo }) => (
        <a href={linkTo} className={`card mb-3 text-decoration-none text-dark ${styles.courseCard}`}>
            <div className="row g-0">
                <div className="col-md-4">
                    <img src={course.avatar} className={styles.courseImage} alt={course.name} />
                </div>
                <div className="col-md-8">
                    <div className="card-body">
                        <h5 className="card-title fw-bold">{course.name}</h5>
                        <p className="card-text text-truncate">Ghi chú ngắn: {course.description}</p>
                        <p className="card-text fw-semibold text-success">
                            {course.price === 0 ? 'Miễn phí' : course.price.toLocaleString('vi-VN') + ' VND'}
                        </p>
                        {date && (
                            <small className="text-muted">
                                Ngày đăng ký: {new Date(date).toLocaleDateString('vi-VN')}
                            </small>
                        )}
                        {actionBtn && <div className="mt-2">{actionBtn}</div>}
                    </div>
                </div>
            </div>
        </a>
    );

    return (
        <main>
            <div className={`position-relative ${styles.headerBanner}`}>
                <div className={styles.overlay}></div>
                <div className="container position-relative text-white py-5">
                    <h1 className="fw-bold">Khóa học của tôi</h1>
                    <p>Quản lý các khóa học bạn đã đăng ký và yêu thích</p>
                </div>
            </div>

            <div className="container mt-4">
                <ul className="nav nav-tabs mb-3">
                    <li className="nav-item">
                        <button className="nav-link active" data-bs-toggle="tab" data-bs-target="#myCourses">
                            Danh sách khóa học
                        </button>
                    </li>
                    <li className="nav-item">
                        <button className="nav-link" data-bs-toggle="tab" data-bs-target="#favCourses">
                            Khóa học yêu thích
                        </button>
                    </li>
                </ul>

                <div className="tab-content">
                    {/* TAB 1 */}
                    <div className="tab-pane fade show active" id="myCourses">
                        <div className="input-group mb-4">
                            <input
                                type="text"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                placeholder="Tìm kiếm khóa học..."
                                className="form-control"
                            />
                            <button className="btn btn-outline-secondary">
                                <FontAwesomeIcon icon={faSearch} size="lg" />
                            </button>
                        </div>

                        {filteredCourses.length === 0 ? (
                            <div className="alert alert-info">Bạn chưa đăng ký khóa học nào.</div>
                        ) : (
                            filteredCourses
                                .slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage)
                                .map((c) => (
                                    <CourseCard
                                        key={c.registeredCourseId}
                                        course={c.course}
                                        date={c.createAt}
                                        linkTo={`/learning?courseId=${c.course.courseId}`}
                                    />
                                ))
                        )}

                        {/* Pagination */}
                        {totalPages > 1 && (
                            <nav className="d-flex justify-content-center mt-4">
                                <ul className="pagination">
                                    <li className={`page-item ${currentPage === 1 && 'disabled'}`}>
                                        <button className="page-link" onClick={() => goToPage(currentPage - 1)}>
                                            ‹
                                        </button>
                                    </li>
                                    {getPages().map((p) => (
                                        <li key={p} className={`page-item ${p === currentPage ? 'active' : ''}`}>
                                            <button className="page-link" onClick={() => goToPage(p)}>
                                                {p}
                                            </button>
                                        </li>
                                    ))}
                                    <li className={`page-item ${currentPage === totalPages && 'disabled'}`}>
                                        <button className="page-link" onClick={() => goToPage(currentPage + 1)}>
                                            ›
                                        </button>
                                    </li>
                                </ul>
                            </nav>
                        )}
                    </div>

                    {/* TAB 2 */}
                    <div className="tab-pane fade" id="favCourses">
                        {listYeuThich.length === 0 ? (
                            <div className="alert alert-info">Danh sách yêu thích của bạn đang trống.</div>
                        ) : (
                            listYeuThich
                                .slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage)
                                .map((item) => (
                                    <CourseCard
                                        key={item.favoriteCourseId}
                                        course={item.course}
                                        linkTo={`/course/course-detail/${item.course.courseId}`}
                                        actionBtn={
                                            <button
                                                className="btn btn-outline-danger btn-sm"
                                                onClick={(e) => {
                                                    e.preventDefault();
                                                    deleteItem(item.favoriteCourseId);
                                                }}
                                            >
                                                <i className="fa-solid fa-trash-can me-1"></i> Xóa
                                            </button>
                                        }
                                    />
                                ))
                        )}
                    </div>
                </div>
            </div>
        </main>
    );
};

export default MyCourse;
