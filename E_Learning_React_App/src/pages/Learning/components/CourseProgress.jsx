import React from 'react';
import { FaAward } from 'react-icons/fa';
import style from '../Learning.module.scss';

const CourseProgress = ({ progressPercentage, onReceiveCertificate, isProcessing }) => (
    <div className="card mb-3">
        <div className="card-body">
            {/* Hàng chứa text và nút */}
            <div className="d-flex justify-content-between align-items-center mb-3">
                <span className="text-primary mb-0 fs-3 fs-bold">Tiến độ khóa học: {progressPercentage.toFixed(2)}%</span>

                {progressPercentage >= 100 && (
                    <button
                        className="btn btn-primary d-flex align-items-center gap-2 fs-4 fs-bold"
                        onClick={onReceiveCertificate}
                        disabled={isProcessing}
                    >
                        <FaAward />
                        Nhận chứng chỉ
                    </button>
                )}
            </div>

            {/* Thanh tiến độ */}
            <div className="progress">
                <div
                    className="progress-bar bg-primary"
                    role="progressbar"
                    style={{ width: `${progressPercentage}%` }}
                    aria-valuenow={progressPercentage}
                    aria-valuemin="0"
                    aria-valuemax="100"
                ></div>
            </div>
        </div>
    </div>
);

export default CourseProgress;
