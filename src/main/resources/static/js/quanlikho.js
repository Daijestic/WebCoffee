document.addEventListener('DOMContentLoaded', function () {
    // CSRF Token
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    if (!csrfToken || !csrfHeader) {
        console.error('CSRF token or header is missing.');
        return;
    }

    // Elements
    const nguyenLieuModal = document.getElementById('nguyenLieuModal');
    const deleteModal = document.getElementById('deleteModal');
    const nguyenLieuForm = document.getElementById('nguyenLieuForm');
    const searchInput = document.getElementById('searchInput');
    const nguyenLieuTable = document.getElementById('nguyenLieuTable');
    const emptyState = document.getElementById('emptyState');
    const paginationElement = document.getElementById('nguyenLieuPagination');
    const historyTableBody = document.getElementById('historyTableBody');
    const openAddModalButton = document.getElementById('openAddModal');

    let currentPage = 1;
    const itemsPerPage = 10;
    let nguyenLieuData = [];

    // Initialize data
    initializeNguyenLieuData();

    // Event Listeners
    document.addEventListener('click', function (e) {
        const target = e.target;

        if (target.closest('.view-btn')) {
            const id = target.closest('.view-btn').getAttribute('data-id');
            showNguyenLieuDetails(id);
        } else if (target.closest('.edit-btn')) {
            const button = target.closest('.edit-btn');
            openEditModal(button);
        } else if (target.closest('.delete-btn')) {
            const id = target.closest('.delete-btn').getAttribute('data-id');
            openDeleteModal(id);
        } else if (target.closest('.close-btn')) {
            closeModals();
        }
    });

    openAddModalButton.addEventListener('click', function () {
        // Clear form values
        document.getElementById('nguyenLieuId').value = '';
        document.getElementById('tenNguyenLieu').value = '';
        document.getElementById('soLuong').value = '';
        document.getElementById('donVi').value = '';

        // Update modal title and button text for adding
        document.getElementById('modalTitle').textContent = 'Thêm Nguyên Liệu';
        document.querySelector('#nguyenLieuForm .btn-primary').textContent = 'Lưu';

        // Show the modal
        nguyenLieuModal.style.display = 'block';
    });


    searchInput.addEventListener('input', filterNguyenLieu);

    nguyenLieuForm.addEventListener('submit', function (e) {
        e.preventDefault();
        saveNguyenLieu();
    });

    // Functions
    function initializeNguyenLieuData() {
        const rows = nguyenLieuTable.querySelectorAll('tbody tr');
        nguyenLieuData = Array.from(rows).map(row => ({
            id: row.querySelector('td[data-label="ID"]').textContent.trim(),
            name: row.querySelector('td[data-label="Tên Nguyên Liệu"]').textContent.trim(),
            quantity: row.querySelector('td[data-label="Số lượng"]').textContent.trim(),
            unit: row.querySelector('td[data-label="Đơn vị"]').textContent.trim(),
            element: row
        }));
        updateTable();
    }

    function filterNguyenLieu() {
        const searchTerm = searchInput.value.trim().toLowerCase();
        const filteredData = nguyenLieuData.filter(item =>
            item.name.toLowerCase().includes(searchTerm) ||
            item.unit.toLowerCase().includes(searchTerm)
        );
        updateTable(filteredData);
    }

    function updateTable(data = nguyenLieuData) {
        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;

        nguyenLieuData.forEach(item => {
            item.element.style.display = 'none';
        });

        const visibleData = data.slice(startIndex, endIndex);
        visibleData.forEach(item => {
            item.element.style.display = '';
        });

        emptyState.style.display = visibleData.length === 0 ? 'block' : 'none';
        updatePagination(data.length);
    }

    function updatePagination(totalItems) {
        const totalPages = Math.ceil(totalItems / itemsPerPage);
        paginationElement.innerHTML = '';

        for (let i = 1; i <= totalPages; i++) {
            const pageItem = document.createElement('li');
            pageItem.className = `page-item ${i === currentPage ? 'active' : ''}`;
            pageItem.innerHTML = `<a class="page-link" href="#">${i}</a>`;
            pageItem.addEventListener('click', () => {
                currentPage = i;
                updateTable();
            });
            paginationElement.appendChild(pageItem);
        }
    }

    function openEditModal(button) {
        const id = button.getAttribute('data-id');
        const name = button.getAttribute('data-name');
        const quantity = button.getAttribute('data-quantity');
        const unit = button.getAttribute('data-unit');

        // Set form values
        document.getElementById('nguyenLieuId').value = id;
        document.getElementById('tenNguyenLieu').value = name;
        document.getElementById('soLuong').value = quantity;
        document.getElementById('donVi').value = unit;

        // Update modal title and button text for editing
        document.getElementById('modalTitle').textContent = 'Chỉnh sửa Nguyên Liệu';
        document.querySelector('#nguyenLieuForm .btn-primary').textContent = 'Cập nhật';

        // Show the modal
        nguyenLieuModal.style.display = 'block';
    }

    function openDeleteModal(id) {
        deleteModal.style.display = 'block';
        document.getElementById('confirmDeleteBtn').onclick = () => deleteNguyenLieu(id);
    }

    function closeModals() {
        nguyenLieuModal.style.display = 'none';
        deleteModal.style.display = 'none';
    }

    function saveNguyenLieu() {
        const formData = new FormData(nguyenLieuForm);
        const id = formData.get('id');
        const url = id ? `/admin/quanlikho/update/${id}` : '/admin/quanlikho/add';

        fetch(url, {
            method: 'POST',
            body: formData,
            headers: { [csrfHeader]: csrfToken }
        })
            .then(response => {
                if (!response.ok) throw new Error('Failed to save data');
                return response.json();
            })
            .then(() => {
                closeModals();
                window.location.reload();
            })
            .catch(error => {
                console.error(error.message);
            });
    }

    function deleteNguyenLieu(id) {
        fetch(`/admin/nguyenlieu/delete/${id}`, {
            method: 'DELETE',
            headers: { [csrfHeader]: csrfToken }
        })
            .then(response => {
                if (!response.ok) throw new Error('Failed to delete data');
                return response.json();
            })
            .then(() => {
                closeModals();
                window.location.reload();
            })
            .catch(error => {
                console.error(error.message);
            });
    }

    function fetchHistoryData(nguyenLieuId) {
        fetch(`/admin/quanlikho/history/${nguyenLieuId}`, {
            method: 'GET',
            headers: { [csrfHeader]: csrfToken }
        })
            .then(response => {
                if (!response.ok) throw new Error('Failed to fetch history data');
                return response.json();
            })
            .then(data => {
                populateHistoryTable(data);
            })
            .catch(error => {
                console.error(error.message);
            });
    }

    function populateHistoryTable(historyData) {
        historyTableBody.innerHTML = ''; // Clear existing rows

        if (historyData.length === 0) {
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = `<td colspan="8" style="text-align: center;">Không có dữ liệu lịch sử</td>`;
            historyTableBody.appendChild(emptyRow);
            return;
        }

        historyData.forEach(item => {
            const row = document.createElement('tr');
            row.innerHTML = `
            <td>${item.idMaPhieu}</td>
            <td>${new Date(item.ngayNhapXuat).toLocaleDateString()}</td>
            <td>${item.hinhThuc}</td>
            <td>${item.soLuong}</td>
            <td>${item.donViTinh}</td>
            <td>${item.giaTien || 'N/A'}</td>
            <td>${item.idUser}</td>
            <td>${item.tenUser}</td>
        `;
            historyTableBody.appendChild(row);
        });
    }

    // Example: Call fetchHistoryData when showing details
    function showNguyenLieuDetails(id) {
        fetch(`/admin/quanlikho/${id}`, {
            method : 'GET',
            headers: { [csrfHeader]: csrfToken }
        })
            .then(response => {
                if (!response.ok) throw new Error('Failed to fetch details');
                return response.json();
            })
            .then(data => {
                document.getElementById('detailsId').textContent = data.idNguyenLieu;
                document.getElementById('detailsName').textContent = data.tenNguyenLieu;
                document.getElementById('detailsQuantity').textContent = data.soLuong;
                document.getElementById('detailsUnit').textContent = data.donVi;
                document.getElementById('detailsStatus').textContent = data.soLuong > 0 ? 'Available' : 'Out of Stock';
                document.getElementById('nguyenLieuDetailsModal').style.display = 'block';

                // Fetch and populate history data
                fetchHistoryData(id);
            })
            .catch(error => {
                console.error(error.message);
            });
    }

    // Attach event listener for view buttons
    document.addEventListener('click', function (e) {
        const target = e.target;
        if (target.closest('.view-btn')) {
            const id = target.closest('.view-btn').getAttribute('data-id');
            showNguyenLieuDetails(id);
        }
    });
});