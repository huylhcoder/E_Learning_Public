import React, { useState, useMemo } from 'react';
import { Offcanvas, Button, Form } from 'react-bootstrap';
import Tippy from '@tippyjs/react/headless';
import { FaPlus, FaChevronDown, FaTimes } from 'react-icons/fa';

import styles from './CategoryOffcanvas.module.scss';

const CategoryOffcanvas = ({
    show,
    onHide,
    editingCategory,
    setEditingCategory,
    onSave,
    handleGenerateSlug,
    categoryTree,
    onRemoveParent,
}) => {
    const [showDropdown, setShowDropdown] = useState(false);
    const [filterText, setFilterText] = useState('');

    // Filter category tree theo tên
    const filterCategories = (nodes, keyword) => {
        return nodes
            .filter((node) => node.name.toLowerCase().includes(keyword.toLowerCase()))
            .map((node) => ({
                ...node,
                children: node.children ? filterCategories(node.children, keyword) : [],
            }));
    };

    const filteredTree = useMemo(() => {
        if (!filterText) return categoryTree;
        return filterCategories(categoryTree, filterText);
    }, [filterText, categoryTree]);

    const findCategoryName = (nodes, id) => {
        for (let node of nodes) {
            if (node.categoryId === id) return node.name;
            if (node.children && node.children.length > 0) {
                const found = findCategoryName(node.children, id);
                if (found) return found;
            }
        }
        return null;
    };

    // Kiểm tra nodeId có phải con/cháu của categoryId không
    const isDescendant = (parent, targetId) => {
        if (!parent.children) return false;
        for (let child of parent.children) {
            if (child.categoryId === targetId) return true;
            if (isDescendant(child, targetId)) return true;
        }
        return false;
    };
    // Recursive render tree
    const renderTree = (nodes, level = 0) =>
        nodes.map((node) => {
            const isSelf = node.categoryId === editingCategory?.categoryId;
            const isChild = isDescendant(node, editingCategory?.categoryId);

            return (
                <div key={node.categoryId} className="mb-1" style={{ paddingLeft: level * 16 }}>
                    <Button
                        variant="outline-primary"
                        size="sm"
                        className="w-100 text-start fs-5 mt-2"
                        disabled={isSelf || isChild} // ❌ disable chính nó + con/cháu của nó
                        onClick={() => {
                            setEditingCategory({ ...editingCategory, parentId: node.categoryId });
                            setShowDropdown(false);
                        }}
                    >
                        {node.name}
                    </Button>
                    {node.children && node.children.length > 0 && renderTree(node.children, level + 1)}
                </div>
            );
        });

    return (
        <Offcanvas show={show} onHide={onHide} placement="end" backdrop="true">
            <Offcanvas.Header closeButton>
                <Offcanvas.Title>
                    <span className="fs-3">{editingCategory?.categoryId ? 'Chỉnh sửa danh mục' : 'Thêm danh mục'}</span>
                </Offcanvas.Title>
            </Offcanvas.Header>
            <Offcanvas.Body>
                <Form>
                    {/* Tên danh mục */}
                    <Form.Group className="mb-3">
                        <Form.Label>Tên danh mục</Form.Label>
                        <Form.Control
                            className="fs-5"
                            type="text"
                            value={editingCategory?.name || ''}
                            onChange={(e) => setEditingCategory({ ...editingCategory, name: e.target.value })}
                        />
                    </Form.Group>

                    {/* Slug */}
                    <Form.Group className="mb-3">
                        <Form.Label>Slug</Form.Label>
                        <div className="row g-2">
                            <div className="col-8">
                                <Form.Control
                                    type="text"
                                    className="fs-5"
                                    value={editingCategory?.slug || ''}
                                    onChange={(e) => setEditingCategory({ ...editingCategory, slug: e.target.value })}
                                />
                            </div>
                            <div className="col-4">
                                <Button
                                    className="btn btn-outline-secondary text-white w-100 fs-5"
                                    onClick={handleGenerateSlug}
                                >
                                    Tạo slug
                                </Button>
                            </div>
                        </div>
                    </Form.Group>

                    {/* Danh mục cha */}
                    <Form.Group className="mb-3">
                        <Form.Label>Danh mục cha</Form.Label>

                        {editingCategory?.parentId && (
                            <div className="mt-2 w-100">
                                <div className="position-relative d-inline-block">
                                    <span className="badge bg-light text-primary border border-primary fs-6 p-2 rounded-pill">
                                        {findCategoryName(categoryTree, editingCategory.parentId) || 'Không tìm thấy'}
                                    </span>

                                    <button
                                        type="button"
                                        className={`${styles.closeBtn} position-absolute top-0 start-100 translate-middle badge border border-light rounded-circle bg-danger`}
                                        onClick={() => onRemoveParent(editingCategory.categoryId)}
                                    >
                                        <FaTimes size={10} className="text-white" />
                                    </button>
                                </div>
                            </div>
                        )}

                        <Tippy
                            interactive
                            visible={showDropdown}
                            onClickOutside={() => setShowDropdown(false)}
                            placement="bottom-start"
                            render={(attrs) => (
                                <div className={styles.dropdownCard} tabIndex="-1" {...attrs}>
                                    <div className={styles.header}>
                                        <Form.Control
                                            className="fs-5"
                                            type="text"
                                            placeholder="Tìm danh mục..."
                                            value={filterText}
                                            onChange={(e) => setFilterText(e.target.value)}
                                        />
                                    </div>
                                    <div className={`${styles.body}`}>
                                        {filteredTree.length > 0 ? (
                                            renderTree(filteredTree)
                                        ) : (
                                            <p className={styles.empty}>Không tìm thấy</p>
                                        )}
                                    </div>
                                </div>
                            )}
                        >
                            <Button
                                variant="outline-secondary"
                                className="d-flex align-items-center justify-content-center gap-2 mt-3 fs-5 w-100"
                                onClick={() => setShowDropdown(!showDropdown)}
                            >
                                {showDropdown ? <FaChevronDown /> : <FaPlus />}
                                {showDropdown ? 'Thu gọn' : 'Chọn danh mục cha'}
                            </Button>
                        </Tippy>
                    </Form.Group>
                </Form>

                <div className="d-flex justify-content-end mt-3">
                    <Button variant="secondary" className="me-2" onClick={onHide}>
                        Đóng
                    </Button>
                    <Button variant="primary" onClick={onSave}>
                        Lưu
                    </Button>
                </div>
            </Offcanvas.Body>
        </Offcanvas>
    );
};

export default CategoryOffcanvas;
