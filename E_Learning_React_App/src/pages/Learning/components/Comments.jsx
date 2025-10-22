import { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faStar } from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';
import axios from '~/utils/CustomizeAxios';
import images from '~/assets/images';
import { formatDate } from '~/utils/format';

const Comments = ({ courseId, token }) => {
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');
    const [rating, setRating] = useState(0);
    const [submitting, setSubmitting] = useState(false);
    const [userComments, setUserComments] = useState([]); // ✅ bình luận của user hiện tại

    useEffect(() => {
        if (courseId && token) {
            loadComments();
            checkUserComment();
        }
    }, [courseId, token]);

    // Load tất cả comment của khóa học
    const loadComments = async () => {
        try {
            const response = await axios.get(`/comment/course/${courseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            // Sắp xếp theo ngày mới nhất
            const sortedComments = response.data.sort((a, b) => new Date(b.createAt) - new Date(a.createAt));
            setComments(sortedComments);
        } catch (error) {
            console.error('Lỗi khi tải đánh giá:', error);
        }
    };

    // ✅ Check user đã comment chưa + lấy comment của họ
    const checkUserComment = async () => {
        try {
            const response = await axios.get(`/comment/check/${courseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            setUserComments(response.data); // lưu list comment của user
        } catch (error) {
            console.error('Lỗi khi check user comment:', error);
        }
    };

    const handleSubmit = async () => {
        if (!newComment || !rating) {
            toast.warn('Vui lòng nhập đánh giá và nhận xét.');
            return;
        }

        try {
            setSubmitting(true);
            await axios.post(
                `/comment`,
                { courseId, content: newComment, starRating: rating },
                { headers: { Authorization: `Bearer ${token}` } },
            );

            toast.success('Đánh giá của bạn đã được gửi.');
            setNewComment('');
            setRating(0);

            loadComments();
            checkUserComment();
        } catch (error) {
            console.error('Lỗi khi gửi đánh giá:', error);
            toast.error('Không thể gửi đánh giá, vui lòng thử lại.');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="mt-4">
            <p className="text-primary fs-2 fw-bold mb-3">Đánh giá khóa học</p>

            {/* ✅ Chỉ hiển thị form nếu user chưa comment */}
            {userComments.length === 0 && (
                <div className="mb-4">
                    {/* Rating stars */}
                    <div className="mb-3">
                        {[1, 2, 3, 4, 5].map((star) => (
                            <FontAwesomeIcon
                                key={star}
                                icon={faStar}
                                className={`me-1 fs-4 ${rating >= star ? 'text-warning' : 'text-secondary'}`}
                                onClick={() => setRating(star)}
                                style={{ cursor: 'pointer' }}
                            />
                        ))}
                    </div>

                    {/* Comment box */}
                    <div className="mb-3">
                        <textarea
                            className="form-control rounded-3 p-3 fs-4"
                            rows="3"
                            placeholder="Chia sẻ cảm nhận của bạn về khóa học..."
                            value={newComment}
                            onChange={(e) => setNewComment(e.target.value)}
                        ></textarea>
                    </div>

                    <button className="btn btn-primary px-3 fs-4 mb-3" onClick={handleSubmit} disabled={submitting}>
                        {submitting ? 'Đang gửi...' : 'Gửi đánh giá'}
                    </button>
                </div>
            )}

            {/* ✅ Bình luận của bạn */}
            {userComments.length > 0 && (
                <div className="mb-4">
                    <p className="fs-5 fw-bold">Đánh giá của bạn</p>
                    {userComments.map((comment) => (
                        <div
                            key={comment.commentId}
                            className="border border-success rounded-3 p-3 mb-3 bg-white shadow-sm"
                        >
                            <div className="d-flex align-items-start">
                                <img
                                    src={comment?.user?.urlProfileImage || images.avatarDefault}
                                    alt={comment?.user?.name}
                                    className="rounded-circle me-3"
                                    width="40"
                                    height="40"
                                />
                                <div className="flex-grow-1">
                                    <span className="fs-5 fw-bold text-success">{comment?.user?.name} (Bạn)</span>
                                    <p className="text-muted mb-0">{formatDate(comment.createAt)}</p>
                                    <div className="mt-1">
                                        {[...Array(comment.starRating)].map((_, i) => (
                                            <FontAwesomeIcon key={i} icon={faStar} className="text-warning fs-5" />
                                        ))}
                                    </div>
                                    <p className="mt-1 mb-0">{comment.content}</p>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {/* Comments list */}
            <div className="mt-4">
                <p className="fs-5 fw-bold">Tất cả đánh giá</p>
                {comments.map((comment) => (
                    <div key={comment.commentId} className="border rounded-3 p-3 mb-3 bg-light">
                        <div className="d-flex align-items-start">
                            <img
                                src={comment.user.urlProfileImage || images.avatarDefault}
                                alt={comment.user.name}
                                className="rounded-circle me-3"
                                width="40"
                                height="40"
                            />
                            <div className="flex-grow-1">
                                <span className="fs-5 fw-bold mt-1">{comment.user.name}</span>
                                <p className="text-muted mb-0">{formatDate(comment.createAt)}</p>
                                <div className="mt-1">
                                    {[...Array(comment.starRating)].map((_, i) => (
                                        <FontAwesomeIcon key={i} icon={faStar} className="text-warning fs-5" />
                                    ))}
                                </div>
                                <p className="text-muted mt-1 mb-0">{comment.content}</p>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Comments;
