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
        'rgba(255, 87, 51, 0.9)',     // đỏ cam sáng
        'rgba(0, 128, 255, 0.9)',     // xanh nước biển tươi
        'rgba(255, 221, 0, 0.9)',     // vàng chanh
        'rgba(0, 204, 102, 0.9)',     // xanh lá sáng
        'rgba(153, 51, 255, 0.9)',    // tím neon
        'rgba(255, 102, 255, 0.9)',   // hồng cánh sen
        'rgba(255, 153, 0, 0.9)',     // cam đậm
        'rgba(51, 255, 255, 0.9)',    // xanh ngọc sáng
        'rgba(0, 0, 153, 0.9)',       // xanh dương đậm
        'rgba(102, 102, 102, 0.9)'    // xám trung tính
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

// Thêm các nút xuất cho từng phần
function addExportButtonsToSections() {
    const sections = document.querySelectorAll('.report-section');
    sections.forEach(section => {
        const header = section.querySelector('.section-header');
        const title = header.querySelector('h2').textContent;

        const exportDiv = document.createElement('div');
        exportDiv.className = 'section-exports';
        exportDiv.innerHTML = `
            <button class="btn btn-sm btn-outline export-section-excel" title="Xuất Excel">
                <i class="fas fa-file-excel"></i>
            </button>
            <button class="btn btn-sm btn-outline export-section-pdf" title="Xuất PDF">
                <i class="fas fa-file-pdf"></i>
            </button>
        `;

        header.appendChild(exportDiv);

        // Thêm sự kiện cho nút xuất Excel
        exportDiv.querySelector('.export-section-excel').addEventListener('click', () => {
            const sectionId = section.querySelector('.chart-container canvas')?.id ||
                section.querySelector('table')?.id;
            exportSectionToExcel(sectionId, title);
        });

        // Thêm sự kiện cho nút xuất PDF
        exportDiv.querySelector('.export-section-pdf').addEventListener('click', () => {
            const sectionId = section.querySelector('.chart-container canvas')?.id ||
                section.querySelector('table')?.id;
            exportSectionToPdf(sectionId, title);
        });
    });
}

