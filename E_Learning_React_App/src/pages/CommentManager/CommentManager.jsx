// Libs
import { useEffect, useState } from 'react';
import axios from '~/utils/CustomizeAxios';
import { toast } from 'react-toastify';
import { Button, Modal } from 'react-bootstrap';
import { FaReply, FaUnlock, FaStar } from 'react-icons/fa';

// Styles
import styles from './CommentManager.module.scss';
import { formatDate } from '~/utils/format';

export default function CommentManager() {
    const [listDanhGia, setListDanhGia] = useState([]);
    const [disabledReplies, setDisabledReplies] = useState(JSON.parse(localStorage.getItem('disabledReplies')) || {});
    const [showModal, setShowModal] = useState(false);
    const [selectedComment, setSelectedComment] = useState(null);
    const [replyContent, setReplyContent] = useState('');

    const base_url = '/comment'; // axios đã set baseURL
    const tokenLogin = localStorage.getItem('token');

    // Load feedback list
    const loadDanhSachDanhGia = async () => {
        try {
            const resp = await axios.get(base_url, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });

            const data = resp.data;
            setListDanhGia(data);

            const updatedDisabled = { ...disabledReplies };
            data.forEach((comment) => {
                if (comment.isReplied) {
                    updatedDisabled[comment.commentId] = true;
                }
            });
            setDisabledReplies(updatedDisabled);
            localStorage.setItem('disabledReplies', JSON.stringify(updatedDisabled));
        } catch (err) {
            console.error('Lỗi không thể tải dữ liệu', err);
        }
    };

    useEffect(() => {
        loadDanhSachDanhGia();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // Render stars
    const getStars = (rating) => {
        const numericRating = Number(rating);
        return [...Array(5)].map((_, i) => (
            <span key={i} className={i < numericRating ? styles.starFilled : styles.star}>
                ★
            </span>
        ));
    };

    // Change status
    const changeStatus = async (commentId) => {
        try {
            const resp = await axios.put(`${base_url}/changeStatus/${commentId}`, null, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });

            const updatedComment = resp.data;
            setListDanhGia((prev) =>
                prev.map((item) => (item.commentId === commentId ? { ...item, status: updatedComment.status } : item)),
            );

            toast.success('Bình luận đã được duyệt!');
        } catch (err) {
            console.error('Lỗi không thể cập nhật trạng thái', err);
            toast.error('Lỗi khi duyệt bình luận');
        }
    };

    // Open reply modal
    const addReplyComment = (comment) => {
        setSelectedComment(comment);
        setReplyContent('');
        setShowModal(true);
    };

    // Submit reply
    const submitReply = async () => {
        if (!replyContent.trim()) {
            toast.error('Vui lòng nhập nội dung phản hồi!');
            return;
        }

        try {
            const reply = {
                userId: 1,
                commentId: selectedComment.commentId,
                content: replyContent,
                replyStatus: true,
            };

            await axios.post(`/api/reply/${selectedComment.commentId}`, reply, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });

            const updatedDisabled = { ...disabledReplies, [selectedComment.commentId]: true };
            setDisabledReplies(updatedDisabled);
            localStorage.setItem('disabledReplies', JSON.stringify(updatedDisabled));

            toast.success('Đã phản hồi bình luận!');
            setShowModal(false);
        } catch (err) {
            console.error('Lỗi khi thêm reply:', err);
            toast.error('Lỗi khi phản hồi bình luận');
        }
    };

    return (
        <div className="content-section">
            <h1 className="mb-3">Danh sách Đánh giá</h1>
            <table className="table table-striped table-hover">
                <thead className="table-dark">
                    <tr>
                        <th># Người dùng</th>
                        <th>Khóa học</th>
                        <th>Nội dung</th>
                        <th>Ngày</th>
                        <th>Đánh giá</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    {listDanhGia.map((item) => (
                        <tr key={item.commentId}>
                            <td>{item.user?.email}</td>
                            <td>{item.course?.name}</td>
                            <td className={styles.textTruncateCell} title={item.content}>
                                {item.content}
                            </td>
                            <td>{formatDate(item.createAt)}</td>
                            <td>{getStars(item.starRating)}</td>
                            <td>
                                <span
                                    className={item.status ? 'text-success fw-bold fs-6' : 'text-danger fw-bold fs-6'}
                                >
                                    {item.status ? 'Đã duyệt' : 'Chưa duyệt'}
                                </span>
                            </td>
                            <td>
                                <Button
                                    variant="primary"
                                    size="sm"
                                    className="btn btn-primary fs-5 me-2"
                                    onClick={() => addReplyComment(item)}
                                    disabled={disabledReplies[item.commentId]}
                                >
                                    <FaReply className="me-1" /> Phản hồi
                                </Button>
                                <Button
                                    className="btn btn-success fs-5"
                                    size="sm"
                                    onClick={() => changeStatus(item.commentId)}
                                    disabled={item.status}
                                >
                                    <FaUnlock className="me-1" /> Duyệt
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {/* Modal */}
            <Modal show={showModal} onHide={() => setShowModal(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Phản hồi bình luận</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <div className="mb-2">
                        <label className="form-label">Nội dung bình luận:</label>
                        <p className="form-control" style={{ whiteSpace: 'normal', wordWrap: 'break-word' }}>
                            {selectedComment?.content}
                        </p>
                    </div>
                    <div className="mb-3">
                        <label className="col-form-label">Nội dung phản hồi:</label>
                        <textarea
                            className="form-control"
                            value={replyContent}
                            onChange={(e) => setReplyContent(e.target.value)}
                        />
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>
                        Đóng
                    </Button>
                    <Button variant="primary" onClick={submitReply}>
                        Gửi
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}
