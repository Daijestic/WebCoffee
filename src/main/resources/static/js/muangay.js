document.addEventListener('DOMContentLoaded', function() {
    // Variables for cart functionality
    let userId = null;
    let csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    let csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    const usernameData = document.body.getAttribute('data-username');

    // DOM elements
    const cartItemsContainer = document.getElementById('cart-items');
    const cartCountElement = document.querySelector('.cart-count');
    const cartTotalPrice = document.querySelector('.cart-total-price');
    const menuCartButton = document.getElementById('menu-cart');
    const cartbar = document.getElementById('cartbar');
    const closeCartButton = document.getElementById('close-cart');    // Initialize cart
    async function initializeCart() {
        if (usernameData) {
            console.log("Username found:", usernameData);
            try {
                const response = await fetch(`/api/users/get-id?username=${usernameData}`);
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();
                userId = data.userId;
                console.log("User ID fetched:", userId);
                // After getting userId, update cart count and load items
                updateCartCount();
                loadCartItems();
            } catch (error) {
                console.error('Error fetching user ID:', error);
            }
        } else {
            console.log('User not logged in');
        }
    }
    
    // Call initialize function
    initializeCart();

    // Cart toggle functionality
    menuCartButton.addEventListener('click', function() {
        cartbar.classList.add('active');
    });

    closeCartButton.addEventListener('click', function() {
        cartbar.classList.remove('active');
    });

    // Add event listeners to all buy buttons
    document.querySelectorAll('.buy-btn, .cart-btn').forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            console.log("Current userId:", userId); // Debug

            if (!usernameData) {
                alert('Vui lòng đăng nhập để thêm vào giỏ hàng!');
                window.location.href = '/login';
                return;
            }

            const productCard = this.closest('.product-card');
            const monId = productCard.getAttribute('data-mon-id');

            if (monId) {
                showProductSelectionModal(monId);
            } else {
                console.error('Không tìm thấy ID món');
            }
        });
    });

    // Function to fetch user ID from username
    async function fetchUserId(username) {
        try {
            const response = await fetch(`/api/users/get-id?username=${username}`);
            const data = await response.json();
            return data.userId;
        } catch (error) {
            console.error('Error fetching user ID:', error);
            return null;
        }
    }

    // Function to update cart count
    function updateCartCount() {
        if (!userId) return;

        fetch(`/api/gio-hang/so-luong?userId=${userId}`)
            .then(response => response.json())
            .then(data => {
                cartCountElement.textContent = data.cartCount;
            })
            .catch(error => console.error('Error updating cart count:', error));
    }

    // Function to load cart items
    function loadCartItems() {
        if (!userId) return;

        fetch(`/api/gio-hang/danh-sach?userId=${userId}`)
            .then(response => response.json())
            .then(cartItems => {
                renderCartItems(cartItems);
                updateCartTotal(cartItems);
            })
            .catch(error => console.error('Error loading cart items:', error));
    }

    // Function to render cart items
    function renderCartItems(cartItems) {
        cartItemsContainer.innerHTML = '';

        if (cartItems.length === 0) {
            cartItemsContainer.innerHTML = '<div class="empty-cart">Giỏ hàng trống</div>';
            return;
        }

        cartItems.forEach(item => {
            const cartItemElement = document.createElement('div');
            cartItemElement.className = 'cart-item';
            cartItemElement.innerHTML = `
                <div class="cart-item-image">
                    <img src="${item.hinhAnh}" alt="${item.tenMon}">
                </div>
                <div class="cart-item-details">
                    <div class="cart-item-title">${item.tenMon}</div>
                    <div class="cart-item-size">Size: ${item.tenSize}</div>
                    <div class="cart-item-price">${formatPrice(item.giaBan)} VNĐ</div>
                    <div class="cart-item-actions">
                        <button class="decrease-qty" data-mon-id="${item.idMon}">-</button>
                        <span class="cart-item-qty">${item.soLuong}</span>
                        <button class="increase-qty" data-mon-id="${item.idMon}">+</button>
                        <button class="remove-item" data-mon-id="${item.idMon}">✖</button>
                    </div>
                </div>
            `;

            cartItemsContainer.appendChild(cartItemElement);
        });

        // Add event listeners to quantity buttons
        document.querySelectorAll('.decrease-qty').forEach(button => {
            button.addEventListener('click', function() {
                decreaseQuantity(this.getAttribute('data-mon-id'));
            });
        });

        document.querySelectorAll('.increase-qty').forEach(button => {
            button.addEventListener('click', function() {
                increaseQuantity(this.getAttribute('data-mon-id'));
            });
        });

        document.querySelectorAll('.remove-item').forEach(button => {
            button.addEventListener('click', function() {
                removeCartItem(this.getAttribute('data-mon-id'));
            });
        });
    }

    // Function to update cart total
    function updateCartTotal(cartItems) {
        const total = cartItems.reduce((sum, item) => sum + (item.giaBan * item.soLuong), 0);
        cartTotalPrice.textContent = formatPrice(total) + ' VNĐ';
    }

    // Function to format price
    function formatPrice(price) {
        return new Intl.NumberFormat('vi-VN').format(price);
    }

    // Function to decrease item quantity
    function decreaseQuantity(monId) {
        if (!userId) return;

        fetch(`/api/gio-hang/danh-sach?userId=${userId}`)
            .then(response => response.json())
            .then(cartItems => {
                const item = cartItems.find(item => item.idMon == monId);

                if (item && item.soLuong > 1) {
                    // Decrease quantity if more than 1
                    updateCartItemQuantity(monId, item.soLuong - 1);
                } else if (item && item.soLuong === 1) {
                    // Remove item if quantity is 1
                    removeCartItem(monId);
                }
            })
            .catch(error => console.error('Error decreasing quantity:', error));
    }

    // Function to increase item quantity
    function increaseQuantity(monId) {
        if (!userId) return;

        fetch(`/api/gio-hang/danh-sach?userId=${userId}`)
            .then(response => response.json())
            .then(cartItems => {
                const item = cartItems.find(item => item.idMon == monId);

                if (item) {
                    // Increase quantity
                    updateCartItemQuantity(monId, item.soLuong + 1);
                }
            })
            .catch(error => console.error('Error increasing quantity:', error));
    }

    // Function to update cart item quantity
    function updateCartItemQuantity(monId, newQuantity) {
        if (!userId) return;

        const requestData = {
            userId: userId,
            monId: parseInt(monId),
            soLuong: newQuantity
        };

        fetch('/api/gio-hang/cap-nhat', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(requestData)
        })
            .then(response => response.json())
            .then(() => {
                // Reload cart after updating
                loadCartItems();
                updateCartCount();
            })
            .catch(error => console.error('Error updating quantity:', error));
    }

    // Function to remove item from cart
    function removeCartItem(monId) {
        if (!userId) return;

        const requestData = {
            userId: userId,
            monId: parseInt(monId)
        };

        fetch('/api/gio-hang/xoa', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(requestData)
        })
            .then(response => response.json())
            .then(() => {
                // Reload cart after removing item
                loadCartItems();
                updateCartCount();
            })
            .catch(error => console.error('Error removing item:', error));
    }

    // Function to clear entire cart
    document.querySelector('.checkout-btn').addEventListener('click', function(event) {
        // Add confirmation before checkout if needed
        if (cartItemsContainer.children.length === 0 ||
            cartItemsContainer.querySelector('.empty-cart')) {
            event.preventDefault();
            alert('Giỏ hàng của bạn đang trống!');
        }
    });

    // Function to show product selection modal (size, quantity, etc.)
    function showProductSelectionModal(monId) {
        // Create modal element
        const modal = document.createElement('div');
        modal.className = 'product-selection-modal';

        // Fetch product details
        fetch(`/api/san-pham/${monId}`)
            .then(response => response.json())
            .then(product => {
                // Create modal content
                modal.innerHTML = `
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3>Thêm vào giỏ hàng</h3>
                            <span class="close-modal">&times;</span>
                        </div>
                        <div class="modal-body">
                            <div class="product-preview">
                                <img src="${product.path}" alt="${product.tenMon}">
                                <div class="product-info">
                                    <h4>${product.tenMon}</h4>
                                    <p class="product-price">${formatPrice(product.giaMonSizeResponses[0].giaBan)} VNĐ</p>
                                </div>
                            </div>
                            <div class="product-options">
                                <div class="size-selection">
                                    <label>Chọn kích thước:</label>
                                    <div class="size-options">
                                        ${product.giaMonSizeResponses.map(size =>
                    `<button class="size-option" data-size-id="${size.idSize}" data-price="${size.giaBan}">
                                                ${size.tenSize} - ${formatPrice(size.giaBan)} VNĐ
                                            </button>`
                ).join('')}
                                    </div>
                                </div>
                                <div class="quantity-selection">
                                    <label>Số lượng:</label>
                                    <div class="quantity-controls">
                                        <button class="decrease-modal-qty">-</button>
                                        <input type="number" class="quantity-input" value="1" min="1" max="10">
                                        <button class="increase-modal-qty">+</button>
                                    </div>
                                </div>
                                <div class="note-input">
                                    <label>Ghi chú:</label>
                                    <textarea class="note-textarea" placeholder="Thêm ghi chú cho món này..."></textarea>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button class="cancel-modal">Hủy</button>
                            <button class="add-to-cart-modal">Thêm vào giỏ hàng</button>
                        </div>
                    </div>
                `;

                // Add modal to document
                document.body.appendChild(modal);

                // Show modal
                setTimeout(() => {
                    modal.classList.add('active');
                }, 10);

                // Set up event listeners for modal
                const closeModal = () => {
                    modal.classList.remove('active');
                    setTimeout(() => {
                        modal.remove();
                    }, 300);
                };

                // Close button click
                modal.querySelector('.close-modal').addEventListener('click', closeModal);
                modal.querySelector('.cancel-modal').addEventListener('click', closeModal);

                // Size selection
                let selectedSizeId = product.giaMonSizeResponses[0].idSize;
                let selectedPrice = product.giaMonSizeResponses[0].giaBan;

                modal.querySelectorAll('.size-option').forEach(button => {
                    button.addEventListener('click', function() {
                        // Remove active class from all size options
                        modal.querySelectorAll('.size-option').forEach(btn => {
                            btn.classList.remove('active');
                        });

                        // Add active class to selected size
                        this.classList.add('active');

                        // Update selected size and price
                        selectedSizeId = this.getAttribute('data-size-id');
                        selectedPrice = this.getAttribute('data-price');
                    });
                });

                // Set first size as active by default
                modal.querySelector('.size-option').classList.add('active');

                // Quantity controls
                const quantityInput = modal.querySelector('.quantity-input');

                modal.querySelector('.decrease-modal-qty').addEventListener('click', function() {
                    if (parseInt(quantityInput.value) > 1) {
                        quantityInput.value = parseInt(quantityInput.value) - 1;
                    }
                });

                modal.querySelector('.increase-modal-qty').addEventListener('click', function() {
                    if (parseInt(quantityInput.value) < 10) {
                        quantityInput.value = parseInt(quantityInput.value) + 1;
                    }
                });

                // Add to cart functionality
                modal.querySelector('.add-to-cart-modal').addEventListener('click', function() {
                    const quantity = parseInt(quantityInput.value);
                    const note = modal.querySelector('.note-textarea').value;

                    addToCart(monId, selectedSizeId, quantity, note);
                    closeModal();
                });
            })
            .catch(error => console.error('Error fetching product details:', error));
    }

    // Function to add product to cart
    function addToCart(monId, sizeId, soLuong, ghiChu) {
        if (!userId) return;

        const requestData = {
            userId: userId,
            monId: parseInt(monId),
            sizeId: parseInt(sizeId),
            soLuong: soLuong,
            ghiChu: ghiChu
        };

        fetch('/api/gio-hang/them', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(requestData)
        })
            .then(response => response.json())
            .then(data => {
                // Show success message
                showNotification('Đã thêm sản phẩm vào giỏ hàng!');

                // Update cart
                loadCartItems();
                updateCartCount();
            })
            .catch(error => console.error('Error adding to cart:', error));
    }

    // Function to show notification
    function showNotification(message) {
        const notification = document.createElement('div');
        notification.className = 'notification';
        notification.textContent = message;

        document.body.appendChild(notification);

        // Show notification
        setTimeout(() => {
            notification.classList.add('active');
        }, 10);

        // Hide and remove notification after delay
        setTimeout(() => {
            notification.classList.remove('active');
            setTimeout(() => {
                notification.remove();
            }, 300);
        }, 3000);
    }

    // Add CSS for new elements
    const style = document.createElement('style');
    style.innerHTML = `
        /* Product Selection Modal */
        .product-selection-modal {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
            opacity: 0;
            visibility: hidden;
            transition: opacity 0.3s ease, visibility 0.3s ease;
        }
        
        .product-selection-modal.active {
            opacity: 1;
            visibility: visible;
        }
        
        .modal-content {
            background-color: #fff;
            border-radius: 8px;
            width: 90%;
            max-width: 500px;
            max-height: 90vh;
            overflow-y: auto;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
        }
        
        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 16px;
            border-bottom: 1px solid #eee;
        }
        
        .modal-header h3 {
            margin: 0;
            font-size: 18px;
        }
        
        .close-modal {
            font-size: 24px;
            cursor: pointer;
            color: #777;
        }
        
        .modal-body {
            padding: 16px;
        }
        
        .product-preview {
            display: flex;
            align-items: center;
            margin-bottom: 16px;
        }
        
        .product-preview img {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: 4px;
            margin-right: 16px;
        }
        
        .product-options {
            margin-top: 16px;
        }
        
        .size-selection, .quantity-selection, .note-input {
            margin-bottom: 16px;
        }
        
        .size-options {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-top: 8px;
        }
        
        .size-option {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            background: #f9f9f9;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .size-option.active {
            background: #4d2a1e;
            color: white;
            border-color: #4d2a1e;
        }
        
        .quantity-controls {
            display: flex;
            align-items: center;
            margin-top: 8px;
        }
        
        .decrease-modal-qty, .increase-modal-qty {
            width: 32px;
            height: 32px;
            background: #f1f1f1;
            border: 1px solid #ddd;
            border-radius: 4px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 18px;
            cursor: pointer;
        }
        
        .quantity-input {
            width: 50px;
            height: 32px;
            border: 1px solid #ddd;
            border-radius: 4px;
            text-align: center;
            margin: 0 8px;
        }
        
        .note-textarea {
            width: 100%;
            height: 80px;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 8px;
            margin-top: 8px;
            resize: none;
        }
        
        .modal-footer {
            display: flex;
            justify-content: flex-end;
            padding: 16px;
            border-top: 1px solid #eee;
            gap: 12px;
        }
        
        .cancel-modal, .add-to-cart-modal {
            padding: 10px 16px;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 500;
        }
        
        .cancel-modal {
            background: #f1f1f1;
            border: 1px solid #ddd;
            color: #333;
        }
        
        .add-to-cart-modal {
            background: #4d2a1e;
            border: 1px solid #4d2a1e;
            color: white;
        }
        
        /* Cart Styles */
        .cart-items {
            max-height: 300px;
            overflow-y: auto;
            padding: 12px;
        }
        
        .cart-item {
            display: flex;
            margin-bottom: 16px;
            padding-bottom: 16px;
            border-bottom: 1px solid #eee;
        }
        
        .cart-item-image {
            width: 60px;
            height: 60px;
            margin-right: 12px;
        }
        
        .cart-item-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            border-radius: 4px;
        }
        
        .cart-item-details {
            flex: 1;
        }
        
        .cart-item-title {
            font-weight: bold;
            margin-bottom: 4px;
        }
        
        .cart-item-size {
            font-size: 12px;
            color: #777;
            margin-bottom: 4px;
        }
        
        .cart-item-price {
            font-weight: 500;
            margin-bottom: 8px;
        }
        
        .cart-item-actions {
            display: flex;
            align-items: center;
        }
        
        .decrease-qty, .increase-qty {
            width: 24px;
            height: 24px;
            border-radius: 4px;
            border: 1px solid #ddd;
            background: #f5f5f5;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
        }
        
        .cart-item-qty {
            margin: 0 8px;
        }
        
        .remove-item {
            margin-left: 12px;
            color: #e74c3c;
            border: none;
            background: none;
            cursor: pointer;
            font-size: 16px;
        }
        
        .empty-cart {
            text-align: center;
            padding: 20px;
            color: #777;
        }
        
        /* Notification */
        .notification {
            position: fixed;
            bottom: 20px;
            right: 20px;
            background: #4d2a1e;
            color: white;
            padding: 12px 20px;
            border-radius: 4px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            z-index: 1000;
            opacity: 0;
            transform: translateY(20px);
            transition: opacity 0.3s ease, transform 0.3s ease;
        }
        
        .notification.active {
            opacity: 1;
            transform: translateY(0);
        }
    `;

    document.head.appendChild(style);
});