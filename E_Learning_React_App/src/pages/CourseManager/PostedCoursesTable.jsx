import React from 'react';
import { Link } from 'react-router-dom';

const formatDuration = (duration) => {
  const hours = Math.floor(duration / 3600);
  const minutes = Math.floor((duration % 3600) / 60);
  const seconds = Math.round(duration % 60);

  if (hours === 0 && minutes === 0) return `${seconds} giây`;
  if (hours > 0) return `${hours} giờ ${minutes} phút ${seconds} giây`;
  return `${minutes} phút ${seconds} giây`;
};

const PostedCoursesTable = ({ list }) => {
  return (
    <table className="table table-hover mt-3">
      <thead>
        <tr>
          <th>Video</th>
          <th>Tên Video</th>
          <th>Chế độ hiển thị</th>
          <th>Thời lượng</th>
          <th>Ngày</th>
          <th>Số bình luận</th>
          <th>Doanh thu</th>
        </tr>
      </thead>
      <tbody>
        {list.map((item) => (
          <tr key={item.courseId}>
            <td>
              <img
                src={
                  item.avatar ||
                  'http://res.cloudinary.com/dxj6jmdm8/image/upload/v1731139264/nmq2pksknbgt87tk2zbn.png'
                }
                alt="Video thumbnail"
                className="me-2 img-fluid"
                style={{ width: '100px', height: '100px' }}
              />
            </td>
            <td>
              <div>
                <div className="video-title tt">{item.name}</div>
                <div className="video-actions float-start">
                  <Link
                    className="btn btn-sm btn-light me-1"
                    to={`/admin/course-detail-manager/${item.courseId}`}
                  >
                    <i className="fa-solid fa-pen"></i> Chi tiết
                  </Link>
                  <Link
                    className="btn btn-sm btn-light"
                    to={`/admin/course-detail-manager/${item.courseId}`}
                  >
                    <i className="fa-regular fa-comment-dots"></i> Xem bình luận
                  </Link>
                </div>
              </div>
            </td>
            <td>
              {item.status === 1 && (
                <p className="badge bg-success">Công khai</p>
              )}
              {item.status === 2 && (
                <p className="badge bg-warning text-dark">Không công khai</p>
              )}
            </td>
            <td>
              {item.courseDuration === 0 ? (
                <span className="badge rounded-pill bg-warning text-dark">
                  Chưa có video
                </span>
              ) : (
                <>
                  {formatDuration(item.courseDuration)}{' '}
                  <i className="fa fa-clock me-1"></i>
                </>
              )}
            </td>
            <td>
              {new Date(item.createAt).toLocaleDateString('vi-VN')}
              <br />
              <small>Ngày tải lên</small>
            </td>
            <td>{item.numberOfComment}</td>
            <td>{item.revenue} VNĐ</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default React.memo(PostedCoursesTable);
