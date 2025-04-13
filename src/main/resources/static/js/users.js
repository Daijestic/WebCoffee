document.addEventListener('DOMContentLoaded', function() {
    // Global variables
    const ITEMS_PER_PAGE = 10;
    let currentPage = 1;
    let userData = [];
    let filteredData = [];

    // CSRF protection
    const token = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    // DOM Elements
    const usersTable = document.getElementById('usersTable');
    const tableBody = usersTable.querySelector('tbody');
    const emptyState = document.getElementById('emptyState');
    const pagination = document.getElementById('pagination');
    const searchInput = document.getElementById('searchInput');
    const roleFilter = document.getElementById('roleFilter');

    // Modals
    const userModal = document.getElementById('userModal');
    const userDetailsModal = document.getElementById('userDetailsModal');
    const deleteModal = document.getElementById('deleteModal');

    // Forms
    const userForm = document.getElementById('userForm');

    // Notification
    const notification = document.querySelector('.notification');

    // ===== Initialize functions =====

    // Fetch all users when page loads
    fetchUsers();

    // Add event listeners
    setupEventListeners();

    // ===== API Functions =====

    // Fetch users from API
    function fetchUsers() {
        fetch('/admin/users', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                userData = data;
                filteredData = [...userData];
                renderUsers();
                setupPagination();
            })
            .catch(error => {
                console.error('Error fetching users:', error);
                showNotification('Lỗi khi tải danh sách người dùng', 'error');
            });
    }

    // Add new user
    function addUser(formData) {
        const requestData = prepareUserData(formData);

        fetch('/admin/users/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify(requestData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                userData.push(data);
                filteredData = [...userData];
                renderUsers();
                setupPagination();
                closeModal(userModal);
                showNotification('Thêm người dùng thành công', 'success');
            })
            .catch(error => {
                console.error('Error adding user:', error);
                showNotification('Lỗi khi thêm người dùng', 'error');
            });
    }

    // Update existing user
    function updateUser(formData) {
        const userId = formData.get('id');
        const requestData = prepareUserData(formData);

        fetch(`/admin/users/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify(requestData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                // Update user in the array
                const index = userData.findIndex(user => user.id === data.id);
                if (index !== -1) {
                    userData[index] = data;
                }
                filteredData = applyFilters(userData);
                renderUsers();
                setupPagination();
                closeModal(userModal);
                showNotification('Cập nhật người dùng thành công', 'success');
            })
            .catch(error => {
                console.error('Error updating user:', error);
                showNotification('Lỗi khi cập nhật người dùng', 'error');
            });
    }

    // Delete user
    function deleteUser(userId) {
        fetch(`/admin/users/${userId}`, {
            method: 'DELETE',
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                // Remove user from array
                userData = userData.filter(user => user.id !== userId);
                filteredData = applyFilters(userData);
                renderUsers();
                setupPagination();
                closeModal(deleteModal);
                showNotification('Xoá người dùng thành công', 'success');
            })
            .catch(error => {
                console.error('Error deleting user:', error);
                showNotification('Lỗi khi xoá người dùng', 'error');
            });
    }

    // Get user details including order history
    function getUserDetails(userId) {
        fetch(`/admin/users/${userId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                displayUserDetails(data);
                openModal(userDetailsModal);
            })
            .catch(error => {
                console.error('Error fetching user details:', error);
                showNotification('Lỗi khi tải thông tin chi tiết người dùng', 'error');
            });
    }

    // ===== UI Functions =====

    // Prepare user data for API requests
    function prepareUserData(formData) {
        const roles = formData.getAll('roles');

        return {
            id: formData.get('id') || null,
            hoTen: formData.get('hoTen'),
            gioiTinh: formData.get('gioiTinh'),
            diaChi: formData.get('diaChi'),
            sdt: formData.get('sdt'),
            email: formData.get('email'),
            username: formData.get('username'),
            password: formData.get('password') || null,
            diemTichLuy: formData.get('diemTichLuy') || 0,
            roles: roles.length > 0 ? roles : ['ROLE_USER']
        };
    }

    // Render users table
    function renderUsers() {
        const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        const paginatedData = filteredData.slice(startIndex, startIndex + ITEMS_PER_PAGE);

        // Clear table
        tableBody.innerHTML = '';

        // Check if we have data
        if (paginatedData.length === 0) {
            usersTable.style.display = 'none';
            emptyState.style.display = 'block';
            return;
        }

        usersTable.style.display = 'table';
        emptyState.style.display = 'none';

        // Add users to table
        paginatedData.forEach(user => {
            const row = document.createElement('tr');
            row.setAttribute('data-roles', user.roles.join(','));

            // Create row content
            row.innerHTML = `
                <td data-label="ID">${user.id}</td>
                <td data-label="Họ Tên">${user.hoTen || ''}</td>
                <td data-label="Username">${user.username || ''}</td>
                <td data-label="Email">${user.email || ''}</td>
                <td data-label="Số điện thoại">${user.sdt || ''}</td>
                <td data-label="Điểm tích lũy" class="user-points">${user.diemTichLuy || 0}</td>
                <td data-label="Vai trò">
                    ${renderRoleBadges(user.roles)}
                </td>
                <td data-label="Thao tác" class="user-actions">
                    <button class="btn btn-info view-btn" data-id="${user.id}">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-warning edit-btn"
                            data-id="${user.id}"
                            data-name="${user.hoTen || ''}"
                            data-gender="${user.gioiTinh || ''}"
                            data-address="${user.diaChi || ''}"
                            data-phone="${user.sdt || ''}"
                            data-email="${user.email || ''}"
                            data-username="${user.username || ''}"
                            data-points="${user.diemTichLuy || 0}"
                            data-roles="${user.roles.join(',')}">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-danger delete-btn" data-id="${user.id}">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            `;

            tableBody.appendChild(row);
        });

        // Add event listeners to buttons
        addTableButtonListeners();
    }

    // Generate role badges HTML
    function renderRoleBadges(roles) {
        return roles.map(role => {
            const badgeClass = role.includes('ADMIN') ? 'badge-admin' :
                (role.includes('STAFF') ? 'badge-staff' : 'badge-user');
            return `<span class="badge ${badgeClass}">${role.replace('ROLE_', '')}</span>`;
        }).join('');
    }

    // Setup pagination
    function setupPagination() {
        const totalPages = Math.ceil(filteredData.length / ITEMS_PER_PAGE);
        pagination.innerHTML = '';

        if (totalPages <= 1) {
            return;
        }

        // Previous button
        const prevButton = document.createElement('button');
        prevButton.innerHTML = '<i class="fas fa-chevron-left"></i>';
        prevButton.disabled = currentPage === 1;
        prevButton.addEventListener('click', () => {
            if (currentPage > 1) {
                currentPage--;
                renderUsers();
                setupPagination();
            }
        });
        pagination.appendChild(prevButton);

        // Page buttons
        for (let i = 1; i <= totalPages; i++) {
            const pageButton = document.createElement('button');
            pageButton.textContent = i;
            pageButton.classList.toggle('active', i === currentPage);
            pageButton.addEventListener('click', () => {
                currentPage = i;
                renderUsers();
                setupPagination();
            });
            pagination.appendChild(pageButton);
        }

        // Next button
        const nextButton = document.createElement('button');
        nextButton.innerHTML = '<i class="fas fa-chevron-right"></i>';
        nextButton.disabled = currentPage === totalPages;
        nextButton.addEventListener('click', () => {
            if (currentPage < totalPages) {
                currentPage++;
                renderUsers();
                setupPagination();
            }
        });
        pagination.appendChild(nextButton);
    }

    // Apply filters
    function applyFilters(data) {
        const searchTerm = searchInput.value.toLowerCase();
        const selectedRole = roleFilter.value;

        return data.filter(user => {
            // Filter by search term
            const matchesSearch =
                (user.hoTen && user.hoTen.toLowerCase().includes(searchTerm)) ||
                (user.email && user.email.toLowerCase().includes(searchTerm)) ||
                (user.sdt && user.sdt.toLowerCase().includes(searchTerm)) ||
                (user.username && user.username.toLowerCase().includes(searchTerm));

            // Filter by role
            const matchesRole = !selectedRole || (user.roles && user.roles.includes(selectedRole));

            return matchesSearch && matchesRole;
        });
    }

    // Display user details in modal
    function displayUserDetails(user) {
        // Basic user info
        document.getElementById('detailsId').textContent = user.id;
        document.getElementById('detailsUsername').textContent = user.username || 'N/A';
        document.getElementById('detailsName').textContent = user.hoTen || 'N/A';
        document.getElementById('detailsGender').textContent = user.gioiTinh || 'N/A';
        document.getElementById('detailsEmail').textContent = user.email || 'N/A';
        document.getElementById('detailsPhone').textContent = user.sdt || 'N/A';
        document.getElementById('detailsAddress').textContent = user.diaChi || 'N/A';
        document.getElementById('detailsPoints').textContent = user.diemTichLuy || '0';

        // Roles
        document.getElementById('detailsRoles').innerHTML = renderRoleBadges(user.roles || []);

        // Orders
        const ordersList = document.getElementById('ordersList');
        ordersList.innerHTML = '';

        if (!user.listHoaDon || user.listHoaDon.length === 0) {
            ordersList.innerHTML = `
                <div style="text-align: center; padding: 20px;">
                    <i class="fas fa-shopping-cart" style="font-size: 48px; color: #ddd;"></i>
                    <h3 style="margin-top: 15px; color: #999;">Chưa có đơn hàng nào</h3>
                </div>
            `;
            return;
        }

        // Display each order
        user.listHoaDon.forEach(order => {
            const orderDate = new Date(order.ngayGioLapHoaDon);
            const formattedDate = `${orderDate.getDate()}/${orderDate.getMonth() + 1}/${orderDate.getFullYear()} ${orderDate.getHours()}:${String(orderDate.getMinutes()).padStart(2, '0')}`;

            const statusClass = order.trangThai === 'Completed' ? 'status-completed' :
                (order.trangThai === 'Pending' ? 'status-pending' : 'status-cancelled');

            const orderElement = document.createElement('div');
            orderElement.className = 'order-item';

            // Create HTML for products
            let productsHtml = '';
            if (order.products && order.products.length > 0) {
                order.products.forEach(item => {
                    productsHtml += `
                        <div class="product-item">
                            <div class="product-name">${item.product ? item.product.tenSanPham : 'N/A'}</div>
                            <div class="product-quantity">x${item.soLuong}</div>
                            <div class="product-note">${item.ghiChu || ''}</div>
                        </div>
                    `;
                });
            } else {
                productsHtml = '<div class="product-item">Không có sản phẩm nào</div>';
            }

            orderElement.innerHTML = `
                <div class="order-header">
                    <div class="order-date">${formattedDate}</div>
                    <div class="order-status ${statusClass}">${order.trangThai}</div>
                </div>
                <div class="order-details">
                    <div><strong>Phương thức thanh toán:</strong> ${order.phuongThucThanhToan || 'N/A'}</div>
                    <div class="order-products">
                        <h4>Sản phẩm:</h4>
                        ${productsHtml}
                    </div>
                    <div class="order-payment">
                        <div class="order-discount">Giảm giá: ${order.giamGia ? order.giamGia.toLocaleString() + 'đ' : '0đ'}</div>
                    </div>
                </div>
            `;

            ordersList.appendChild(orderElement);
        });
    }

    // Show notification
    function showNotification(message, type = 'success') {
        if (!notification) return;

        notification.textContent = message;
        notification.className = 'notification';

        if (type === 'error') {
            notification.style.backgroundColor = 'var(--danger-color)';
        } else if (type === 'warning') {
            notification.style.backgroundColor = 'var(--warning-color)';
        } else {
            notification.style.backgroundColor = 'var(--primary-color)';
        }

        notification.classList.add('show');

        setTimeout(() => {
            notification.classList.remove('show');
        }, 3000);
    }

    // Open modal
    function openModal(modal) {
        modal.style.display = 'block';
    }

    // Close modal
    function closeModal(modal) {
        modal.style.display = 'none';
    }

    // Reset form
    function resetForm(form) {
        form.reset();
        form.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
            checkbox.checked = false;
        });

        // Default role for new users
        if (form.id === 'userForm') {
            form.querySelector('input[value="ROLE_USER"]').checked = true;
        }
    }

    // Fill form with user data for editing
    function fillEditForm(user) {
        document.getElementById('modalTitle').textContent = 'Sửa Thông Tin Người Dùng';
        document.getElementById('userId').value = user.id;
        document.getElementById('hoTen').value = user.name || '';
        document.getElementById('gioiTinh').value = user.gender || 'Nam';
        document.getElementById('diaChi').value = user.address || '';
        document.getElementById('sdt').value = user.phone || '';
        document.getElementById('email').value = user.email || '';
        document.getElementById('username').value = user.username || '';
        document.getElementById('diemTichLuy').value = user.points || 0;

        // Password is not required when editing
        document.getElementById('password').removeAttribute('required');
        document.getElementById('passwordRequired').style.display = 'none';

        // Set roles
        const roles = user.roles.split(',');
        document.querySelectorAll('input[name="roles"]').forEach(checkbox => {
            checkbox.checked = roles.includes(checkbox.value);
        });

        // Change form action
        userForm.action = `/admin/users/${user.id}`;
        userForm.method = 'PUT';
    }

    // Reset form for adding new user
    function setupAddForm() {
        document.getElementById('modalTitle').textContent = 'Thêm Người Dùng';
        document.getElementById('userId').value = '';
        document.getElementById('password').setAttribute('required', '');
        document.getElementById('passwordRequired').style.display = 'inline';

        // Set default role
        document.querySelectorAll('input[name="roles"]').forEach(checkbox => {
            checkbox.checked = checkbox.value === 'ROLE_USER';
        });

        // Reset form action
        userForm.action = '/admin/users/add';
        userForm.method = 'POST';
    }

    // ===== Event Listeners =====

    function setupEventListeners() {
        // Filter input events
        searchInput.addEventListener('input', () => {
            currentPage = 1;
            filteredData = applyFilters(userData);
            renderUsers();
            setupPagination();
        });

        roleFilter.addEventListener('change', () => {
            currentPage = 1;
            filteredData = applyFilters(userData);
            renderUsers();
            setupPagination();
        });

        // Open add user modal
        document.getElementById('openAddModal').addEventListener('click', () => {
            resetForm(userForm);
            setupAddForm();
            openModal(userModal);
        });

        // Close modals
        document.querySelectorAll('.close-btn, #cancelBtn, #closeViewBtn').forEach(btn => {
            btn.addEventListener('click', () => {
                closeModal(userModal);
                closeModal(userDetailsModal);
                closeModal(deleteModal);
            });
        });

        // Cancel delete
        document.getElementById('cancelDeleteBtn').addEventListener('click', () => {
            closeModal(deleteModal);
        });

        // Form submission
        userForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            const userId = formData.get('id');

            if (userId) {
                updateUser(formData);
            } else {
                addUser(formData);
            }
        });

        // Tab functionality for user details modal
        document.querySelectorAll('.tab').forEach(tab => {
            tab.addEventListener('click', function() {
                const tabId = this.getAttribute('data-tab');

                // Update active tab
                document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
                this.classList.add('active');

                // Show selected tab content
                document.querySelectorAll('.tab-content').forEach(content => {
                    content.classList.remove('active');
                });
                document.getElementById(tabId).classList.add('active');
            });
        });
    }

    // Add listeners to dynamically created buttons in the table
    function addTableButtonListeners() {
        // View user details
        document.querySelectorAll('.view-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const userId = this.getAttribute('data-id');
                getUserDetails(userId);
            });
        });

        // Edit user
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                resetForm(userForm);

                // Get user data from button attributes
                const userData = {
                    id: this.getAttribute('data-id'),
                    name: this.getAttribute('data-name'),
                    gender: this.getAttribute('data-gender'),
                    address: this.getAttribute('data-address'),
                    phone: this.getAttribute('data-phone'),
                    email: this.getAttribute('data-email'),
                    username: this.getAttribute('data-username'),
                    points: this.getAttribute('data-points'),
                    roles: this.getAttribute('data-roles')
                };

                fillEditForm(userData);
                openModal(userModal);
            });
        });

        // Delete user
        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const userId = this.getAttribute('data-id');

                // Set up confirm delete button
                const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
                confirmDeleteBtn.onclick = function() {
                    deleteUser(userId);
                };

                openModal(deleteModal);
            });
        });
    }
});