// Xuất phần cụ thể sang Excel
async function exportSectionToExcel(sectionId, title) {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    try {
        showNotification(`Đang xuất dữ liệu ${title} sang Excel...`);

        let data = [];
        const wb = XLSX.utils.book_new();

        switch(sectionId) {
            case 'salesChart':
                // Lấy dữ liệu doanh thu theo thời gian
                const salesData = await fetchData(`/admin/api/reports/sales?startDate=${startDate}&endDate=${endDate}&groupBy=daily`);

                // Tạo bảng doanh thu theo thời gian
                const salesDataArr = [
                    [`BÁO CÁO ${title.toUpperCase()}`, '', ''],
                    [`Từ ngày: ${startDate}`, `Đến ngày: ${endDate}`, ''],
                    ['', '', ''],
                    ['Ngày', 'Doanh thu (VNĐ)', 'Số đơn hàng']
                ];

                salesData.forEach(item => {
                    salesDataArr.push([item.date, item.revenue, item.orders]);
                });

                // Thêm dòng tổng cộng
                const totalRevenue = salesData.reduce((sum, item) => sum + item.revenue, 0);
                const totalOrders = salesData.reduce((sum, item) => sum + item.orders, 0);
                salesDataArr.push(['Tổng cộng', totalRevenue, totalOrders]);

                const salesSheet = XLSX.utils.aoa_to_sheet(salesDataArr);
                salesSheet['!merges'] = [{s: {r: 0, c: 0}, e: {r: 0, c: 2}}];
                XLSX.utils.book_append_sheet(wb, salesSheet, 'Doanh thu theo thời gian');
                break;

            case 'topProductsTable':
                // Lấy dữ liệu sản phẩm bán chạy
                const topProducts = await fetchData(`/admin/api/reports/top-products?startDate=${startDate}&endDate=${endDate}&limit=10`);

                // Tạo bảng sản phẩm bán chạy
                const topProductsData = [
                    [`BÁO CÁO ${title.toUpperCase()}`, '', '', '', '', ''],
                    [`Từ ngày: ${startDate}`, `Đến ngày: ${endDate}`, '', '', '', ''],
                    ['', '', '', '', '', ''],
                    ['Mã SP', 'Tên sản phẩm', 'Danh mục', 'Số lượng', 'Doanh thu', 'Tỷ trọng']
                ];

                topProducts.forEach(product => {
                    topProductsData.push([
                        product.id,
                        product.name,
                        product.category,
                        product.quantity,
                        product.revenue,
                        product.percentage + '%'
                    ]);
                });

                const topProductsSheet = XLSX.utils.aoa_to_sheet(topProductsData);
                topProductsSheet['!merges'] = [
                    {s: {r: 0, c: 0}, e: {r: 0, c: 5}},
                    {s: {r: 1, c: 0}, e: {r: 1, c: 1}},
                    {s: {r: 1, c: 2}, e: {r: 1, c: 5}}
                ];
                XLSX.utils.book_append_sheet(wb, topProductsSheet, 'Sản phẩm bán chạy');
                break;

            case 'categoryChart':
            case 'categoryTable':
                // Lấy dữ liệu danh mục
                const categories = await fetchData(`/admin/api/reports/categories?startDate=${startDate}&endDate=${endDate}`);

                // Tạo bảng doanh thu theo danh mục
                const categoriesData = [
                    [`BÁO CÁO ${title.toUpperCase()}`, '', '', ''],
                    [`Từ ngày: ${startDate}`, `Đến ngày: ${endDate}`, '', ''],
                    ['', '', '', ''],
                    ['Danh mục', 'Số lượng', 'Doanh thu', 'Tỷ trọng']
                ];

                categories.forEach(category => {
                    categoriesData.push([
                        category.category,
                        category.quantity,
                        category.revenue,
                        category.percentage + '%'
                    ]);
                });

                const categoriesSheet = XLSX.utils.aoa_to_sheet(categoriesData);
                categoriesSheet['!merges'] = [
                    {s: {r: 0, c: 0}, e: {r: 0, c: 3}},
                    {s: {r: 1, c: 0}, e: {r: 1, c: 1}},
                    {s: {r: 1, c: 2}, e: {r: 1, c: 3}}
                ];
                XLSX.utils.book_append_sheet(wb, categoriesSheet, 'Doanh thu theo danh mục');
                break;

            case 'comparisonChart':
                const comparisonPeriod = document.getElementById('comparisonPeriod').value;
                const comparisonData = await fetchData(`/admin/api/reports/comparison?period=${comparisonPeriod}`);

                // Tạo bảng so sánh
                const comparisonHeader = [`BÁO CÁO ${title.toUpperCase()}`, '', ''];
                const periodLabel = getComparisonLabel(comparisonPeriod);
                const comparisonDataArr = [
                    [`Từ ngày: ${startDate}`, `Đến ngày: ${endDate}`, ''],
                    ['', '', ''],
                    ['Ngày', 'Kỳ hiện tại', 'Kỳ trước']
                ];

                for (let i = 0; i < comparisonData.current.length; i++) {
                    comparisonDataArr.push([
                        comparisonData.current[i].date,
                        comparisonData.current[i].revenue,
                        comparisonData.previous[i].revenue
                    ]);
                }

                const comparisonSheet = XLSX.utils.aoa_to_sheet([comparisonHeader, ...comparisonDataArr]);
                comparisonSheet['!merges'] = [
                    {s: {r: 0, c: 0}, e: {r: 0, c: 2}},
                    {s: {r: 1, c: 0}, e: {r: 1, c: 1}}
                ];
                XLSX.utils.book_append_sheet(wb, comparisonSheet, 'So sánh theo thời gian');
                break;

            case 'paymentMethodChart':
            case 'paymentMethodTable':
                // Lấy dữ liệu phương thức thanh toán
                const paymentMethods = await fetchData(`/admin/api/reports/payment-methods?startDate=${startDate}&endDate=${endDate}`);

                // Tạo bảng phương thức thanh toán
                const paymentsData = [
                    [`BÁO CÁO ${title.toUpperCase()}`, '', '', ''],
                    [`Từ ngày: ${startDate}`, `Đến ngày: ${endDate}`, '', ''],
                    ['', '', '', ''],
                    ['Phương thức', 'Số đơn hàng', 'Doanh thu', 'Tỷ trọng']
                ];

                paymentMethods.forEach(method => {
                    paymentsData.push([
                        method.method,
                        method.orders,
                        method.revenue,
                        method.percentage + '%'
                    ]);
                });

                const paymentsSheet = XLSX.utils.aoa_to_sheet(paymentsData);
                paymentsSheet['!merges'] = [
                    {s: {r: 0, c: 0}, e: {r: 0, c: 3}},
                    {s: {r: 1, c: 0}, e: {r: 1, c: 1}},
                    {s: {r: 1, c: 2}, e: {r: 1, c: 3}}
                ];
                XLSX.utils.book_append_sheet(wb, paymentsSheet, 'Phương thức thanh toán');
                break;

            case 'inventoryMovementTable':
                // Lấy dữ liệu biến động kho
                const inventory = await fetchData(`/admin/api/reports/inventory?startDate=${startDate}&endDate=${endDate}`);

                // Tạo bảng biến động kho
                const inventoryData = [
                    [`BÁO CÁO ${title.toUpperCase()}`, '', '', '', '', ''],
                    [`Từ ngày: ${startDate}`, `Đến ngày: ${endDate}`, '', '', '', ''],
                    ['', '', '', '', '', ''],
                    ['Nguyên liệu', 'Tồn đầu kỳ', 'Nhập kho', 'Xuất kho', 'Tồn cuối kỳ', 'Đơn vị']
                ];

                inventory.forEach(item => {
                    inventoryData.push([
                        item.name,
                        item.initialStock,
                        item.import,
                        item.export,
                        item.finalStock,
                        item.unit
                    ]);
                });

                const inventorySheet = XLSX.utils.aoa_to_sheet(inventoryData);
                inventorySheet['!merges'] = [
                    {s: {r: 0, c: 0}, e: {r: 0, c: 5}},
                    {s: {r: 1, c: 0}, e: {r: 1, c: 2}},
                    {s: {r: 1, c: 3}, e: {r: 1, c: 5}}
                ];
                XLSX.utils.book_append_sheet(wb, inventorySheet, 'Biến động kho hàng');
                break;
        }

        // Xuất file Excel
        const fileName = `bao-cao-${title.toLowerCase().replace(/\s+/g, '-')}-${startDate}-${endDate}.xlsx`;
        const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'binary' });

        function s2ab(s) {
            const buf = new ArrayBuffer(s.length);
            const view = new Uint8Array(buf);
            for (let i = 0; i < s.length; i++) view[i] = s.charCodeAt(i) & 0xFF;
            return buf;
        }

        const blob = new Blob([s2ab(wbout)], { type: 'application/octet-stream' });
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        link.click();

        showNotification(`Xuất ${title} sang Excel thành công`);
    } catch (error) {
        console.error(`Lỗi khi xuất ${title} sang Excel:`, error);
        showNotification(`Không thể xuất ${title} sang Excel: ${error.message}`, 'error');
    }
}

