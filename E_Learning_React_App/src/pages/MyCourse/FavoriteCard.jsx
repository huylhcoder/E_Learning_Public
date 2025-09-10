import React from 'react';
import styles from './MyCourse.module.scss';

const FavoriteCard = ({ dto, linkTo, actionBtn }) => {
    return (
        <div className="col">
            <a href={linkTo} className={`card h-100 text-decoration-none ${styles.courseCard}`}>
                <img src={dto.avatar} className={styles.courseImage} alt={dto.courseName} />
                <div className="card-body">
                    <h6 className={`fw-bold mb-2 ${styles.textTruncate}`}>{dto.courseName}</h6>
                    <p className="mb-2 fw-semibold text-success">
                        {dto.price?.toLocaleString('vi-VN')} đ
                    </p>
                    {dto.description && (
                        <p className="mb-2 text-muted">Mô tả ngắn: {dto.description}</p>
                    )}
                </div>

                {actionBtn && <div className="card-footer bg-white border-0">{actionBtn}</div>}
            </a>
        </div>
    );
};

export default FavoriteCard;
