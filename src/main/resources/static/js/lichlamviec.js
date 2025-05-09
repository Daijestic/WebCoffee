document.addEventListener('DOMContentLoaded', function() {
    // Token CSRF
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // Elements
    const scheduleModal = document.getElementById('scheduleModal');
    const scheduleForm = document.getElementById('scheduleForm');
    const addScheduleBtn = document.getElementById('addScheduleBtn');
    const closeBtn = document.querySelector('.close');
    const searchInput = document.getElementById('searchInput');
    const schedulesTable = document.querySelector('.table');

    // Khởi tạo biến
    let currentPage = getPageFromUrl();
    let scheduleData = [];
    const itemsPerPage = 10;
    let isFiltering = false;
    let totalServerPages = 1;

    // Khởi tạo dữ liệu
    initializeScheduleData();

    // Lấy trang từ URL
    function getPageFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        const page = urlParams.get('pageNo');
        return page && !isNaN(parseInt(page)) ? parseInt(page) : 1;
    }

    // Xử lý sự kiện khi mở modal thêm lịch làm việc
    addScheduleBtn.addEventListener('click', function() {
        resetForm();
        document.getElementById('modalTitle').textContent = 'Thêm lịch làm việc';
        loadUsers();
        loadShifts();
        scheduleForm.dataset.action = 'add';
        scheduleModal.style.display = 'block';
    });

    // Đóng modal
    closeBtn.addEventListener('click', function() {
        scheduleModal.style.display = 'none';
    });

    // Đóng modal bằng nút Cancel
    function closeModal() {
        scheduleModal.style.display = 'none';
    }

    // Tìm kiếm
    searchInput.addEventListener('input', function() {
        isFiltering = this.value.trim() !== '';
        if (isFiltering) {
            currentPage = 1;
        }
        filterSchedules();
    });

    // Khởi tạo dữ liệu lịch làm việc
    function initializeScheduleData() {
        scheduleData = [];
        const rows = schedulesTable.querySelectorAll('tbody tr');

        rows.forEach(row => {
            const cells = row.querySelectorAll('td');
            const id = cells[0].textContent;
            const employeeName = cells[1].textContent;
            const workDate = cells[2].textContent;
            const shiftId = cells[3].textContent;
            const startTime = cells[4].textContent;
            const endTime = cells[5].textContent;

            scheduleData.push({
                id, employeeName, workDate, shiftId, startTime, endTime,
                element: row
            });
        });

        // Lấy tổng số trang từ phân trang
        const paginationInfo = document.querySelector('.pagination .page-link span:last-child');
        if (paginationInfo) {
            totalServerPages = parseInt(paginationInfo.textContent) || 1;
        }

        isFiltering = false;
        showInitialServerData();
    }

    // Hiển thị dữ liệu ban đầu từ server
    function showInitialServerData() {
        // Đảm bảo tất cả các hàng đều được hiển thị (server đã xử lý phân trang)
        scheduleData.forEach(schedule => {
            schedule.element.style.display = '';
        });
    }

    // Lọc lịch làm việc
    function filterSchedules() {
        const searchValue = searchInput.value.toLowerCase();

        const filteredSchedules = scheduleData.filter(schedule => {
            return schedule.employeeName.toLowerCase().includes(searchValue) ||
                schedule.workDate.toLowerCase().includes(searchValue) ||
                schedule.shiftId.toLowerCase().includes(searchValue) ||
                schedule.startTime.toLowerCase().includes(searchValue) ||
                schedule.endTime.toLowerCase().includes(searchValue);
        });

        updateFilteredTable(filteredSchedules);
    }

    // Cập nhật bảng sau khi lọc
    function updateFilteredTable(filteredSchedules) {
        // Ẩn tất cả rows
        scheduleData.forEach(schedule => {
            schedule.element.style.display = 'none';
        });

        // Hiển thị rows đã lọc
        if (filteredSchedules.length > 0) {
            // Tính toán tổng số trang dựa trên dữ liệu đã lọc
            const totalFilteredPages = Math.ceil(filteredSchedules.length / itemsPerPage);
            let effectiveTotalPages = totalFilteredPages;

            // Nếu không lọc và có giá trị tổng trang từ server, sử dụng giá trị từ server
            if (!isFiltering && totalServerPages > 0) {
                effectiveTotalPages = totalServerPages;
            }

            // Kiểm tra giới hạn trang
            if (isFiltering && currentPage > totalFilteredPages) {
                currentPage = 1;
            }

            // Tính chỉ số bắt đầu và kết thúc cho dữ liệu đã lọc
            const startIndex = (currentPage - 1) * itemsPerPage;
            const endIndex = Math.min(startIndex + itemsPerPage, filteredSchedules.length);

            // Hiển thị rows của trang hiện tại
            if (isFiltering) {
                // Khi đang lọc, hiển thị theo phân trang client-side
                for (let i = startIndex; i < endIndex; i++) {
                    filteredSchedules[i].element.style.display = '';
                }
            } else {
                // Khi KHÔNG lọc, hiển thị TẤT CẢ dữ liệu đã được phân trang từ server
                filteredSchedules.forEach(schedule => {
                    schedule.element.style.display = '';
                });
            }

            // Cập nhật phân trang
            if (isFiltering) {
                updateClientPagination(effectiveTotalPages, currentPage);
            }
        } else {
            // Không có kết quả phù hợp
            const tbody = schedulesTable.querySelector('tbody');
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = `<td colspan="7" class="text-center">Không có kết quả phù hợp</td>`;
            tbody.innerHTML = '';
            tbody.appendChild(emptyRow);

            if (isFiltering) {
                updateClientPagination(1, 1);
            }
        }
    }

    // Cập nhật phân trang client-side
    function updateClientPagination(totalPages, currentPage) {
        const paginationContainer = document.querySelector('.pagination');
        if (!paginationContainer) return;

        totalPages = Math.max(1, totalPages);

        const paginationHTML = `
            <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="javascript:void(0);" data-page="1">
                    <i class="fas fa-angle-double-left"></i>
                </a>
            </li>
            <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="javascript:void(0);" data-page="${currentPage - 1}">
                    <i class="fas fa-angle-left"></i>
                </a>
            </li>
            <li class="page-item">
                <span class="page-link">
                    Trang <span>${currentPage}</span> / <span>${totalPages}</span>
                </span>
            </li>
            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                <a class="page-link" href="javascript:void(0);" data-page="${currentPage + 1}">
                    <i class="fas fa-angle-right"></i>
                </a>
            </li>
            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                <a class="page-link" href="javascript:void(0);" data-page="${totalPages}">
                    <i class="fas fa-angle-double-right"></i>
                </a>
            </li>
        `;

        paginationContainer.innerHTML = paginationHTML;

        // Sự kiện click cho nút phân trang
        document.querySelectorAll('.pagination .page-link[data-page]').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const page = parseInt(this.getAttribute('data-page'));
                if (!isNaN(page) && page !== currentPage && page > 0 && page <= totalPages) {
                    if (isFiltering) {
                        // Xử lý phân trang client-side
                        currentPage = page;
                        filterSchedules();
                    } else {
                        // Chuyển trang server-side
                        window.location.href = `/admin/lichlamviec?pageNo=${page}`;
                    }
                }
            });
        });
    }

    // Reset form
    function resetForm() {
        scheduleForm.reset();
        document.getElementById('scheduleId').value = '';
        document.getElementById('userId').innerHTML = '<option value="">Chọn nhân viên</option>';
        document.getElementById('shiftId').innerHTML = '<option value="">Chọn ca làm việc</option>';

        // Set default date to today
        const today = new Date();
        const formattedDate = today.toISOString().substr(0, 10);
        document.getElementById('workDate').value = formattedDate;
    }

    // Load danh sách nhân viên
    function loadUsers() {
        const userSelect = document.getElementById('userId');
        userSelect.innerHTML = '<option value="">Đang tải...</option>';

        fetch('/api/users/employees', {
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
            .then(users => {
                userSelect.innerHTML = '<option value="">Chọn nhân viên</option>';
                users.forEach(user => {
                    const option = document.createElement('option');
                    option.value = user.id;
                    option.textContent = user.hoTen;
                    userSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error:', error);
                userSelect.innerHTML = '<option value="">Lỗi tải danh sách</option>';
            });
    }

    // Load danh sách ca làm việc
    function loadShifts() {
        const shiftSelect = document.getElementById('shiftId');
        shiftSelect.innerHTML = '<option value="">Đang tải...</option>';

        fetch('/api/calamviec', {
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải danh sách ca làm việc');
                }
                return response.json();
            })
            .then(shifts => {
                shiftSelect.innerHTML = '<option value="">Chọn ca làm việc</option>';
                shifts.forEach(shift => {
                    const option = document.createElement('option');
                    option.value = shift.id;
                    option.textContent = `${shift.tenCa} (${formatTime(shift.gioBatDau)} - ${formatTime(shift.gioKetThuc)})`;
                    shiftSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error:', error);
                shiftSelect.innerHTML = '<option value="">Lỗi tải danh sách</option>';
            });
    }

    // Format thời gian
    function formatTime(timeString) {
        if (!timeString) return 'N/A';
        const date = new Date(timeString);
        return date.toLocaleTimeString('vi-VN', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    // Format ngày tháng
    function formatDate(dateString) {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    }

    // Xem chi tiết lịch làm việc
    window.viewSchedule = function(scheduleId) {
        fetch(`/api/lichlamviec/${scheduleId}`, {
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải chi tiết lịch làm việc');
                }
                return response.json();
            })
            .then(schedule => {
                // Tạo modal xem chi tiết (có thể tạo modal riêng hoặc sử dụng modal chỉnh sửa với các trường bị disable)
                alert(`Chi tiết lịch làm việc #${scheduleId}:
Nhân viên: ${schedule.tenUser}
Ngày làm: ${formatDate(schedule.ngayLam)}
Ca làm việc: ${schedule.tenCa}
Giờ vào: ${formatTime(schedule.gioVao)}
Giờ ra: ${formatTime(schedule.gioRa) || 'Chưa kết thúc'}`);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Đã xảy ra lỗi khi tải chi tiết lịch làm việc');
            });
    };

    // Chỉnh sửa lịch làm việc
    window.editSchedule = function(scheduleId) {
        fetch(`/api/lichlamviec/${scheduleId}`, {
            headers: {
                [header]: token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải chi tiết lịch làm việc');
                }
                return response.json();
            })
            .then(schedule => {
                document.getElementById('modalTitle').textContent = 'Chỉnh sửa lịch làm việc';
                document.getElementById('scheduleId').value = scheduleId;

                // Load danh sách và thiết lập giá trị
                loadUsers();
                loadShifts();

                // Đặt ngày làm việc
                const workDate = new Date(schedule.ngayLam);
                const formattedDate = workDate.toISOString().substr(0, 10);
                document.getElementById('workDate').value = formattedDate;

                // Đợi một chút để danh sách được tải xong trước khi thiết lập giá trị
                setTimeout(() => {
                    document.getElementById('userId').value = schedule.userId;
                    document.getElementById('shiftId').value = schedule.idCa;
                }, 1000);

                scheduleForm.dataset.action = 'edit';
                scheduleModal.style.display = 'block';
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Đã xảy ra lỗi khi tải chi tiết lịch làm việc');
            });
    };

    // Xóa lịch làm việc
    window.deleteSchedule = function(scheduleId) {
        if (confirm('Bạn có chắc chắn muốn xóa lịch làm việc này?')) {
            fetch(`/api/lichlamviec/${scheduleId}`, {
                method: 'DELETE',
                headers: {
                    [header]: token
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Không thể xóa lịch làm việc');
                    }
                    return response.json();
                })
                .then(data => {
                    alert('Xóa lịch làm việc thành công');
                    window.location.reload();
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Đã xảy ra lỗi khi xóa lịch làm việc');
                });
        }
    };

    // Xử lý form
    scheduleForm.addEventListener('submit', function(e) {
        e.preventDefault();

        const userId = document.getElementById('userId').value;
        const workDate = document.getElementById('workDate').value;
        const shiftId = document.getElementById('shiftId').value;
        const scheduleId = document.getElementById('scheduleId').value;

        if (!userId || !workDate || !shiftId) {
            alert('Vui lòng điền đầy đủ thông tin');
            return;
        }

        const formData = {
            userId,
            ngayLam: workDate,
            idCa: shiftId
        };

        if (scheduleId) {
            formData.idLichLam = scheduleId;
        }

        const action = scheduleForm.dataset.action;
        const url = action === 'edit' ? `/api/lichlamviec/${scheduleId}` : '/api/lichlamviec';
        const method = action === 'edit' ? 'PUT' : 'POST';

        fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errorData => {
                        throw new Error(errorData.message || `Lỗi khi ${action === 'edit' ? 'cập nhật' : 'thêm'} lịch làm việc`);
                    });
                }
                return response.json();
            })
            .then(data => {
                alert(`${action === 'edit' ? 'Cập nhật' : 'Thêm'} lịch làm việc thành công`);
                scheduleModal.style.display = 'none';
                window.location.reload();
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message || `Đã xảy ra lỗi khi ${action === 'edit' ? 'cập nhật' : 'thêm'} lịch làm việc`);
            });
    });

    // Đóng modal khi click bên ngoài
    window.addEventListener('click', function(e) {
        if (e.target === scheduleModal) {
            scheduleModal.style.display = 'none';
        }
    });
});