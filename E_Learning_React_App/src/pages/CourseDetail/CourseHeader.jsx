function CourseHeader({ title, rating, comments, registered }) {
  return (
    <div className="bg-dark text-white py-4">
      <div className="container">
        <h2>Khoá học: {title}</h2>
        <div>
          <span className="text-warning me-2">
            {'★'.repeat(Math.round(rating)) + '☆'.repeat(5 - Math.round(rating))}
          </span>
          <span>({comments} đánh giá) • {registered} học viên</span>
        </div>
      </div>
    </div>
  );
}

export default CourseHeader;
