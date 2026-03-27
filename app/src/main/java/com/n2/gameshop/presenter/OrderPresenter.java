package com.n2.gameshop.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.n2.gameshop.database.OrderDAO;
import com.n2.gameshop.database.ProductDAO;
import com.n2.gameshop.model.CartItem;
import com.n2.gameshop.model.Order;
import com.n2.gameshop.model.OrderDetail;
import com.n2.gameshop.model.Product;
import com.n2.gameshop.utils.CartManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderPresenter {

    public interface View {
        void onOrderPlaced(int orderId);
        void onOrdersLoaded(List<Order> orders);
        void onOrderDetailsLoaded(List<OrderDetail> details);
        void onCartLoaded(List<CartItem> cartItems, double total);
        void onError(String message);
    }

    private final View view;
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final CartManager cartManager;
    private final Handler mainHandler;

    public OrderPresenter(Context context, View view) {
        Context appContext = context.getApplicationContext();
        this.view = view;
        this.orderDAO = new OrderDAO(appContext);
        this.productDAO = new ProductDAO(appContext);
        this.cartManager = new CartManager(appContext);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void loadCart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<Integer, Integer> items = cartManager.getAllCartItems();
                    final List<CartItem> cartItems = new ArrayList<>();
                    double total = 0;
                    for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                        Product product = productDAO.getProductById(entry.getKey());
                        if (product != null) {
                            CartItem ci = new CartItem(product, entry.getValue());
                            cartItems.add(ci);
                            total += ci.getSubtotal();
                        }
                    }
                    final double finalTotal = total;
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onCartLoaded(cartItems, finalTotal);
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onError("Lỗi tải giỏ hàng: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void placeOrder(final int userId, final List<CartItem> cartItems,
                           final String paymentMethod, final String note) {
        if (cartItems == null || cartItems.isEmpty()) {
            view.onError("Giỏ hàng trống");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    double totalAmount = 0;
                    for (CartItem ci : cartItems) {
                        totalAmount += ci.getSubtotal();
                    }

                    String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date());

                    Order order = new Order();
                    order.setUserId(userId);
                    order.setTotalAmount(totalAmount);
                    order.setPaymentMethod(paymentMethod);
                    order.setStatus("pending");
                    order.setNote(note);
                    order.setCreatedAt(createdAt);

                    final long orderId = orderDAO.insertOrder(order);
                    if (orderId <= 0) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                view.onError("Đặt hàng thất bại");
                            }
                        });
                        return;
                    }

                    for (CartItem ci : cartItems) {
                        OrderDetail detail = new OrderDetail();
                        detail.setOrderId((int) orderId);
                        detail.setProductId(ci.getProduct().getId());
                        detail.setQuantity(ci.getQuantity());
                        detail.setUnitPrice(ci.getProduct().getPrice());
                        orderDAO.insertOrderDetail(detail);

                        // Decrease stock
                        orderDAO.decreaseStock(ci.getProduct().getId(), ci.getQuantity());
                    }

                    // Clear cart
                    cartManager.clearCart();

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onOrderPlaced((int) orderId);
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onError("Lỗi đặt hàng: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void loadOrdersByUser(final int userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Order> orders = orderDAO.getOrdersByUser(userId);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onOrdersLoaded(orders);
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onError("Lỗi tải đơn hàng: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void loadOrderDetails(final int orderId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<OrderDetail> details = orderDAO.getOrderDetails(orderId);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onOrderDetailsLoaded(details);
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onError("Lỗi tải chi tiết đơn hàng: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void updateQuantity(int productId, int newQuantity) {
        cartManager.setQuantity(productId, newQuantity);
    }

    public void removeFromCart(int productId) {
        cartManager.removeFromCart(productId);
    }
}

