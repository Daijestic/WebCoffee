document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const modal = document.getElementById('xuatKhoModal');
    const detailsModal = document.getElementById('xuatKhoDetailsModal');
    const deleteModal = document.getElementById('deleteModal');
    const modalTitle = document.getElementById('modalTitle');
    const form = document.getElementById('xuatKhoForm');
    const itemsContainer = document.getElementById('itemsContainer');
    const addItemBtn = document.getElementById('addItemBtn');
    const emptyState = document.getElementById('emptyState');
    const searchInput = document.querySelector('.search-input');
    const xuatKhoTable = document.getElementById('xuatKhoTable');

    // CSRF Token for AJAX requests
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // Initialize
    checkTableEmpty();
    loadEmployeeData();
    loadNguyenLieuData();

    // Notification timeout
    const notification = document.getElementById('notification');
    if (notification) {
        setTimeout(() => {
            notification.style.display = 'none';
        }, 3000);
    }

    // Open add modal
    document.getElementById('openAddModal').addEventListener('click', function() {
        openAddModal();
    });

    // Close buttons
    document.querySelectorAll('.close-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            modal.style.display = 'none';
            detailsModal.style.display = 'none';
            deleteModal.style.display = 'none';
        });
    });

    // Cancel buttons
    document.getElementById('cancelBtn').addEventListener('click', function() {
        modal.style.display = 'none';
    });

    document.getElementById('closeViewBtn').addEventListener('click', function() {
        detailsModal.style.display = 'none';
    });

    document.getElementById('cancelDeleteBtn').addEventListener('click', function() {
        deleteModal.style.display = 'none';
    });

    // Add Item button
    addItemBtn.addEventListener('click', function() {
        addItemRow();
    });

    // View, Edit and Delete buttons event delegation
    document.querySelector('tbody').addEventListener('click', function(e) {
        const target = e.target.closest('.btn');
        if (!target) return;

        const id = target.getAttribute('data-id');

        if (target.classList.contains('view-btn')) {
            viewPhieuXuatKho(id);
        } else if (target.classList.contains('edit-btn')) {
            editPhieuXuatKho(id);
        } else if (target.classList.contains('delete-btn')) {
            openDeleteModal(id);
        }
    });

    // Items container - event delegation for remove buttons
    itemsContainer.addEventListener('click', function(e) {
        const target = e.target.closest('.remove-item');
        if (target) {
            const itemRow = target.closest('.item-row');
            itemRow.remove();
            updateItemIndices();
        }
    });

    // Search functionality
    searchInput.addEventListener('input', function() {
        const query = this.value.toLowerCase();
        const rows = xuatKhoTable.querySelectorAll('tbody tr');

        rows.forEach(row => {
            const id = row.querySelector('td[data-label="ID Phiếu"]').textContent;
            const nhanVien = row.querySelector('td[data-label="Nhân viên"]').textContent;
            const ngayXuat = row.querySelector('td[data-label="Ngày xuất"]').textContent;

            const match = id.includes(query) ||
                nhanVien.toLowerCase().includes(query) ||
                ngayXuat.toLowerCase().includes(query);

            row.style.display = match ? '' : 'none';
        });

        checkTableEmpty();
    });

    // Form submit
    form.addEventListener('submit', function(e) {
        e.preventDefault();

        // Validate form
        if (!validateForm()) {
            return;
        }

        const formData = new FormData(form);
        const jsonData = formToJson(formData);

        // AJAX request to save
        const url = jsonData.idPhieuXuatKho ? '/admin/xuatkho/update' : '/admin/xuatkho/add';
        const method = jsonData.idPhieuXuatKho ? 'PUT' : 'POST';

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify(jsonData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success !== false) {
                    // Success
                    window.location.href = '/admin/xuatkho?message=' + encodeURIComponent(data.message);
                } else {
                    // Error
                    alert('Lỗi: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Đã xảy ra lỗi khi lưu phiếu xuất kho');
            });
    });

    // Delete confirmation
    document.getElementById('confirmDeleteBtn').addEventListener('click', function() {
        const id = this.getAttribute('data-id');
        if (!id) return;

        fetch(`/admin/xuatkho/${id}`, {
            method: 'DELETE',
            headers: {
                [header]: token
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    window.location.href = '/admin/xuatkho?message=' + encodeURIComponent(data.message);
                } else {
                    alert('Lỗi: ' + data.message);
                    deleteModal.style.display = 'none';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Đã xảy ra lỗi khi xoá phiếu xuất kho');
                deleteModal.style.display = 'none';
            });
    });

    // Functions
    function openAddModal() {
        modalTitle.textContent = 'Thêm Phiếu Xuất Kho';
        form.reset();
        document.getElementById('phieuXuatKhoId').value = '';

        // Set today's date as default
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('ngayXuat').value = today;

        // Clear existing items except template
        const items = itemsContainer.querySelectorAll('.item-row:not(.template)');
        items.forEach(item => item.remove());

        // Add first item row
        addItemRow();

        // Show modal
        modal.style.display = 'block';
    }

    function editPhieuXuatKho(id) {
        fetch(`/admin/xuatkho/${id}`)
            .then(response => response.json())
            .then(data => {
                modalTitle.textContent = 'Cập Nhật Phiếu Xuất Kho';

                // Set basic info
                document.getElementById('phieuXuatKhoId').value = data.idPhieuXuatKho;
                document.getElementById('ngayXuat').value = new Date(data.ngayXuat).toISOString().split('T')[0];
                document.getElementById('nhanVien').value = data.idNhanVien || '';

                // Clear existing items except template
                const items = itemsContainer.querySelectorAll('.item-row:not(.template)');
                items.forEach(item => item.remove());

                // Add item rows for each chi tiết
                if (data.chiTietXuatKhoList && data.chiTietXuatKhoList.length > 0) {
                    data.chiTietXuatKhoList.forEach(chiTiet => {
                        const row = addItemRow();
                        const nguyenLieuSelect = row.querySelector('.nguyen-lieu-select');
                        const soLuongInput = row.querySelector('.soLuong');

                        nguyenLieuSelect.value = chiTiet.idNguyenLieu;
                        soLuongInput.value = chiTiet.soLuong;
                    });
                } else {
                    addItemRow();
                }

                // Show modal
                modal.style.display = 'block';
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Đã xảy ra lỗi khi tải thông tin phiếu xuất kho');
            });
    }

    function viewPhieuXuatKho(id) {
        fetch(`/admin/xuatkho/${id}`)
            .then(response => response.json())
            .then(data => {
                // Populate detail fields
                document.getElementById('detailsId').textContent = data.idPhieuXuatKho;
                document.getElementById('detailsNgayXuat').textContent = new Date(data.ngayXuat).toLocaleDateString('vi-VN');
                document.getElementById('detailsIdNhanVien').textContent = data.idNhanVien || 'N/A';
                document.getElementById('detailsTenNhanVien').textContent = data.tenNhanVien || 'N/A';

                // Clear and populate chi tiết table
                const tableBody = document.getElementById('chiTietTableBody');
                tableBody.innerHTML = '';

                if (data.chiTietXuatKhoList && data.chiTietXuatKhoList.length > 0) {
                    data.chiTietXuatKhoList.forEach(chiTiet => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td>${chiTiet.idNguyenLieu}</td>
                            <td>${chiTiet.tenNguyenLieu}</td>
                            <td>${chiTiet.soLuong}</td>
                        `;
                        tableBody.appendChild(row);
                    });
                } else {
                    const row = document.createElement('tr');
                    row.innerHTML = '<td colspan="3" class="text-center">Không có dữ liệu</td>';
                    tableBody.appendChild(row);
                }

                // Show modal
                detailsModal.style.display = 'block';
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Đã xảy ra lỗi khi tải thông tin phiếu xuất kho');
            });
    }

    function openDeleteModal(id) {
        document.getElementById('confirmDeleteBtn').setAttribute('data-id', id);
        deleteModal.style.display = 'block';
    }

    function addItemRow() {
        // Clone template
        const template = document.querySelector('.item-row.template');
        const newRow = template.cloneNode(true);

        // Set up the new row
        newRow.classList.remove('template');
        newRow.style.display = '';

        // Add to container
        itemsContainer.appendChild(newRow);

        // Update indices
        updateItemIndices();

        return newRow;
    }

    function updateItemIndices() {
        const rows = itemsContainer.querySelectorAll('.item-row:not(.template)');
        rows.forEach((row, index) => {
            // Update names with correct indices
            const nguyenLieuSelect = row.querySelector('.nguyen-lieu-select');
            const soLuongInput = row.querySelector('.soLuong');

            nguyenLieuSelect.name = `chiTietXuatKhoList[${index}].idNguyenLieu`;
            soLuongInput.name = `chiTietXuatKhoList[${index}].soLuong`;
        });
    }

    function loadEmployeeData() {
        fetch('/admin/employee/all')
            .then(response => response.json())
            .then(data => {
                const nhanVienSelect = document.getElementById('nhanVien');

                // Clear existing options
                const defaultOption = nhanVienSelect.querySelector('option');
                nhanVienSelect.innerHTML = '';
                nhanVienSelect.appendChild(defaultOption);

                // Add employee options
                data.forEach(employee => {
                    const option = document.createElement('option');
                    option.value = employee.id;
                    option.textContent = `${employee.lastName} ${employee.firstName}`;
                    nhanVienSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error loading employee data:', error);
            });
    }

    function loadNguyenLieuData() {
        fetch('/admin/nguyenlieu/all')
            .then(response => response.json())
            .then(data => {
                // Store the data for later use when adding rows
                window.nguyenLieuData = data;

                // Update the template's select options
                updateNguyenLieuOptions(document.querySelector('.item-row.template .nguyen-lieu-select'));
            })
            .catch(error => {
                console.error('Error loading nguyen lieu data:', error);
            });
    }

    function updateNguyenLieuOptions(selectElement) {
        if (!window.nguyenLieuData) return;

        // Clear existing options
        const defaultOption = selectElement.querySelector('option');
        selectElement.innerHTML = '';
        selectElement.appendChild(defaultOption);

        // Add nguyen lieu options
        window.nguyenLieuData.forEach(nguyenLieu => {
            const option = document.createElement('option');
            option.value = nguyenLieu.idNguyenLieu;
            option.textContent = nguyenLieu.tenNguyenLieu;
            selectElement.appendChild(option);
        });
    }

    function checkTableEmpty() {
        const visibleRows = Array.from(xuatKhoTable.querySelectorAll('tbody tr'))
            .filter(row => row.style.display !== 'none');

        if (visibleRows.length === 0) {
            emptyState.style.display = 'block';
            xuatKhoTable.style.display = 'none';
        } else {
            emptyState.style.display = 'none';
            xuatKhoTable.style.display = 'table';
        }
    }

    function validateForm() {
        // Basic validation
        const ngayXuat = document.getElementById('ngayXuat').value;
        const nhanVien = document.getElementById('nhanVien').value;

        if (!ngayXuat) {
            alert('Vui lòng chọn ngày xuất');
            return false;
        }

        if (!nhanVien) {
            alert('Vui lòng chọn nhân viên');
            return false;
        }

        // Check if there are any items
        const items = itemsContainer.querySelectorAll('.item-row:not(.template)');
        if (items.length === 0) {
            alert('Vui lòng thêm ít nhất một nguyên liệu');
            return false;
        }

        // Check each item
        let valid = true;
        items.forEach((item, index) => {
            const nguyenLieu = item.querySelector('.nguyen-lieu-select').value;
            const soLuong = item.querySelector('.soLuong').value;

            if (!nguyenLieu) {
                alert(`Vui lòng chọn nguyên liệu cho dòng ${index + 1}`);
                valid = false;
                return;
            }

            if (!soLuong || soLuong <= 0) {
                alert(`Vui lòng nhập số lượng hợp lệ cho dòng ${index + 1}`);
                valid = false;
                return;
            }
        });

        return valid;
    }

    function formToJson(formData) {
        const jsonObject = {};

        // Handle basic fields
        for (const [key, value] of formData.entries()) {
            // Skip chiTietXuatKhoList, we'll handle it separately
            if (!key.startsWith('chiTietXuatKhoList')) {
                jsonObject[key] = value;
            }
        }

        // Handle complex structure for chiTietXuatKhoList
        const chiTietXuatKhoList = [];
        const items = itemsContainer.querySelectorAll('.item-row:not(.template)');

        items.forEach((item, index) => {
            const idNguyenLieu = item.querySelector('.nguyen-lieu-select').value;
            const soLuong = item.querySelector('.soLuong').value;

            chiTietXuatKhoList.push({
                idNguyenLieu: parseInt(idNguyenLieu),
                soLuong: parseInt(soLuong)
            });
        });

        jsonObject.chiTietXuatKhoList = chiTietXuatKhoList;

        // Convert string ID to number if present
        if (jsonObject.idPhieuXuatKho && jsonObject.idPhieuXuatKho !== '') {
            jsonObject.idPhieuXuatKho = parseInt(jsonObject.idPhieuXuatKho);
        } else {
            delete jsonObject.idPhieuXuatKho; // Remove empty ID for new records
        }

        // Convert idNhanVien to number
        if (jsonObject.idNhanVien) {
            jsonObject.idNhanVien = parseInt(jsonObject.idNhanVien);
        }

        return jsonObject;
    }

    // Initialize table state
    function initialize() {
        checkTableEmpty();
    }

    // Call initialize
    initialize();
});