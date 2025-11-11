import React, { useState, useEffect } from 'react';
import axios from '~/utils/CustomizeAxios';
import MetricCard from './MetricCard';
import RevenueChart from './RevenueChart';
import CourseStatisticsTable from './CourseStatisticsTable';
import { FaGraduationCap, FaDollarSign, FaUsers, FaBook } from 'react-icons/fa'; // Ví dụ icons

const Dashboard = () => {
    const [filter, setFilter] = useState('LAST_MONTH');
    const [summary, setSummary] = useState(null);
    const [loading, setLoading] = useState(true);

    const filterOptions = [
        { label: 'Hôm qua', value: 'YESTERDAY' },
        { label: 'Tuần trước', value: 'LAST_WEEK' },
        { label: 'Tháng trước', value: 'LAST_MONTH' },
        { label: 'Năm trước', value: 'LAST_YEAR' },
        { label: 'Tất cả', value: 'ALL' },
    ];

    const fetchDashboardData = async (currentFilter) => {
        setLoading(true);
        const token = localStorage.getItem('token'); // THAY THẾ 'jwtToken' bằng key chính xác

        if (!token) {
            console.error('Không có Token. Vui lòng đăng nhập.');
            setLoading(false);
            return;
        }

        try {
            const response = await axios.get('/dashboard/summary', {
                // Đảm bảo đúng endpoint
                params: { filter: currentFilter },
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            setSummary(response.data);
        } catch (error) {
            console.error('Lỗi khi gọi API Dashboard:', error);
            if (error.response && error.response.status === 401) {
                alert('Phiên làm việc đã hết hạn.');
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDashboardData(filter);
    }, [filter]);

    if (loading) return <div style={{ padding: '50px', textAlign: 'center' }}>Đang tải dữ liệu...</div>;
    if (!summary) return <div style={{ padding: '50px', textAlign: 'center' }}>Không có dữ liệu tổng quan.</div>;

    return (
        <div style={{ padding: '20px' }}>
            <h2>Tổng Quan Hệ Thống</h2>

            {/* Khu vực Lọc Ngày/Tuần/Tháng (sẽ kích hoạt re-render của Dashboard) */}
            <div style={{ marginBottom: '20px' }}>
                <label className='me-3'>Lọc theo thời gian: </label>
                <select
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                    style={{ padding: '8px', borderRadius: '4px' }}
                >
                    {/* ... options ... */}
                    {filterOptions.map((opt) => (
                        <option key={opt.value} value={opt.value}>
                            {opt.label}
                        </option>
                    ))}
                </select>
            </div>

            {/* Khu vực Metric Cards và Biểu đồ (Cần re-render khi filter thay đổi) */}
            <div
                style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
                    gap: '20px',
                    marginBottom: '40px',
                }}
            >
                {/* ... MetricCard components ... */}
                <MetricCard data={summary.totalRevenue} IconComponent={FaDollarSign} />
                <MetricCard data={summary.newUsers} IconComponent={FaUsers} />
                <MetricCard data={summary.completedCourses} IconComponent={FaGraduationCap} />
                <MetricCard data={summary.newCourses} IconComponent={FaBook} />
            </div>

            {summary.revenueChartData && <RevenueChart data={summary.revenueChartData} />}

            {/* Thêm một đường phân cách */}
            <hr style={{ margin: '40px 0', border: 'none', borderTop: '1px dashed #ccc' }} />

            {/* KHU VỰC BẢNG THỐNG KÊ (Không bị ảnh hưởng bởi state 'filter') */}
            {/* Vì CourseStatisticsTable không nhận props nào liên quan đến 'filter', 
                nó sẽ không re-render khi 'filter' thay đổi (nhờ React.memo) */}
            <CourseStatisticsTable />
        </div>
    );
};

export default Dashboard;
