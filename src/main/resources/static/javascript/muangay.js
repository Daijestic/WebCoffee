console.log("muangay.js is loaded");

document.addEventListener("DOMContentLoaded", function () {
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
      btn.addEventListener('click', function() {
          const input = this.nextElementSibling;
          let value = parseInt(input.value);
          if (value > 1) {
              input.value = value - 1;
              capNhatTongTien();
          }
      });
  });
  
  increaseBtns.forEach(btn => {
      btn.addEventListener('click', function() {
          const input = this.previousElementSibling;
          let value = parseInt(input.value);
          input.value = value + 1;
          capNhatTongTien();
      });
  });
  
  // Thêm chức năng nhập số lượng trực tiếp
  quantityInputs.forEach(input => {
      // Xử lý khi người dùng nhập số lượng
      input.addEventListener('input', function() {
          // Đảm bảo giá trị nhập vào là số nguyên dương
          let value = parseInt(this.value);
          
          // Nếu người dùng xóa hết số hoặc nhập một giá trị không phải số
          if (isNaN(value) || value <= 0) {
              this.value = 1;
          }
          
          // Cập nhật tổng tiền sau mỗi thay đổi
          capNhatTongTien();
      });
      
      // Xử lý khi người dùng rời khỏi ô nhập
      input.addEventListener('blur', function() {
          // Đảm bảo giá trị tối thiểu là 1
          if (this.value === '' || parseInt(this.value) < 1) {
              this.value = 1;
          }
          // Đảm bảo cập nhật lại tổng tiền
          capNhatTongTien();
      });
      
      // Xử lý khi người dùng nhấn Enter
      input.addEventListener('keyup', function(e) {
          if (e.key === 'Enter') {
              this.blur(); // Loại bỏ focus khỏi input
              capNhatTongTien();
          }
      });
      
      // Ngăn chặn các ký tự không phải số
      input.addEventListener('keypress', function(e) {
          if (!/[0-9]/.test(e.key)) {
              e.preventDefault();
          }
      });
  });
  
  removeItemBtns.forEach(btn => {
      btn.addEventListener('click', function() {
          this.closest('.cart-item').remove();
          capNhatTongTien();
          capNhatSoLuongGioHang();
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
  }
  
  // Hàm cập nhật số lượng sản phẩm trong giỏ hàng
  function capNhatSoLuongGioHang() {
      const cartItems = document.querySelectorAll('.cart-item').length;
      const cartCountElement = document.querySelector('.cart-count');
      if (cartCountElement) {
          cartCountElement.textContent = cartItems;
      }
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
          
          // Ẩn phần footer khi giỏ hàng trống
          if (cartFooter) {
              cartFooter.style.display = 'none';
          }
      } else if (cartFooter) {
          // Hiển thị lại footer nếu có sản phẩm
          cartFooter.style.display = 'block';
      }
  }
  
  kiemTraGioHangTrong();
  });
   