// Xuất phần cụ thể sang PDF
async function exportSectionToPdf(sectionId, title) {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    try {
        showNotification(`Đang xuất dữ liệu ${title} sang PDF...`);

        // Khởi tạo nội dung PDF
        const docContent = [];
        let chartImg = null;

        // Thêm tiêu đề
        docContent.push(
            { text: `BÁO CÁO ${title.toUpperCase()}`, style: 'header', alignment: 'center' },
            { text: `Thời gian: Từ ${startDate} đến ${endDate}`, style: 'subheader', alignment: 'center', margin: [0, 0, 0, 20] }
        );

        switch(sectionId) {
            case 'salesChart':
                // Lấy ảnh biểu đồ doanh thu
                chartImg = getChartImageBase64('salesChart');

                // Lấy dữ liệu doanh thu theo thời gian
                const salesData = await fetchData(`/admin/api/reports/sales?startDate=${startDate}&endDate=${endDate}&groupBy=daily`);

                // Thêm biểu đồ
                if (chartImg) {
                    docContent.push(
                        { text: 'Biểu đồ doanh thu theo thời gian:', style: 'chartLabel' },
                        {
                            image: chartImg,
                            width: 500,
                            alignment: 'center',
                            margin: [0, 5, 0, 15]
                        }
                    );
                }

                // Thêm bảng dữ liệu
                docContent.push(
                    { text: 'Bảng doanh thu theo thời gian:', style: 'sectionHeader', margin: [0, 10, 0, 5] },
                    {
                        table: {
                            headerRows: 1,
                            widths: ['*', 'auto', 'auto'],
                            body: [
                                [
                                    { text: 'Ngày', style: 'tableHeader' },
                                    { text: 'Doanh thu', style: 'tableHeader' },
                                    { text: 'Số đơn hàng', style: 'tableHeader' }
                                ],
                                ...salesData.map(item => [
                                    item.date,
                                    formatCurrency(item.revenue),
                                    item.orders.toString()
                                ]),
                                [
                                    { text: 'Tổng cộng', style: 'tableHeader' },
                                    {
                                        text: formatCurrency(salesData.reduce((sum, item) => sum + item.revenue, 0)),
                                        style: 'tableHeader'
                                    },
                                    {
                                        text: salesData.reduce((sum, item) => sum + item.orders, 0).toString(),
                                        style: 'tableHeader'
                                    }
                                ]
                            ]
                        },
                        margin: [0, 0, 0, 15]
                    }
                );
                break;

            case 'topProductsTable':
                // Lấy dữ liệu sản phẩm bán chạy
                const topProducts = await fetchData(`/admin/api/reports/top-products?startDate=${startDate}&endDate=${endDate}&limit=10`);

                // Thêm bảng dữ liệu
                docContent.push(
                    {
                        table: {
                            headerRows: 1,
                            widths: [40, '*', 70, 50, 70, 50],
                            body: [
                                [
                                    { text: 'Mã SP', style: 'tableHeader' },
                                    { text: 'Tên sản phẩm', style: 'tableHeader' },
                                    { text: 'Danh mục', style: 'tableHeader' },
                                    { text: 'Số lượng', style: 'tableHeader' },
                                    { text: 'Doanh thu', style: 'tableHeader' },
                                    { text: 'Tỷ trọng', style: 'tableHeader' }
                                ],
                                ...topProducts.map(product => [
                                    product.id,
                                    product.name,
                                    product.category,
                                    product.quantity,
                                    formatCurrency(product.revenue),
                                    product.percentage + '%'
                                ])
                            ]
                        },
                        margin: [0, 10, 0, 15]
                    }
                );
                break;

            case 'categoryChart':
            case 'categoryTable':
                // Lấy ảnh biểu đồ danh mục
                chartImg = getChartImageBase64('categoryChart');

                // Lấy dữ liệu danh mục
                const categories = await fetchData(`/admin/api/reports/categories?startDate=${startDate}&endDate=${endDate}`);

                // Thêm bảng dữ liệu
                docContent.push(
                    {
                        table: {
                            headerRows: 1,
                            widths: ['*', 'auto', 'auto', 'auto'],
                            body: [
                                [
                                    { text: 'Danh mục', style: 'tableHeader' },
                                    { text: 'Số lượng', style: 'tableHeader' },
                                    { text: 'Doanh thu', style: 'tableHeader' },
                                    { text: 'Tỷ trọng', style: 'tableHeader' }
                                ],
                                ...categories.map(category => [
                                    category.category,
                                    category.quantity,
                                    formatCurrency(category.revenue),
                                    category.percentage + '%'
                                ])
                            ]
                        },
                        margin: [0, 5, 0, 15]
                    }
                );

                // Thêm biểu đồ
                if (chartImg) {
                    docContent.push(
                        { text: 'Biểu đồ doanh thu theo danh mục:', style: 'chartLabel', margin: [0, 10, 0, 5] },
                        {
                            image: chartImg,
                            width: 450,
                            alignment: 'center',
                            margin: [0, 5, 0, 15]
                        }
                    );
                }
                break;

            case 'comparisonChart':
                // Lấy ảnh biểu đồ so sánh
                chartImg = getChartImageBase64('comparisonChart');

                // Lấy dữ liệu so sánh
                const comparisonPeriod = document.getElementById('comparisonPeriod').value;
                const comparisonData = await fetchData(`/admin/api/reports/comparison?period=${comparisonPeriod}`);
                const periodLabel = getComparisonLabel(comparisonPeriod);

                // Thêm biểu đồ
                if (chartImg) {
                    docContent.push(
                        { text: `Biểu đồ so sánh ${periodLabel}:`, style: 'chartLabel' },
                        {
                            image: chartImg,
                            width: 500,
                            alignment: 'center',
                            margin: [0, 5, 0, 15]
                        }
                    );
                }

                // Thêm bảng dữ liệu
                docContent.push(
                    { text: `Bảng so sánh ${periodLabel}:`, style: 'sectionHeader', margin: [0, 10, 0, 5] },
                    {
                        table: {
                            headerRows: 1,
                            widths: ['*', '*', '*'],
                            body: [
                                [
                                    { text: 'Ngày', style: 'tableHeader' },
                                    { text: 'Kỳ hiện tại', style: 'tableHeader' },
                                    { text: 'Kỳ trước', style: 'tableHeader' }
                                ],
                                ...comparisonData.current.map((item, index) => [
                                    item.date,
                                    formatCurrency(item.revenue),
                                    formatCurrency(comparisonData.previous[index].revenue)
                                ])
                            ]
                        },
                        margin: [0, 0, 0, 15]
                    }
                );
                break;

            case 'paymentMethodChart':
            case 'paymentMethodTable':
                // Lấy ảnh biểu đồ phương thức thanh toán
                chartImg = getChartImageBase64('paymentMethodChart');

                // Lấy dữ liệu phương thức thanh toán
                const paymentMethods = await fetchData(`/admin/api/reports/payment-methods?startDate=${startDate}&endDate=${endDate}`);

                // Thêm bảng dữ liệu
                docContent.push(
                    {
                        table: {
                            headerRows: 1,
                            widths: ['*', 'auto', 'auto', 'auto'],
                            body: [
                                [
                                    { text: 'Phương thức', style: 'tableHeader' },
                                    { text: 'Số đơn hàng', style: 'tableHeader' },
                                    { text: 'Doanh thu', style: 'tableHeader' },
                                    { text: 'Tỷ trọng', style: 'tableHeader' }
                                ],
                                ...paymentMethods.map(method => [
                                    method.method,
                                    method.orders,
                                    formatCurrency(method.revenue),
                                    method.percentage + '%'
                                ])
                            ]
                        },
                        margin: [0, 5, 0, 15]
                    }
                );

                // Thêm biểu đồ
                if (chartImg) {
                    docContent.push(
                        { text: 'Biểu đồ phương thức thanh toán:', style: 'chartLabel', margin: [0, 10, 0, 5] },
                        {
                            image: chartImg,
                            width: 400,
                            alignment: 'center',
                            margin: [0, 5, 0, 15]
                        }
                    );
                }
                break;

            case 'inventoryMovementTable':
                // Lấy dữ liệu biến động kho
                const inventory = await fetchData(`/admin/api/reports/inventory?startDate=${startDate}&endDate=${endDate}`);

                // Thêm bảng dữ liệu
                docContent.push(
                    {
                        table: {
                            headerRows: 1,
                            widths: ['*', 'auto', 'auto', 'auto', 'auto', 'auto'],
                            body: [
                                [
                                    { text: 'Nguyên liệu', style: 'tableHeader' },
                                    { text: 'Tồn đầu kỳ', style: 'tableHeader' },
                                    { text: 'Nhập kho', style: 'tableHeader' },
                                    { text: 'Xuất kho', style: 'tableHeader' },
                                    { text: 'Tồn cuối kỳ', style: 'tableHeader' },
                                    { text: 'Đơn vị', style: 'tableHeader' }
                                ],
                                ...inventory.map(item => [
                                    item.name,
                                    item.initialStock,
                                    item.import,
                                    item.export,
                                    item.finalStock,
                                    item.unit
                                ])
                            ]
                        },
                        margin: [0, 5, 0, 15]
                    }
                );
                break;
        }

        // Tạo PDF với nội dung đã tạo
        const docDefinition = {
            content: docContent,
            styles: {
                header: {
                    fontSize: 20,
                    bold: true,
                    margin: [0, 0, 0, 10]
                },
                subheader: {
                    fontSize: 14,
                    margin: [0, 0, 0, 5]
                },
                sectionHeader: {
                    fontSize: 16,
                    bold: true,
                    margin: [0, 10, 0, 10],
                    color: '#1e88e5'
                },
                tableHeader: {
                    bold: true,
                    fontSize: 12,
                    fillColor: '#f5f5f5'
                },
                chartLabel: {
                    bold: true,
                    fontSize: 12,
                    margin: [0, 15, 0, 5]
                }
            },
            defaultStyle: {
                font: 'Roboto'
            },
            info: {
                title: `Báo cáo ${title} ${startDate} đến ${endDate}`,
                author: 'DND Coffee Admin',
                subject: `Báo cáo ${title}`
            }
        };

        // Tạo và tải xuống PDF
        pdfMake.createPdf(docDefinition).download(`bao-cao-${title.toLowerCase().replace(/\s+/g, '-')}-${startDate}-${endDate}.pdf`);
        showNotification(`Xuất ${title} sang PDF thành công`);
    } catch (error) {
        console.error(`Lỗi khi xuất ${title} sang PDF:`, error);
        showNotification(`Không thể xuất ${title} sang PDF: ${error.message}`, 'error');
    }
}

