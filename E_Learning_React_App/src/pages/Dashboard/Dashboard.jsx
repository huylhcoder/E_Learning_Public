// src/pages/Statistical.jsx
import React, { useEffect, useState } from 'react';
import axios from '~/utils/CustomizeAxios';
import { FaFire, FaFileExport, FaChevronDown, FaChevronUp, FaCircleInfo } from 'react-icons/fa';
// import 'bootstrap/dist/css/bootstrap.min.css';
// import 'bootstrap/dist/js/bootstrap.bundle.min.js';

const Dashboard = () => {
    const tokenLogin = localStorage.getItem('token');

    const [listKhoaHoc, setListKhoaHoc] = useState([]);
    const [listNguoiDung, setListNguoiDung] = useState([]);
    const [listKhoaHocDaBan, setListKhoaHocDaBan] = useState([]);
    const [listChiTietNguoiDung, setListChiTietNguoiDung] = useState([]);

    const [totalRevenue, setTotalRevenue] = useState(0);
    const [totalCourses, setTotalCourses] = useState(0);
    const [totalUser, setTotalUser] = useState(0);
    const [totalCoursesComplete, setTotalCoursesComplete] = useState(0);
    const [totalRevenueFormatted, setTotalRevenueFormatted] = useState('0 VND');

    const [itemsToShow, setItemsToShow] = useState(10);
    const [itemsToShowUser, setItemsToShowUser] = useState(10);

    // ---- API CALLS ----
    const loadKhoaHoc = async () => {
        try {
            const resp = await axios.get('course', {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
            setListKhoaHoc(resp.data);
            setTotalCourses(resp.data.length);
        } catch (err) {
            console.error('Lỗi load khóa học', err);
        }
    };

    const loadNguoiDung = async () => {
        try {
            const resp = await axios.get(`course-progress/user-admin`, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
            setListNguoiDung(resp.data);
            setTotalUser(resp.data.length);
        } catch (err) {
            console.error('Lỗi load người dùng', err);
        }
    };

    const loadKhoaHocHoanThanh = async () => {
        try {
            const resp = await axios.get(`course-progress/total-complete`, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
            setTotalCoursesComplete(resp.data.length);
        } catch (err) {
            console.error('Lỗi load khóa học hoàn thành', err);
        }
    };

    const loadDoanhThu = async () => {
        try {
            const resp = await axios.get(`course-progress/total-revenue`, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
            const sum = resp.data.reduce((s, c) => s + (c.price || 0), 0);
            setTotalRevenue(sum);
            setTotalRevenueFormatted(sum.toLocaleString('vi-VN') + ' VND');
        } catch (err) {
            console.error('Lỗi load doanh thu', err);
        }
    };

    // ---- INIT ----
    useEffect(() => {
        loadKhoaHoc();
        loadNguoiDung();
        loadKhoaHocHoanThanh();
        loadDoanhThu();
    }, []);

    return (
        <main>
            {/* Thanh tổng hợp */}
            <div className="py-3">
                <div className="container">
                    <div className="row">
                        <div className="col-md-3 mb-4">
                            <div className="summary-card text-white p-3 rounded" style={{ backgroundColor: '#6f42c1' }}>
                                <div className="card-change">Tổng Doanh Thu</div>
                                <div className="card-value fw-bolder">{totalRevenueFormatted}</div>
                            </div>
                        </div>
                        <div className="col-md-3 mb-4">
                            <div className="summary-card text-white p-3 rounded" style={{ backgroundColor: '#0d6efd' }}>
                                <div className="card-change">Tổng số khóa học</div>
                                <div className="card-value fw-bolder">{totalCourses} khóa học</div>
                            </div>
                        </div>
                        <div className="col-md-3 mb-4">
                            <div className="summary-card text-white p-3 rounded" style={{ backgroundColor: '#0dcaf0' }}>
                                <div className="card-change">Tổng số người dùng</div>
                                <div className="card-value fw-bolder">{totalUser} học viên</div>
                            </div>
                        </div>
                        <div className="col-md-3 mb-4">
                            <div className="summary-card text-white p-3 rounded" style={{ backgroundColor: '#fd7e14' }}>
                                <div className="card-change">Số chứng chỉ đã cấp</div>
                                <div className="card-value fw-bolder">{totalCoursesComplete}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Danh sách khóa học */}
            <div className="container py-3">
                <h5 className="fw-bold">Thông tin khóa học</h5>
                <div className="card">
                    <div className="card-body">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>STT</th>
                                    <th>Mã khóa học</th>
                                    <th>Tên khóa học</th>
                                    <th>Số bài học</th>
                                    <th>Giá</th>
                                    <th>Số người theo dõi</th>
                                </tr>
                            </thead>
                            <tbody>
                                {listKhoaHoc.slice(0, itemsToShow).map((c, idx) => (
                                    <tr key={c.courseId}>
                                        <td className="text-center">{idx + 1}</td>
                                        <td className="text-center">{c.courseId}</td>
                                        <td>{c.name}</td>
                                        <td className="text-center">{c.numberOfLesson}</td>
                                        <td className="float-end">
                                            {c.price === 0 ? 'Miễn phí' : c.price.toLocaleString('vi-VN') + ' VND'}
                                        </td>
                                        <td className="text-center">{c.follow}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        <div className="text-center">
                            {itemsToShow < listKhoaHoc.length ? (
                                <button
                                    className="btn btn-outline-primary fs-5 p-2"
                                    onClick={() => setItemsToShow(listKhoaHoc.length)}
                                >
                                    Xem thêm <FaChevronDown />
                                </button>
                            ) : (
                                listKhoaHoc.length > 10 && (
                                    <button className="btn btn-secondary fs-5 p-2" onClick={() => setItemsToShow(10)}>
                                        Thu gọn <FaChevronUp />
                                    </button>
                                )
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </main>
    );
};

export default Dashboard;
