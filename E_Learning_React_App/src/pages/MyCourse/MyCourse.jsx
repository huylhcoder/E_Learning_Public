import React, { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import axios from '~/utils/CustomizeAxios';
import styles from './MyCourse.module.scss';

import CourseCard from './CourseCard';
import FavoriteCard from './FavoriteCard';
import Pagination from './Pagination';

const MyCourse = () => {
    const token = localStorage.getItem('token');
    const [itemsPerPage] = useState(8);
    const [currentPage, setCurrentPage] = useState(1);
    const [registeredCourses, setRegisteredCourses] = useState([]);
    const [filteredCourses, setFilteredCourses] = useState([]);
    const [listYeuThich, setListYeuThich] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [searchQuery, setSearchQuery] = useState('');

    useEffect(() => {
        if (!token) {
            window.location.href = '/login';
        } else {
            loadCuaToi();
            loadYeuThich();
        }
    }, []);

    useEffect(() => {
        if (searchQuery.trim()) {
            const query = searchQuery.toLowerCase();
            const filtered = registeredCourses.filter((c) => (c.courseName || '').toLowerCase().includes(query));
            setFilteredCourses(filtered);
            setTotalPages(Math.ceil(filtered.length / itemsPerPage));
            setCurrentPage(1);
        } else {
            setFilteredCourses(registeredCourses);
            setTotalPages(Math.ceil(registeredCourses.length / itemsPerPage));
            setCurrentPage(1);
        }
    }, [searchQuery, registeredCourses, itemsPerPage]);

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
        if (!window.confirm('Bạn có chắc chắn muốn xóa khỏi yêu thích?')) return;

        try {
            await axios.delete(`/favorite-course/delete/${favoriteCourseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            loadYeuThich();
        } catch (err) {
            console.error('Lỗi khi xóa khóa học', err);
        }
    };

    const goToPage = (page) => {
        if (page >= 1 && page <= totalPages) {
            setCurrentPage(page);
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    };

    return (
        <main>
            <div className={`position-relative ${styles.headerBanner}`}>
                <div className={styles.overlay}></div>
                <div className="position-relative text-white py-5 px-5">
                    <h1 className="fw-bold">Khóa học của tôi</h1>
                    <p>Các khóa học bạn đã đăng ký và yêu thích</p>
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
                        <div className="input-group mt-5 mb-3">
                            <input
                                type="text"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                placeholder="Tên khóa học..."
                                className="fs-4 form-control"
                            />
                            <button className="btn btn-outline-secondary fs-4">
                                <FontAwesomeIcon icon={faSearch} size="lg" />
                            </button>
                        </div>

                        <div className="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4 mb-3">
                            {filteredCourses
                                .slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage)
                                .map((c) => (
                                    <CourseCard
                                        key={c.registeredCourseId}
                                        dto={c}
                                        date={c.createAt}
                                        linkTo={`/learning?courseId=${c.courseId}`}
                                    />
                                ))}
                        </div>

                        <Pagination currentPage={currentPage} totalPages={totalPages} goToPage={goToPage} />
                    </div>

                    {/* TAB 2 */}
                    <div className="tab-pane fade" id="favCourses">
                        {listYeuThich.length === 0 ? (
                            <div className="alert alert-info">Danh sách yêu thích của bạn đang trống.</div>
                        ) : (
                            <div className="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
                                {listYeuThich
                                    .slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage)
                                    .map((item) => (
                                        <FavoriteCard
                                            key={item.favoriteCourseId}
                                            dto={{
                                                courseName: item.course.name,
                                                avatar: item.course.avatar,
                                                price: item.course.price,
                                                description: item.course.description,
                                            }}
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
                                    ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </main>
    );
};

export default MyCourse;
