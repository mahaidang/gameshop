package com.n2.gameshop.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.n2.gameshop.database.CategoryDAO;
import com.n2.gameshop.database.OrderDAO;
import com.n2.gameshop.database.ProductDAO;
import com.n2.gameshop.database.UserDAO;
import com.n2.gameshop.model.Category;
import com.n2.gameshop.model.Order;
import com.n2.gameshop.model.OrderDetail;
import com.n2.gameshop.model.Product;
import com.n2.gameshop.model.User;
import com.n2.gameshop.utils.Constants;
import com.n2.gameshop.utils.DateUtils;
import com.n2.gameshop.utils.Validator;

import java.util.ArrayList;
import java.util.List;

public class AdminPresenter {

    // ===================== USER MANAGEMENT =====================
    public interface UserView {
        void onUsersLoaded(List<User> users);
        void onUserSaved(String message);
        void onUserDeleted(String message);
        void onUserError(String message);
    }

    // ===================== CATEGORY MANAGEMENT =====================
    public interface CategoryView {
        void onCategoriesLoaded(List<Category> categories);
        void onCategorySaved(String message);
        void onCategoryDeleted(String message);
        void onCategoryError(String message);
    }

    // ===================== PRODUCT MANAGEMENT =====================
    public interface ProductView {
        void onProductsLoaded(List<Product> products);
        void onCategoriesLoaded(List<Category> categories);
        void onProductSaved(String message);
        void onProductDeleted(String message);
        void onProductError(String message);
    }

    // ===================== ORDER MANAGEMENT =====================
    public interface OrderView {
        void onOrdersLoaded(List<Order> orders, double totalRevenue);
        void onOrderDetailsLoaded(Order order, List<OrderDetail> details);
        void onOrderStatusUpdated(String message);
        void onOrderError(String message);
    }

    private final UserDAO userDAO;
    private final CategoryDAO categoryDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;
    private final Handler mainHandler;

