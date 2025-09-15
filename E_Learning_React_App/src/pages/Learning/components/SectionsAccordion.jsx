import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faClock, faQuestionCircle } from '@fortawesome/free-solid-svg-icons';
import { formatDuration } from '~/utils/format';

const SectionsAccordion = ({ sections, currentLesson, onLessonClick, onTestClick }) => (
  <div className="accordion" id="sectionsAccordion">
    {sections.map((section) => (
      <div className="accordion-item" key={section.sectionId}>
        <h2 className="accordion-header">
          <button
            className="accordion-button collapsed fw-bold fs-4 mt-3"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target={`#section${section.sectionId}`}
          >
            Phần {section.sectionNumber}: {section.name}
          </button>
        </h2>
        <div
          id={`section${section.sectionId}`}
          className="accordion-collapse collapse"
          data-bs-parent="#sectionsAccordion"
        >
          <div className="accordion-body p-0">
            <ul className="list-group list-group-flush">
              {section.listLesson.map((lesson) => (
                <li
                  key={lesson.lessonId}
                  className={`list-group-item p-2 d-flex flex-column ${
                    currentLesson?.lessonId === lesson.lessonId ? 'active' : ''
                  }`}
                  onClick={() => onLessonClick(lesson)}
                  style={{ cursor: 'pointer' }}
                >
                  <div className="d-flex align-items-center">
                    <i className="fas fa-play-circle me-2 text-primary"></i>
                    <span className="fw-bold">
                      Bài {lesson.lessonNumber}: {lesson.name}
                    </span>
                  </div>
                  <div className="d-flex align-items-center ms-4 small">
                    <FontAwesomeIcon icon={faClock} className="me-2" />
                    <span>{formatDuration(lesson.lessonDuration)}</span>
                  </div>
                </li>
              ))}
              {section.listTest.map((test) => (
                <li
                  key={test.testId}
                  className="list-group-item mt-2"
                  onClick={() => onTestClick(test)}
                  style={{ cursor: 'pointer' }}
                >
                  <FontAwesomeIcon icon={faQuestionCircle} className="ms-2 pe-2" />
                  {test.title}
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    ))}
  </div>
);

export default SectionsAccordion;
