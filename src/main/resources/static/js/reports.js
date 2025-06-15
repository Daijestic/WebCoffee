// Biến lưu trữ các đối tượng biểu đồ
let salesChart, categoryChart, comparisonChart, paymentMethodChart;

// Token CSRF
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

// Format tiền tệ VND
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

// Format phần trăm
function formatPercent(value) {
    return value.toFixed(2) + '%';
}

// Hiển thị thông báo
function showNotification(message, type = 'success') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type}`;
    notification.style.display = 'block';

    setTimeout(() => {
        notification.style.display = 'none';
    }, 3000);
}

// Lấy dữ liệu tổng quan
async function loadSummaryData(startDate, endDate) {
    try {
        let url = '/admin/api/reports/summary';
        if (startDate && endDate) {
            url += `?startDate=${startDate}&endDate=${endDate}`;
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error('Không thể tải dữ liệu tổng quan');

        const data = await response.json();

        // Hiển thị dữ liệu trên các thẻ thống kê
        document.getElementById('totalOrders').textContent = data.totalOrders;
        document.getElementById('totalRevenue').textContent = formatCurrency(data.totalRevenue);
        document.getElementById('totalCustomers').textContent = data.totalCustomers;
        document.getElementById('growthRate').textContent = data.growthRate + '%';
    } catch (error) {
        console.error('Lỗi khi tải dữ liệu tổng quan:', error);
        showNotification('Không thể tải dữ liệu tổng quan', 'error');
    }
}

// Tải dữ liệu doanh thu theo thời gian
async function loadSalesData(startDate, endDate, groupBy) {
    try {
        let url = `/admin/api/reports/sales?groupBy=${groupBy}`;
        if (startDate && endDate) {
            url += `&startDate=${startDate}&endDate=${endDate}`;
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error('Không thể tải dữ liệu doanh thu');

        const data = await response.json();

        // Chuẩn bị dữ liệu cho biểu đồ
        const labels = data.map(item => item.date);
        const revenues = data.map(item => item.revenue);
        const orders = data.map(item => item.orders);

        // Cập nhật biểu đồ doanh thu
        updateSalesChart(labels, revenues, orders);
    } catch (error) {
        console.error('Lỗi khi tải dữ liệu doanh thu:', error);
        showNotification('Không thể tải dữ liệu doanh thu', 'error');
    }
}

// Cập nhật biểu đồ doanh thu
function updateSalesChart(labels, revenues, orders) {
    const ctx = document.getElementById('salesChart').getContext('2d');
    const chartType = document.getElementById('salesChartType').value;

    // Hủy biểu đồ cũ nếu tồn tại
    if (salesChart) {
        salesChart.destroy();
    }

    salesChart = new Chart(ctx, {
        type: chartType,
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Doanh thu (VNĐ)',
                    data: revenues,
                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1,
                    yAxisID: 'y'
                },
                {
                    label: 'Số đơn hàng',
                    data: orders,
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    borderColor: 'rgba(255, 99, 132, 1)',
                    borderWidth: 1,
                    yAxisID: 'y1'
                }
            ]
        },
        options: {
            scales: {
                y: {
                    type: 'linear',
                    display: true,
                    position: 'left',
                    title: {
                        display: true,
                        text: 'Doanh thu (VNĐ)'
                    }
                },
                y1: {
                    type: 'linear',
                    display: true,
                    position: 'right',
                    grid: {
                        drawOnChartArea: false
                    },
                    title: {
                        display: true,
                        text: 'Số đơn hàng'
                    }
                }
            },
            responsive: true,
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            let label = context.dataset.label || '';
                            if (label) {
                                label += ': ';
                            }
                            if (context.dataset.yAxisID === 'y') {
                                label += formatCurrency(context.raw);
                            } else {
                                label += context.raw;
                            }
                            return label;
                        }
                    }
                }
            }
        }
    });
}

// Tải dữ liệu sản phẩm bán chạy
async function loadTopProducts(startDate, endDate, limit) {
    try {
        let url = `/admin/api/reports/top-products?limit=${limit}`;
        if (startDate && endDate) {
            url += `&startDate=${startDate}&endDate=${endDate}`;
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error('Không thể tải dữ liệu sản phẩm');

        const data = await response.json();

        // Cập nhật bảng sản phẩm bán chạy
        const tableBody = document.querySelector('#topProductsTable tbody');
        tableBody.innerHTML = '';

        data.forEach(product => {
            const row = tableBody.insertRow();
            row.innerHTML = `
                <td>${product.id}</td>
                <td>${product.name}</td>
                <td>${product.category}</td>
                <td>${product.quantity}</td>
                <td>${formatCurrency(product.revenue)}</td>
                <td>${formatPercent(product.percentage)}</td>
            `;
        });
    } catch (error) {
        console.error('Lỗi khi tải dữ liệu sản phẩm:', error);
        showNotification('Không thể tải dữ liệu sản phẩm', 'error');
    }
}

// Tải dữ liệu doanh thu theo danh mục
async function loadCategoryData(startDate, endDate) {
    try {
        let url = '/admin/api/reports/categories';
        if (startDate && endDate) {
            url += `?startDate=${startDate}&endDate=${endDate}`;
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error('Không thể tải dữ liệu danh mục');

        const data = await response.json();

        // Cập nhật bảng doanh thu theo danh mục
        const tableBody = document.querySelector('#categoryTable tbody');
        tableBody.innerHTML = '';

        data.forEach(category => {
            const row = tableBody.insertRow();
            row.innerHTML = `
                <td>${category.category}</td>
                <td>${category.quantity}</td>
                <td>${formatCurrency(category.revenue)}</td>
                <td>${formatPercent(category.percentage)}</td>
            `;
        });

        // Cập nhật biểu đồ danh mục
        updateCategoryChart(data);
    } catch (error) {
        console.error('Lỗi khi tải dữ liệu danh mục:', error);
        showNotification('Không thể tải dữ liệu danh mục', 'error');
    }
}

// Cập nhật biểu đồ danh mục
function updateCategoryChart(data) {
    const ctx = document.getElementById('categoryChart').getContext('2d');

    // Chuẩn bị dữ liệu cho biểu đồ
    const labels = data.map(item => item.category);
    const values = data.map(item => item.revenue);
    const backgroundColors = generateColors(data.length);

    // Hủy biểu đồ cũ nếu tồn tại
    if (categoryChart) {
        categoryChart.destroy();
    }

    categoryChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: backgroundColors
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'right',
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = formatCurrency(context.raw);
                            const percentage = formatPercent(data[context.dataIndex].percentage);
                            return `${label}: ${value} (${percentage})`;
                        }
                    }
                }
            }
        }
    });
}

// Tạo mảng màu cho biểu đồ
function generateColors(count) {
    const baseColors = [
        'rgba(255, 99, 132, 0.8)',
        'rgba(54, 162, 235, 0.8)',
        'rgba(255, 206, 86, 0.8)',
        'rgba(75, 192, 192, 0.8)',
        'rgba(153, 102, 255, 0.8)',
        'rgba(255, 159, 64, 0.8)',
        'rgba(199, 199, 199, 0.8)',
        'rgba(83, 102, 255, 0.8)',
        'rgba(40, 159, 64, 0.8)',
        'rgba(210, 199, 199, 0.8)'
    ];

    let colors = [];
    for (let i = 0; i < count; i++) {
        colors.push(baseColors[i % baseColors.length]);
    }
    return colors;
}

// Tải dữ liệu so sánh theo thời gian
async function loadComparisonData(period) {
    try {
        const response = await fetch(`/admin/api/reports/comparison?period=${period}`);
        if (!response.ok) throw new Error('Không thể tải dữ liệu so sánh');

        const data = await response.json();

        // Cập nhật biểu đồ so sánh
        updateComparisonChart(data, period);
    } catch (error) {
        console.error('Lỗi khi tải dữ liệu so sánh:', error);
        showNotification('Không thể tải dữ liệu so sánh', 'error');
    }
}

// Cập nhật biểu đồ so sánh
function updateComparisonChart(data, period) {
    const ctx = document.getElementById('comparisonChart').getContext('2d');

    // Chuẩn bị dữ liệu cho biểu đồ
    const currentData = data.current;
    const previousData = data.previous;

    const labels = currentData.map(item => item.date);
    const currentRevenues = currentData.map(item => item.revenue);
    const previousRevenues = previousData.map(item => item.revenue);

    // Xác định nhãn cho giai đoạn
    let periodLabel;
    switch (period) {
        case 'week': periodLabel = 'tuần'; break;
        case 'month': periodLabel = 'tháng'; break;
        case 'quarter': periodLabel = 'quý'; break;
        case 'year': periodLabel = 'năm'; break;
        default: periodLabel = 'giai đoạn';
    }

    // Hủy biểu đồ cũ nếu tồn tại
    if (comparisonChart) {
        comparisonChart.destroy();
    }

    comparisonChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: `${periodLabel} hiện tại`,
                    data: currentRevenues,
                    backgroundColor: 'rgba(54, 162, 235, 0.5)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                },
                {
                    label: `${periodLabel} trước`,
                    data: previousRevenues,
                    backgroundColor: 'rgba(255, 99, 132, 0.5)',
                    borderColor: 'rgba(255, 99, 132, 1)',
                    borderWidth: 1
                }
            ]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Doanh thu (VNĐ)'
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            let label = context.dataset.label || '';
                            if (label) {
                                label += ': ';
                            }
                            label += formatCurrency(context.raw);
                            return label;
                        }
                    }
                }
            }
        }
    });
}

// Tải dữ liệu phương thức thanh toán
async function loadPaymentMethodsData(startDate, endDate) {
    try {
        let url = '/admin/api/reports/payment-methods';
        if (startDate && endDate) {
            url += `?startDate=${startDate}&endDate=${endDate}`;
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error('Không thể tải dữ liệu phương thức thanh toán');

        const data = await response.json();

        // Cập nhật bảng phương thức thanh toán
        const tableBody = document.querySelector('#paymentMethodTable tbody');
        tableBody.innerHTML = '';

        data.forEach(method => {
            const row = tableBody.insertRow();
            row.innerHTML = `
                <td>${method.method}</td>
                <td>${method.orders}</td>
                <td>${formatCurrency(method.revenue)}</td>
                <td>${formatPercent(method.percentage)}</td>
            `;
        });

        // Cập nhật biểu đồ phương thức thanh toán
        updatePaymentMethodChart(data);
    } catch (error) {
        console.error('Lỗi khi tải dữ liệu phương thức thanh toán:', error);
        showNotification('Không thể tải dữ liệu phương thức thanh toán', 'error');
    }
}

// Cập nhật biểu đồ phương thức thanh toán
function updatePaymentMethodChart(data) {
    const ctx = document.getElementById('paymentMethodChart').getContext('2d');

    // Chuẩn bị dữ liệu cho biểu đồ
    const labels = data.map(item => item.method);
    const values = data.map(item => item.revenue);
    const backgroundColors = generateColors(data.length);

    // Hủy biểu đồ cũ nếu tồn tại
    if (paymentMethodChart) {
        paymentMethodChart.destroy();
    }

    paymentMethodChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: backgroundColors
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'right',
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = formatCurrency(context.raw);
                            const percentage = formatPercent(data[context.dataIndex].percentage);
                            return `${label}: ${value} (${percentage})`;
                        }
                    }
                }
            }
        }
    });
}

// Tải dữ liệu biến động kho
async function loadInventoryData(startDate, endDate) {
    try {
        let url = '/admin/api/reports/inventory';
        if (startDate && endDate) {
            url += `?startDate=${startDate}&endDate=${endDate}`;
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error('Không thể tải dữ liệu kho');

        const data = await response.json();

        // Cập nhật bảng biến động kho
        const tableBody = document.querySelector('#inventoryMovementTable tbody');
        tableBody.innerHTML = '';

        data.forEach(item => {
            const row = tableBody.insertRow();
            row.innerHTML = `
                <td>${item.name}</td>
                <td>${item.initialStock}</td>
                <td>${item.import}</td>
                <td>${item.export}</td>
                <td>${item.finalStock}</td>
                <td>${item.unit}</td>
            `;
        });
    } catch (error) {
        console.error('Lỗi khi tải dữ liệu kho:', error);
        showNotification('Không thể tải dữ liệu kho', 'error');
    }
}

// Xuất báo cáo Excel
function exportExcelReport() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    let url = '/admin/api/reports/export/excel';
    if (startDate && endDate) {
        url += `?startDate=${startDate}&endDate=${endDate}`;
    }

    window.location.href = url;
    showNotification('Đang xuất báo cáo Excel...');
}

// Xuất báo cáo PDF
function exportPdfReport() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    let url = '/admin/api/reports/export/pdf';
    if (startDate && endDate) {
        url += `?startDate=${startDate}&endDate=${endDate}`;
    }

    window.location.href = url;
    showNotification('Đang xuất báo cáo PDF...');
}

// Tải tất cả dữ liệu báo cáo
function loadAllReportData() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const reportType = document.getElementById('reportType').value;
    const topProductsLimit = document.getElementById('topProductsLimit').value;
    const comparisonPeriod = document.getElementById('comparisonPeriod').value;

    loadSummaryData(startDate, endDate);
    loadSalesData(startDate, endDate, reportType);
    loadTopProducts(startDate, endDate, topProductsLimit);
    loadCategoryData(startDate, endDate);
    loadComparisonData(comparisonPeriod);
    loadPaymentMethodsData(startDate, endDate);
    loadInventoryData(startDate, endDate);
}

// Khởi tạo trang
document.addEventListener('DOMContentLoaded', function() {
    // Khởi tạo giá trị mặc định cho các trường ngày
    const today = new Date();
    const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const formatDateForInput = date => {
        return date.toISOString().split('T')[0];
    };

    document.getElementById('startDate').value = formatDateForInput(firstDayOfMonth);
    document.getElementById('endDate').value = formatDateForInput(today);

    // Tải dữ liệu ban đầu
    loadAllReportData();

    // Xử lý sự kiện nút "Áp dụng" bộ lọc
    document.getElementById('applyFilter').addEventListener('click', loadAllReportData);

    // Xử lý sự kiện thay đổi loại báo cáo
    document.getElementById('reportType').addEventListener('change', function() {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        loadSalesData(startDate, endDate, this.value);
    });

    // Xử lý sự kiện thay đổi loại biểu đồ doanh thu
    document.getElementById('salesChartType').addEventListener('change', function() {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        loadSalesData(startDate, endDate, document.getElementById('reportType').value);
    });

    // Xử lý sự kiện thay đổi số lượng sản phẩm bán chạy
    document.getElementById('topProductsLimit').addEventListener('change', function() {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        loadTopProducts(startDate, endDate, this.value);
    });

    // Xử lý sự kiện thay đổi kỳ so sánh
    document.getElementById('comparisonPeriod').addEventListener('change', function() {
        loadComparisonData(this.value);
    });

    // Xử lý sự kiện xuất báo cáo
    document.getElementById('exportExcelBtn').addEventListener('click', exportExcelReport);
    document.getElementById('exportPdfBtn').addEventListener('click', exportPdfReport);
});