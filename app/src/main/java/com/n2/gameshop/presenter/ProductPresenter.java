package com.n2.gameshop.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.n2.gameshop.database.CategoryDAO;
import com.n2.gameshop.database.ProductDAO;
import com.n2.gameshop.model.Category;
import com.n2.gameshop.model.Product;

import java.util.List;

public class ProductPresenter {

    public interface View {
        void onProductsLoaded(List<Product> products);
        void onCategoriesLoaded(List<Category> categories);
        void onError(String message);
    }

    private final View view;
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    private final Handler mainHandler;

    public ProductPresenter(Context context, View view) {
        Context appContext = context.getApplicationContext();
        this.view = view;
        this.productDAO = new ProductDAO(appContext);
        this.categoryDAO = new CategoryDAO(appContext);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void loadAllProducts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Product> products = productDAO.getAllProducts();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onProductsLoaded(products);
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onError("Lỗi tải sản phẩm: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void loadProductsByCategory(final int categoryId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Product> products = productDAO.getProductsByCategory(categoryId);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onProductsLoaded(products);
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onError("Lỗi tải sản phẩm: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void searchProducts(final String keyword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Product> products = productDAO.searchProducts(keyword);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onProductsLoaded(products);
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onError("Lỗi tìm kiếm: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void loadCategories() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Category> categories = categoryDAO.getAllCategories();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onCategoriesLoaded(categories);
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onError("Lỗi tải danh mục: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }
}

