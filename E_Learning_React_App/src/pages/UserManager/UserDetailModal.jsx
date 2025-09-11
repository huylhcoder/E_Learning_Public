import { Modal, Button } from 'react-bootstrap';
import classNames from 'classnames/bind';
import styles from './UserManager.module.scss';

const cx = classNames.bind(styles);

export default function UserDetailModal({ show, onClose, courses }) {
    
    return (
        <Modal show={show} onHide={onClose} size="lg" centered>
            <Modal.Header closeButton>
                <Modal.Title>Tiến độ khóa học</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {courses.length === 0 ? (
                    <div className="text-center">Người dùng chưa đăng ký khóa học nào.</div>
                ) : (
                    <table className={`table ${cx('modernTable', 'styledTable')}`}>
                        <thead>
                            <tr>
                                <th>Khóa học đăng ký</th>
                                <th>Tổng số bài học</th>
                                <th>Tổng số bài kiếm tra</th>
                                <th>Tiến độ</th>                               
                            </tr>
                        </thead>
                        <tbody>
                            {courses.map((c, i) => (
                                <tr key={i}>
                                    <td>{c.course?.name}</td>
                                    <td>{c.totalLessionComplete}/{c.totalLession} Bài</td>
                                    <td>{c.totalTestComplete}/{c.totalQuiz} Bài</td>
                                    <td>{c?.progressPercentage.toFixed(2)}%</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onClose}>
                    Đóng
                </Button>
            </Modal.Footer>
        </Modal>
    );
}
