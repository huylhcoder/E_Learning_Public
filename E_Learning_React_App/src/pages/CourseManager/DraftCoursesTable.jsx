import React from 'react';

const formatDuration = (duration) => {
  const hours = Math.floor(duration / 3600);
  const minutes = Math.floor((duration % 3600) / 60);
  const seconds = Math.round(duration % 60);

  if (hours === 0 && minutes === 0) return `${seconds} giây`;
  if (hours > 0) return `${hours} giờ ${minutes} phút ${seconds} giây`;
  return `${minutes} phút ${seconds} giây`;
};

const DraftCoursesTable = ({ list, onRemove }) => (
  <table className="table table-hover mt-3">
    <thead>
      <tr>
        <th>Video</th>
        <th>Tên khóa học</th>
        <th>Chế độ hiển thị</th>
        <th>Thời lượng</th>
        <th>Ngày</th>
        <th>Hành động</th>
      </tr>
    </thead>
    <tbody>
      {list.map((item) => (
        <tr key={item.courseId}>
          <td>
            <img
              src={item.avatar ||
                'http://res.cloudinary.com/dxj6jmdm8/image/upload/v1731139264/nmq2pksknbgt87tk2zbn.png'}
              alt="Video thumbnail"
              className="me-2 img-fluid"
              style={{ width: '100px', height: '100px' }}
            />
          </td>
          <td>{item.name}</td>
          <td><span className="badge bg-secondary">Bản nháp</span></td>
          <td>
            {item.courseDuration === 0 ? (
              <span className="badge rounded-pill bg-warning text-dark">Chưa có video</span>
            ) : (
              <>
                {formatDuration(item.courseDuration)} <i className="fa fa-clock me-1"></i>
              </>
            )}
          </td>
          <td>
            {new Date(item.createAt).toLocaleDateString('vi-VN')}
            <br />
            <small>Ngày tải lên</small>
          </td>
          <td>
            <button className="btn btn-danger btn-sm me-2" onClick={() => onRemove(item.courseId)}>
              Xóa
            </button>
            <a
              className="btn btn-warning btn-sm"
              href={`#!assets/views/admin/course_manager_detail/${item.courseId}`}
            >
              <i className="fa-solid fa-pen"></i> Chi tiết
            </a>
          </td>
        </tr>
      ))}
    </tbody>
  </table>
);

export default React.memo(DraftCoursesTable);
