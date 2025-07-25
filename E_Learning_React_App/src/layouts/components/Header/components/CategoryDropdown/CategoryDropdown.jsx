import React, { useState, useEffect } from 'react';
import Tippy from '@tippyjs/react/headless';
import { NavLink } from 'react-router-dom';
import { FaAngleRight } from 'react-icons/fa';
import styles from './CategoryDropdown.module.scss';
import axios from 'axios';

function convertCategoriesToMenuItems(categories) {
    return categories.map((category) => ({
        label: category.name,
        to: `/course/search?category=${category.slug || category.categoryId}`,
        children: category.children?.length ? convertCategoriesToMenuItems(category.children) : null,
    }));
}

function MenuList({ items }) {
    const [submenu, setSubmenu] = useState(null);

    return (
        <ul className={styles.menuList}>
            {items.map((item, idx) => (
                <li
                    key={idx}
                    onMouseEnter={() => setSubmenu(item.children ? idx : null)}
                    onMouseLeave={() => setSubmenu(null)}
                    className={item.children ? styles.hasSubmenu : ''}
                >
                    {item.children ? (
                        <span className={`${styles.menuItem} d-flex align-items-center justify-content-between`}>
                            <span>{item.label}</span>
                            <span className={styles.arrowIcon}>
                                <FaAngleRight />
                            </span>
                        </span>
                    ) : (
                        <NavLink className={`${styles.menuItem} d-flex align-items-center`} to={item.to}>
                            <span>{item.label}</span>
                        </NavLink>
                    )}
                    {item.children && submenu === idx && (
                        <div className={styles.submenu}>
                            <MenuList items={item.children} />
                        </div>
                    )}
                </li>
            ))}
        </ul>
    );
}

const CategoryDropdown = () => {
    const [menuItems, setMenuItems] = useState([]);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/v1/category/tree');
                const menuData = convertCategoriesToMenuItems(response.data);
                setMenuItems(menuData);
            } catch (error) {
                console.error('Failed to fetch categories:', error);
            }
        };

        fetchCategories();
    }, []);

    return (
        <Tippy
            interactive
            placement="bottom-start"
            trigger="mouseenter focus"
            render={(attrs) => (
                <div className={styles.dropdownMenu} tabIndex="-1" {...attrs}>
                    <MenuList items={menuItems} />
                </div>
            )}
        >
            <button className={`${styles.dropdownButton}`} type="button">
                Danh mục khóa học
            </button>
        </Tippy>
    );
};

export default CategoryDropdown;