// Thêm vào phần khởi tạo trang
document.addEventListener('DOMContentLoaded', function() {
    // Các hàm khởi tạo hiện tại...

    // Thêm các nút xuất cho từng phần
    addExportButtonsToSections();

    // CSS cho các nút xuất trong từng phần
    const style = document.createElement('style');
    style.textContent = `
        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .section-exports {
            display: flex;
            gap: 5px;
        }
        .btn-sm {
            padding: 4px 8px;
            font-size: 12px;
        }
    `;
    document.head.appendChild(style);
});


// // Xuất báo cáo Excel
// function exportExcelReport() {
//     const startDate = document.getElementById('startDate').value;
//     const endDate = document.getElementById('endDate').value;
//
//     let url = '/admin/api/reports/export/excel';
//     if (startDate && endDate) {
//         url += `?startDate=${startDate}&endDate=${endDate}`;
//     }
//
//     window.location.href = url;
//     showNotification('Đang xuất báo cáo Excel...');
// }
//
// // Xuất báo cáo PDF
// function exportPdfReport() {
//     const startDate = document.getElementById('startDate').value;
//     const endDate = document.getElementById('endDate').value;
//
//     let url = '/admin/api/reports/export/pdf';
//     if (startDate && endDate) {
//         url += `?startDate=${startDate}&endDate=${endDate}`;
//     }
//
//     window.location.href = url;
//     showNotification('Đang xuất báo cáo PDF...');
// }

