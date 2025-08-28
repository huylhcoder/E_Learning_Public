
function CourseTabs({ overviewTab, detailsTab }) {
    return (
        <>
            <ul className="nav nav-tabs" id="myTab" role="tablist">
                <li className="nav-item" role="presentation">
                    <button
                        className="nav-link active"
                        id="overview-tab"
                        data-bs-toggle="tab"
                        data-bs-target="#overview"
                        type="button"
                        role="tab"
                    >
                        Thông tin tổng quát
                    </button>
                </li>
                <li className="nav-item" role="presentation">
                    <button
                        className="nav-link"
                        id="details-tab"
                        data-bs-toggle="tab"
                        data-bs-target="#details"
                        type="button"
                        role="tab"
                    >
                        Các phần của khóa học
                    </button>
                </li>
            </ul>

            <div className="tab-content mt-4" id="myTabContent">
                <div className="tab-pane fade show active" id="overview" role="tabpanel">
                    {overviewTab}
                </div>
                <div className="tab-pane fade" id="details" role="tabpanel">
                    {detailsTab}
                </div>
            </div>
        </>
    );
}
export default CourseTabs;
