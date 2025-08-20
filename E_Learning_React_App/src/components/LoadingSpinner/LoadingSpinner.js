import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';

const LoadingSpinner = () => (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', flexDirection: 'column' }}>
        <FontAwesomeIcon icon={faSpinner} spin size="3x" className="text-primary" />
        <span style={{ marginTop: '10px', fontSize: '18px', color: '#0d6efd' }}>
            Loading...
        </span>
    </div>
);

export default LoadingSpinner;
