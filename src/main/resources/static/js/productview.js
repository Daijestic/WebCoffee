document.addEventListener('DOMContentLoaded', function() {
    // Display notification if exists
    const notification = document.getElementById('notification');
    if (notification) {
        notification.classList.add('show');
        setTimeout(() => {
            notification.classList.remove('show');
        }, 3000);
    }

    // Check if we have a newProduct flag and show the notification
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('newProduct') === 'true') {
        // Clear the URL parameter without page refresh
        window.history.replaceState({}, document.title, window.location.pathname);
    }

    // Get elements
    const productModal = document.getElementById('productModal');
    const deleteModal = document.getElementById('deleteModal');
    const productForm = document.getElementById('productForm');
    const openAddModalBtn = document.getElementById('openAddModal');
    const cancelBtn = document.getElementById('cancelBtn');
    const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    const closeBtn = document.querySelector('.close-btn');
    const searchInput = document.getElementById('searchInput');
    const categoryFilter = document.getElementById('categoryFilter');
    const productsContainer = document.getElementById('productsContainer');
    const emptyState = document.getElementById('emptyState');
    const modalTitle = document.getElementById('modalTitle');
    const fileInput = document.getElementById('file');
    const imagePreview = document.getElementById('imagePreview');
    const previewContainer = document.getElementById('previewContainer');
    const isEditInput = document.getElementById('isEdit');
    const originalIdInput = document.getElementById('originalId');

    // Get edit and delete buttons
    const editButtons = document.querySelectorAll('.edit-btn');
    const deleteButtons = document.querySelectorAll('.delete-btn');

    // Track current product ID for deletion
    let currentProductId = '';

    // Open add product modal
    openAddModalBtn.addEventListener('click', function() {
        resetForm();
        modalTitle.textContent = 'Thêm Sản Phẩm';
        isEditInput.value = 'false';
        productModal.style.display = 'block';
    });

    // Close modal
    closeBtn.addEventListener('click', function() {
        productModal.style.display = 'none';
    });

    // Cancel button
    cancelBtn.addEventListener('click', function() {
        productModal.style.display = 'none';
    });

    // Cancel delete
    cancelDeleteBtn.addEventListener('click', function() {
        deleteModal.style.display = 'none';
    });

    // Confirm delete
    confirmDeleteBtn.addEventListener('click', function() {
        if (currentProductId) {
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            fetch('/products/delete/' + currentProductId, {
                method: 'DELETE',
                headers: {
                    'X-XSRF-TOKEN': csrfToken // Sử dụng header đúng
                }
            })
                .then(response => {
                    if (response.ok) {
                        console.log("Xóa thành công sản phẩm ID: " + currentProductId);
                        window.location.href = '/admin/products'; // Chuyển hướng thủ công
                    } else {
                        console.error("Xóa thất bại");
                    }
                })
                .catch(error => console.error("Lỗi:", error));
        }
    });

    // Image preview
    fileInput.addEventListener('change', function() {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                imagePreview.src = e.target.result;
                previewContainer.style.display = 'block';
            }
            reader.readAsDataURL(file);
        } else {
            previewContainer.style.display = 'none';
        }
    });

    // Edit product handler
    editButtons.forEach(button => {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const name = this.getAttribute('data-name');
            const category = this.getAttribute('data-category');
            const price = this.getAttribute('data-price');
            const description = this.getAttribute('data-description');
            const imagePath = this.getAttribute('data-image');

            // Fill the form
            document.getElementById('tenMon').value = name;
            document.getElementById('loaiMon').value = category;
            document.getElementById('giaBan').value = price;
            document.getElementById('moTa').value = description;

            console.log("ID:", id);
            // Set form action and method
            productForm.action = '/products/update/' + id;

            // Show image preview if available
            if (imagePath && imagePath !== 'null') {
                imagePreview.src = imagePath;
                previewContainer.style.display = 'block';
            }

            // Set edit mode
            modalTitle.textContent = 'Chỉnh Sửa Sản Phẩm';
            isEditInput.value = 'true';
            originalIdInput.value = id;

            // Show modal
            productModal.style.display = 'block';
        });
    });

    // Delete product handler
    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            currentProductId = this.getAttribute('data-id');
            deleteModal.style.display = 'block';
        });
    });

    // Search and filter functionality
    function filterProducts() {
        const searchValue = searchInput.value.toLowerCase();
        const categoryValue = categoryFilter.value.toLowerCase();
        let visibleCount = 0;

        const products = productsContainer.querySelectorAll('.product-card');
        products.forEach(product => {
            const name = product.querySelector('.product-name').textContent.toLowerCase();
            const description = product.querySelector('.product-description').textContent.toLowerCase();
            const category = product.getAttribute('data-category').toLowerCase();

            const matchesSearch = name.includes(searchValue) || description.includes(searchValue);
            const matchesCategory = !categoryValue || category === categoryValue;

            if (matchesSearch && matchesCategory) {
                product.style.display = 'block';
                visibleCount++;
            } else {
                product.style.display = 'none';
            }
        });

        // Show empty state if no products match
        if (visibleCount === 0) {
            emptyState.style.display = 'block';
        } else {
            emptyState.style.display = 'none';
        }
    }

    searchInput.addEventListener('input', filterProducts);
    categoryFilter.addEventListener('change', filterProducts);

    // Close modals when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target === productModal) {
            productModal.style.display = 'none';
        }
        if (event.target === deleteModal) {
            deleteModal.style.display = 'none';
        }
    });

    // Form validation
    productForm.addEventListener('submit', function(event) {
        const tenMon = document.getElementById('tenMon').value.trim();
        const loaiMon = document.getElementById('loaiMon').value.trim();
        const giaBan = document.getElementById('giaBan').value.trim();

        if (!tenMon || !loaiMon || !giaBan) {
            event.preventDefault();
            alert('Vui lòng điền đầy đủ thông tin bắt buộc!');
        }
    });

    // Reset form
    function resetForm() {
        productForm.reset();
        previewContainer.style.display = 'none';
        productForm.action = '/products/upload';
    }

    // Run initial filter
    filterProducts();
});