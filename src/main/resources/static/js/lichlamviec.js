document.addEventListener('DOMContentLoaded', function() {
    // DOM elements
    const modal = document.getElementById('scheduleModal');
    const modalTitle = document.getElementById('modalTitle');
    const closeBtn = document.querySelector('.close');
    const scheduleForm = document.getElementById('scheduleForm');
    const searchInput = document.getElementById('searchInput');
    const addScheduleBtn = document.getElementById('addScheduleBtn');

    // CSRF protection
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    // Initialize
    loadEmployees();
    loadShifts();

    // Event listeners
    addScheduleBtn.addEventListener('click', openAddModal);
    closeBtn.addEventListener('click', closeModal);
    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            closeModal();
        }
    });
    scheduleForm.addEventListener('submit', handleFormSubmit);
    searchInput.addEventListener('input', handleSearch);

    /**
     * Load all employees for the dropdown
     */
    function loadEmployees() {
        fetch('/admin/employee/all')
            .then(response => response.json())
            .then(data => {
                const userSelect = document.getElementById('userId');
                userSelect.innerHTML = '<option value="">-- Chọn nhân viên --</option>';

                data.forEach(user => {
                    const option = document.createElement('option');
                    option.value = user.id;
                    option.textContent = user.fullName ? user.fullName : user.username;
                    userSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error loading employees:', error);
                showNotification('Lỗi khi tải danh sách nhân viên', 'error');
            });
    }

    /**
     * Load all work shifts for the dropdown
     */
    function loadShifts() {
        fetch('/admin/calamviec/all')
            .then(response => response.json())
            .then(data => {
                const shiftSelect = document.getElementById('shiftId');
                shiftSelect.innerHTML = '<option value="">-- Chọn ca làm việc --</option>';

                data.forEach(shift => {
                    const option = document.createElement('option');
                    option.value = shift.idCa;
                    option.textContent = `Ca ${shift.idCa} (${formatTime(new Date(shift.gioBatDau))} - ${formatTime(new Date(shift.gioKetThuc))})`;
                    shiftSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error loading shifts:', error);
                showNotification('Lỗi khi tải danh sách ca làm việc', 'error');
            });
    }

    /**
     * Open modal to add a new schedule
     */
    function openAddModal() {
        modalTitle.textContent = 'Thêm lịch làm việc';
        document.getElementById('scheduleId').value = '';
        scheduleForm.reset();

        // Set default date to today
        const today = new Date();
        const formattedDate = today.toISOString().split('T')[0];
        document.getElementById('workDate').value = formattedDate;

        modal.style.display = 'block';
    }

    /**
     * Open modal to view schedule details
     */
    function viewSchedule(scheduleId) {
        fetchScheduleDetails(scheduleId, (scheduleData) => {
            modalTitle.textContent = 'Xem chi tiết lịch làm việc';
            fillFormWithScheduleData(scheduleData);

            // Disable form fields for view-only
            const formElements = scheduleForm.elements;
            for (let i = 0; i < formElements.length; i++) {
                formElements[i].disabled = true;
            }

            modal.style.display = 'block';
        });
    }

    /**
     * Open modal to edit a schedule
     */
    function editSchedule(scheduleId) {
        fetchScheduleDetails(scheduleId, (scheduleData) => {
            modalTitle.textContent = 'Chỉnh sửa lịch làm việc';
            fillFormWithScheduleData(scheduleData);

            // Enable form fields for editing
            const formElements = scheduleForm.elements;
            for (let i = 0; i < formElements.length; i++) {
                formElements[i].disabled = false;
            }

            modal.style.display = 'block';
        });
    }

    /**
     * Delete a schedule
     */
    function deleteSchedule(scheduleId) {
        if (confirm('Bạn có chắc chắn muốn xóa lịch làm việc này?')) {
            const headers = new Headers({
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            });

            fetch(`/admin/lichlamviec/${scheduleId}`, {
                method: 'DELETE',
                headers: headers
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showNotification(data.message, 'success');
                        setTimeout(() => {
                            window.location.reload();
                        }, 1500);
                    } else {
                        showNotification(data.message, 'error');
                    }
                })
                .catch(error => {
                    console.error('Error deleting schedule:', error);
                    showNotification('Lỗi khi xóa lịch làm việc', 'error');
                });
        }
    }

    /**
     * Close the modal
     */
    function closeModal() {
        modal.style.display = 'none';
        scheduleForm.reset();

        // Re-enable form elements in case they were disabled
        const formElements = scheduleForm.elements;
        for (let i = 0; i < formElements.length; i++) {
            formElements[i].disabled = false;
        }
    }

    /**
     * Handle form submission for adding/updating schedules
     */
    function handleFormSubmit(event) {
        event.preventDefault();

        const scheduleId = document.getElementById('scheduleId').value;
        const userId = document.getElementById('userId').value;
        const workDate = document.getElementById('workDate').value;
        const shiftId = document.getElementById('shiftId').value;

        // Validate the form
        if (!userId || !workDate || !shiftId) {
            showNotification('Vui lòng điền đầy đủ thông tin', 'error');
            return;
        }

        const scheduleData = {
            idLichLam: scheduleId || null,
            idUser: userId,
            ngayLam: workDate,
            idCa: shiftId
        };

        const isUpdate = scheduleId ? true : false;
        const url = isUpdate ? `/admin/lichlamviec/update` : `/admin/lichlamviec/add`;
        const method = isUpdate ? 'PUT' : 'POST';

        const headers = new Headers({
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        });

        fetch(url, {
            method: method,
            headers: headers,
            body: JSON.stringify(scheduleData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showNotification(data.message, 'success');
                    setTimeout(() => {
                        window.location.reload();
                    }, 1500);
                } else {
                    showNotification(data.message || 'Lỗi khi lưu lịch làm việc', 'error');
                }
            })
            .catch(error => {
                console.error('Error saving schedule:', error);
                showNotification('Lỗi khi lưu lịch làm việc', 'error');
            });
    }

    /**
     * Fetch schedule details by ID
     */
    function fetchScheduleDetails(scheduleId, callback) {
        fetch(`/admin/lichlamviec/${scheduleId}`)
            .then(response => response.json())
            .then(data => {
                if (callback && typeof callback === 'function') {
                    callback(data);
                }
            })
            .catch(error => {
                console.error('Error fetching schedule details:', error);
                showNotification('Lỗi khi tải thông tin lịch làm việc', 'error');
            });
    }

    /**
     * Fill form with schedule data
     */
    function fillFormWithScheduleData(scheduleData) {
        document.getElementById('scheduleId').value = scheduleData.idLichLam;
        document.getElementById('userId').value = scheduleData.idUser;

        // Format date for input
        const workDate = new Date(scheduleData.ngayLam);
        const formattedDate = workDate.toISOString().split('T')[0];
        document.getElementById('workDate').value = formattedDate;

        document.getElementById('shiftId').value = scheduleData.idCa;
    }

    /**
     * Handle search functionality
     */
    function handleSearch() {
        const searchTerm = searchInput.value.toLowerCase();
        const rows = document.querySelectorAll('tbody tr');

        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            if (text.includes(searchTerm)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }

    /**
     * Show notification
     */
    function showNotification(message, type = 'info') {
        // Check if notification container exists, if not create it
        let notificationContainer = document.getElementById('notification-container');
        if (!notificationContainer) {
            notificationContainer = document.createElement('div');
            notificationContainer.id = 'notification-container';
            notificationContainer.style.position = 'fixed';
            notificationContainer.style.top = '20px';
            notificationContainer.style.right = '20px';
            notificationContainer.style.zIndex = '1000';
            document.body.appendChild(notificationContainer);
        }

        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.style.padding = '15px';
        notification.style.marginBottom = '10px';
        notification.style.borderRadius = '4px';
        notification.style.boxShadow = '0 4px 8px rgba(0,0,0,0.1)';
        notification.style.minWidth = '250px';
        notification.style.animation = 'fadeIn 0.5s ease-out';

        // Set background color based on type
        switch (type) {
            case 'success':
                notification.style.backgroundColor = '#4CAF50';
                notification.style.color = 'white';
                break;
            case 'error':
                notification.style.backgroundColor = '#F44336';
                notification.style.color = 'white';
                break;
            case 'warning':
                notification.style.backgroundColor = '#FF9800';
                notification.style.color = 'white';
                break;
            default:
                notification.style.backgroundColor = '#2196F3';
                notification.style.color = 'white';
        }

        notification.innerHTML = `
            <div style="display: flex; align-items: center; justify-content: space-between;">
                <span>${message}</span>
                <span style="margin-left: 15px; cursor: pointer; font-weight: bold;" onclick="this.parentElement.parentElement.remove()">×</span>
            </div>
        `;

        notificationContainer.appendChild(notification);

        // Remove notification after 5 seconds
        setTimeout(() => {
            notification.style.animation = 'fadeOut 0.5s ease-out';
            setTimeout(() => {
                notification.remove();
            }, 500);
        }, 5000);
    }

    /**
     * Format time HH:MM
     */
    function formatTime(date) {
        if (!date || !(date instanceof Date) || isNaN(date)) return '';
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${hours}:${minutes}`;
    }

    // Define functions in global scope for use in HTML onclick attributes
    window.viewSchedule = viewSchedule;
    window.editSchedule = editSchedule;
    window.deleteSchedule = deleteSchedule;
    window.closeModal = closeModal;

    // Add CSS for notifications
    const style = document.createElement('style');
    style.textContent = `
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        @keyframes fadeOut {
            from { opacity: 1; transform: translateY(0); }
            to { opacity: 0; transform: translateY(-20px); }
        }
    `;
    document.head.appendChild(style);
});