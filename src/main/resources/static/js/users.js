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
    initializeData();

    // Add event listeners
    setupEventListeners();

    // ===== API Functions =====

    // Hàm kiểm tra nếu người dùng phù hợp với bộ lọc - đơn giản hóa
    function applyFilters(data) {
        const searchTerm = searchInput.value.toLowerCase().trim();
        const selectedRole = roleFilter.value;

        console.log("Áp dụng bộ lọc:", {
            searchTerm: searchTerm,
            selectedRole: selectedRole
        });

        return data.filter(user => {
            // Lọc theo từ khóa tìm kiếm
            const matchesSearch =
                searchTerm === '' ||
                (user.hoTen && user.hoTen.toLowerCase().includes(searchTerm)) ||
                (user.email && user.email.toLowerCase().includes(searchTerm)) ||
                (user.sdt && user.sdt.toLowerCase().includes(searchTerm)) ||
                (user.username && user.username.toLowerCase().includes(searchTerm));

            // Lọc theo vai trò - phương pháp đơn giản
            let matchesRole = selectedRole === '';

            if (!matchesRole && user.roles) {
                // Kiểm tra vai trò đơn giản - nếu bất kỳ phần tử nào trong mảng roles chứa giá trị selectedRole
                if (Array.isArray(user.roles)) {
                    for (let role of user.roles) {
                        if (role === selectedRole) {
                            matchesRole = true;
                            break;
                        }
                    }
                } else if (typeof user.roles === 'string') {
                    matchesRole = user.roles === selectedRole;
                }
            }

            return matchesSearch && matchesRole;
        });
    }

    // Cải thiện hàm initializeData - đơn giản hóa
    function initializeData() {
        console.log("Đang khởi tạo dữ liệu người dùng...");
        const rows = tableBody.querySelectorAll('tr');

        userData = Array.from(rows).map(row => {
            // Lấy ID từ dòng hiện tại
            const id = row.querySelector('td[data-label="ID"]').textContent;

            // Lấy thông tin vai trò - phương pháp đơn giản và ổn định
            let roles = [];

            // Lấy từ cột vai trò
            const roleCells = row.querySelectorAll('td[data-label="Vai trò"] .badge');
            if (roleCells && roleCells.length > 0) {
                // Nếu có badges, lấy từ các thẻ badge
                roles = Array.from(roleCells).map(badge => {
                    const roleText = badge.textContent.trim();
                    // Thêm tiền tố ROLE_ nếu chưa có
                    return roleText.startsWith('ROLE_') ? roleText : `ROLE_${roleText}`;
                });
            } else if (row.hasAttribute('data-roles')) {
                // Nếu không có badges, thử lấy từ thuộc tính data-roles
                const rolesAttr = row.getAttribute('data-roles');
                if (rolesAttr.includes(',')) {
                    // Nếu là danh sách phân tách bằng dấu phẩy
                    roles = rolesAttr.split(',').map(r => r.trim());
                } else {
                    // Nếu là một giá trị duy nhất
                    roles = [rolesAttr.trim()];
                }
            }

            // Đảm bảo định dạng ROLE_XXX cho tất cả vai trò
            roles = roles.map(role => {
                if (role.startsWith('[') && role.endsWith(']')) {
                    // Xử lý trường hợp [USER]
                    const cleanRole = role.substring(1, role.length - 1);
                    return cleanRole.startsWith('ROLE_') ? cleanRole : `ROLE_${cleanRole}`;
                }
                return role.startsWith('ROLE_') ? role : `ROLE_${role}`;
            });

            // Lấy thông tin người dùng khác
            const hoTen = row.querySelector('td[data-label="Họ Tên"]').textContent;
            const username = row.querySelector('td[data-label="Username"]').textContent;
            const email = row.querySelector('td[data-label="Email"]').textContent;
            const sdt = row.querySelector('td[data-label="Số điện thoại"]').textContent;
            const diemTichLuy = row.querySelector('td[data-label="Điểm tích lũy"]').textContent;

            console.log(`Đã khởi tạo người dùng ID ${id} với vai trò:`, roles);

            return {
                id,
                hoTen,
                username,
                email,
                sdt,
                diemTichLuy,
                roles,
                gioiTinh: null,
                diaChi: null
            };
        });

        console.log("Tổng số người dùng đã khởi tạo:", userData.length);
        filteredData = [...userData];
        renderUsers();
        setupPagination();
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

    // ===== Phần chức năng lọc =====

    // Thiết lập event listeners cho bộ lọc - đơn giản hóa
    function setupFilterListeners() {
        // Lọc theo vai trò
        roleFilter.addEventListener('change', function() {
            console.log("Lọc theo vai trò:", this.value);
            currentPage = 1;

            // Khi thay đổi vai trò, đặt lại dữ liệu và áp dụng bộ lọc mới
            if (this.value === '') {
                // Nếu không chọn vai trò, hiển thị tất cả
                filteredData = [...userData];
            } else {
                // Áp dụng bộ lọc vai trò
                filteredData = applyFilters(userData);
            }

            console.log("Kết quả sau khi lọc:", filteredData.length);
            renderUsers();
            setupPagination();
        });

        // Lọc theo từ khóa tìm kiếm
        searchInput.addEventListener('input', function() {
            console.log("Tìm kiếm:", this.value);
            currentPage = 1;
            filteredData = applyFilters(userData);
            renderUsers();
            setupPagination();
        });
    }


    // Reset và khởi tạo lại bộ lọc
    function resetFilters() {
        searchInput.value = '';
        roleFilter.value = '';
        filteredData = [...userData];
        currentPage = 1;
        renderUsers();
        setupPagination();
    }

    // Thiết lập event listeners cho bộ lọc


    // Display user details in modal
    function displayUserDetails(userData) {
        // Hiển thị thông tin cá nhân
        document.getElementById('detailsId').textContent = userData.id || 'N/A';
        document.getElementById('detailsUsername').textContent = userData.username || 'N/A';
        document.getElementById('detailsName').textContent = userData.hoTen || 'N/A';
        document.getElementById('detailsGender').textContent = userData.gioiTinh || 'N/A';
        document.getElementById('detailsEmail').textContent = userData.email || 'N/A';
        document.getElementById('detailsPhone').textContent = userData.sdt || 'N/A';
        document.getElementById('detailsAddress').textContent = userData.diaChi || 'N/A';
        document.getElementById('detailsPoints').textContent = userData.diemTichLuy || '0';
        
        // Hiển thị vai trò
        const rolesContainer = document.getElementById('detailsRoles');
        rolesContainer.innerHTML = '';
        if (userData.roles && userData.roles.length > 0) {
            userData.roles.forEach(role => {
                const roleClass = role.includes('ADMIN') ? 'badge-admin' : (role.includes('STAFF') ? 'badge-staff' : 'badge-user');
                const displayRole = role.replace('ROLE_', '');
                rolesContainer.innerHTML += `<span class="badge ${roleClass}">${displayRole}</span> `;
            });
        } else {
            rolesContainer.textContent = 'Không có vai trò';
        }

        // Hiển thị lịch sử đơn hàng
        const ordersContainer = document.getElementById('ordersList');
        ordersContainer.innerHTML = '';

// Kiểm tra xem userData tồn tại hay không
        if (!userData) {
            ordersContainer.innerHTML = `
        <div class="empty-orders">
            <i class="fas fa-exclamation-circle"></i>
            <p>Không thể tải thông tin người dùng</p>
        </div>
    `;
            return;
        }

// Kiểm tra xem có lịch sử đơn hàng không
        if (userData.listHoaDon && userData.listHoaDon.length > 0) {
            userData.listHoaDon.forEach((order, index) => {
                // Tạo HTML cho mỗi đơn hàng và thêm vào ordersContainer
                const orderElement = document.createElement('div');
                orderElement.className = 'order-item';

                // Tính tổng tiền từ các sản phẩm trong đơn hàng (nếu có dữ liệu)
                let tongTien = 0;
                let productsHtml = '<p>Không có sản phẩm</p>';

                if (order.products && order.products.length > 0) {
                    tongTien = order.products.reduce((total, item) => {
                        // Giả sử sản phẩm có giá và số lượng
                        const productPrice = item.product?.gia || 0;
                        return total + (productPrice * item.soLuong);
                    }, 0);

                    // Tạo HTML hiển thị danh sách sản phẩm
                    productsHtml = `
                <table class="order-products-table">
                    <thead>
                        <tr>
                            <th>Sản phẩm</th>
                            <th>Số lượng</th>
                            <th>Giá</th>
                            <th>Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${order.products.map(item => `
                            <tr>
                                <td>${item.product?.tenSanPham || 'Không có tên'}</td>
                                <td>${item.soLuong}</td>
                                <td>${(item.product?.gia || 0).toLocaleString('vi-VN')} VNĐ</td>
                                <td>${((item.product?.gia || 0) * item.soLuong).toLocaleString('vi-VN')} VNĐ</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            `;
                }

                // Tính tiền sau giảm giá
                const giamGia = order.giamGia || 0;
                const thanhToan = tongTien - giamGia;

                // Tạo nội dung HTML cho đơn hàng dựa trên dữ liệu
                orderElement.innerHTML = `
            <div class="order-header">
                <h3>Đơn hàng #${index + 1}</h3>
                <span class="order-date">${new Date(order.ngayGioLapHoaDon).toLocaleDateString('vi-VN')} ${new Date(order.ngayGioLapHoaDon).toLocaleTimeString('vi-VN')}</span>
                <span class="order-status ${getStatusClass(order.trangThai)}">${order.trangThai}</span>
            </div>
            <div class="order-details">
                <div class="order-summary">
                    <p><strong>Phương thức thanh toán:</strong> ${order.phuongThucThanhToan || 'Không có thông tin'}</p>
                    <p><strong>Tổng tiền:</strong> ${tongTien.toLocaleString('vi-VN')} VNĐ</p>
                    <p><strong>Giảm giá:</strong> ${giamGia.toLocaleString('vi-VN')} VNĐ</p>
                    <p><strong>Thanh toán:</strong> ${thanhToan.toLocaleString('vi-VN')} VNĐ</p>
                </div>
                <div class="order-products">
                    <h4>Danh sách sản phẩm</h4>
                    ${productsHtml}
                </div>
            </div>
        `;

                ordersContainer.appendChild(orderElement);
            });
        } else {
            // Hiển thị thông báo nếu không có đơn hàng
            ordersContainer.innerHTML = `
        <div class="empty-orders">
            <i class="fas fa-shopping-cart"></i>
            <p>Người dùng chưa có đơn hàng nào</p>
        </div>
    `;
        }

// Hàm trả về class CSS dựa trên trạng thái đơn hàng
        function getStatusClass(status) {
            switch(status?.toLowerCase()) {
                case 'đã giao':
                case 'hoàn thành':
                    return 'status-completed';
                case 'đang giao':
                case 'đang xử lý':
                    return 'status-processing';
                case 'đã hủy':
                    return 'status-cancelled';
                default:
                    return 'status-default';
            }
        }
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

    // Trong hàm setupEventListeners, đảm bảo gọi setupFilterListeners
    function setupEventListeners() {
        // Thiết lập bộ lọc
        setupFilterListeners();
        
        // ... các event listener khác
        
        // Open add user modal
        document.getElementById('openAddModal').addEventListener('click', () => {
            resetForm(userForm);
            setupAddForm();
            openModal(userModal);
        });
        
        // ... phần còn lại của hàm
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

    // Debug helper - thêm này để kiểm tra dữ liệu
    function debugData() {
        console.log("userData:", userData);
        console.log("filteredData:", filteredData);
        console.log("Selected role:", roleFilter.value);
    }

    // ===== Sửa đổi hàm main =====

    document.addEventListener('DOMContentLoaded', function() {
        // ... các biến và DOM elements khác ...
        
        // Kiểm tra xem có dữ liệu người dùng trong bảng không
        if (tableBody && tableBody.querySelectorAll('tr').length > 0) {
            console.log("Bảng người dùng đã có dữ liệu, khởi tạo từ HTML");
            // Khởi tạo dữ liệu từ HTML hiện có
            initializeData();
        } else {
            console.log("Không có dữ liệu người dùng, sẽ tải từ API");
            // Có thể thêm hàm fetchUsers() ở đây để lấy dữ liệu từ API nếu cần
        }
        
        // Thiết lập event listeners
        setupEventListeners();
        
        // Kiểm tra xem bộ lọc đã được thiết lập chưa
        if (roleFilter && searchInput) {
            console.log("Thiết lập các sự kiện cho bộ lọc");
            setupFilterListeners();
        } else {
            console.error("Không tìm thấy các phần tử bộ lọc trong DOM");
        }
    });
});
// Trong hàm setupEventListeners hoặc bất kỳ hàm nào xử lý các sự kiện
document.getElementById('cancelBtn').addEventListener('click', function() {
    // Đóng modal hoặc thực hiện hành động huỷ phù hợp
    userModal.style.display = 'none';
});
function setupEventListeners() {
    // Các sự kiện khác...
    
    // Xử lý tab trong modal chi tiết
    const tabs = document.querySelectorAll('.tabs .tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            // Gỡ bỏ active class từ tất cả tabs và tab contents
            document.querySelectorAll('.tabs .tab').forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            
            // Thêm active class cho tab hiện tại
            this.classList.add('active');
            
            // Hiển thị nội dung tương ứng
            const targetTab = this.getAttribute('data-tab');
            document.getElementById(targetTab).classList.add('active');
        });
    });
    
    // Các nút đóng modal
    document.getElementById('closeViewBtn').addEventListener('click', function() {
        closeModal(userDetailsModal);
    });
    
    document.getElementById('closeDetailsBtn').addEventListener('click', function() {
        closeModal(userDetailsModal);
    });
    
    document.getElementById('cancelBtn').addEventListener('click', function() {
        closeModal(userModal);
    });
    
    document.getElementById('cancelDeleteBtn').addEventListener('click', function() {
        closeModal(deleteModal);
    });
}

// Hàm xử lý chuyển đổi tab trong modal xem chi tiết người dùng
function setupTabSwitching() {
    const tabs = document.querySelectorAll('.tabs .tab');
    
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            // Xác định parent modal để tránh xung đột nếu có nhiều modal với tabs
            const parentModal = this.closest('.modal-content');
            
            // Loại bỏ lớp active từ tất cả các tab trong cùng modal
            parentModal.querySelectorAll('.tabs .tab').forEach(t => t.classList.remove('active'));
            
            // Thêm lớp active cho tab được click
            this.classList.add('active');
            
            // Lấy id của tab content cần hiển thị
            const tabId = this.getAttribute('data-tab');
            
            // Ẩn tất cả các tab content trong cùng modal
            parentModal.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
            
            // Hiển thị tab content tương ứng
            parentModal.querySelector(`#${tabId}`).classList.add('active');
            
            // Nếu tab là lịch sử đơn hàng, tải dữ liệu đơn hàng
            if (tabId === 'userOrders') {
                const userId = document.getElementById('detailsId').textContent.trim();
                loadUserOrders(userId);
            }
            
            console.log(`Đã chuyển sang tab: ${tabId}`);
        });
    });
}

// Hàm tải dữ liệu đơn hàng của người dùng
function loadUserOrders(userId) {
    console.log(`Đang tải đơn hàng cho người dùng ID: ${userId}`);
    const ordersList = document.getElementById('ordersList');
    ordersList.innerHTML = '<div class="text-center p-4"><i class="fas fa-spinner fa-spin"></i> Đang tải dữ liệu đơn hàng...</div>';
    
    fetch(`/admin/users/${userId}/orders`, {
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
        displayUserOrders(data);
    })
    .catch(error => {
        console.error('Lỗi khi tải đơn hàng:', error);
        ordersList.innerHTML = '<div class="text-center p-4 text-danger"><i class="fas fa-exclamation-circle"></i> Không thể tải dữ liệu đơn hàng</div>';
    });
}

// Hiển thị đơn hàng
function displayUserOrders(orders) {
    console.log(`Hiển thị ${orders.length} đơn hàng`);
    const ordersList = document.getElementById('ordersList');
    
    if (!orders || orders.length === 0) {
        ordersList.innerHTML = '<div class="text-center p-4 text-muted"><i class="fas fa-shopping-cart"></i><p class="mt-2">Người dùng chưa có đơn hàng nào</p></div>';
        return;
    }
    
    let html = '<div class="orders-list">';
    
    orders.forEach(order => {
        const orderDate = new Date(order.ngayDat).toLocaleDateString('vi-VN');
        let statusClass = '';
        
        switch(order.trangThai?.toLowerCase()) {
            case 'hoàn thành':
            case 'đã hoàn thành':
            case 'dahoanthanh':
                statusClass = 'status-completed';
                break;
            case 'đang xử lý':
            case 'chờ xử lý':
            case 'dangxuly':
                statusClass = 'status-pending';
                break;
            case 'đã hủy':
            case 'hủy':
            case 'dahuy':
                statusClass = 'status-cancelled';
                break;
            default:
                statusClass = 'status-pending';
        }
        
        html += `
            <div class="order-item">
                <div class="order-header">
                    <div><strong>Mã đơn:</strong> #${order.id}</div>
                    <div><strong>Ngày đặt:</strong> ${orderDate}</div>
                    <div><span class="order-status ${statusClass}">${order.trangThai || 'Đang xử lý'}</span></div>
                </div>
                <div class="order-details">
                    <div><strong>Tổng tiền:</strong> ${(order.tongTien || 0).toLocaleString('vi-VN')}đ</div>
                    <div><strong>Phương thức:</strong> ${order.phuongThucThanhToan || 'N/A'}</div>
                </div>
            </div>
        `;
    });
    
    html += '</div>';
    ordersList.innerHTML = html;
}

// Gọi hàm setupTabSwitching trong hàm setupEventListeners
function setupEventListeners() {
    // Các sự kiện khác giữ nguyên
    
    // Thêm hàm thiết lập tab switching
    setupTabSwitching();
    setupTabEventListeners()
    // Khi mở modal xem chi tiết người dùng, đảm bảo tab đầu tiên được chọn
    document.querySelectorAll('.view-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            // Các xử lý khác giữ nguyên
            
            // Reset về tab đầu tiên
            setTimeout(() => {
                const userDetailsModal = document.getElementById('userDetailsModal');
                userDetailsModal.querySelector('.tab[data-tab="userInfo"]').classList.add('active');
                userDetailsModal.querySelector('.tab[data-tab="userOrders"]').classList.remove('active');
                userDetailsModal.querySelector('#userInfo').classList.add('active');
                userDetailsModal.querySelector('#userOrders').classList.remove('active');
            }, 100);
        });
    });
    
    // Các nút đóng modal
    document.querySelectorAll('.close-btn, #closeViewBtn').forEach(btn => {
        btn.addEventListener('click', function() {
            closeModal(userDetailsModal);
        });
    });
    
    // Các event listeners khác trong hàm setupEventListeners giữ nguyên
}
// Thêm đoạn code này vào phần setupEventListeners của bạn
function setupTabEventListeners() {
    const tabs = document.querySelectorAll('.tab');
    const tabContents = document.querySelectorAll('.tab-content');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const tabId = tab.getAttribute('data-tab');

            // Xóa lớp active khỏi tất cả các tab và tab content
            tabs.forEach(t => t.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));

            // Thêm lớp active vào tab và tab content được chọn
            tab.classList.add('active');
            document.getElementById(tabId).classList.add('active');

            // Nếu tab là lịch sử đơn hàng, tải lịch sử đơn hàng
            if (tabId === 'userOrders') {
                // Gọi hàm hiển thị lịch sử đơn hàng ở đây
                // Đã được xử lý trong hàm displayUserDetails
            }
        });
    });
}
window.addEventListener('click', function(event) {
    const userDetailsModal = document.getElementById('userDetailsModal');
    const userModal = document.getElementById('userModal');
    const deleteModal = document.getElementById('deleteModal');

    if (event.target === userDetailsModal) {
        closeModal(userDetailsModal);
    } else if (event.target === userModal) {
        closeModal(userModal);
    } else if (event.target === deleteModal) {
        closeModal(deleteModal);
    }
});

// Thêm sự kiện đóng modal cho các nút đóng trong modal Chi tiết người dùng
document.addEventListener('DOMContentLoaded', function() {
    // Xử lý nút đóng modal Chi tiết người dùng
    const closeDetailsBtn = document.getElementById('closeDetailsBtn');
    const closeViewBtn = document.getElementById('closeViewBtn');
    const userDetailsModal = document.getElementById('userDetailsModal');

    if (closeDetailsBtn) {
        closeDetailsBtn.addEventListener('click', function() {
            console.log("Nút đóng chi tiết được nhấp");
            closeModal(userDetailsModal);
        });
    }

    if (closeViewBtn) {
        closeViewBtn.addEventListener('click', function() {
            console.log("Nút đóng xem được nhấp");
            closeModal(userDetailsModal);
        });
    }

    // Thêm trực tiếp sự kiện đóng vào các nút - cách tiếp cận trực tiếp
    document.querySelectorAll('.close-btn, #closeViewBtn').forEach(element => {
        element.onclick = function() {
            console.log("Đóng modal qua selector");
            const modal = this.closest('.modal');
            if (modal) {
                modal.style.display = 'none';
            }
        };
    });
});
