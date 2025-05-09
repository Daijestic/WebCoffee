document.addEventListener('DOMContentLoaded', function() {
    // CSRF token setup
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    // Elements
    const elements = {
        notification: document.getElementById('notification'),
        productModal: document.getElementById('productModal'),
        deleteModal: document.getElementById('deleteModal'),
        productForm: document.getElementById('productForm'),
        modalTitle: document.getElementById('modalTitle'),
        searchInput: document.getElementById('searchInput'),
        categoryFilter: document.getElementById('categoryFilter'),
        productsContainer: document.getElementById('productsContainer'),
        emptyState: document.getElementById('emptyState'),
        fileInput: document.getElementById('file'),
        imagePreview: document.getElementById('imagePreview'),
        previewContainer: document.getElementById('previewContainer'),
        isEditInput: document.getElementById('isEdit'),
        originalIdInput: document.getElementById('originalId')
    };

    let currentProductId = null;

    // Notification handler
    const handleNotification = (message, type = 'success') => {
        if (elements.notification) {
            elements.notification.textContent = message;
            elements.notification.className = `notification ${type}`;
            elements.notification.classList.add('show');
            setTimeout(() => elements.notification.classList.remove('show'), 3000);
        }
    };

    // Image preview handler
    const handleImagePreview = (file) => {
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                elements.imagePreview.src = e.target.result;
                elements.previewContainer.style.display = 'block';
            };
            reader.readAsDataURL(file);
        } else {
            elements.previewContainer.style.display = 'none';
        }
    };

    // Form reset handler
    const resetForm = () => {
        elements.productForm.reset();
        elements.previewContainer.style.display = 'none';
        elements.productForm.action = '/products/upload';
        elements.isEditInput.value = 'false';
        elements.originalIdInput.value = '';
    };

    // Product filtering
    const filterProducts = () => {
        const searchValue = elements.searchInput.value.toLowerCase();
        const categoryValue = elements.categoryFilter.value.toLowerCase();
        let visibleCount = 0;

        document.querySelectorAll('.product-card').forEach(product => {
            const name = product.querySelector('.product-name').textContent.toLowerCase();
            const description = product.querySelector('.product-description').textContent.toLowerCase();
            const category = product.getAttribute('data-category').toLowerCase();

            const matchesSearch = name.includes(searchValue) || description.includes(searchValue);
            const matchesCategory = !categoryValue || category === categoryValue;

            product.style.display = matchesSearch && matchesCategory ? 'block' : 'none';
            if (matchesSearch && matchesCategory) visibleCount++;
        });

        elements.emptyState.style.display = visibleCount === 0 ? 'block' : 'none';
    };

    // Delete product handler
    const deleteProduct = async (id) => {
        try {
            const response = await fetch(`/products/delete/${id}`, {
                method: 'DELETE',
                headers: {
                    [header]: token
                }
            });

            if (response.ok) {
                handleNotification('Xóa sản phẩm thành công');
                window.location.href = '/admin/products';
            } else {
                throw new Error('Lỗi khi xóa sản phẩm');
            }
        } catch (error) {
            handleNotification(error.message, 'error');
        }
    };

    // Event Listeners
    document.getElementById('openAddModal').addEventListener('click', () => {
        resetForm();
        elements.modalTitle.textContent = 'Thêm Sản Phẩm';
        elements.productModal.style.display = 'block';
    });

    elements.fileInput.addEventListener('change', (e) => {
        handleImagePreview(e.target.files[0]);
    });

    elements.searchInput.addEventListener('input', filterProducts);
    elements.categoryFilter.addEventListener('change', filterProducts);

    // Edit button handlers
    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', () => {
            const data = button.dataset;
            elements.modalTitle.textContent = 'Chỉnh Sửa Sản Phẩm';
            elements.isEditInput.value = 'true';
            elements.originalIdInput.value = data.id;
            elements.productForm.action = `/products/update/${data.id}`;

            // Fill form data
            document.getElementById('tenMon').value = data.name;
            document.getElementById('loaiMon').value = data.category;
            document.getElementById('giaBan').value = data.price;
            document.getElementById('moTa').value = data.description;

            if (data.image && data.image !== 'null') {
                elements.imagePreview.src = data.image;
                elements.previewContainer.style.display = 'block';
            }

            elements.productModal.style.display = 'block';
        });
    });

    // Delete button handlers
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', () => {
            currentProductId = button.dataset.id;
            elements.deleteModal.style.display = 'block';
        });
    });

    // Form validation
    elements.productForm.addEventListener('submit', (e) => {
        const requiredFields = ['tenMon', 'loaiMon', 'giaBan'].map(id => document.getElementById(id));
        const isValid = requiredFields.every(field => field.value.trim());

        if (!isValid) {
            e.preventDefault();
            handleNotification('Vui lòng điền đầy đủ thông tin bắt buộc!', 'error');
        }
    });

    // Modal close handlers
    document.querySelectorAll('.close-btn, #cancelBtn, #cancelDeleteBtn').forEach(button => {
        button.addEventListener('click', () => {
            elements.productModal.style.display = 'none';
            elements.deleteModal.style.display = 'none';
        });
    });

    document.getElementById('confirmDeleteBtn').addEventListener('click', () => {
        if (currentProductId) {
            deleteProduct(currentProductId);
            elements.deleteModal.style.display = 'none';
        }
    });

    // Close modals on outside click
    window.addEventListener('click', (e) => {
        if (e.target === elements.productModal || e.target === elements.deleteModal) {
            elements.productModal.style.display = 'none';
            elements.deleteModal.style.display = 'none';
        }
    });

    // Initial filter
    filterProducts();
});