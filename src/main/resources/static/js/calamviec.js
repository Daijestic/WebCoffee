// DOM Elements
document.addEventListener('DOMContentLoaded', function() {
    // Initialize the page
    initializePage();

    // Fix for "Thêm ca làm" button
    const addButton = document.querySelector('.btn-primary[onclick="openAddNguyenLieuModal()"]');
    if (addButton) {
        addButton.onclick = function() {
            openAddNguyenLieuModal();
        };
    }
});

// Make sure openAddNguyenLieuModal is in global scope for direct HTML onclick
window.openAddNguyenLieuModal = openAddNguyenLieuModal;
window.closeModal = closeModal;
window.closeModalAndEdit = closeModalAndEdit;
window.saveNguyenLieu = saveNguyenLieu;

// Global variables
let currentCaId = null;
let csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
let csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

/**
 * Initialize the page and attach event listeners
 */
function initializePage() {
    console.log("Initializing page and attaching event listeners...");

    // Add event listeners for action buttons using direct onclick property
    document.querySelectorAll('.btn-view').forEach(btn => {
        btn.onclick = function() {
            console.log("View button clicked");
            const row = this.closest('tr');
            const caId = row.cells[0].textContent;
            viewCaLamViec(caId);
        };
    });

    document.querySelectorAll('.btn-edit').forEach(btn => {
        btn.onclick = function() {
            console.log("Edit button clicked");
            const row = this.closest('tr');
            const caId = row.cells[0].textContent;
            editCaLamViec(caId);
        };
    });

    document.querySelectorAll('.btn-delete').forEach(btn => {
        btn.onclick = function() {
            console.log("Delete button clicked");
            const row = this.closest('tr');
            const caId = row.cells[0].textContent;
            deleteCaLamViec(caId);
        };
    });

    // Setup search functionality
    setupSearch();
}

/**
 * Open modal for adding a new work shift
 */
function openAddNguyenLieuModal() {
    currentCaId = null;
    document.getElementById('NguyenLieuModal').style.display = 'block';
    document.querySelector('.modalTitle').textContent = 'Thêm ca mới';

    // Reset form
    document.getElementById('gioBatDau').value = '';
    document.getElementById('gioKetThuc').value = '';
}

/**
 * Close modal by ID
 */
function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

/**
 * Close view modal and open edit modal
 */
function closeModalAndEdit(caId) {
    closeModal('viewNguyenLieuModal');
    editCaLamViec(caId);
}

/**
 * Save work shift (create new or update existing)
 */
function saveNguyenLieu(e) {
    console.log("saveNguyenLieu function called");
    if (e) e.preventDefault();
    if (window.event) window.event.preventDefault();

    // Validate form
    if (!validateForm()) {
        return;
    }

    // Get form data
    const gioBatDau = document.getElementById('gioBatDau').value;
    const gioKetThuc = document.getElementById('gioKetThuc').value;

    // Create request body
    const requestBody = {
        idCa: currentCaId,
        gioVao: formatTimeToDate(gioBatDau),
        gioRa: formatTimeToDate(gioKetThuc)
    };

    // API endpoint
    const url = currentCaId ? '/admin/calamviec/update' : '/admin/calamviec/add';

    // Send request
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(requestBody)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Lỗi khi lưu ca làm việc.');
            }
            return response.json();
        })
        .then(data => {
            showNotification(data.message || 'Lưu ca làm việc thành công.', 'success');
            closeModal('NguyenLieuModal');
            // Reload page to reflect changes
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        })
        .catch(error => {
            showNotification(error.message, 'error');
        });
}

/**
 * View work shift details
 */
function viewCaLamViec(caId) {
    fetch(`/admin/calamviec/${caId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Không thể tải thông tin ca làm việc.');
            }
            return response.json();
        })
        .then(data => {
            const detailsContainer = document.getElementById('NguyenLieuDetails');

            // Tính giờ kết thúc: nếu có gioRa thì dùng, nếu không thì lấy gioVao + 2 giờ
            const gioVao = new Date(data.gioVao);
            const gioRa = data.gioRa ? new Date(data.gioRa) : new Date(gioVao.getTime() + 2 * 60 * 60 * 1000);

            detailsContainer.innerHTML = `
                <h3>Ca ${data.idCa}</h3>
                <p><strong>Giờ bắt đầu:</strong> ${formatDateTime(gioVao)}</p>
                <p><strong>Giờ kết thúc:</strong> ${formatDateTime(gioRa)}</p>
            `;

            // Update edit button
            const editButton = document.querySelector('#viewNguyenLieuModal .edit');
            editButton.onclick = function () { closeModalAndEdit(data.idCa); };

            // Show modal
            document.getElementById('viewNguyenLieuModal').style.display = 'block';
        })
        .catch(error => {
            showNotification(error.message, 'error');
        });
}


/**
 * Edit work shift
 */
function editCaLamViec(caId) {
    fetch(`/admin/calamviec/${caId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Không thể tải thông tin ca làm việc.');
            }
            return response.json();
        })
        .then(data => {
            // Set current ID
            currentCaId = data.idCa;

            // Update modal title
            document.querySelector('.modalTitle').textContent = `Chỉnh sửa ca ${data.idCa}`;

            // Định nghĩa giờ vào trước
            const gioVao = new Date(data.gioVao);
            const gioRa = data.gioRa ? new Date(data.gioRa) : new Date(gioVao.getTime() + 2 * 60 * 60 * 1000);

            // Set form values
            document.getElementById('gioBatDau').value = formatTimeOnly(gioVao);
            document.getElementById('gioKetThuc').value = formatTimeOnly(gioRa);

            // Show modal
            document.getElementById('NguyenLieuModal').style.display = 'block';
        })
        .catch(error => {
            showNotification(error.message, 'error');
        });
}