// Hàm lấy ảnh base64 từ canvas
function getChartImageBase64(chartId) {
    const canvas = document.getElementById(chartId);
    return canvas ? canvas.toDataURL('image/png') : null;
}

// Xuất Excel với đầy đủ dữ liệu
async function exportExcelReport() {
    showNotification('Đang tạo báo cáo Excel...');

    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const comparisonPeriod = document.getElementById('comparisonPeriod').value;

    try {
        // Lấy dữ liệu từ các API
        const summary = await fetchData(`/admin/api/reports/summary?startDate=${startDate}&endDate=${endDate}`);
        const topProducts = await fetchData(`/admin/api/reports/top-products?startDate=${startDate}&endDate=${endDate}&limit=10`);
        const categories = await fetchData(`/admin/api/reports/categories?startDate=${startDate}&endDate=${endDate}`);
        const paymentMethods = await fetchData(`/admin/api/reports/payment-methods?startDate=${startDate}&endDate=${endDate}`);
        const inventory = await fetchData(`/admin/api/reports/inventory?startDate=${startDate}&endDate=${endDate}`);
        const salesData = await fetchData(`/admin/api/reports/sales?startDate=${startDate}&endDate=${endDate}&groupBy=daily`);
        const comparisonData = await fetchData(`/admin/api/reports/comparison?period=${comparisonPeriod}`);

        // Tạo workbook
        const wb = XLSX.utils.book_new();

        // Sheet Tổng quan
        const summaryData = [
            ['BÁO CÁO DOANH THU', ''],
            [`Từ ngày: ${startDate}`, `Đến ngày: ${endDate}`],
            ['', ''],
            ['Chỉ tiêu', 'Giá trị'],
            ['Tổng số đơn hàng', summary.totalOrders],
            ['Tổng doanh thu', formatCurrency(summary.totalRevenue)],
            ['Số lượng khách hàng mới', summary.totalCustomers],
            ['Tăng trưởng', summary.growthRate + '%']
        ];
        const summarySheet = XLSX.utils.aoa_to_sheet(summaryData);
        XLSX.utils.book_append_sheet(wb, summarySheet, 'Tổng quan');

        // Sheet Doanh thu theo thời gian
        const salesDataArr = [
            ['BÁO CÁO DOANH THU THEO THỜI GIAN', '', ''],
            [`Từ ngày: ${startDate}`, `Đến ngày: ${endDate}`, ''],
            ['', '', ''],
            ['Ngày', 'Doanh thu (VNĐ)', 'Số đơn hàng']
        ];
        salesData.forEach(item => {
            salesDataArr.push([item.date, item.revenue, item.orders]);
        });
        // Thêm tổng cộng ở cuối bảng
        const totalRevenue = salesData.reduce((sum, item) => sum + item.revenue, 0);
        const totalOrders = salesData.reduce((sum, item) => sum + item.orders, 0);
        salesDataArr.push(['Tổng cộng', totalRevenue, totalOrders]);

        // Định dạng ô tiêu đề
        const salesSheet = XLSX.utils.aoa_to_sheet(salesDataArr);
        // Định dạng các ô header đậm
        salesSheet['!merges'] = [{s: {r: 0, c: 0}, e: {r: 0, c: 2}}]; // Hợp nhất ô tiêu đề
        XLSX.utils.book_append_sheet(wb, salesSheet, 'Doanh thu theo thời gian');

        // Sheet So sánh theo thời gian
        const comparisonHeader = [`So sánh ${getComparisonLabel(comparisonPeriod)}`];
        const comparisonDataArr = [
            ['Ngày', 'Kỳ hiện tại', 'Kỳ trước']
        ];
        for (let i = 0; i < comparisonData.current.length; i++) {
            comparisonDataArr.push([
                comparisonData.current[i].date,
                comparisonData.current[i].revenue,
                comparisonData.previous[i].revenue
            ]);
        }
        const comparisonSheet = XLSX.utils.aoa_to_sheet([comparisonHeader, [''], ...comparisonDataArr]);
        XLSX.utils.book_append_sheet(wb, comparisonSheet, 'So sánh theo thời gian');

        // Sheet Sản phẩm bán chạy
        const topProductsData = [
            ['Mã SP', 'Tên sản phẩm', 'Danh mục', 'Số lượng', 'Doanh thu', 'Tỷ trọng']
        ];
        topProducts.forEach(product => {
            topProductsData.push([
                product.id,
                product.name,
                product.category,
                product.quantity,
                product.revenue,
                product.percentage + '%'
            ]);
        });
        const topProductsSheet = XLSX.utils.aoa_to_sheet(topProductsData);
        XLSX.utils.book_append_sheet(wb, topProductsSheet, 'Sản phẩm bán chạy');

        // Sheet Doanh thu theo danh mục
        const categoriesData = [
            ['Danh mục', 'Số lượng', 'Doanh thu', 'Tỷ trọng']
        ];
        categories.forEach(category => {
            categoriesData.push([
                category.category,
                category.quantity,
                category.revenue,
                category.percentage + '%'
            ]);
        });
        const categoriesSheet = XLSX.utils.aoa_to_sheet(categoriesData);
        XLSX.utils.book_append_sheet(wb, categoriesSheet, 'Doanh thu theo danh mục');

        // Sheet Phương thức thanh toán
        const paymentsData = [
            ['Phương thức', 'Số đơn hàng', 'Doanh thu', 'Tỷ trọng']
        ];
        paymentMethods.forEach(method => {
            paymentsData.push([
                method.method,
                method.orders,
                method.revenue,
                method.percentage + '%'
            ]);
        });
        const paymentsSheet = XLSX.utils.aoa_to_sheet(paymentsData);
        XLSX.utils.book_append_sheet(wb, paymentsSheet, 'Phương thức thanh toán');

        // Sheet Biến động kho
        const inventoryData = [
            ['Nguyên liệu', 'Tồn đầu kỳ', 'Nhập kho', 'Xuất kho', 'Tồn cuối kỳ', 'Đơn vị']
        ];
        inventory.forEach(item => {
            inventoryData.push([
                item.name,
                item.initialStock,
                item.import,
                item.export,
                item.finalStock,
                item.unit
            ]);
        });
        const inventorySheet = XLSX.utils.aoa_to_sheet(inventoryData);
        XLSX.utils.book_append_sheet(wb, inventorySheet, 'Biến động kho hàng');

        // Xuất file
        const fileName = `bao-cao-doanh-thu-${startDate}-${endDate}.xlsx`;
        const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'binary' });

        function s2ab(s) {
            const buf = new ArrayBuffer(s.length);
            const view = new Uint8Array(buf);
            for (let i = 0; i < s.length; i++) view[i] = s.charCodeAt(i) & 0xFF;
            return buf;
        }

        const blob = new Blob([s2ab(wbout)], { type: 'application/octet-stream' });
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        link.click();

        showNotification('Xuất báo cáo Excel thành công');
    } catch (error) {
        console.error('Lỗi khi tạo báo cáo Excel:', error);
        showNotification('Không thể tạo báo cáo Excel', 'error');
    }
}

