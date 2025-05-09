document.addEventListener('DOMContentLoaded', function() {
    // Token CSRF
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // Elements
    const xuatKhoModal = document.getElementById('xuatKhoModal');
    const xuatKhoDetailsModal = document.getElementById('xuatKhoDetailsModal');
    const deleteModal = document.getElementById('deleteModal');
    const xuatKhoForm = document.getElementById('xuatKhoForm');
    const openAddModalBtn = document.getElementById('openAddModal');
    const closeButtons = document.querySelectorAll('.close-btn');
    const cancelBtn = document.getElementById('cancelBtn');
    const closeViewBtn = document.getElementById('closeViewBtn');
    const searchInput = document.querySelector('.search-input');
    const xuatKhoTable = document.getElementById('xuatKhoTable');
    const emptyState = document.getElementById('emptyState');
    const notificationElement = document.getElementById('notification');
    const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    const paginationElement = document.getElementById('usersPagination');
    const addItemBtn = document.getElementById('addItemBtn');
    const itemsContainer = document.getElementById('itemsContainer');

    // Lấy trang từ URL
    function getPageFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        const page = urlParams.get('pageNo');
        const pageNumber = page && !isNaN(parseInt(page)) ? parseInt(page) : 1;
        console.log("Lấy trang từ URL:", pageNumber);
        return pageNumber;
    }

    // Khởi tạo biến
    let currentPage = getPageFromUrl();
    console.log("Khởi tạo trang hiện tại:", currentPage);

    // Các biến khác
    let currentDeleteId = null;
    const itemsPerPage = 10;
    let xuatKhoData = [];
    let totalServerPages = 1;
    let itemCount = 0;

    // Flag để nhận biết trạng thái khởi tạo ban đầu
    let initializing = true;
    // Flag để theo dõi xem có đang lọc hay không
    let isFiltering = false;

    // Khởi tạo
    initializeXuatKhoData();
    loadDropdownData();

    // Xử lý sự kiện khi mở modal thêm phiếu xuất kho
    openAddModalBtn.addEventListener('click', function() {
        resetForm();
        document.getElementById('modalTitle').textContent = 'Thêm Phiếu Xuất Kho';
        // Set ngày xuất mặc định là ngày hiện tại
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('ngayXuat').value = today;
        xuatKhoForm.setAttribute('action', '/admin/xuatkho/add');
        xuatKhoModal.style.display = 'block';
        // Thêm một dòng nguyên liệu mặc định
        addNewItem();
    });

    // Đóng modals
    closeButtons.forEach(button => {
        button.addEventListener('click', function() {
            xuatKhoModal.style.display = 'none';
            xuatKhoDetailsModal.style.display = 'none';
            deleteModal.style.display = 'none';
        });
    });

    cancelBtn.addEventListener('click', function() {
        xuatKhoModal.style.display = 'none';
    });

    closeViewBtn.addEventListener('click', function() {
        xuatKhoDetailsModal.style.display = 'none';
    });

    cancelDeleteBtn.addEventListener('click', function() {
        deleteModal.style.display = 'none';
    });

    // Xử lý nút thêm nguyên liệu
    addItemBtn.addEventListener('click', function() {
        addNewItem();
    });

    // Xóa dòng nguyên liệu
    document.addEventListener('click', function(e) {
        if (e.target.closest('.remove-item')) {
            const button = e.target.closest('.remove-item');
            const itemRow = button.closest('.item-row');
            if (document.querySelectorAll('.item-row:not(.template)').length > 1) {
                itemRow.remove();
                updateItemsIndexes();
            } else {
                showNotification('Phải có ít nhất một nguyên liệu', 'error');
            }
        }
    });

    // Xử lý nút Edit
    document.addEventListener('click', function(e) {
        if (e.target.closest('.edit-btn')) {
            const button = e.target.closest('.edit-btn');
            const phieuId = button.getAttribute('data-id');
            fetchPhieuXuatKhoDetails(phieuId, true);
        }
    });

    // Xử lý nút Delete
    document.addEventListener('click', function(e) {
        if (e.target.closest('.delete-btn')) {
            const button = e.target.closest('.delete-btn');
            const phieuId = button.getAttribute('data-id');
            currentDeleteId = phieuId;
            deleteModal.style.display = 'block';
        }
    });

    // Xác nhận xóa
    confirmDeleteBtn.addEventListener('click', function() {
        if (currentDeleteId) {
            deletePhieuXuatKho(currentDeleteId);
            deleteModal.style.display = 'none';
        }
    });

    // Xử lý nút View
    document.addEventListener('click', function(e) {
        if (e.target.closest('.view-btn')) {
            const button = e.target.closest('.view-btn');
            const phieuId = button.getAttribute('data-id');
            fetchPhieuXuatKhoDetails(phieuId, false);
        }
    });

    // Tìm kiếm
    searchInput.addEventListener('input', function() {
        // Cập nhật flag lọc
        isFiltering = this.value.trim() !== '';

        // Chỉ reset trang khi có giá trị tìm kiếm
        if (isFiltering) {
            currentPage = 1;
        }
        filterXuatKho();
    });

    // Khởi tạo dữ liệu
    function initializeXuatKhoData() {
        xuatKhoData = [];
        const rows = xuatKhoTable.querySelectorAll('tbody tr');

        rows.forEach(row => {
            const id = row.querySelector('td[data-label="ID Phiếu"]').textContent;
            const nhanVien = row.querySelector('td[data-label="Nhân viên"]').textContent;
            const ngayXuat = row.querySelector('td[data-label="Ngày xuất"]').textContent;
            const soMatHang = row.querySelector('td[data-label="Số mặt hàng"]').textContent;

            xuatKhoData.push({
                id, nhanVien, ngayXuat, soMatHang,
                element: row
            });
        });

        // Lấy tổng số trang từ HTML
        const paginationInfo = paginationElement?.querySelector('.users-page-info .users-page-link');
        if (paginationInfo) {
            const pageInfoText = paginationInfo.textContent;
            const matches = pageInfoText.match(/Trang\s+(\d+)\s+\/\s+(\d+)/);
            if (matches && matches.length >= 3) {
                totalServerPages = parseInt(matches[2]) || 1;
                console.log(`Đã lấy từ HTML: totalPages=${totalServerPages}`);
            }
        }

        // Trong quá trình khởi tạo, không áp dụng lọc
        isFiltering = false;

        // Khi khởi tạo, chỉ hiển thị tất cả người dùng đã tải từ server
        showInitialServerData();

        // Đánh dấu hoàn thành khởi tạo
        initializing = false;
    }

    // Hàm mới: hiển thị dữ liệu ban đầu đã được tải từ server
    function showInitialServerData() {
        console.log("updateTable (initial): currentPage =", currentPage);

        // Khi trang web mới tải, dữ liệu đã được hiển thị theo phân trang từ server
        // Chỉ đảm bảo rằng các phần tử UI được cập nhật chính xác
        emptyState.style.display = xuatKhoData.length > 0 ? 'none' : 'block';

        // Đảm bảo tất cả các hàng đều được hiển thị (server đã xử lý phân trang)
        xuatKhoData.forEach(phieu => {
            phieu.element.style.display = '';
        });

        // Cập nhật phân trang dựa trên giá trị từ server
        console.log("Trước updatePagination (initial): currentPage =", currentPage);
        updatePagination(totalServerPages, currentPage);
    }

    // Lọc phiếu xuất kho
    function filterXuatKho() {
        const searchValue = searchInput.value.toLowerCase();

        const filteredPhieu = xuatKhoData.filter(phieu => {
            return !searchValue ||
                phieu.id.toLowerCase().includes(searchValue) ||
                phieu.nhanVien.toLowerCase().includes(searchValue) ||
                phieu.ngayXuat.toLowerCase().includes(searchValue);
        });

        updateFilteredTable(filteredPhieu);
    }

    // Cập nhật bảng sau khi lọc
    function updateFilteredTable(filteredPhieu) {
        // Ẩn tất cả rows
        xuatKhoData.forEach(phieu => {
            phieu.element.style.display = 'none';
        });

        // Hiển thị rows đã lọc
        if (filteredPhieu.length > 0) {
            emptyState.style.display = 'none';

            // Tính toán tổng số trang dựa trên dữ liệu đã lọc
            const totalFilteredPages = Math.ceil(filteredPhieu.length / itemsPerPage);
            let effectiveTotalPages = totalFilteredPages;

            // Nếu không lọc và có giá trị tổng trang từ server, sử dụng giá trị từ server
            if (!isFiltering && totalServerPages > 0) {
                effectiveTotalPages = totalServerPages;
                console.log("Sử dụng tổng số trang từ server:", effectiveTotalPages);
            }

            // Kiểm tra giới hạn trang
            if (isFiltering && currentPage > totalFilteredPages) {
                currentPage = 1;
            }

            // Tính chỉ số bắt đầu và kết thúc cho dữ liệu đã lọc
            const startIndex = (currentPage - 1) * itemsPerPage;
            const endIndex = Math.min(startIndex + itemsPerPage, filteredPhieu.length);

            // Hiển thị rows của trang hiện tại
            if (isFiltering) {
                // Khi đang lọc, hiển thị theo phân trang client-side
                console.log(`Hiển thị dữ liệu đã lọc (trang ${currentPage}): ${startIndex} đến ${endIndex-1}`);
                for (let i = startIndex; i < endIndex; i++) {
                    filteredPhieu[i].element.style.display = '';
                }
            } else {
                // Khi KHÔNG lọc, hiển thị TẤT CẢ dữ liệu đã được phân trang từ server
                console.log("Hiển thị dữ liệu từ server (không lọc)");
                filteredPhieu.forEach(phieu => {
                    phieu.element.style.display = '';
                });
            }

            // Debug log
            console.log("Trước updatePagination: currentPage =", currentPage);

            // Cập nhật phân trang
            updatePagination(effectiveTotalPages, currentPage);
        } else {
            // Trạng thái trống
            emptyState.style.display = 'block';
            if (paginationElement) {
                paginationElement.innerHTML = '';
            }
        }
    }

    // Cập nhật bảng
    function updateTable() {
        console.log("updateTable: currentPage =", currentPage);
        // Khi gọi bằng lệnh mà không phải hàm khởi tạo ban đầu
        if (isFiltering) {
            // Nếu đang lọc, làm theo quy trình lọc
            filterXuatKho();
        } else {
            // Nếu không lọc, hiển thị tất cả dữ liệu hiện tại
            updateFilteredTable(xuatKhoData);
        }
    }

    // Cập nhật UI phân trang
    function updatePagination(totalPages, currentPage) {
        if (!paginationElement) return;

        totalPages = Math.max(1, totalPages);
        console.log("Cập nhật phân trang với currentPage:", currentPage, "và totalPages:", totalPages);

        let paginationHTML = '';

        // Nút đầu trang
        paginationHTML += `
        <li class="users-page-item ${currentPage === 1 ? 'disabled' : ''}">
            <a class="users-page-link" href="javascript:void(0);" data-page="1" aria-label="Đầu trang">
                <i class="fas fa-angle-double-left"></i>
            </a>
        </li>`;

        // Nút trang trước
        paginationHTML += `
        <li class="users-page-item ${currentPage === 1 ? 'disabled' : ''}">
            <a class="users-page-link" href="javascript:void(0);" data-page="${currentPage - 1}" aria-label="Trang trước">
                <i class="fas fa-angle-left"></i>
            </a>
        </li>`;

        // Thông tin trang hiện tại
        paginationHTML += `
        <li class="users-page-item users-page-info">
            <span class="users-page-link">
                Trang <span>${currentPage}</span> / <span>${totalPages}</span>
            </span>
        </li>`;

        // Nút trang sau
        paginationHTML += `
        <li class="users-page-item ${currentPage >= totalPages ? 'disabled' : ''}">
            <a class="users-page-link" href="javascript:void(0);" data-page="${currentPage + 1}" aria-label="Trang sau">
                <i class="fas fa-angle-right"></i>
            </a>
        </li>`;

        // Nút cuối trang
        paginationHTML += `
        <li class="users-page-item ${currentPage >= totalPages ? 'disabled' : ''}">
            <a class="users-page-link" href="javascript:void(0);" data-page="${totalPages}" aria-label="Cuối trang">
                <i class="fas fa-angle-double-right"></i>
            </a>
        </li>`;

        // Gán HTML vào phần tử phân trang
        paginationElement.innerHTML = paginationHTML;

        // Sự kiện click cho nút phân trang
        document.querySelectorAll('#usersPagination .users-page-link[data-page]').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const page = parseInt(this.getAttribute('data-page'));
                if (!isNaN(page) && page !== currentPage && page > 0 && page <= totalPages) {
                    if (isFiltering) {
                        // Xử lý phân trang client-side
                        currentPage = page;
                        console.log("Chuyển trang (client):", currentPage);
                        filterXuatKho();
                    } else {
                        // Chuyển trang server-side
                        console.log("Chuyển trang (server) đến:", page);
                        window.location.href = `/admin/xuatkho?pageNo=${page}`;
                    }
                }
            });
        });
    }

    // Reset form
    function resetForm() {
        xuatKhoForm.reset();
        document.getElementById('phieuXuatKhoId').value = '';

        // Xóa tất cả các dòng nguyên liệu hiện tại
        const itemRows = document.querySelectorAll('.item-row:not(.template)');
        itemRows.forEach(row => row.remove());

        // Reset biến đếm
        itemCount = 0;
    }

    // Thêm dòng nguyên liệu mới
    function addNewItem() {
        const template = document.querySelector('.item-row.template');
        const newRow = template.cloneNode(true);
        newRow.classList.remove('template');
        newRow.style.display = '';

        // Cập nhật index
        updateItemIndexes(newRow, itemCount);

        itemsContainer.appendChild(newRow);
        itemCount++;

        // Tải danh sách nguyên liệu cho select box mới
        setupNguyenLieuSelect(newRow.querySelector('.nguyen-lieu-select'));
    }

    // Cập nhật index cho tất cả các dòng
    function updateItemsIndexes() {
        const rows = document.querySelectorAll('.item-row:not(.template)');
        rows.forEach((row, index) => {
            updateItemIndexes(row, index);
        });
    }

    // Cập nhật index cho một dòng cụ thể
    function updateItemIndexes(row, index) {
        const nguyenLieuSelect = row.querySelector('.nguyen-lieu-select');
        const soLuongInput = row.querySelector('.soLuong');

        if (nguyenLieuSelect) {
            nguyenLieuSelect.name = `chiTietXuatKhoList[${index}].idNguyenLieu`;
        }

        if (soLuongInput) {
            soLuongInput.name = `chiTietXuatKhoList[${index}].soLuong`;
        }
    }

    // Tải dữ liệu dropdown (nhân viên và nguyên liệu)
    function loadDropdownData() {
        // Tải danh sách nhân viên
        fetch('/api/nhanvien/list', {
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải danh sách nhân viên');
                }
                return response.json();
            })
            .then(data => {
                const nhanVienSelect = document.getElementById('nhanVien');
                nhanVienSelect.innerHTML = '<option value="">-- Chọn nhân viên --</option>';

                data.forEach(nv => {
                    const option = document.createElement('option');
                    option.value = nv.id;
                    option.textContent = `${nv.hoTen} (${nv.id})`;
                    nhanVienSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error loading nhan vien:', error);
                showNotification('Không thể tải danh sách nhân viên', 'error');
            });
    }

    // Setup select box nguyên liệu
    function setupNguyenLieuSelect(selectElement) {
        // Tải danh sách nguyên liệu từ API
        fetch('/api/nguyenlieu/available', {
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải danh sách nguyên liệu');
                }
                return response.json();
            })
            .then(data => {
                selectElement.innerHTML = '<option value="">-- Chọn nguyên liệu --</option>';

                data.forEach(nl => {
                    const option = document.createElement('option');
                    option.value = nl.id;
                    option.textContent = `${nl.tenNguyenLieu} (${nl.donVi}) - Còn ${nl.soLuong}`;
                    option.setAttribute('data-available', nl.soLuong);
                    selectElement.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error loading nguyen lieu:', error);
                showNotification('Không thể tải danh sách nguyên liệu', 'error');
            });
    }

    // Lấy chi tiết phiếu xuất kho
    function fetchPhieuXuatKhoDetails(phieuId, isEdit) {
        fetch(`/api/xuatkho/${phieuId}`, {
            method: 'GET',
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải chi tiết phiếu xuất kho');
                }
                return response.json();
            })
            .then(data => {
                if (isEdit) {
                    populateEditForm(data);
                } else {
                    showPhieuXuatKhoDetails(data);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Đã xảy ra lỗi khi tải chi tiết phiếu xuất kho', 'error');
            });
    }

    // Điền dữ liệu vào form chỉnh sửa
    function populateEditForm(data) {
        resetForm();

        document.getElementById('modalTitle').textContent = 'Chỉnh Sửa Phiếu Xuất Kho';
        document.getElementById('phieuXuatKhoId').value = data.idPhieuXuatKho;

        // Format ngày về định dạng YYYY-MM-DD cho input date
        const ngayXuat = new Date(data.ngayXuat);
        const formattedDate = ngayXuat.toISOString().split('T')[0];
        document.getElementById('ngayXuat').value = formattedDate;

        // Chọn nhân viên
        if (data.idNhanVien) {
            document.getElementById('nhanVien').value = data.idNhanVien;
        }

        // Thêm các dòng nguyên liệu
        if (data.chiTietXuatKhoList && data.chiTietXuatKhoList.length > 0) {
            data.chiTietXuatKhoList.forEach((chiTiet, index) => {
                addNewItem();

                // Timeout để đảm bảo selectbox đã được tạo và tải dữ liệu
                setTimeout(() => {
                    const rows = document.querySelectorAll('.item-row:not(.template)');
                    const lastRow = rows[rows.length - 1];
                    const nguyenLieuSelect = lastRow.querySelector('.nguyen-lieu-select');
                    const soLuongInput = lastRow.querySelector('.soLuong');

                    if (nguyenLieuSelect && soLuongInput) {
                        // Kiểm tra xem option đã tồn tại chưa
                        if (nguyenLieuSelect.querySelector(`option[value="${chiTiet.idNguyenLieu}"]`)) {
                            nguyenLieuSelect.value = chiTiet.idNguyenLieu;
                        } else {
                            // Nếu chưa có, tạo option mới
                            const option = document.createElement('option');
                            option.value = chiTiet.idNguyenLieu;
                            option.textContent = chiTiet.tenNguyenLieu || `Nguyên liệu #${chiTiet.idNguyenLieu}`;
                            nguyenLieuSelect.appendChild(option);
                            nguyenLieuSelect.value = chiTiet.idNguyenLieu;
                        }

                        soLuongInput.value = chiTiet.soLuong;
                    }
                }, 500);
            });
        } else {
            // Nếu không có chi tiết, thêm một dòng trống
            addNewItem();
        }

        xuatKhoForm.setAttribute('action', '/admin/xuatkho/update');
        xuatKhoModal.style.display = 'block';
    }

    // Hiển thị chi tiết phiếu xuất kho
    function showPhieuXuatKhoDetails(data) {
        document.getElementById('detailsId').textContent = data.idPhieuXuatKho || 'N/A';

        // Format ngày thành dd/MM/yyyy
        const ngayXuat = data.ngayXuat ? new Date(data.ngayXuat) : null;
        const formattedDate = ngayXuat ?
            `${ngayXuat.getDate().toString().padStart(2, '0')}/${(ngayXuat.getMonth() + 1).toString().padStart(2, '0')}/${ngayXuat.getFullYear()}` :
            'N/A';
        document.getElementById('detailsNgayXuat').textContent = formattedDate;

        document.getElementById('detailsIdNhanVien').textContent = data.idNhanVien || 'N/A';
        document.getElementById('detailsTenNhanVien').textContent = data.tenNhanVien || 'N/A';

        // Hiển thị danh sách chi tiết
        const chiTietTableBody = document.getElementById('chiTietTableBody');
        chiTietTableBody.innerHTML = '';

        if (data.chiTietXuatKhoList && data.chiTietXuatKhoList.length > 0) {
            data.chiTietXuatKhoList.forEach(chiTiet => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${chiTiet.idNguyenLieu || 'N/A'}</td>
                    <td>${chiTiet.tenNguyenLieu || 'N/A'}</td>
                    <td>${chiTiet.soLuong || '0'}</td>
                `;
                chiTietTableBody.appendChild(row);
            });
        } else {
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = `
                <td colspan="3" style="text-align: center;">Không có dữ liệu chi tiết</td>
            `;
            chiTietTableBody.appendChild(emptyRow);
        }

        xuatKhoDetailsModal.style.display = 'block';
    }

    // Xóa phiếu xuất kho
    function deletePhieuXuatKho(phieuId) {
        fetch(`/admin/xuatkho/${phieuId}`, {
            method: 'DELETE',
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể xóa phiếu xuất kho');
                }
                return response.text();
            })
            .then(() => {
                const phieuIndex = xuatKhoData.findIndex(phieu => phieu.id === phieuId);
                if (phieuIndex !== -1) {
                    xuatKhoData.splice(phieuIndex, 1);
                }

                showNotification('Xóa phiếu xuất kho thành công');
                updateTable();

                setTimeout(() => {
                    window.location.reload();
                }, 100);
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Đã xảy ra lỗi khi xóa phiếu xuất kho', 'error');
            });
    }

    // Hiển thị thông báo
    function showNotification(message, type = 'success') {
        if (!notificationElement) return;

        notificationElement.textContent = message;
        notificationElement.className = 'notification ' + type;
        notificationElement.style.display = 'block';

        setTimeout(() => {
            notificationElement.style.display = 'none';
        }, 5000);
    }

    // Thêm hoặc cập nhật phiếu xuất kho
    xuatKhoForm.addEventListener('submit', function(e) {
        e.preventDefault();

        // Kiểm tra xem đã chọn nhân viên chưa
        const nhanVienId = document.getElementById('nhanVien').value;
        if (!nhanVienId) {
            showNotification('Vui lòng chọn nhân viên xuất kho', 'error');
            return;
        }

        // Kiểm tra các dòng nguyên liệu
        const rows = document.querySelectorAll('.item-row:not(.template)');
        if (rows.length === 0) {
            showNotification('Vui lòng thêm ít nhất một nguyên liệu', 'error');
            return;
        }

        // Kiểm tra từng dòng
        let valid = true;
        rows.forEach(row => {
            const nguyenLieuId = row.querySelector('.nguyen-lieu-select').value;
            const soLuong = row.querySelector('.soLuong').value;

            if (!nguyenLieuId || !soLuong || parseInt(soLuong) <= 0) {
                valid = false;
            }
        });

        if (!valid) {
            showNotification('Vui lòng kiểm tra lại thông tin nguyên liệu', 'error');
            return;
        }

        // Kiểm tra nguyên liệu trùng
        const nguyenLieuIds = Array.from(rows).map(row => row.querySelector('.nguyen-lieu-select').value);
        const uniqueIds = new Set(nguyenLieuIds);
        if (uniqueIds.size !== nguyenLieuIds.length) {
            showNotification('Các nguyên liệu không được trùng nhau', 'error');
            return;
        }

        // Kiểm tra số lượng với số lượng tồn
        let quantityValid = true;
        rows.forEach(row => {
            const select = row.querySelector('.nguyen-lieu-select');
            const soLuong = parseInt(row.querySelector('.soLuong').value);

            if (select.selectedIndex > 0) {
                const option = select.options[select.selectedIndex];
                const available = parseInt(option.getAttribute('data-available') || 0);

                if (soLuong > available) {
                    quantityValid = false;
                    showNotification(`Nguyên liệu "${option.textContent}" chỉ còn ${available} đơn vị`, 'error');
                }
            }
        });

        if (!quantityValid) {
            return;
        }

        // Tất cả kiểm tra hợp lệ, gửi form
        const formData = new FormData(this);
        const action = this.getAttribute('action');
        const method = 'POST';

        fetch(action, {
            method: method,
            body: formData,
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Có lỗi xảy ra');
                }
                return response.text();
            })
            .then(data => {
                showNotification('Lưu phiếu xuất kho thành công');
                xuatKhoModal.style.display = 'none';

                // Reload page after successful operation
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Đã xảy ra lỗi khi lưu phiếu xuất kho', 'error');
            });
    });

    // Xử lý validate số lượng nguyên liệu
    document.addEventListener('change', function(e) {
        if (e.target.matches('.nguyen-lieu-select')) {
            const select = e.target;
            const soLuongInput = select.closest('.form-row').querySelector('.soLuong');

            if (select.selectedIndex > 0) {
                const option = select.options[select.selectedIndex];
                const available = parseInt(option.getAttribute('data-available') || 0);
                soLuongInput.max = available;

                // Thêm tooltip hiển thị số lượng tồn
                soLuongInput.setAttribute('title', `Số lượng tồn: ${available}`);

                // Reset số lượng nếu đã nhập
                if (parseInt(soLuongInput.value) > available) {
                    soLuongInput.value = available;
                }
            }
        }
    });

    // Xử lý validate khi nhập số lượng
    document.addEventListener('input', function(e) {
        if (e.target.matches('.soLuong')) {
            const input = e.target;
            const value = parseInt(input.value);
            const max = parseInt(input.max);

            if (value <= 0) {
                input.value = 1;
            } else if (max && value > max) {
                input.value = max;
                showNotification(`Số lượng không được vượt quá ${max}`, 'warning');
            }
        }
    });

    // Xử lý validation cho form
    document.addEventListener('input', function(e) {
        if (e.target.matches('input[required], select[required]')) {
            validateInput(e.target);
        }
    });

    document.addEventListener('change', function(e) {
        if (e.target.matches('input[required], select[required]')) {
            validateInput(e.target);
        }
    });

    function validateInput(input) {
        if (!input.value) {
            input.classList.add('invalid');
            if (!input.nextElementSibling || !input.nextElementSibling.classList.contains('error-message')) {
                const errorMessage = document.createElement('div');
                errorMessage.classList.add('error-message');
                errorMessage.textContent = 'Trường này là bắt buộc';
                input.parentNode.insertBefore(errorMessage, input.nextSibling);
            }
        } else {
            input.classList.remove('invalid');
            if (input.nextElementSibling && input.nextElementSibling.classList.contains('error-message')) {
                input.nextElementSibling.remove();
            }
        }
    }

    // Xử lý khi nhấn các phím
    document.addEventListener('keydown', function(e) {
        // Đóng modal khi nhấn ESC
        if (e.key === 'Escape') {
            xuatKhoModal.style.display = 'none';
            xuatKhoDetailsModal.style.display = 'none';
            deleteModal.style.display = 'none';
        }
    });

    // Close notification when clicked
    if (notificationElement) {
        notificationElement.addEventListener('click', function() {
            this.style.display = 'none';
        });
    }

    // Auto hide notification after 5 seconds
    if (notificationElement && notificationElement.textContent.trim() !== '') {
        setTimeout(() => {
            notificationElement.style.display = 'none';
        }, 5000);
    }

    // Xử lý export Excel
    document.querySelector('.btn-outline').addEventListener('click', function() {
        exportToExcel();
    });

    function exportToExcel() {
        fetch('/admin/xuatkho/export', {
            method: 'GET',
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể xuất dữ liệu');
                }
                return response.blob();
            })
            .then(blob => {
                // Tạo URL cho blob
                const url = window.URL.createObjectURL(blob);

                // Tạo thẻ a để tải xuống
                const downloadLink = document.createElement('a');
                downloadLink.href = url;
                downloadLink.download = 'phieu-xuat-kho.xlsx';

                // Thêm thẻ a vào body
                document.body.appendChild(downloadLink);

                // Click vào thẻ a để tải xuống
                downloadLink.click();

                // Xóa thẻ a
                document.body.removeChild(downloadLink);

                // Giải phóng URL
                window.URL.revokeObjectURL(url);
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Đã xảy ra lỗi khi xuất dữ liệu', 'error');
            });
    }

    // Hàm kiểm tra trạng thái hiển thị của bảng
    function checkTableVisibility() {
        const rows = xuatKhoTable.querySelectorAll('tbody tr');
        let visibleCount = 0;

        rows.forEach(row => {
            if (row.style.display !== 'none') {
                visibleCount++;
            }
        });

        if (visibleCount === 0) {
            emptyState.style.display = 'block';
        } else {
            emptyState.style.display = 'none';
        }
    }

    // Xử lý khi thay đổi kích thước cửa sổ
    window.addEventListener('resize', function() {
        adjustTableResponsiveness();
    });

    // Điều chỉnh bảng để responsive
    function adjustTableResponsiveness() {
        const windowWidth = window.innerWidth;
        const table = document.getElementById('xuatKhoTable');

        if (windowWidth < 768) {
            table.classList.add('mobile-view');
        } else {
            table.classList.remove('mobile-view');
        }
    }

    // Khởi tạo responsive
    adjustTableResponsiveness();

    // Lắng nghe sự kiện kéo chuột để phân trang
    let touchstartX = 0;
    let touchendX = 0;

    document.querySelector('.users-table-container').addEventListener('touchstart', function(e) {
        touchstartX = e.changedTouches[0].screenX;
    });

    document.querySelector('.users-table-container').addEventListener('touchend', function(e) {
        touchendX = e.changedTouches[0].screenX;
        handleSwipe();
    });

    function handleSwipe() {
        // Swipe left to next page
        if (touchendX < touchstartX - 100) {
            const nextPageLink = document.querySelector('.users-page-item:nth-last-child(2) .users-page-link');
            if (nextPageLink && !nextPageLink.parentElement.classList.contains('disabled')) {
                nextPageLink.click();
            }
        }

        // Swipe right to previous page
        if (touchendX > touchstartX + 100) {
            const prevPageLink = document.querySelector('.users-page-item:nth-child(2) .users-page-link');
            if (prevPageLink && !prevPageLink.parentElement.classList.contains('disabled')) {
                prevPageLink.click();
            }
        }
    }
});