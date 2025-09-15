const CourseProgress = ({ progressPercentage }) => (
  <div className="card mb-3">
    <div className="card-body">
      <span className="h3 text-primary">
        Tiến độ khóa học: {progressPercentage.toFixed(2)}% complete
      </span>
      <div className="progress">
        <div
          className="progress-bar bg-primary"
          role="progressbar"
          style={{ width: `${progressPercentage}%` }}
          aria-valuenow={progressPercentage}
          aria-valuemin="0"
          aria-valuemax="100"
        ></div>
      </div>
    </div>
  </div>
);

export default CourseProgress;