/**
 * Delete work shift
 */
function deleteCaLamViec(caId) {
    if (!confirm(`Bạn có chắc chắn muốn xóa ca làm việc ${caId}?`)) {
        return;
    }

    fetch(`/admin/calamviec/${caId}`, {
        method: 'DELETE',
        headers: {
            [csrfHeader]: csrfToken
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Không thể xóa ca làm việc.');
            }
            return response.json();
        })
        .then(data => {
            showNotification(data.message || 'Xóa ca làm việc thành công.', 'success');
            // Reload page to reflect changes
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        })
        .catch(error => {
            showNotification(error.message, 'error');
        });
}

/**
 * Validate form inputs
 */
function validateForm() {
    const gioBatDau = document.getElementById('gioBatDau').value;
    const gioKetThuc = document.getElementById('gioKetThuc').value;

    if (!gioBatDau) {
        showNotification('Vui lòng nhập giờ bắt đầu.', 'error');
        return false;
    }

    if (!gioKetThuc) {
        showNotification('Vui lòng nhập giờ kết thúc.', 'error');
        return false;
    }

    // Check if end time is after start time
    if (gioBatDau >= gioKetThuc) {
        showNotification('Giờ kết thúc phải sau giờ bắt đầu.', 'error');
        return false;
    }

    return true;
}

/**
 * Setup search functionality
 */
function setupSearch() {
    const searchInput = document.querySelector('.search-input');

    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();
        const rows = document.querySelectorAll('tbody tr');

        rows.forEach(row => {
            const caId = row.cells[0].textContent.toLowerCase();
            const startTime = row.cells[1].textContent.toLowerCase();
            const endTime = row.cells[2].textContent.toLowerCase();

            const match = caId.includes(searchTerm) ||
                startTime.includes(searchTerm) ||
                endTime.includes(searchTerm);

            row.style.display = match ? '' : 'none';
        });
    });
}

/**
 * Format time string to Date object
 */
function formatTimeToDate(timeStr) {
    const now = new Date();
    const [hours, minutes] = timeStr.split(':');

    now.setHours(parseInt(hours, 10));
    now.setMinutes(parseInt(minutes, 10));
    now.setSeconds(0);

    return now.toISOString();
}

/**
 * Format date to display time only (HH:MM)
 */
function formatTimeOnly(dateStr) {
    if (!dateStr) return '';

    const date = new Date(dateStr);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    return `${hours}:${minutes}`;
}

/**
 * Format date to display (HH:MM)
 */
function formatDateTime(dateStr) {
    if (!dateStr) return 'N/A';

    const date = new Date(dateStr);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    return `${hours}:${minutes}`;
}

/**
 * Show notification message
 */
function showNotification(message, type = 'info') {
    // Check if notification container exists, if not create it
    let notificationContainer = document.getElementById('notificationContainer');
    if (!notificationContainer) {
        notificationContainer = document.createElement('div');
        notificationContainer.id = 'notificationContainer';
        notificationContainer.style.position = 'fixed';
        notificationContainer.style.top = '20px';
        notificationContainer.style.right = '20px';
        notificationContainer.style.zIndex = '1000';
        document.body.appendChild(notificationContainer);
    }

    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.style.backgroundColor = type === 'success' ? '#4caf50' :
        type === 'error' ? '#f44336' : '#2196f3';
    notification.style.color = 'white';
    notification.style.padding = '15px';
    notification.style.marginBottom = '10px';
    notification.style.borderRadius = '4px';
    notification.style.boxShadow = '0 2px 5px rgba(0,0,0,0.2)';
    notification.style.minWidth = '250px';
    notification.style.cursor = 'pointer';
    notification.textContent = message;

    // Add to container
    notificationContainer.appendChild(notification);

    // Remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);

    // Allow clicking to dismiss
    notification.onclick = function() {
        this.remove();
    };
}