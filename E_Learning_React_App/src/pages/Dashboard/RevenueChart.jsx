// RevenueChart.jsx
import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const RevenueChart = ({ data }) => {
    console.log(data);
    
    if (!data || data.length === 0)
        return (
            <div
                style={{
                    height: 350,
                    textAlign: 'center',
                    paddingTop: '100px',
                    border: '1px solid #e5e7eb',
                    borderRadius: '10px',
                }}
            >
                Không có dữ liệu doanh thu để hiển thị.
            </div>
        );

    // Hàm định dạng Tooltip
    const formatTooltip = (value) => [`${value.toLocaleString('vi-VN')} VNĐ`, 'Doanh Thu'];

    return (
        <div
            style={{
                width: '100%',
                height: 350,
                marginTop: '30px',
                border: '1px solid #e5e7eb',
                borderRadius: '10px',
                padding: '20px',
                backgroundColor: '#ffffff',
            }}
        >
            <h3>📈 Biểu Đồ Tăng Trưởng Doanh Thu</h3>
            <ResponsiveContainer width="100%" height="90%">
                <LineChart data={data} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#f3f4f6" />
                    <XAxis dataKey="label" stroke="#6b7280" />
                    <YAxis
                        tickFormatter={(value) => value.toLocaleString('vi-VN')} // Định dạng trục Y
                        stroke="#6b7280"
                    />
                    <Tooltip formatter={formatTooltip} />
                    <Legend />
                    <Line
                        type="monotone"
                        dataKey="value"
                        name="Doanh Thu"
                        stroke="#34d399" // Màu xanh lá đẹp
                        strokeWidth={2}
                        activeDot={{ r: 6 }}
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
};

export default RevenueChart;
