package com.n2.gameshop.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.n2.gameshop.R;
import com.n2.gameshop.adapter.ReviewAdapter;
import com.n2.gameshop.database.ProductDAO;
import com.n2.gameshop.model.Product;
import com.n2.gameshop.model.Review;
import com.n2.gameshop.model.User;
import com.n2.gameshop.preferences.SessionManager;
import com.n2.gameshop.presenter.ReviewPresenter;
import com.n2.gameshop.utils.CartManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity implements ReviewPresenter.View {

    private static final int REQUEST_CODE_REVIEW = 100;

    private ImageView imgProductDetail;
    private TextView tvDetailName, tvDetailPrice, tvDetailPlatform;
    private TextView tvDetailStock, tvDetailDescription;
    private RatingBar ratingBarAverage;
    private TextView tvAverageRating, tvReviewsHeader, tvNoReviews;
    private MaterialButton btnBack, btnAddToCart, btnBuyNow, btnWriteReview;
    private RecyclerView rvReviews;

    private ReviewPresenter reviewPresenter;
    private ReviewAdapter reviewAdapter;
    private CartManager cartManager;
    private SessionManager sessionManager;

    private int productId = -1;
    private Product currentProduct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack();
            }
        });

        productId = getIntent().getIntExtra("product_id", -1);
        if (productId <= 0) {
            Toast.makeText(this, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        reviewPresenter = new ReviewPresenter(this, this);
        cartManager = new CartManager(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupReviewsList();
        setupActions();
        loadProductDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (productId > 0) {
            reviewPresenter.loadReviews(productId);
            loadAverageRating();
            checkReviewEligibility();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgProductDetail = findViewById(R.id.imgProductDetail);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailPlatform = findViewById(R.id.tvDetailPlatform);
        tvDetailStock = findViewById(R.id.tvDetailStock);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        ratingBarAverage = findViewById(R.id.ratingBarAverage);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvReviewsHeader = findViewById(R.id.tvReviewsHeader);
        tvNoReviews = findViewById(R.id.tvNoReviews);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnWriteReview = findViewById(R.id.btnWriteReview);
        rvReviews = findViewById(R.id.rvReviews);
    }

    private void setupReviewsList() {
        reviewAdapter = new ReviewAdapter(this, new ArrayList<Review>());
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setNestedScrollingEnabled(false);
        rvReviews.setAdapter(reviewAdapter);
    }

    private void setupActions() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentProduct == null) return;
                if (currentProduct.getStock() <= 0) {
                    Toast.makeText(ProductDetailActivity.this, "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                    return;
                }
                cartManager.addToCart(currentProduct.getId(), 1);
                Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });

        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentProduct == null) return;
                if (currentProduct.getStock() <= 0) {
                    Toast.makeText(ProductDetailActivity.this, "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                    return;
                }
                cartManager.addToCart(currentProduct.getId(), 1);
                startActivity(new Intent(ProductDetailActivity.this, CheckoutActivity.class));
            }
        });

        btnWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, ReviewActivity.class);
                intent.putExtra("product_id", productId);
                if (currentProduct != null) {
                    intent.putExtra("product_name", currentProduct.getName());
                }
                startActivityForResult(intent, REQUEST_CODE_REVIEW);
            }
        });
    }

    private void loadProductDetail() {
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                ProductDAO productDAO = new ProductDAO(getApplicationContext());
                final Product product = productDAO.getProductById(productId);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (product == null) {
                            Toast.makeText(ProductDetailActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                        currentProduct = product;
                        displayProduct(product);
                    }
                });
            }
        }).start();
    }

    private void displayProduct(Product product) {
        tvDetailName.setText(product.getName());

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvDetailPrice.setText(nf.format(product.getPrice()) + " ₫");

        tvDetailPlatform.setText(product.getPlatform());
        tvDetailStock.setText("Còn: " + product.getStock());
        tvDetailDescription.setText(product.getDescription());

        if (product.getStock() <= 0) {
            btnAddToCart.setEnabled(false);
            btnAddToCart.setText("Hết hàng");
            btnBuyNow.setEnabled(false);
            btnBuyNow.setText("Hết hàng");
        }

        String imageUrl = product.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder_game)
                    .error(R.drawable.ic_placeholder_game)
                    .centerCrop()
                    .into(imgProductDetail);
        } else {
            imgProductDetail.setImageResource(R.drawable.ic_placeholder_game);
        }
    }

    private void loadAverageRating() {
        reviewPresenter.getAverageRating(productId, new ReviewPresenter.OnAverageRatingCallback() {
            @Override
            public void onResult(float averageRating) {
                ratingBarAverage.setRating(averageRating);
                tvAverageRating.setText(String.format(Locale.getDefault(), "%.1f", averageRating));
            }
        });
    }

    private void checkReviewEligibility() {
        User user = sessionManager.getLoggedInUser();
        if (user == null || "admin".equals(user.getRole())) {
            btnWriteReview.setVisibility(View.GONE);
            return;
        }

        // Show "Viết đánh giá" — presenter will hide it if already reviewed
        btnWriteReview.setVisibility(View.VISIBLE);
        reviewPresenter.checkCanReview(user.getId(), productId);
    }

    private void navigateBack() {
        if (isTaskRoot()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        finish();
    }

    // ===== ReviewPresenter.View callbacks =====

    @Override
    public void onReviewsLoaded(List<Review> reviews) {
        reviewAdapter.updateData(reviews);
        int count = reviews != null ? reviews.size() : 0;
        tvReviewsHeader.setText("Đánh giá (" + count + ")");

        if (count == 0) {
            tvNoReviews.setVisibility(View.VISIBLE);
            rvReviews.setVisibility(View.GONE);
        } else {
            tvNoReviews.setVisibility(View.GONE);
            rvReviews.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onReviewSubmitted() {
        Toast.makeText(this, "Đánh giá đã được gửi", Toast.LENGTH_SHORT).show();
        reviewPresenter.loadReviews(productId);
        loadAverageRating();
        btnWriteReview.setVisibility(View.GONE);
    }

    @Override
    public void onError(String message) {
        if ("ALREADY_REVIEWED".equals(message)) {
            btnWriteReview.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REVIEW && resultCode == RESULT_OK) {
            reviewPresenter.loadReviews(productId);
            loadAverageRating();
            btnWriteReview.setVisibility(View.GONE);
        }
    }
}
