import classNames from 'classnames/bind';

import styles from './Carousel.module.scss';
import bgCode from '~/assets/images/background/bg_hop_1.jpg';
import bgHop1 from '~/assets/images/background/bg_coding_1.jpg';
import bgHop2 from '~/assets/images/background/bg_hoc_data_1.jpg';

const cx = classNames.bind(styles);

const carouselImages = [
    { src: bgHop1, alt: 'Lập trình' },
    { src: bgCode, alt: 'Bán hàng' },
    { src: bgHop2, alt: 'Marketing' },
];

const Carousel = () => {
    return (
        <div id="carouselExampleControls" className={cx('carousel', 'carousel slide', 'mb-5')} data-bs-ride="carousel">
            <div className="carousel-inner">
                {carouselImages.map((img, idx) => (
                    <div className={`carousel-item${idx === 0 ? ' active' : ''}`} key={img.src}>
                        <img src={img.src} className="d-block w-100" alt={img.alt} />
                    </div>
                ))}
            </div>
            <button
                className="carousel-control-prev"
                type="button"
                data-bs-target="#carouselExampleControls"
                data-bs-slide="prev"
            >
                <span className="carousel-control-prev-icon" aria-hidden="true"></span>
                <span className="visually-hidden">Previous</span>
            </button>
            <button
                className="carousel-control-next"
                type="button"
                data-bs-target="#carouselExampleControls"
                data-bs-slide="next"
            >
                <span className="carousel-control-next-icon" aria-hidden="true"></span>
                <span className="visually-hidden">Next</span>
            </button>
            <div className="carousel-indicators">
                {carouselImages.map((_, idx) => (
                    <button
                        key={idx}
                        type="button"
                        data-bs-target="#carouselExampleControls"
                        data-bs-slide-to={idx}
                        className={idx === 0 ? 'active' : ''}
                        aria-current={idx === 0 ? 'true' : undefined}
                        aria-label={`Slide ${idx + 1}`}
                    ></button>
                ))}
            </div>
        </div>
    );
};

export default Carousel;
