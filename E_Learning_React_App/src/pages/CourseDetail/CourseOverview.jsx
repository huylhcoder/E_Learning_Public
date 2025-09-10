function CourseOverview({ levelName, categories, contentDescription, description }) {
    return (
        <div className="mb-4">
            <span className="fs-3 mb-3 fw-bold">Giới thiệu khóa học</span>

            <p>
                <strong>Trình độ:</strong> {levelName}
            </p>
            {/* <p>
                <strong>Danh mục:</strong>{' '}
                {categories.length > 0 ? categories.map((c) => c.categoryName).join(', ') : 'Không có'}
            </p> */}
             <p>
                <strong>Danh mục:</strong>{' '}
                {categories.length > 0 ? (
                    categories.map((c) => (
                        <span key={c.categoryId} className="badge bg-primary me-2">
                            {c.categoryName}
                        </span>
                    ))
                ) : (
                    <span className="text-muted">Không có</span>
                )}
            </p>
            <p>
                <strong>Chi tiết:</strong>
            </p>
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
