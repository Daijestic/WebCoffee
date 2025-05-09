document.addEventListener('DOMContentLoaded', function() {
    // CSRF setup
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    // Elements
    const searchInput = document.getElementById('searchInput');
    const statusFilter = document.getElementById('statusFilter');
    const viewOrderModal = document.getElementById('viewOrderModal');
    const updateStatusModal = document.getElementById('updateStatusModal');
    const updateStatusForm = document.getElementById('updateStatusForm');
    let currentOrderId = null;

    // Search and filter functionality
    function filterOrders() {
        const searchTerm = searchInput.value.toLowerCase();
        const statusValue = statusFilter.value;
        const rows = document.querySelectorAll('tbody tr');
        let visibleCount = 0;

        rows.forEach(row => {
            const orderNumber = row.cells[0].textContent;
            const customerName = row.cells[1].textContent;
            const status = row.querySelector('.status-badge').textContent;

            const matchesSearch = orderNumber.toLowerCase().includes(searchTerm) ||
                customerName.toLowerCase().includes(searchTerm);
            const matchesStatus = statusValue === '' || status === statusValue;

            if (matchesSearch && matchesStatus) {
                row.style.display = '';
                visibleCount++;
            } else {
                row.style.display = 'none';
            }
        });

        const emptyState = document.querySelector('.empty-state');
        if (emptyState) {
            emptyState.style.display = visibleCount === 0 ? 'block' : 'none';
        }
    }

    if (searchInput) searchInput.addEventListener('input', filterOrders);
    if (statusFilter) statusFilter.addEventListener('change', filterOrders);

    // View order details
    document.querySelectorAll('.view-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const orderId = btn.getAttribute('data-id');
            const customerName = btn.getAttribute('data-user');
            const row = btn.closest('tr');

            const orderDetails = {
                idHoaDon: orderId,
                tenUser: customerName,
                ngayGioLapHoaDon: row.cells[2].textContent,
                phuongThucThanhToan: row.cells[3].textContent,
                tongTien: row.cells[4].textContent,
                phiShip: row.cells[5].textContent,
                diemDaDung: row.cells[6].textContent,
                hinhThuc: row.cells[7].textContent,
                trangThai: row.querySelector('.status-badge').textContent
            };

            displayOrderDetails(orderDetails);
            viewOrderModal.style.display = 'block';
        });
    });

    // Update order status
    document.querySelectorAll('.update-status-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            currentOrderId = btn.getAttribute('data-id');
            const currentStatus = btn.getAttribute('data-status');

            const statusModalContent = `
                <form id="updateStatusForm">
                    <div class="form-group">
                        <label for="newStatus">Trạng thái mới</label>
                        <select id="newStatus" name="newStatus" required>
                            <option value="Chờ xác nhận" ${currentStatus === 'Chờ xác nhận' ? 'selected' : ''}>Chờ xác nhận</option>
                            <option value="Đang xử lý" ${currentStatus === 'Đang xử lý' ? 'selected' : ''}>Đang xử lý</option>
                            <option value="Đang giao" ${currentStatus === 'Đang giao' ? 'selected' : ''}>Đang giao</option>
                            <option value="Hoàn thành" ${currentStatus === 'Hoàn thành' ? 'selected' : ''}>Hoàn thành</option>
                            <option value="Đã hủy" ${currentStatus === 'Đã hủy' ? 'selected' : ''}>Đã hủy</option>
                        </select>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Cập nhật</button>
                    </div>
                </form>
            `;

            updateStatusModal.querySelector('.modal-content').innerHTML =
                `<span class="close">&times;</span>
                 <h2>Cập nhật trạng thái đơn hàng #${currentOrderId}</h2>
                 ${statusModalContent}`;

            updateStatusModal.style.display = 'block';

            // Rebind form submit event
            const newForm = document.getElementById('updateStatusForm');
            newForm.addEventListener('submit', handleStatusUpdate);

            // Rebind close button
            updateStatusModal.querySelector('.close').addEventListener('click', () => {
                updateStatusModal.style.display = 'none';
            });
        });
    });

    async function handleStatusUpdate(e) {
        e.preventDefault();
        const newStatus = document.getElementById('newStatus').value;

        try {
            const response = await fetch(`/admin/donhang/${currentOrderId}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                },
                body: JSON.stringify({ status: newStatus })
            });

            if (response.ok) {
                showNotification('Cập nhật trạng thái thành công');
                setTimeout(() => window.location.reload(), 1000);
            } else {
                throw new Error('Cập nhật thất bại');
            }
        } catch (error) {
            showNotification('Không thể cập nhật trạng thái', 'error');
        }

        updateStatusModal.style.display = 'none';
    }

    // Modal close handlers
    document.querySelectorAll('.close').forEach(closeBtn => {
        closeBtn.addEventListener('click', function() {
            this.closest('.modal').style.display = 'none';
        });
    });

    window.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal')) {
            e.target.style.display = 'none';
        }
    });

    function displayOrderDetails(order) {
        document.getElementById('orderNumber').textContent = `#${order.idHoaDon}`;

        const detailsHTML = `
            <div class="order-info">
                <div class="info-row">
                    <label>Khách hàng:</label>
                    <span>${order.tenUser}</span>
                </div>
                <div class="info-row">
                    <label>Ngày đặt:</label>
                    <span>${order.ngayGioLapHoaDon}</span>
                </div>
                <div class="info-row">
                    <label>Phương thức:</label>
                    <span>${order.phuongThucThanhToan}</span>
                </div>
                <div class="info-row">
                    <label>Hình thức:</label>
                    <span>${order.hinhThuc}</span>
                </div>
                <div class="info-row">
                    <label>Trạng thái:</label>
                    <span class="status-badge ${order.trangThai.toLowerCase()}">${order.trangThai}</span>
                </div>
                <div class="info-row">
                    <label>Phí ship:</label>
                    <span>${order.phiShip}</span>
                </div>
                <div class="info-row">
                    <label>Điểm đã dùng:</label>
                    <span>${order.diemDaDung}</span>
                </div>
                <div class="info-row">
                    <label>Tổng tiền:</label>
                    <span>${order.tongTien}</span>
                </div>
            </div>
        `;

        document.getElementById('orderDetails').innerHTML = detailsHTML;
    }

    function showNotification(message, type = 'success') {
        const notification = document.getElementById('notification');
        if (notification) {
            notification.textContent = message;
            notification.className = `notification ${type}`;
            notification.style.display = 'block';
            setTimeout(() => {
                notification.style.display = 'none';
            }, 3000);
        }
    }
});