// Hàm lấy nhãn kỳ so sánh
function getComparisonLabel(period) {
    switch (period) {
        case 'week': return 'tuần này/tuần trước';
        case 'month': return 'tháng này/tháng trước';
        case 'quarter': return 'quý này/quý trước';
        case 'year': return 'năm này/năm trước';
        default: return 'kỳ này/kỳ trước';
    }
}

// Xuất PDF đầy đủ với biểu đồ
async function exportPdfReport() {
    showNotification('Đang tạo báo cáo PDF...');

    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const comparisonPeriod = document.getElementById('comparisonPeriod').value;

    try {
        // Lấy dữ liệu
        const summary = await fetchData(`/admin/api/reports/summary?startDate=${startDate}&endDate=${endDate}`);
        const topProducts = await fetchData(`/admin/api/reports/top-products?startDate=${startDate}&endDate=${endDate}&limit=10`);
        const categories = await fetchData(`/admin/api/reports/categories?startDate=${startDate}&endDate=${endDate}`);
        const paymentMethods = await fetchData(`/admin/api/reports/payment-methods?startDate=${startDate}&endDate=${endDate}`);
        const inventory = await fetchData(`/admin/api/reports/inventory?startDate=${startDate}&endDate=${endDate}`);
        const salesData = await fetchData(`/admin/api/reports/sales?startDate=${startDate}&endDate=${endDate}&groupBy=daily`);
        const comparisonData = await fetchData(`/admin/api/reports/comparison?period=${comparisonPeriod}`);

        // Lấy ảnh biểu đồ
        const salesChartImg = getChartImageBase64('salesChart');
        const categoryChartImg = getChartImageBase64('categoryChart');
        const paymentChartImg = getChartImageBase64('paymentMethodChart');
        const comparisonChartImg = getChartImageBase64('comparisonChart');

        // Định nghĩa font và styles
        const docDefinition = {
            content: [
                // Tiêu đề báo cáo
                { text: 'BÁO CÁO DOANH THU', style: 'header', alignment: 'center' },
                { text: `Thời gian: Từ ${startDate} đến ${endDate}`, style: 'subheader', alignment: 'center', margin: [0, 0, 0, 20] },

                // Phần tổng quan
                { text: 'TỔNG QUAN', style: 'sectionHeader' },
                {
                    table: {
                        headerRows: 1,
                        widths: ['*', '*'],
                        body: [
                            [{ text: 'Chỉ tiêu', style: 'tableHeader' }, { text: 'Giá trị', style: 'tableHeader' }],
                            ['Tổng số đơn hàng', summary.totalOrders.toString()],
                            ['Tổng doanh thu', formatCurrency(summary.totalRevenue)],
                            ['Số lượng khách hàng mới', summary.totalCustomers.toString()],
                            ['Tăng trưởng', summary.growthRate + '%']
                        ]
                    },
                    margin: [0, 10, 0, 15]
                },

                // Biểu đồ doanh thu
                { text: 'DOANH THU THEO THỜI GIAN', style: 'sectionHeader' },
                salesChartImg ? { text: 'Biểu đồ doanh thu theo thời gian:', style: 'chartLabel' } : {},
                salesChartImg ? {
                    image: salesChartImg,
                    width: 500,
                    alignment: 'center',
                    margin: [0, 5, 0, 10]
                } : {},

                // Thêm bảng doanh thu theo thời gian
                { text: 'Bảng doanh thu theo thời gian:', style: 'chartLabel', margin: [0, 10, 0, 5] },
                {
                    table: {
                        headerRows: 1,
                        widths: ['*', 'auto', 'auto'],
                        body: [
                            [
                                { text: 'Ngày', style: 'tableHeader' },
                                { text: 'Doanh thu', style: 'tableHeader' },
                                { text: 'Số đơn hàng', style: 'tableHeader' }
                            ],
                            ...salesData.map(item => [
                                item.date,
                                formatCurrency(item.revenue),
                                item.orders.toString()
                            ]),
                            [
                                { text: 'Tổng cộng', style: 'tableHeader' },
                                {
                                    text: formatCurrency(salesData.reduce((sum, item) => sum + item.revenue, 0)),
                                    style: 'tableHeader'
                                },
                                {
                                    text: salesData.reduce((sum, item) => sum + item.orders, 0).toString(),
                                    style: 'tableHeader'
                                }
                            ]
                        ]
                    },
                    margin: [0, 0, 0, 15]
                },

                // Biểu đồ so sánh doanh thu
                { text: `SO SÁNH DOANH THU ${getComparisonLabel(comparisonPeriod).toUpperCase()}`, style: 'sectionHeader', pageBreak: 'before' },
                comparisonChartImg ? {
                    image: comparisonChartImg,
                    width: 500,
                    alignment: 'center',
                    margin: [0, 5, 0, 20]
                } : {},

                {
                    table: {
                        headerRows: 1,
                        widths: ['*', '*', '*'],
                        body: [
                            [
                                { text: 'Ngày', style: 'tableHeader' },
                                { text: 'Kỳ hiện tại', style: 'tableHeader' },
                                { text: 'Kỳ trước', style: 'tableHeader' }
                            ],
                            ...comparisonData.current.map((item, index) => [
                                item.date,
                                formatCurrency(item.revenue),
                                formatCurrency(comparisonData.previous[index].revenue)
                            ])
                        ]
                    },
                    margin: [0, 10, 0, 15]
                },

                // Phần sản phẩm bán chạy
                { text: 'SẢN PHẨM BÁN CHẠY', style: 'sectionHeader', pageBreak: 'before' },
                {
                    table: {
                        headerRows: 1,
                        widths: [40, '*', 70, 50, 70, 50],
                        body: [
                            [
                                { text: 'Mã SP', style: 'tableHeader' },
                                { text: 'Tên sản phẩm', style: 'tableHeader' },
                                { text: 'Danh mục', style: 'tableHeader' },
                                { text: 'Số lượng', style: 'tableHeader' },
                                { text: 'Doanh thu', style: 'tableHeader' },
                                { text: 'Tỷ trọng', style: 'tableHeader' }
                            ],
                            ...topProducts.map(product => [
                                product.id,
                                product.name,
                                product.category,
                                product.quantity,
                                formatCurrency(product.revenue),
                                product.percentage + '%'
                            ])
                        ]
                    },
                    margin: [0, 10, 0, 15]
                },

                // Phần doanh thu theo danh mục
                { text: 'DOANH THU THEO DANH MỤC', style: 'sectionHeader', margin: [0, 15, 0, 10] },
                {
                    table: {
                        headerRows: 1,
                        widths: ['*', 'auto', 'auto', 'auto'],
                        body: [
                            [
                                { text: 'Danh mục', style: 'tableHeader' },
                                { text: 'Số lượng', style: 'tableHeader' },
                                { text: 'Doanh thu', style: 'tableHeader' },
                                { text: 'Tỷ trọng', style: 'tableHeader' }
                            ],
                            ...categories.map(category => [
                                category.category,
                                category.quantity,
                                formatCurrency(category.revenue),
                                category.percentage + '%'
                            ])
                        ]
                    },
                    margin: [0, 5, 0, 15]
                },

                // Biểu đồ danh mục
                categoryChartImg ? { text: 'Biểu đồ doanh thu theo danh mục:', style: 'chartLabel' } : {},
                categoryChartImg ? {
                    image: categoryChartImg,
                    width: 450,
                    alignment: 'center',
                    margin: [0, 5, 0, 20]
                } : {},

                // Phần phương thức thanh toán
                { text: 'THỐNG KÊ PHƯƠNG THỨC THANH TOÁN', style: 'sectionHeader', pageBreak: 'before' },
                {
                    table: {
                        headerRows: 1,
                        widths: ['*', 'auto', 'auto', 'auto'],
                        body: [
                            [
                                { text: 'Phương thức', style: 'tableHeader' },
                                { text: 'Số đơn hàng', style: 'tableHeader' },
                                { text: 'Doanh thu', style: 'tableHeader' },
                                { text: 'Tỷ trọng', style: 'tableHeader' }
                            ],
                            ...paymentMethods.map(method => [
                                method.method,
                                method.orders,
                                formatCurrency(method.revenue),
                                method.percentage + '%'
                            ])
                        ]
                    },
                    margin: [0, 5, 0, 15]
                },

                // Biểu đồ phương thức thanh toán
                paymentChartImg ? { text: 'Biểu đồ phương thức thanh toán:', style: 'chartLabel' } : {},
                paymentChartImg ? {
                    image: paymentChartImg,
                    width: 400,
                    alignment: 'center',
                    margin: [0, 5, 0, 20]
                } : {},

                // Phần biến động kho hàng
                { text: 'BIẾN ĐỘNG KHO HÀNG', style: 'sectionHeader', margin: [0, 15, 0, 10] },
                {
                    table: {
                        headerRows: 1,
                        widths: ['*', 'auto', 'auto', 'auto', 'auto', 'auto'],
                        body: [
                            [
                                { text: 'Nguyên liệu', style: 'tableHeader' },
                                { text: 'Tồn đầu kỳ', style: 'tableHeader' },
                                { text: 'Nhập kho', style: 'tableHeader' },
                                { text: 'Xuất kho', style: 'tableHeader' },
                                { text: 'Tồn cuối kỳ', style: 'tableHeader' },
                                { text: 'Đơn vị', style: 'tableHeader' }
                            ],
                            ...inventory.map(item => [
                                item.name,
                                item.initialStock,
                                item.import,
                                item.export,
                                item.finalStock,
                                item.unit
                            ])
                        ]
                    },
                    margin: [0, 5, 0, 15]
                }
            ],

            // Định nghĩa styles
            styles: {
                header: {
                    fontSize: 20,
                    bold: true,
                    margin: [0, 0, 0, 10]
                },
                subheader: {
                    fontSize: 14,
                    margin: [0, 0, 0, 5]
                },
                sectionHeader: {
                    fontSize: 16,
                    bold: true,
                    margin: [0, 10, 0, 10],
                    color: '#1e88e5'
                },
                tableHeader: {
                    bold: true,
                    fontSize: 12,
                    fillColor: '#f5f5f5'
                },
                chartLabel: {
                    bold: true,
                    fontSize: 12,
                    margin: [0, 15, 0, 5]
                }
            },

            // Đặt font Roboto cho toàn bộ tài liệu
            defaultStyle: {
                font: 'Roboto'
            },

            // Thông tin tài liệu
            info: {
                title: `Báo cáo doanh thu ${startDate} đến ${endDate}`,
                author: 'DND Coffee Admin',
                subject: 'Báo cáo thống kê doanh thu'
            }
        };

        // Tạo và tải xuống PDF
        pdfMake.createPdf(docDefinition).download(`bao-cao-doanh-thu-${startDate}-${endDate}.pdf`);
        showNotification('Xuất báo cáo PDF thành công');
    } catch (error) {
        console.error('Lỗi khi tạo báo cáo PDF:', error);
        showNotification('Không thể tạo báo cáo PDF: ' + error.message, 'error');
    }
}

// Hàm hỗ trợ để lấy dữ liệu từ API
async function fetchData(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return await response.json();
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