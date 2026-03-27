package com.n2.gameshop.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.n2.gameshop.R;
import com.n2.gameshop.model.Review;
import com.n2.gameshop.model.User;
import com.n2.gameshop.preferences.SessionManager;
import com.n2.gameshop.presenter.ReviewPresenter;

import java.util.List;

public class ReviewActivity extends AppCompatActivity implements ReviewPresenter.View {

    private TextView tvReviewProductName;
    private RatingBar ratingBarSubmit;
    private TextInputEditText edtReviewComment;
    private MaterialButton btnSubmitReview;
    private MaterialButton btnCancelReview;

    private ReviewPresenter reviewPresenter;
    private SessionManager sessionManager;

    private int productId = -1;
    private String productName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        productId = getIntent().getIntExtra("product_id", -1);
        productName = getIntent().getStringExtra("product_name");
        if (productName == null) productName = "";

        if (productId <= 0) {
            Toast.makeText(this, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        reviewPresenter = new ReviewPresenter(this, this);
        sessionManager = new SessionManager(this);

        User user = sessionManager.getLoggedInUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupActions(user);
    }

    private void initViews() {
        tvReviewProductName = findViewById(R.id.tvReviewProductName);
        ratingBarSubmit = findViewById(R.id.ratingBarSubmit);
        edtReviewComment = findViewById(R.id.edtReviewComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        btnCancelReview = findViewById(R.id.btnCancelReview);

        tvReviewProductName.setText(productName);
    }

    private void setupActions(final User user) {
        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rating = (int) ratingBarSubmit.getRating();
                String comment = edtReviewComment.getText() != null
                        ? edtReviewComment.getText().toString() : "";
                reviewPresenter.submitReview(user.getId(), productId, rating, comment);
            }
        });

        btnCancelReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // ===== ReviewPresenter.View callbacks =====

    @Override
    public void onReviewsLoaded(List<Review> reviews) {
        // not used in this activity
    }

    @Override
    public void onReviewSubmitted() {
        Toast.makeText(this, "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

