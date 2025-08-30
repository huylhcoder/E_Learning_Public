const SectionOverview = ({ section, setSection, handleSectionUpdate }) => {
  return (
    <div className="card">
      <div className="card-body">
        <h5 className="card-title fs-3 text-primary fw-bold mb-3">Thông tin phần</h5>
        <form onSubmit={handleSectionUpdate}>
          <div className="mb-3">
            <label className="form-label ms-2">Tên phần</label>
            <input
              type="text"
              className="form-control fs-4"
              value={section.name || ""}
              onChange={(e) => setSection({ ...section, name: e.target.value })}
            />
          </div>
          <div className="mb-3">
            <label className="form-label ms-2">Mô tả</label>
            <textarea
              className="form-control fs-4"
              rows="4"
              value={section.description || ""}
              onChange={(e) =>
                setSection({ ...section, description: e.target.value })
              }
            />
          </div>
          <button type="submit" className="btn btn-primary">
            <i className="fas fa-save"></i> Lưu thay đổi
          </button>
        </form>
      </div>
    </div>
  );
};

export default SectionOverview;
