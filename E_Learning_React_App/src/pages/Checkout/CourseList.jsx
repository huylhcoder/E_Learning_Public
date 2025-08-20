const CourseList = ({ courses }) => (
  <table className="table">
    <thead>
      <tr>
        <th>Khóa học</th>
        <th>Tên khóa học</th>
        <th>Đơn giá</th>
      </tr>
    </thead>
    <tbody>
      {courses.map((course) => (
        <tr key={course.courseId}>
          <td>
            <img
              src={course.avatar}
              alt="avatar"
              className="img-cart"
              style={{ width: '80px', height: '80px', objectFit: 'cover', borderRadius: '8px' }}
            />
          </td>
          <td><strong>{course.name}</strong></td>
          <td>{course.price.toLocaleString('vi-VN')} VND</td>
        </tr>
      ))}
    </tbody>
  </table>
);

export default CourseList;
