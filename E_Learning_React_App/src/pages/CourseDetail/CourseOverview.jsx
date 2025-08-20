function CourseOverview({ levelName, categories, contentDescription, description }) {
  return (
    <div className="mb-4">
      <span className="h3 fw-bold">Giới thiệu khóa học</span>

      <p><strong>Trình độ:</strong> {levelName}</p>
      <p><strong>Danh mục:</strong> {categories.length > 0 ? categories.map(c => c.name).join(', ') : 'Không có'}</p>

      <div className="mt-3">
        {contentDescription ? (
          <div dangerouslySetInnerHTML={{ __html: contentDescription }} />
        ) : (
          <p>{description}</p>
        )}
      </div>
    </div>
  );
}

export default CourseOverview;
