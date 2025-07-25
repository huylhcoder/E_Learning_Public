import PropTypes from 'prop-types';
import { useState, forwardRef } from 'react';
import classNames from 'classnames';
import images from '~/assets/images';
import styles from './Image.module.scss';

//Do Tippy no khong nhan duoc ref cua cai img trong Component Image
//Nen minh can forwardRef de om nguyen cai component lai
//Va truyen mot cai ref tu tren xuong de vao img de no forward duoc img trong Component
//Khi component mouted thi img truyen vao ref xong thang Tippy no nhan duoc cai ref moi hien thi duoc

//Khong truyen fallback tu ngoai vao thi no se lay cai image bang noImage
//Neu nhu ben ngoai co fallback thi no se lay fallback
const Image = forwardRef(({ src, alt, className, fallback: customFallback = images.noImage, ...props }, ref) => {
    const [fallback, setFallback] = useState('');

    const handleError = () => {
        setFallback(customFallback);
    };

    return (
        // Khi cai anh bi loi thi no se roi vao onError
        <img
            className={classNames(styles.wrapper, className)}
            ref={ref}
            src={fallback || src}
            alt={alt}
            {...props} //de tat cac cac prop con lai vao
            onError={handleError}
        />
    );
});

Image.propTypes = {
    src: PropTypes.string,
    alt: PropTypes.string,
    className: PropTypes.string,
    fallback: PropTypes.string,
};

export default Image;
