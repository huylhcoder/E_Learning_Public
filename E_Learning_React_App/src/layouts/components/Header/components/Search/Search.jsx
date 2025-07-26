import { useEffect, useState, useRef } from 'react';
import { faCircleXmark, faSpinner } from '@fortawesome/free-solid-svg-icons';
import { FaSearch } from 'react-icons/fa';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import HeadlessTippy from '@tippyjs/react/headless';
import classNames from 'classnames/bind';
import { Link, useNavigate  } from 'react-router-dom';

import * as searchServices from '~/services/searchService';
import { Wrapper as PopperWrapper } from '~/components/Popper';
import { SearchIcon } from '~/components/Icons';
import { useDebounce } from '~/hooks';
import styles from './Search.module.scss';

const cx = classNames.bind(styles);

function Search() {
    const [searchValue, setSearchValue] = useState('');
    const [searchResult, setSearchResult] = useState([]);
    const [showResult, setShowResult] = useState(false);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const debouncedValue = useDebounce(searchValue, 500);
    const inputRef = useRef();
    const wrapperRef = useRef(); // ref để lấy width

    useEffect(() => {
        if (!debouncedValue.trim()) {
            setSearchResult([]);
            return;
        }

        const fetchApi = async () => {
            setLoading(true);
            try {
                const result = await searchServices.search(debouncedValue);
                setSearchResult(result);
            } catch (error) {
                console.error('Search failed:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchApi();
    }, [debouncedValue]);

    const handleClear = () => {
        setSearchValue('');
        setSearchResult([]);
        inputRef.current.focus();
    };

    const handleChange = (e) => {
        const value = e.target.value;
        if (!value.startsWith(' ')) {
            setSearchValue(value);
        }
    };

     const handleSearch = (searchText) => {
    if (!searchText) return;
    navigate(`/course/search?courseName=${encodeURIComponent(searchText)}`);
    showResult(false);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      handleSearch(searchValue);
    }
  };

    return (
        <div className={cx('search-wrapper')} ref={wrapperRef}>
            <HeadlessTippy
                interactive
                visible={showResult && searchResult.length > 0}
                placement="bottom-start"
                render={(attrs) => (
                    <div
                        className={cx('search-result')}
                        tabIndex="-1"
                        {...attrs}
                        style={{ width: wrapperRef.current?.offsetWidth || 450 }}
                    >
                        <PopperWrapper>
                            <h4 className={cx('search-title')}>Khóa học</h4>
                            {searchResult.map((result, index) => (
                                <Link
                                    key={index}
                                    to={`/course/search?courseName=${encodeURIComponent(result.name)}`}
                                    className={cx('suggestion-item')}
                                    onClick={() => setShowResult(false)}
                                >
                                    <FaSearch className="me-2 text-muted" /> {result.name}
                                </Link>
                            ))}
                        </PopperWrapper>
                    </div>
                )}
                onClickOutside={() => setShowResult(false)}
            >
                <div style={{ position: 'relative' }}>
                    <input
                        ref={inputRef}
                        value={searchValue}
                        placeholder="Tên khóa học..."
                        spellCheck={false}
                        onChange={handleChange}
                        onKeyDown={handleKeyDown}
                        onFocus={() => setShowResult(true)}
                        className={cx('search-input')}
                    />
                    {!!searchValue && !loading && (
                        <button type="button" className={cx('clear-btn')} onClick={handleClear} tabIndex={-1}>
                            <FontAwesomeIcon icon={faCircleXmark} />
                        </button>
                    )}
                    {loading && (
                        <span className={cx('loading-icon')}>
                            <FontAwesomeIcon icon={faSpinner} spin />
                        </span>
                    )}
                    <button
                        className={cx('search-btn')}
                        type="button"
                        tabIndex={-1}
                        onMouseDown={(e) => e.preventDefault()}
                    >
                        <SearchIcon />
                    </button>
                </div>
            </HeadlessTippy>
        </div>
    );
}

export default Search;
