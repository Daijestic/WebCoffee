document.addEventListener('DOMContentLoaded', function() {
    // Khởi tạo ngày mặc định
    const today = new Date();
    const thirtyDaysAgo = new Date(today);
    thirtyDaysAgo.setDate(today.getDate() - 30);

    document.getElementById('startDate').valueAsDate = thirtyDaysAgo;
    document.getElementById('endDate').valueAsDate = today;

    // Load dữ liệu ban đầu
    loadAllData();

    // Xử lý sự kiện
    document.getElementById('applyFilter').addEventListener('click', function() {
        loadAllData();
    });

    document.getElementById('salesChartType').addEventListener('change', function() {
        updateSalesChart();
    });

    document.getElementById('topProductsLimit').addEventListener('change', function() {
        loadTopProductsData();
    });

    document.getElementById('comparisonPeriod').addEventListener('change', function() {
        loadComparisonData();
    });

    document.getElementById('exportExcelBtn').addEventListener('click', function() {
        exportToExcel();
    });

    document.getElementById('exportPdfBtn').addEventListener('click', function() {
        exportToPdf();
    });
});

// Lấy token CSRF
function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}

function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
}

// Hàm tải tất cả dữ liệu
function loadAllData() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const reportType = document.getElementById('reportType').value;

    loadSummaryData(startDate, endDate);
    loadSalesData(startDate, endDate, reportType);
    loadCategoryData(startDate, endDate);
    loadTopProductsData(startDate, endDate);
    loadPaymentMethodsData(startDate, endDate);
    loadInventoryData(startDate, endDate);
    loadComparisonData();
}

// Hàm tải dữ liệu thống kê tổng quan
function loadSummaryData(startDate, endDate) {
    fetch(`/admin/api/reports/summary?startDate=${startDate}&endDate=${endDate}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('totalOrders').textContent = data.totalOrders.toLocaleString('vi-VN');
            document.getElementById('totalRevenue').textContent = data.totalRevenue.toLocaleString('vi-VN') + '₫';
            document.getElementById('totalCustomers').textContent = data.totalCustomers.toLocaleString('vi-VN');
            document.getElementById('growthRate').textContent = data.growthRate + '%';
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu thống kê:', error);
        });
}

// Hàm tải dữ liệu doanh thu
function loadSalesData(startDate, endDate, groupBy) {
    fetch(`/admin/api/reports/sales?startDate=${startDate}&endDate=${endDate}&groupBy=${groupBy}`)
        .then(response => response.json())
        .then(data => {
            const chartType = document.getElementById('salesChartType').value;
            updateSalesChartWithData(data, chartType);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu doanh thu:', error);
        });
}

// Hàm tải dữ liệu danh mục
function loadCategoryData(startDate, endDate) {
    fetch(`/admin/api/reports/categories?startDate=${startDate}&endDate=${endDate}`)
        .then(response => response.json())
        .then(data => {
            updateCategoryChartWithData(data);
            updateCategoryTableWithData(data);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu danh mục:', error);
        });
}

// Hàm tải dữ liệu sản phẩm bán chạy
function loadTopProductsData(startDate, endDate) {
    const limit = document.getElementById('topProductsLimit').value;
    fetch(`/admin/api/reports/top-products?startDate=${startDate}&endDate=${endDate}&limit=${limit}`)
        .then(response => response.json())
        .then(data => {
            updateTopProductsTableWithData(data);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu sản phẩm bán chạy:', error);
        });
}

// Hàm tải dữ liệu phương thức thanh toán
function loadPaymentMethodsData(startDate, endDate) {
    fetch(`/admin/api/reports/payment-methods?startDate=${startDate}&endDate=${endDate}`)
        .then(response => response.json())
        .then(data => {
            updatePaymentMethodChartWithData(data);
            updatePaymentMethodTableWithData(data);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu phương thức thanh toán:', error);
        });
}

// Hàm tải dữ liệu biến động kho
function loadInventoryData(startDate, endDate) {
    fetch(`/admin/api/reports/inventory?startDate=${startDate}&endDate=${endDate}`)
        .then(response => response.json())
        .then(data => {
            updateInventoryTableWithData(data);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu biến động kho:', error);
        });
}

// Hàm tải dữ liệu so sánh
function loadComparisonData() {
    const period = document.getElementById('comparisonPeriod').value;
    fetch(`/admin/api/reports/comparison?period=${period}`)
        .then(response => response.json())
        .then(data => {
            updateComparisonChartWithData(data);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu so sánh:', error);
        });
}

// Hàm cập nhật biểu đồ doanh thu với dữ liệu từ API
function updateSalesChartWithData(data, chartType) {
    const ctx = document.getElementById('salesChart').getContext('2d');

    // Xóa biểu đồ cũ nếu có
    if (window.salesChart) {
        window.salesChart.destroy();
    }

    const labels = data.map(item => item.date);
    const values = data.map(item => item.revenue);

    window.salesChart = new Chart(ctx, {
        type: chartType,
        data: {
            labels: labels,
            datasets: [{
                label: 'Doanh thu',
                data: values,
                backgroundColor: 'rgba(54, 162, 235, 0.5)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return value.toLocaleString('vi-VN') + '₫';
                        }
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return context.parsed.y.toLocaleString('vi-VN') + '₫';
                        }
                    }
                }
            }
        }
    });
}

// Tương tự với các hàm cập nhật cho các biểu đồ và bảng khác
function updateCategoryChartWithData(data) {
    // Tương tự như updateSalesChartWithData
    const ctx = document.getElementById('categoryChart').getContext('2d');

    if (window.categoryChart) {
        window.categoryChart.destroy();
    }

    window.categoryChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: data.map(item => item.name),
            datasets: [{
                data: data.map(item => item.revenue),
                backgroundColor: [
                    'rgba(255, 99, 132, 0.5)',
                    'rgba(54, 162, 235, 0.5)',
                    'rgba(255, 206, 86, 0.5)',
                    'rgba(75, 192, 192, 0.5)',
                    'rgba(153, 102, 255, 0.5)',
                    'rgba(255, 159, 64, 0.5)'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const value = context.parsed;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = Math.round(value / total * 100);
                            return context.label + ': ' + value.toLocaleString('vi-VN') + '₫ (' + percentage + '%)';
                        }
                    }
                }
            }
        }
    });
}

// Hàm cập nhật bảng danh mục
function updateCategoryTableWithData(data) {
    const tableBody = document.querySelector('#categoryTable tbody');
    tableBody.innerHTML = '';

    data.forEach(category => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${category.name}</td>
            <td>${category.quantity.toLocaleString('vi-VN')}</td>
            <td>${category.revenue.toLocaleString('vi-VN')}₫</td>
            <td>${category.percentage}%</td>
        `;
        tableBody.appendChild(row);
    });
}

// Các hàm xuất báo cáo
function exportToExcel() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    const url = `/admin/api/reports/export/excel?startDate=${startDate}&endDate=${endDate}`;
    window.location.href = url;
}

function exportToPdf() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    const url = `/admin/api/reports/export/pdf?startDate=${startDate}&endDate=${endDate}`;
    window.location.href = url;
}