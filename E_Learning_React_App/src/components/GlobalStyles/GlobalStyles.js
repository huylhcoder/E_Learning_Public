import PropTypes from 'prop-types';
import './GlobalStyles.scss';

//Nhan vao mot cai Children
function GlobalStyles({ children }) {
    return children;
}

GlobalStyles.propTypes = {
    children: PropTypes.node.isRequired,
};

export default GlobalStyles;
