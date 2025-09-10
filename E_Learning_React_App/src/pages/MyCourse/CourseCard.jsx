import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBookOpen, faCheckCircle, faPlay } from '@fortawesome/free-solid-svg-icons';
import styles from './MyCourse.module.scss';

const CourseCard = ({ dto, date, linkTo, actionBtn }) => {
    const progress = dto.progressPercentage || 0;

    let statusText = 'Bắt đầu học';
    let statusColor = 'text-secondary';
    let progressBar = 'bg-secondary';
    let statusIcon = faPlay;

    if (progress > 0 && progress < 100) {
        statusText = 'Đang học';
        statusColor = 'text-primary';
        progressBar = 'bg-primary';
        statusIcon = faBookOpen;
    } else if (progress === 100) {
        statusText = 'Hoàn thành';
        statusColor = 'text-success';
        progressBar = 'bg-success';
        statusIcon = faCheckCircle;
    }

    return (
        <div className="col">
            <a href={linkTo} className={`card h-100 text-decoration-none ${styles.courseCard}`}>
                <img src={dto.avatar} className={styles.courseImage} alt={dto.courseName} />
                <div className="card-body">
                    <h6 className={`fw-bold mb-2 ${styles.textTruncate}`}>{dto.courseName}</h6>

                    <div className="d-flex justify-content-between align-items-center mb-2">
                        <span className={`small fw-semibold ${statusColor}`}>
                            <FontAwesomeIcon icon={statusIcon} className="me-1" />
                            {statusText}
                        </span>
                        <span className="small">{progress.toFixed(2)}%</span>
                    </div>

                    <div className="progress" style={{ height: '6px' }}>
                        <div
                            className={`progress-bar ${progressBar}`}
                            role="progressbar"
                            style={{ width: `${progress}%` }}
                        />
                    </div>

                    {date && (
                        <small className="text-muted d-block mt-2">
                            Ngày đăng ký: {new Date(date).toLocaleDateString('vi-VN')}
                        </small>
                    )}
                </div>

                {actionBtn && <div className="card-footer bg-white border-0">{actionBtn}</div>}
            </a>
        </div>
    );
};

export default CourseCard;
