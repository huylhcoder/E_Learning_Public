import React, { useState, useEffect } from 'react';
import axios from '~/utils/CustomizeAxios';

const CourseStatisticsTable = () => {
    const [stats, setStats] = useState([]);
    const [sortBy, setSortBy] = useState('NEWEST');
    const [loading, setLoading] = useState(false);

    const sortOptions = [
        { label: 'Khóa học mới nhất', value: 'NEWEST' },
        { label: 'Doanh thu cao nhất', value: 'REVENUE_DESC' },
        { label: 'Cấp chứng chỉ nhiều nhất', value: 'CERTIFICATE_DESC' },
    ];

    const token = localStorage.getItem('jwtToken'); // Lấy Token

    const fetchStatistics = async (sortValue) => {
        setLoading(true);
        try {
            const response = await axios.get('/dashboard/course-statistics', {
                params: { sortBy: sortValue },
                headers: { Authorization: `Bearer ${token}` },
            });
            setStats(response.data);
        } catch (error) {
            console.error('Lỗi khi tải thống kê khóa học:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleExport = () => {
        // Gọi API Export Excel
        axios
            .get('/dashboard/course-statistics/export-excel', {
                params: { sortBy: sortBy },
                headers: { Authorization: `Bearer ${token}` },
                responseType: 'blob', // Rất quan trọng để nhận dạng file
            })
            .then((response) => {
                // Tạo URL tạm thời cho file blob
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                // Lấy tên file từ header (nếu backend trả về) hoặc đặt tên mặc định
                link.setAttribute('download', 'thong_ke_doanh_thu_khoa_hoc.xlsx');
                document.body.appendChild(link);
                link.click();
                link.remove();
                window.URL.revokeObjectURL(url);
            })
            .catch((error) => console.error('Lỗi khi xuất Excel:', error));
    };

    useEffect(() => {
        fetchStatistics(sortBy);
    }, [sortBy]);

    return (
        <div
            style={{
                padding: '20px',
                backgroundColor: '#fff',
                borderRadius: '10px',
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
            }}
        >
            <h3>📊 Thống Kê Doanh Thu Khóa Học</h3>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                {/* Bộ lọc */}
                <select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value)}
                    style={{ padding: '8px', borderRadius: '4px' }}
                >
                    {sortOptions.map((opt) => (
                        <option key={opt.value} value={opt.value}>
                            {opt.label}
                        </option>
                    ))}
                </select>

                {/* Nút Xuất Excel */}
                <button
                    onClick={handleExport}
                    style={{
                        padding: '8px 15px',
                        backgroundColor: '#10b981',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer',
                    }}
                >
                    Xuất Excel
                </button>
            </div>

            {loading ? (
                <div>Đang tải dữ liệu bảng...</div>
            ) : (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ backgroundColor: '#f3f4f6' }}>
                            <th style={tableHeaderStyle}>ID</th>
                            <th style={tableHeaderStyle}>Tên Khóa Học</th>
                            <th style={tableHeaderStyle}>Ngày Tạo</th>
                            <th style={tableHeaderStyle}>Tổng Doanh Thu</th>
                            <th style={tableHeaderStyle}>Lượt Đăng Ký</th>
                            <th style={tableHeaderStyle}>Số Chứng Chỉ</th>
                        </tr>
                    </thead>
                    <tbody>
                        {stats.map((course) => (
                            <tr key={course.courseId} style={tableRowStyle}>
                                <td style={tableCellStyle}>{course.courseId}</td>
                                <td style={tableCellStyle}>{course.courseName}</td>
                                <td style={tableCellStyle}>{new Date(course.createAt).toLocaleDateString()}</td>
                                <td style={{ ...tableCellStyle, fontWeight: 'bold' }}>
                                    {course.totalRevenue.toLocaleString('vi-VN')} VNĐ
                                </td>
                                <td style={tableCellStyle}>{course.registrationCount.toLocaleString()}</td>
                                <td style={tableCellStyle}>{course.certificateCount.toLocaleString()}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

// CSS styles đơn giản cho bảng
const tableHeaderStyle = { padding: '10px', borderBottom: '2px solid #e5e7eb', textAlign: 'left' };
const tableCellStyle = { padding: '10px', borderBottom: '1px solid #e5e7eb' };
const tableRowStyle = { transition: 'background-color 0.2s' }; // Thêm hiệu ứng hover nếu cần

// Thêm dòng này ở cuối file:
export default React.memo(CourseStatisticsTable);
