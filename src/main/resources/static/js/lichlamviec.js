// Document ready function
document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('scheduleModal');
    const closeModal = document.getElementsByClassName('close')[0];
    const addScheduleBtn = document.getElementById('addScheduleBtn');
    const scheduleForm = document.getElementById('scheduleForm');
    const searchInput = document.getElementById('searchInput');

    // Load employees and shifts when the page loads
    loadEmployees();
    loadShifts();

    // Add event listeners
    addScheduleBtn.addEventListener('click', function() {
        openModal('add');
    });

    closeModal.addEventListener('click', function() {
        modal.style.display = 'none';
    });

    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });

    scheduleForm.addEventListener('submit', function(e) {
        e.preventDefault();
        saveSchedule();
    });

    searchInput.addEventListener('input', function() {
        searchSchedules();
    });
});

// Global variables
let currentAction = 'add';
const token = document.querySelector('meta[name="_csrf"]').content;
const header = document.querySelector('meta[name="_csrf_header"]').content;

// Functions
function openModal(action, scheduleId = null) {
    currentAction = action;
    const modal = document.getElementById('scheduleModal');
    const modalTitle = document.getElementById('modalTitle');

    // Reset form
    document.getElementById('scheduleForm').reset();
    document.getElementById('scheduleId').value = '';

    if (action === 'add') {
        modalTitle.textContent = 'Thêm lịch làm việc';
        // Set today's date as default
        const today = new Date();
        const dateStr = today.toISOString().split('T')[0];
        document.getElementById('workDate').value = dateStr;
    } else if (action === 'edit' && scheduleId) {
        modalTitle.textContent = 'Cập nhật lịch làm việc';
        document.getElementById('scheduleId').value = scheduleId;
        fetchScheduleDetails(scheduleId);
    } else if (action === 'view' && scheduleId) {
        modalTitle.textContent = 'Xem lịch làm việc';
        fetchScheduleDetails(scheduleId);
        // Disable form fields for view mode
        document.querySelectorAll('#scheduleForm input, #scheduleForm select').forEach(el => {
            el.disabled = true;
        });
        document.querySelector('#scheduleForm button[type="submit"]').style.display = 'none';
    }

    modal.style.display = 'block';
}

function closeModal() {
    const modal = document.getElementById('scheduleModal');
    modal.style.display = 'none';

    // Re-enable form fields and submit button (for when coming from view mode)
    document.querySelectorAll('#scheduleForm input, #scheduleForm select').forEach(el => {
        el.disabled = false;
    });
    document.querySelector('#scheduleForm button[type="submit"]').style.display = 'block';
}

function loadEmployees() {
    fetch('/admin/employee/all')
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('userId');
            select.innerHTML = '<option value="">-- Chọn nhân viên --</option>';

            data.forEach(employee => {
                const option = document.createElement('option');
                option.value = employee.id;
                option.textContent = employee.fullName;
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error loading employees:', error);
            showNotification('Lỗi khi tải danh sách nhân viên', 'error');
        });
}

function loadShifts() {
    fetch('/admin/calamviec/all')
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('shiftId');
            select.innerHTML = '<option value="">-- Chọn ca làm việc --</option>';

            data.forEach(shift => {
                const option = document.createElement('option');
                option.value = shift.idCa;
                option.textContent = `Ca ${shift.idCa}: ${formatTime(new Date(shift.gioBatDau))} - ${formatTime(new Date(shift.gioKetThuc))}`;
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error loading shifts:', error);
            showNotification('Lỗi khi tải danh sách ca làm việc', 'error');
        });
}

