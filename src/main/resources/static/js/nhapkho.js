document.addEventListener('DOMContentLoaded', function() {
    // Token CSRF
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // Elements
    const phieuNhapKhoModal = document.getElementById('phieuNhapKhoModal');
    const viewPhieuModal = document.getElementById('viewPhieuModal');
    const deleteModal = document.getElementById('deleteModal');
    const phieuNhapKhoForm = document.getElementById('phieuNhapKhoForm');
    const openAddModalBtn = document.getElementById('openAddModal');
    const closeButtons = document.querySelectorAll('.close-btn');
    const cancelBtn = document.getElementById('cancelBtn');
    const closeViewBtn = document.getElementById('closeViewBtn');
    const closeViewModalBtn = document.getElementById('closeViewModalBtn');
    const searchInput = document.querySelector('.search-input');
    const phieuNhapKhoTable = document.getElementById('phieuNhapKhoTable');
    const emptyState = document.getElementById('emptyState');
    const notificationElement = document.getElementById('notification');
    const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    const paginationElement = document.getElementById('phieuNhapKhoPagination');
    const addItemBtn = document.getElementById('addItemBtn');
    const chiTietTableBody = document.getElementById('chiTietTableBody');
    const tongTienElement = document.getElementById('tongTien');

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
    let phieuNhapKhoData = [];
    let totalServerPages = 1;
    let nguyenLieuList = [];
    let nhaCungCapList = [];
    let nhanVienList = [];
    let chiTietCounter = 0;

    // Flag để nhận biết trạng thái khởi tạo ban đầu
    let initializing = true;
    // Flag để theo dõi xem có đang lọc hay không
    let isFiltering = false;

    // Khởi tạo
    initializePhieuNhapKhoData();
    loadNguyenLieuData();
    loadNhaCungCapData();
    loadNhanVienData();

    // Xử lý sự kiện khi mở modal thêm phiếu nhập kho
    openAddModalBtn.addEventListener('click', function() {
        resetForm();
        document.getElementById('modalTitle').textContent = 'Thêm Phiếu Nhập Kho';
        phieuNhapKhoForm.setAttribute('action', '/admin/nhapkho/add');
        phieuNhapKhoModal.style.display = 'block';

        // Set default date to today
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('ngayNhap').value = today;
    });

    // Đóng modals
    closeButtons.forEach(button => {
        button.addEventListener('click', function() {
            phieuNhapKhoModal.style.display = 'none';
            viewPhieuModal.style.display = 'none';
            deleteModal.style.display = 'none';
        });
    });

    cancelBtn.addEventListener('click', function() {
        phieuNhapKhoModal.style.display = 'none';
    });

    closeViewBtn.addEventListener('click', function() {
        viewPhieuModal.style.display = 'none';
    });

    closeViewModalBtn.addEventListener('click', function() {
        viewPhieuModal.style.display = 'none';
    });

    cancelDeleteBtn.addEventListener('click', function() {
        deleteModal.style.display = 'none';
    });

    // Xử lý nút Edit
    document.addEventListener('click', function(e) {
        if (e.target.closest('.edit-btn')) {
            const button = e.target.closest('.edit-btn');
            const phieuId = button.getAttribute('data-id');
            editPhieuNhapKho(phieuId);
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
            deletePhieuNhapKho(currentDeleteId);
            deleteModal.style.display = 'none';
        }
    });

    // Xử lý nút View
    document.addEventListener('click', function(e) {
        if (e.target.closest('.view-btn')) {
            const button = e.target.closest('.view-btn');
            const phieuId = button.getAttribute('data-id');
            viewPhieuNhapKho(phieuId);
        }
    });

    // Lọc và tìm kiếm
    searchInput.addEventListener('input', function() {
        // Cập nhật flag lọc
        isFiltering = this.value.trim() !== '';

        // Chỉ reset trang khi có giá trị tìm kiếm
        if (isFiltering) {
            currentPage = 1;
        }
        filterPhieuNhapKho();
    });

    // Xử lý thêm chi tiết nguyên liệu
    addItemBtn.addEventListener('click', function() {
        addChiTietRow();
    });

    // Khởi tạo dữ liệu
    function initializePhieuNhapKhoData() {
        phieuNhapKhoData = [];
        const rows = phieuNhapKhoTable.querySelectorAll('tbody tr');

        rows.forEach(row => {
            const id = row.querySelector('td[data-label="ID"]').textContent;
            const nhaCungCap = row.querySelector('td[data-label="Nhà Cung Cấp"]').textContent;
            const nhanVien = row.querySelector('td[data-label="Nhân Viên"]').textContent;
            const ngayNhap = row.querySelector('td[data-label="Ngày Nhập"]').textContent;
            const soMatHang = row.querySelector('td[data-label="Số Mặt Hàng"]').textContent;
            const trangThai = row.querySelector('td[data-label="Trạng thái"] .badge').textContent;

            phieuNhapKhoData.push({
                id, nhaCungCap, nhanVien, ngayNhap, soMatHang, trangThai,
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

        // Khi khởi tạo, chỉ hiển thị tất cả phiếu nhập kho đã tải từ server
        showInitialServerData();

        // Đánh dấu hoàn thành khởi tạo
        initializing = false;
    }

    // Hàm mới: hiển thị dữ liệu ban đầu đã được tải từ server
    function showInitialServerData() {
        console.log("updateTable (initial): currentPage =", currentPage);

        // Khi trang web mới tải, phiếu nhập kho đã được hiển thị theo phân trang từ server
        // Chỉ đảm bảo rằng các phần tử UI được cập nhật chính xác
        emptyState.style.display = 'none';

        // Đảm bảo tất cả các hàng đều được hiển thị (server đã xử lý phân trang)
        phieuNhapKhoData.forEach(phieu => {
            phieu.element.style.display = '';
        });

        // Cập nhật phân trang dựa trên giá trị từ server
        console.log("Trước updatePagination (initial): currentPage =", currentPage);
        updatePagination(totalServerPages, currentPage);
    }

    // Lọc phiếu nhập kho
    function filterPhieuNhapKho() {
        const searchValue = searchInput.value.toLowerCase();

        const filteredPhieuNhapKho = phieuNhapKhoData.filter(phieu => {
            const matchesSearch = !searchValue ||
                phieu.id.toLowerCase().includes(searchValue) ||
                phieu.nhaCungCap.toLowerCase().includes(searchValue) ||
                phieu.nhanVien.toLowerCase().includes(searchValue) ||
                phieu.ngayNhap.toLowerCase().includes(searchValue) ||
                phieu.trangThai.toLowerCase().includes(searchValue);

            return matchesSearch;
        });

        updateFilteredTable(filteredPhieuNhapKho);
    }

    // Cập nhật bảng sau khi lọc
    function updateFilteredTable(filteredPhieuNhapKho) {
        // Ẩn tất cả rows
        phieuNhapKhoData.forEach(phieu => {
            phieu.element.style.display = 'none';
        });

        // Hiển thị rows đã lọc
        if (filteredPhieuNhapKho.length > 0) {
            emptyState.style.display = 'none';

            // Tính toán tổng số trang dựa trên dữ liệu đã lọc
            const totalFilteredPages = Math.ceil(filteredPhieuNhapKho.length / itemsPerPage);
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
            const endIndex = Math.min(startIndex + itemsPerPage, filteredPhieuNhapKho.length);

            // Hiển thị rows của trang hiện tại
            if (isFiltering) {
                // Khi đang lọc, hiển thị theo phân trang client-side
                console.log(`Hiển thị dữ liệu đã lọc (trang ${currentPage}): ${startIndex} đến ${endIndex-1}`);
                for (let i = startIndex; i < endIndex; i++) {
                    filteredPhieuNhapKho[i].element.style.display = '';
                }
            } else {
                // Khi KHÔNG lọc, hiển thị TẤT CẢ dữ liệu đã được phân trang từ server
                console.log("Hiển thị dữ liệu từ server (không lọc)");
                filteredPhieuNhapKho.forEach(phieu => {
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
            filterPhieuNhapKho();
        } else {
            // Nếu không lọc, hiển thị tất cả phiếu nhập kho hiện tại
            updateFilteredTable(phieuNhapKhoData);
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
        document.querySelectorAll('#phieuNhapKhoPagination .users-page-link[data-page]').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const page = parseInt(this.getAttribute('data-page'));
                if (!isNaN(page) && page !== currentPage && page > 0 && page <= totalPages) {
                    if (isFiltering) {
                        // Xử lý phân trang client-side
                        currentPage = page;
                        console.log("Chuyển trang (client):", currentPage);
                        filterPhieuNhapKho();
                    } else {
                        // Chuyển trang server-side
                        console.log("Chuyển trang (server) đến:", page);
                        window.location.href = `/admin/nhapkho?pageNo=${page}`;
                    }
                }
            });
        });
    }

    // Reset form
    function resetForm() {
        phieuNhapKhoForm.reset();
        document.getElementById('phieuNhapKhoId').value = '';

        // Reset chi tiết table
        chiTietTableBody.innerHTML = '';
        chiTietCounter = 0;
        updateTongTien();

        // Thêm hàng chi tiết mặc định
        addChiTietRow();
    }

    // Thêm hàng chi tiết nhập kho
    function addChiTietRow() {
        chiTietCounter++;
        const row = document.createElement('tr');
        row.setAttribute('data-index', chiTietCounter);

        row.innerHTML = `
        <td>${chiTietCounter}</td>
        <td>
            <select name="chiTietNhapKhoList[${chiTietCounter-1}].idNguyenLieu" class="nguyen-lieu-select" required onchange="updateDonVi(this)">
                <option value="">-- Chọn nguyên liệu --</option>
                ${nguyenLieuList.map(nl => `<option value="${nl.id}" data-donvi="${nl.donViTinh}">${nl.tenNguyenLieu}</option>`).join('')}
            </select>
        </td>
        <td>
            <input type="number" name="chiTietNhapKhoList[${chiTietCounter-1}].soLuong" min="1" required class="so-luong" onchange="updateThanhTien(this.closest('tr'))">
        </td>
        <td>
            <input type="text" name="chiTietNhapKhoList[${chiTietCounter-1}].donViTinh" class="don-vi" readonly>
        </td>
        <td>
            <input type="number" name="chiTietNhapKhoList[${chiTietCounter-1}].giaTien" min="0" required class="don-gia" onchange="updateThanhTien(this.closest('tr'))">
        </td>
        <td class="thanh-tien">0 VNĐ</td>
        <td>
            <button type="button" class="btn btn-danger delete-item-btn" onclick="deleteChiTietRow(this)">
                <i class="fas fa-trash"></i>
            </button>
        </td>
    `;

        chiTietTableBody.appendChild(row);
    }

    // Load dữ liệu nguyên liệu
    function loadNguyenLieuData() {
        fetch('/admin/nguyenlieu/all', {
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải dữ liệu nguyên liệu');
                }
                return response.json();
            })
            .then(data => {
                nguyenLieuList = data;

                // Thêm hàng chi tiết mặc định
                if (chiTietTableBody.children.length === 0) {
                    addChiTietRow();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Đã xảy ra lỗi khi tải dữ liệu nguyên liệu', 'error');
            });
    }

    // Load dữ liệu nhà cung cấp
    function loadNhaCungCapData() {
        fetch('/admin/nhacungcap', {
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải dữ liệu nhà cung cấp');
                }
                return response.json();
            })
            .then(data => {
                nhaCungCapList = data;
                const selectElement = document.getElementById('nhaCungCap');
                selectElement.innerHTML = '<option value="">-- Chọn nhà cung cấp --</option>';

                data.forEach(ncc => {
                    const option = document.createElement('option');
                    option.value = ncc.id;
                    option.textContent = ncc.tenNhaCungCap;
                    selectElement.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Đã xảy ra lỗi khi tải dữ liệu nhà cung cấp', 'error');
            });
    }

    // Load dữ liệu nhân viên
    function loadNhanVienData() {
        fetch('/admin/employee/all', {
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải dữ liệu nhân viên');
                }
                return response.json();
            })
            .then(data => {
                nhanVienList = data;
                const selectElement = document.getElementById('nhanVien');
                selectElement.innerHTML = '<option value="">-- Chọn nhân viên --</option>';

                data.forEach(nv => {
                    const option = document.createElement('option');
                    option.value = nv.id;
                    option.textContent = nv.hoTen;
                    selectElement.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Đã xảy ra lỗi khi tải dữ liệu nhân viên', 'error');
            });
    }

    // View phiếu nhập kho
    function viewPhieuNhapKho(phieuId) {
        fetch(`/admin/nhapkho/${phieuId}`, {
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải dữ liệu phiếu nhập kho');
                }
                return response.json();
            })
            .then(phieu => {
                document.getElementById('detailsId').textContent = phieu.idPhieuNhapKho;
                document.getElementById('detailsNgayNhap').textContent = formatDate(phieu.ngayNhap);
                document.getElementById('detailsNhaCungCap').textContent = phieu.tenNhaCungCap || 'Không xác định';
                document.getElementById('detailsNhanVien').textContent = phieu.tenNhanVien || 'Không xác định';
                document.getElementById('detailsGhiChu').textContent = phieu.ghiChu || 'Không có ghi chú';

                // Hiển thị chi tiết nhập kho
                const chiTietTableBody = document.getElementById('viewChiTietTableBody');
                chiTietTableBody.innerHTML = '';

                let tongTien = 0;

                phieu.chiTietNhapKhoList.forEach((chiTiet, index) => {
                    const row = document.createElement('tr');
                    const thanhTien = chiTiet.soLuong * chiTiet.giaTien;
                    tongTien += thanhTien;

                    row.innerHTML = `
                        <td>${index + 1}</td>
                        <td>${chiTiet.tenNguyenLieu}</td>
                        <td>${chiTiet.soLuong}</td>
                        <td>${chiTiet.donViTinh || 'N/A'}</td>
                        <td>${formatCurrency(chiTiet.giaTien)}</td>
                        <td>${formatCurrency(thanhTien)}</td>
                    `;

                    chiTietTableBody.appendChild(row);
                });

                document.getElementById('viewTongTien').textContent = formatCurrency(tongTien);

                viewPhieuModal.style.display = 'block';
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Đã xảy ra lỗi khi tải dữ liệu phiếu nhập kho', 'error');
            });
    }

    // Edit phiếu nhập kho
    function editPhieuNhapKho(phieuId) {
        fetch(`/admin/nhapkho/${phieuId}`, {
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải dữ liệu phiếu nhập kho');
                }
                return response.json();
            })
            .then(phieu => {
                resetForm();
                document.getElementById('modalTitle').textContent = 'Chỉnh Sửa Phiếu Nhập Kho';
                phieuNhapKhoForm.setAttribute('action', '/admin/nhapkho/update');

                // Điền dữ liệu phiếu
                document.getElementById('phieuNhapKhoId').value = phieu.idPhieuNhapKho;
                document.getElementById('ngayNhap').value = formatDateInput(phieu.ngayNhap);
                document.getElementById('nhaCungCap').value = phieu.idNhaCungCap;
                document.getElementById('nhanVien').value = phieu.idNhanVien;
                document.getElementById('ghiChu').value = phieu.ghiChu || '';

                // Xóa hàng chi tiết mặc định
                chiTietTableBody.innerHTML = '';
                chiTietCounter = 0;

                // Thêm các hàng chi tiết
                phieu.chiTietNhapKhoList.forEach((chiTiet, index) => {
                    chiTietCounter++;
                    const row = document.createElement('tr');
                    row.setAttribute('data-index', chiTietCounter);

                    row.innerHTML = `
                        <td>${chiTietCounter}</td>
                        <td>
                            <select name="chiTietNhapKhoList[${chiTietCounter-1}].idNguyenLieu" class="nguyen-lieu-select" required onchange="updateDonVi(this)">
                                <option value="">-- Chọn nguyên liệu --</option>
                                ${nguyenLieuList.map(nl => `<option value="${nl.id}" data-donvi="${nl.donViTinh}" ${nl.id == chiTiet.idNguyenLieu ? 'selected' : ''}>${nl.tenNguyenLieu}</option>`).join('')}
                            </select>
                        </td>
                        <td>
                            <input type="number" name="chiTietNhapKhoList[${chiTietCounter-1}].soLuong" min="1" required class="so-luong" value="${chiTiet.soLuong}" onchange="updateThanhTien(this.closest('tr'))">
                        </td>
                        <td>
                            <input type="text" name="chiTietNhapKhoList[${chiTietCounter-1}].donViTinh" class="don-vi" readonly value="${chiTiet.donViTinh || ''}">
                        </td>
                        <td>
                            <input type="number" name="chiTietNhapKhoList[${chiTietCounter-1}].giaTien" min="0" required class="don-gia" value="${chiTiet.giaTien}" onchange="updateThanhTien(this.closest('tr'))">
                        </td>
                        <td class="thanh-tien">${formatCurrency(chiTiet.soLuong * chiTiet.giaTien)}</td>
                        <td>
                            <button type="button" class="btn btn-danger delete-item-btn" onclick="deleteChiTietRow(this)">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    `;

                    chiTietTableBody.appendChild(row);

                    // Cập nhật đơn vị
                    const selectElement = row.querySelector('.nguyen-lieu-select');
                    const donViInput = row.querySelector('.don-vi');
                    updateDonVi(selectElement);
                });

                // Cập nhật tổng tiền
                updateTongTien();

                phieuNhapKhoModal.style.display = 'block';
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Đã xảy ra lỗi khi tải dữ liệu phiếu nhập kho', 'error');
            });
    }

    // Xóa phiếu nhập kho
    function deletePhieuNhapKho(phieuId) {
        fetch(`/admin/nhapkho/delete/${phieuId}`, {
            method: 'DELETE',
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể xóa phiếu nhập kho');
                }
                return response.json();
            })
            .then(data => {
                showNotification(data.message || 'Xóa phiếu nhập kho thành công', 'success');

                // Xóa dòng khỏi bảng
                const phieuToRemove = phieuNhapKhoData.find(p => p.id === phieuId);
                if (phieuToRemove) {
                    phieuToRemove.element.remove();

                    // Cập nhật mảng dữ liệu
                    phieuNhapKhoData = phieuNhapKhoData.filter(p => p.id !== phieuId);

                    // Cập nhật bảng
                    updateTable();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Đã xảy ra lỗi khi xóa phiếu nhập kho: ' + error.message, 'error');
            });
    }

    // Hiển thị thông báo
    function showNotification(message, type = 'success') {
        if (!notificationElement) return;

        notificationElement.textContent = message;
        notificationElement.className = 'notification ' + type;
        notificationElement.style.display = 'block';

        // Tự động ẩn sau 5 giây
        setTimeout(() => {
            notificationElement.style.display = 'none';
        }, 5000);
    }

    // Format ngày tháng
    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    }

    // Format ngày tháng cho input date
    function formatDateInput(dateString) {
        const date = new Date(dateString);
        return date.toISOString().split('T')[0];
    }

    // Format số tiền
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND',
            minimumFractionDigits: 0
        }).format(amount).replace('₫', 'VNĐ');
    }

    // Xử lý submit form
    phieuNhapKhoForm.addEventListener('submit', function(e) {
        e.preventDefault();

        // Kiểm tra tính hợp lệ của form
        if (!this.checkValidity()) {
            this.reportValidity();
            return;
        }

        // Lấy URL action từ form
        const actionUrl = this.getAttribute('action');

        // Tạo FormData từ form
        const formData = new FormData(this);

        // Gửi request
        fetch(actionUrl, {
            method: 'POST',
            headers: {
                [header]: token
            },
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Có lỗi xảy ra khi lưu phiếu nhập kho');
                }
                return response.json();
            })
            .then(data => {
                showNotification(data.message || 'Lưu phiếu nhập kho thành công', 'success');
                phieuNhapKhoModal.style.display = 'none';

                // Sau khi lưu thành công, refresh trang để load dữ liệu mới
                setTimeout(() => {
                    window.location.reload();
                }, 500);
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Đã xảy ra lỗi: ' + error.message, 'error');
            });
    });
});

