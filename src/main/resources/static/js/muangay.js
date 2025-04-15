console.log("muangay.js is loaded");

document.addEventListener("DOMContentLoaded", function () {
    // Lắng nghe sự kiện click cho các nút "Chọn mua"
    const buyBtns = document.querySelectorAll('.buy-btn');

    buyBtns.forEach(btn => {
        btn.addEventListener('click', function () {
            const productId = this.getAttribute('data-product-id');
            const productName = this.getAttribute('data-product-name');
            const productPrice = this.getAttribute('data-product-price');
            const productImage = this.getAttribute('data-product-image');

            // Gọi hàm thêm sản phẩm vào giỏ hàng
            themVaoGioHang(productId, productName, productPrice, productImage);
        });
    });

    // Hàm thêm sản phẩm vào giỏ hàng
    function themVaoGioHang(productId, productName, productPrice, productImage) {
        let gioHang = JSON.parse(localStorage.getItem("gioHang")) || [];

        // Chuyển productId thành số để so sánh
        const id = parseInt(productId);

        // Kiểm tra nếu sản phẩm đã có thì tăng số lượng
        const index = gioHang.findIndex(item => parseInt(item.id) === id);
        if (index !== -1) {
            gioHang[index].quantity += 1;
        } else {
            gioHang.push({
                id: productId, // Lưu nguyên dạng chuỗi để đồng bộ với dữ liệu ban đầu
                name: productName,
                price: parseInt(productPrice),
                image: productImage,
                quantity: 1
            });
        }

        // Lưu lại vào localStorage
        localStorage.setItem("gioHang", JSON.stringify(gioHang));

        // Cập nhật giao diện
        hienThiGioHang();
    }

    const menuToggle = document.getElementById("menu-toggle");
    const sidebar = document.getElementById("sidebar");
    const closeMenu = document.getElementById("close-menu"); // Đóng menu
    const menuCart = document.getElementById("menu-cart");
    const cartbar = document.getElementById("cartbar");
    const closeCart = document.getElementById("close-cart"); // Đóng giỏ hàng

    // Mở thanh menu khi nhấn vào menu toggle
    menuToggle.addEventListener("click", function () {
        sidebar.classList.add("active");
    });

    // Đóng thanh menu khi nhấn vào nút đóng
    closeMenu.addEventListener("click", function () {
        sidebar.classList.remove("active");
    });

    // Mở thanh giỏ hàng khi nhấn vào menu cart
    menuCart.addEventListener("click", function () {
        cartbar.classList.add("active");
    });

    // Đóng thanh giỏ hàng khi nhấn vào nút đóng giỏ hàng
    closeCart.addEventListener("click", function () {
        cartbar.classList.remove("active");
    });

    // Xử lý các menu có submenu
    document.querySelectorAll(".has-submenu > a").forEach(item => {
        item.addEventListener("click", function (e) {
            e.preventDefault();
            this.parentElement.classList.toggle("active");
        });
    });

    // Chức năng các nút tăng/giảm số lượng
    const decreaseBtns = document.querySelectorAll('.decrease');
    const increaseBtns = document.querySelectorAll('.increase');
    const removeItemBtns = document.querySelectorAll('.cart-item-remove');
    const quantityInputs = document.querySelectorAll('.quantity-input');

    decreaseBtns.forEach(btn => {
        btn.addEventListener('click', function () {
            const input = this.nextElementSibling;
            let value = parseInt(input.value);
            if (value > 1) {
                input.value = value - 1;
                capNhatTongTien();
                capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
            }
        });
    });

    increaseBtns.forEach(btn => {
        btn.addEventListener('click', function () {
            const input = this.previousElementSibling;
            let value = parseInt(input.value);
            input.value = value + 1;
            capNhatTongTien();
            capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
        });
    });

    // Thêm chức năng nhập số lượng trực tiếp
    quantityInputs.forEach(input => {
        // Xử lý khi người dùng nhập số lượng
        input.addEventListener('input', function () {
            // Đảm bảo giá trị nhập vào là số nguyên dương
            let value = parseInt(this.value);

            // Nếu người dùng xóa hết số hoặc nhập một giá trị không phải số
            if (isNaN(value) || value <= 0) {
                this.value = 1;
            }

            // Cập nhật tổng tiền và localStorage sau mỗi thay đổi
            capNhatTongTien();
            capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
        });

        // Xử lý khi người dùng rời khỏi ô nhập
        input.addEventListener('blur', function () {
            // Đảm bảo giá trị tối thiểu là 1
            if (this.value === '' || parseInt(this.value) < 1) {
                this.value = 1;
            }
            // Cập nhật tổng tiền và localStorage
            capNhatTongTien();
            capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
        });

        // Xử lý khi người dùng nhấn Enter
        input.addEventListener('keyup', function (e) {
            if (e.key === 'Enter') {
                this.blur(); // Loại bỏ focus khỏi input
                capNhatTongTien();
                capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
            }
        });

        // Ngăn chặn các ký tự không phải số
        input.addEventListener('keypress', function (e) {
            if (!/[0-9]/.test(e.key)) {
                e.preventDefault();
            }
        });
    });

    removeItemBtns.forEach(btn => {
        btn.addEventListener('click', function () {
            this.closest('.cart-item').remove();
            capNhatTongTien();
            capNhatSoLuongGioHang();
            capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
            kiemTraGioHangTrong(); // Kiểm tra xem giỏ hàng có trống không sau khi xóa
        });
    });

    // Hàm cập nhật tổng tiền
    function capNhatTongTien() {
        const cartItems = document.querySelectorAll('.cart-item');
        let total = 0;

        cartItems.forEach(item => {
            const quantityInput = item.querySelector('.quantity-input');
            const priceElement = item.querySelector('.cart-item-price');

            if (quantityInput && priceElement) {
                const quantity = parseInt(quantityInput.value) || 1; // Mặc định là 1 nếu không hợp lệ
                const priceText = priceElement.textContent;
                const price = parseInt(priceText.replace(/\D/g, ''));
                total += quantity * price;
            }
        });

        document.querySelector('.cart-total-price').textContent = dinhDangTien(total) + 'đ';
        ganSuKienChoGioHang();
    }

    // Hàm cập nhật số lượng sản phẩm trong giỏ hàng
    function capNhatSoLuongGioHang() {
        let tongSoLuong = 0;
        const quantityInputs = document.querySelectorAll('.cart-item .quantity-input');

        quantityInputs.forEach(input => {
            const soLuong = parseInt(input.value) || 0;
            tongSoLuong += soLuong;
        });

        const cartCountElement = document.querySelector('.cart-count');
        if (cartCountElement) {
            cartCountElement.textContent = tongSoLuong;
        }

        ganSuKienChoGioHang();
    }

    // Hàm định dạng tiền tệ
    function dinhDangTien(price) {
        return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
    }

    // Khởi tạo giỏ hàng
    capNhatTongTien();
    capNhatSoLuongGioHang();

    // Thêm trạng thái giỏ hàng trống
    function kiemTraGioHangTrong() {
        const cartItems = document.querySelectorAll('.cart-item').length;
        const cartItemsContainer = document.getElementById('cart-items');
        const cartFooter = document.querySelector('.cart-footer');

        if (cartItems === 0) {
            const emptyCartHTML = `
                <div class="empty-cart">
                    <div class="empty-cart-icon">🛒</div>
                    <p>Giỏ hàng của bạn đang trống</p>
                    <p>Hãy thêm sản phẩm vào giỏ hàng!</p>
                </div>
            `;
            cartItemsContainer.innerHTML = emptyCartHTML;

            if (cartFooter) {
                cartFooter.style.display = 'none';
            }
        } else {
            // Xóa thông báo trống nếu có
            const emptyCartDiv = document.querySelector('.empty-cart');
            if (emptyCartDiv) {
                emptyCartDiv.remove();
            }

            if (cartFooter) {
                cartFooter.style.display = 'block';
            }
        }
    }

    kiemTraGioHangTrong();

    function ganSuKienChoGioHang() {
        const decreaseBtns = document.querySelectorAll('.decrease');
        const increaseBtns = document.querySelectorAll('.increase');
        const removeItemBtns = document.querySelectorAll('.cart-item-remove');
        const quantityInputs = document.querySelectorAll('.quantity-input');

        decreaseBtns.forEach(btn => {
            btn.onclick = function () {
                const input = this.nextElementSibling;
                let value = parseInt(input.value);
                if (value > 1) {
                    input.value = value - 1;
                    capNhatTongTien();
                    capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
                }
            };
        });

        increaseBtns.forEach(btn => {
            btn.onclick = function () {
                const input = this.previousElementSibling;
                let value = parseInt(input.value);
                input.value = value + 1;
                capNhatTongTien();
                capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
            };
        });

        removeItemBtns.forEach(btn => {
            btn.onclick = function () {
                this.closest('.cart-item').remove();
                capNhatTongTien();
                capNhatSoLuongGioHang();
                capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
                kiemTraGioHangTrong();
            };
        });

        quantityInputs.forEach(input => {
            input.oninput = function () {
                let value = parseInt(this.value);
                if (isNaN(value) || value <= 0) this.value = 1;
                capNhatTongTien();
                capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
            };
            input.onblur = function () {
                if (this.value === '' || parseInt(this.value) < 1) this.value = 1;
                capNhatTongTien();
                capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
            };
            input.onkeyup = function (e) {
                if (e.key === 'Enter') {
                    this.blur();
                    capNhatTongTien();
                    capNhatLocalStorage(); // Đã sửa để lưu đúng vào localStorage
                }
            };
            input.onkeypress = function (e) {
                if (!/[0-9]/.test(e.key)) e.preventDefault();
            };
        });
    }

    function hienThiGioHang() {
        const cartItemsContainer = document.getElementById('cart-items');
        cartItemsContainer.innerHTML = "";

        const gioHang = JSON.parse(localStorage.getItem("gioHang")) || [];

        gioHang.forEach(item => {
            const cartItemHTML = `
                <div class="cart-item" data-product-id="${item.id}">
                    <img src="${item.image}" alt="Sản phẩm">
                    <div class="cart-item-info">
                        <div class="cart-item-title">${item.name}</div>
                        <div class="cart-item-price">${dinhDangTien(item.price)}đ</div>
                        <div class="cart-item-quantity">
                            <div class="quantity-btn decrease">-</div>
                            <input type="number" min="1" class="quantity-input" value="${item.quantity}">
                            <div class="quantity-btn increase">+</div>
                        </div>
                        <div class="cart-item-remove">Bỏ sản phẩm</div>
                    </div>
                </div>`;
            cartItemsContainer.insertAdjacentHTML('beforeend', cartItemHTML);
        });

        ganSuKienChoGioHang();
        capNhatTongTien();
        capNhatSoLuongGioHang();
        kiemTraGioHangTrong();
        // Bỏ gọi capNhatLocalStorage() ở đây vì không cần thiết
    }

    // Hàm cập nhật localStorage (đã sửa)
    function capNhatLocalStorage() {
        const cartItems = document.querySelectorAll('.cart-item');
        let gioHang = [];

        cartItems.forEach(item => {
            const id = item.getAttribute('data-product-id'); // Sửa: data-product-id
            const name = item.querySelector('.cart-item-title').textContent; // Sửa: .cart-item-title
            const price = parseInt(item.querySelector('.cart-item-price').textContent.replace(/\D/g, ''));
            const image = item.querySelector('img').getAttribute('src'); // Sửa: img
            const quantity = parseInt(item.querySelector('.quantity-input').value);

            gioHang.push({
                id: id,
                name: name,
                price: price,
                image: image,
                quantity: quantity
            });
        });

        localStorage.setItem('gioHang', JSON.stringify(gioHang));
        console.log('Giỏ hàng đã được cập nhật trong localStorage:', gioHang); // Thêm log để kiểm tra
    }
    hienThiGioHang(); // Gọi khi tải trang
});

document.querySelectorAll('.view-all-btn').forEach(btn => {
    btn.addEventListener('click', function () {
        const loai = this.getAttribute('data-loai');
        // Điều hướng sang trang chi tiết với tham số loại
        window.location.href = `/xemtatca?loai=${encodeURIComponent(loai)}`;
    });
});

window.addEventListener("pageshow", function (event) {
    if (event.persisted || performance.getEntriesByType("navigation")[0].type === "back_forward") {
        hienThiGioHang();
    }
});