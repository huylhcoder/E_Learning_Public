import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faStar } from '@fortawesome/free-solid-svg-icons';
import { formatDate } from '~/utils/format';

const Comments = ({ comments, images }) => {
    return (
        <div className="mt-4">
            <span className="fs-3 fw-bold">Đánh giá của khóa học</span>
            {comments.map((comment) => (
                <div key={comment.id} className="border rounded-3 p-3 mb-3 bg-light">
                    <div className="d-flex align-items-start">
                        {/* Avatar */}
                        <img
                            src={comment.user.avatar || images.avatarDefault}
                            alt={comment.user.name}
                            className="rounded-circle me-3"
                            width="40"
                            height="40"
                        />

                        {/* Nội dung */}
                        <div className="flex-grow-1">
                            <span className="fs-5 fw-bold mt-1">{comment.user.name}</span>
                            {/* format ngày tại đây */}
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
    );
};

export default Comments;