function fetchScheduleDetails(scheduleId) {
    fetch(`/admin/lichlamviec/${scheduleId}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('userId').value = data.idUser;

            // Format the date for input[type=date]
            const workDate = new Date(data.ngayLam);
            const formattedDate = workDate.toISOString().split('T')[0];
            document.getElementById('workDate').value = formattedDate;

            document.getElementById('shiftId').value = data.idCa;
        })
        .catch(error => {
            console.error('Error fetching schedule details:', error);
            showNotification('Lỗi khi tải thông tin lịch làm việc', 'error');
        });
}

function saveSchedule() {
    const scheduleId = document.getElementById('scheduleId').value;
    const userId = document.getElementById('userId').value;
    const workDate = document.getElementById('workDate').value;
    const shiftId = document.getElementById('shiftId').value;

    // Validate form
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

    // Determine API endpoint based on action
    const url = '/api/lichlamviec' + (scheduleId ? '/update' : '/add');

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token
        },
        body: JSON.stringify(scheduleData)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success || data.message) {
                showNotification(data.message || 'Lưu lịch làm việc thành công', 'success');
                closeModal();
                // Reload page to show updated data
                window.location.reload();
            } else {
                showNotification('Có lỗi xảy ra', 'error');
            }
        })
        .catch(error => {
            console.error('Error saving schedule:', error);
            showNotification('Lỗi khi lưu lịch làm việc', 'error');
        });
}

function viewSchedule(scheduleId) {
    openModal('view', scheduleId);
}

function editSchedule(scheduleId) {
    openModal('edit', scheduleId);
}

function deleteSchedule(scheduleId) {
    if (confirm('Bạn có chắc chắn muốn xóa lịch làm việc này không?')) {
        fetch(`/api/lichlamviec/${scheduleId}`, {
            method: 'DELETE',
            headers: {
                [header]: token
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showNotification('Xóa lịch làm việc thành công', 'success');
                    // Reload page to show updated data
                    window.location.reload();
                } else {
                    showNotification(data.message || 'Có lỗi xảy ra khi xóa', 'error');
                }
            })
            .catch(error => {
                console.error('Error deleting schedule:', error);
                showNotification('Lỗi khi xóa lịch làm việc', 'error');
            });
    }
}

function searchSchedules() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const rows = document.querySelectorAll('table tbody tr');

    rows.forEach(row => {
        const employeeName = row.cells[1].textContent.toLowerCase();
        const date = row.cells[2].textContent.toLowerCase();
        const shift = row.cells[3].textContent.toLowerCase();

        const matchesSearch = employeeName.includes(searchTerm) ||
            date.includes(searchTerm) ||
            shift.includes(searchTerm);

        row.style.display = matchesSearch ? '' : 'none';
    });
}

// Helper functions
function formatTime(date) {
    return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
}

function showNotification(message, type = 'info') {
    // Check if notification container exists
    let notificationContainer = document.getElementById('notification-container');

    if (!notificationContainer) {
        // Create container if it doesn't exist
        notificationContainer = document.createElement('div');
        notificationContainer.id = 'notification-container';
        notificationContainer.style.position = 'fixed';
        notificationContainer.style.top = '20px';
        notificationContainer.style.right = '20px';
        notificationContainer.style.zIndex = '9999';
        document.body.appendChild(notificationContainer);
    }

    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <span>${message}</span>
            <button class="close-notification">&times;</button>
        </div>
    `;

    // Style the notification
    notification.style.backgroundColor = type === 'success' ? '#4CAF50' :
        type === 'error' ? '#f44336' : '#2196F3';
    notification.style.color = 'white';
    notification.style.padding = '16px';
    notification.style.marginBottom = '15px';
    notification.style.borderRadius = '4px';
    notification.style.boxShadow = '0 2px 5px rgba(0,0,0,0.2)';
    notification.style.display = 'flex';
    notification.style.justifyContent = 'space-between';
    notification.style.alignItems = 'center';
    notification.style.minWidth = '300px';

    // Add close button event
    const closeBtn = notification.querySelector('.close-notification');
    closeBtn.style.background = 'none';
    closeBtn.style.border = 'none';
    closeBtn.style.color = 'white';
    closeBtn.style.fontSize = '20px';
    closeBtn.style.cursor = 'pointer';
    closeBtn.style.marginLeft = '15px';

    closeBtn.addEventListener('click', function() {
        notificationContainer.removeChild(notification);
    });

    // Add to container
    notificationContainer.appendChild(notification);

    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentNode === notificationContainer) {
            notificationContainer.removeChild(notification);
        }
    }, 5000);
}