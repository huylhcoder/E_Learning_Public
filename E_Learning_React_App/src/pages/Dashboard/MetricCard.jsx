// MetricCard.jsx

import React from 'react';

const MetricCard = ({ data, IconComponent }) => {
    if (!data) return null;

    const { title, currentValue, percentageChange, currencyUnit } = data;

    const isPositive = percentageChange >= 0;
    const color = isPositive ? '#16a34a' : '#dc2626'; // Xanh lá đậm/Đỏ đậm
    const sign = isPositive ? '▲' : '▼';

    // Định dạng giá trị (thêm đơn vị tiền tệ nếu có)
    const formattedValue = currencyUnit
        ? `${currentValue.toLocaleString('vi-VN')} ${currencyUnit}`
        : currentValue.toLocaleString();

    return (
        <div
            style={{
                padding: '20px',
                border: '1px solid #e5e7eb',
                borderRadius: '10px',
                backgroundColor: '#ffffff',
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
            }}
        >
            {/* Tiêu đề và Icon */}
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
                <IconComponent size={24} style={{ marginRight: '10px', color: '#10b981' }} />
                <h4 style={{ margin: 0, color: '#4b5563' }}>{title}</h4>
            </div>

            {/* Giá trị hiện tại */}
            <p style={{ fontSize: '2.5em', fontWeight: '800', margin: '5px 0 10px 0', color: '#1f2937' }}>
                {formattedValue}
            </p>

            {/* Chỉ số tăng trưởng */}
            <p style={{ color: color, fontWeight: 'bold', margin: 0, fontSize: '1em' }}>
                {sign} {Math.abs(percentageChange).toFixed(2)}% so với kỳ trước
            </p>
        </div>
    );
};

export default MetricCard;
