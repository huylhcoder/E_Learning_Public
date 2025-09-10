function CourseCurriculum({ sections }) {
  return (
    <div className="mb-4">
      <span className="h3 fw-bold">Nội dung khóa học</span>
      <div className="accordion" id="curriculumAccordion">
        {sections.map((sec, idx) => (
          <div className="accordion-item" key={idx}>
            <h2 className="accordion-header" id={`heading-${idx}`}>
              <button
                className="accordion-button collapsed fs-4 fw-bold"
                type="button"
                data-bs-toggle="collapse"
                data-bs-target={`#collapse-${idx}`}
              >
                Phần {idx + 1}: {sec.name}
              </button>
            </h2>
            <div id={`collapse-${idx}`} className="accordion-collapse collapse">
              <div className="accordion-body">
                <p>{sec.description}</p>
                {sec.contentDescription && (
                  <div dangerouslySetInnerHTML={{ __html: sec.contentDescription }} />
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default CourseCurriculum;