    public AdminPresenter(Context context) {
        Context appContext = context.getApplicationContext();
        this.userDAO = new UserDAO(appContext);
        this.categoryDAO = new CategoryDAO(appContext);
        this.productDAO = new ProductDAO(appContext);
        this.orderDAO = new OrderDAO(appContext);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // ==================== USER OPERATIONS ====================

    public void loadUsers(final UserView view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<User> all = userDAO.getAllUsers();
                final List<User> nonAdmin = new ArrayList<>();
                for (User u : all) {
                    if (!Constants.ROLE_ADMIN.equalsIgnoreCase(u.getRole())) {
                        nonAdmin.add(u);
                    }
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.onUsersLoaded(nonAdmin);
                    }
                });
            }
        }).start();
    }

    public void saveUser(final UserView view, final User user, final boolean isNew) {
        // Validate
        if (!Validator.isNotEmpty(user.getUsername())) {
            view.onUserError("Username không được để trống");
            return;
        }
        if (!Validator.isValidEmail(user.getEmail())) {
            view.onUserError("Email không hợp lệ");
            return;
        }
        if (isNew && !Validator.isValidPassword(user.getPassword())) {
            view.onUserError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final long result;
                if (isNew) {
                    // Check duplicates
                    User existing = userDAO.findByUsername(user.getUsername());
                    if (existing != null) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                view.onUserError("Username đã tồn tại");
                            }
                        });
                        return;
                    }
                    existing = userDAO.findByEmail(user.getEmail());
                    if (existing != null) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                view.onUserError("Email đã tồn tại");
                            }
                        });
                        return;
                    }
                    result = userDAO.insert(user);
                } else {
                    result = userDAO.updateUser(user);
                }

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result > 0) {
                            view.onUserSaved(isNew ? "Thêm user thành công" : "Cập nhật user thành công");
                        } else {
                            view.onUserError("Thao tác thất bại");
                        }
                    }
                });
            }
        }).start();
    }

    public void deleteUser(final UserView view, final int userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int result = userDAO.deleteUser(userId);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result > 0) {
                            view.onUserDeleted("Xoá user thành công");
                        } else {
                            view.onUserError("Xoá user thất bại");
                        }
                    }
                });
            }
        }).start();
    }

    // ==================== CATEGORY OPERATIONS ====================

    public void loadCategories(final CategoryView view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Category> categories = categoryDAO.getAllCategories();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.onCategoriesLoaded(categories);
                    }
                });
            }
        }).start();
    }

    public void saveCategory(final CategoryView view, final Category category, final boolean isNew) {
        if (!Validator.isNotEmpty(category.getName())) {
            view.onCategoryError("Tên danh mục không được để trống");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final long result;
                if (isNew) {
                    result = categoryDAO.insertCategory(category);
                } else {
                    result = categoryDAO.updateCategory(category);
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result > 0) {
                            view.onCategorySaved(isNew ? "Thêm danh mục thành công" : "Cập nhật danh mục thành công");
                        } else {
                            view.onCategoryError("Thao tác thất bại (tên có thể bị trùng)");
                        }
                    }
                });
            }
        }).start();
    }

    public void deleteCategory(final CategoryView view, final int categoryId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int result = categoryDAO.deleteCategory(categoryId);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result > 0) {
                            view.onCategoryDeleted("Xoá danh mục thành công");
                        } else {
                            view.onCategoryError("Xoá thất bại (danh mục có thể đang chứa sản phẩm)");
                        }
                    }
                });
            }
        }).start();
    }

    // ==================== PRODUCT OPERATIONS ====================

    public void loadProducts(final ProductView view, final int categoryId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Product> products;
                if (categoryId <= 0) {
                    products = productDAO.getAllProductsAdmin();
                } else {
                    products = productDAO.getProductsByCategoryAdmin(categoryId);
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.onProductsLoaded(products);
                    }
                });
            }
        }).start();
    }

    public void loadCategoriesForProduct(final ProductView view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Category> categories = categoryDAO.getAllCategories();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.onCategoriesLoaded(categories);
                    }
                });
            }
        }).start();
    }

    public void saveProduct(final ProductView view, final Product product, final boolean isNew) {
        if (!Validator.isNotEmpty(product.getName())) {
            view.onProductError("Tên sản phẩm không được để trống");
            return;
        }
        if (product.getPrice() < 0) {
            view.onProductError("Giá phải >= 0");
            return;
        }
        if (product.getCategoryId() <= 0) {
            view.onProductError("Vui lòng chọn danh mục");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final long result;
                if (isNew) {
                    result = productDAO.insertProduct(product);
                } else {
                    result = productDAO.updateProduct(product);
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result > 0) {
                            view.onProductSaved(isNew ? "Thêm sản phẩm thành công" : "Cập nhật sản phẩm thành công");
                        } else {
                            view.onProductError("Thao tác thất bại");
                        }
                    }
                });
            }
        }).start();
    }

    public void deleteProduct(final ProductView view, final int productId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int result = productDAO.deleteProduct(productId);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result > 0) {
                            view.onProductDeleted("Xoá sản phẩm thành công");
                        } else {
                            view.onProductError("Xoá thất bại");
                        }
                    }
                });
            }
        }).start();
    }

    // ==================== ORDER OPERATIONS ====================

    public void loadOrders(final OrderView view, final int filterType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Order> orders;
                String today = DateUtils.today();

                switch (filterType) {
                    case Constants.FILTER_TODAY:
                        orders = orderDAO.getOrdersByDateRange(today, today);
                        break;
                    case Constants.FILTER_THIS_WEEK:
                        orders = orderDAO.getOrdersByDateRange(DateUtils.startOfWeek(), today);
                        break;
                    case Constants.FILTER_THIS_MONTH:
                        orders = orderDAO.getOrdersByDateRange(DateUtils.startOfMonth(), today);
                        break;
                    case Constants.FILTER_THIS_YEAR:
                        orders = orderDAO.getOrdersByDateRange(DateUtils.startOfYear(), today);
                        break;
                    default:
                        orders = orderDAO.getAllOrders();
                        break;
                }

                double totalRevenue = 0;
                for (Order o : orders) {
                    totalRevenue += o.getTotalAmount();
                }

                final double revenue = totalRevenue;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.onOrdersLoaded(orders, revenue);
                    }
                });
            }
        }).start();
    }

    public void loadOrderDetails(final OrderView view, final Order order) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<OrderDetail> details = orderDAO.getOrderDetails(order.getId());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.onOrderDetailsLoaded(order, details);
                    }
                });
            }
        }).start();
    }

    public void updateOrderStatus(final OrderView view, final int orderId, final String newStatus) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int result = orderDAO.updateOrderStatus(orderId, newStatus);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result > 0) {
                            view.onOrderStatusUpdated("Cập nhật trạng thái thành công");
                        } else {
                            view.onOrderError("Cập nhật thất bại");
                        }
                    }
                });
            }
        }).start();
    }
}

