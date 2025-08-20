import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { formatDuration } from '~/utils/format';

const QuizResult = ({ result, onReset, navigate }) => {
    // if (!result || !quiz) return null;
     if (!result) return null;

    const { score, status, maxScore, completionTime, numberOfCorrectAnswer } = result;
    const passed = status;

    return (
        <div className="container">
            <div className="course-description box mt-4">
                <h3 className="text-capitalize mb-4">Kết quả bài kiểm tra</h3>

                <div className={`alert ${passed ? 'alert-success' : 'alert-danger'}`}>
                    {passed ? 'Chúc mừng! Bạn đã vượt qua bài kiểm tra.' : 'Bạn chưa vượt qua bài kiểm tra.'}
                </div>

                <table className="table table-bordered">
                    <tbody>
                        <tr>
                            <td>Số câu đúng</td>
                            <td>
                                {numberOfCorrectAnswer}
                            </td>
                        </tr>
                        <tr>
                            <td>Điểm số</td>
                            <td>{score.toFixed(1)} / 10.0</td>
                        </tr>
                        <tr>
                            <td>Thời gian hoàn thành</td>
                            <td>{formatDuration(Math.round(completionTime))}</td>
                        </tr>
                        <tr>
                            <td>Điểm cao nhất (≥ 9: Pass)</td>
                            <td>{maxScore.toFixed(1)} / 10.0</td>
                        </tr>
                        <tr>
                            <td>Trạng thái</td>
                            <td>{passed ? 'Đạt' : 'Không đạt'}</td>
                        </tr>
                    </tbody>
                </table>

                <div className="mt-3">
                    <button onClick={() => navigate(-1)} className="btn btn-secondary me-2">
                        <FontAwesomeIcon icon={faArrowLeft} /> Quay lại
                    </button>
                    <button onClick={onReset} className="btn btn-warning">
                        Làm lại
                    </button>
                </div>
            </div>
        </div>
    );
};

export default QuizResult;