// Các hàm toàn cục cần được định nghĩa bên ngoài DOMContentLoaded
// vì chúng được gọi trực tiếp từ HTML

// Cập nhật đơn vị cho nguyên liệu
function updateDonVi(selectElement) {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    const donViInput = selectElement.closest('tr').querySelector('.don-vi');

    if (selectedOption && selectedOption.value) {
        const donVi = selectedOption.getAttribute('data-donvi') || '';
        donViInput.value = donVi;
    } else {
        donViInput.value = '';
    }

    // Cập nhật thành tiền khi thay đổi nguyên liệu
    updateThanhTien(selectElement.closest('tr'));
}

// Cập nhật thành tiền cho một hàng
function updateThanhTien(row) {
    const soLuongInput = row.querySelector('.so-luong');
    const donGiaInput = row.querySelector('.don-gia');
    const thanhTienCell = row.querySelector('.thanh-tien');

    const soLuong = parseFloat(soLuongInput.value) || 0;
    const donGia = parseFloat(donGiaInput.value) || 0;
    const thanhTien = soLuong * donGia;

    thanhTienCell.textContent = formatCurrency(thanhTien);

    // Cập nhật tổng tiền
    updateTongTien();
}

// Cập nhật tổng tiền cho toàn bộ phiếu
function updateTongTien() {
    const tongTienElement = document.getElementById('tongTien');
    if (!tongTienElement) return;

    let tongTien = 0;
    const thanhTienCells = document.querySelectorAll('#chiTietTableBody .thanh-tien');

    thanhTienCells.forEach(cell => {
        const thanhTienText = cell.textContent.replace(/[^\d]/g, '');
        const thanhTien = parseFloat(thanhTienText) || 0;
        tongTien += thanhTien;
    });

    tongTienElement.textContent = formatCurrency(tongTien);
}

// Xóa một hàng chi tiết
function deleteChiTietRow(button) {
    const row = button.closest('tr');
    const rowIndex = parseInt(row.getAttribute('data-index'));

    if (document.querySelectorAll('#chiTietTableBody tr').length > 1) {
        row.remove();

        // Cập nhật lại thứ tự và tên các field
        updateChiTietIndices();

        // Cập nhật tổng tiền
        updateTongTien();
    } else {
        alert('Phải có ít nhất một chi tiết nhập kho');
    }
}

// Cập nhật lại chỉ số cho các hàng chi tiết
function updateChiTietIndices() {
    const rows = document.querySelectorAll('#chiTietTableBody tr');

    rows.forEach((row, index) => {
        const stt = index + 1;
        row.querySelector('td:first-child').textContent = stt;

        // Cập nhật tên các field
        row.querySelectorAll('[name^="chiTietNhapKhoList["]').forEach(input => {
            const fieldName = input.name.replace(/chiTietNhapKhoList\[\d+\]/, `chiTietNhapKhoList[${index}]`);
            input.name = fieldName;
        });
    });
}

// Format số tiền toàn cục
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0
    }).format(amount).replace('₫', 'VNĐ');
}