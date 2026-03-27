package com.n2.gameshop.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.n2.gameshop.database.ReviewDAO;
import com.n2.gameshop.model.Review;
import com.n2.gameshop.utils.Validator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewPresenter {

    public interface View {
        void onReviewsLoaded(List<Review> reviews);
        void onReviewSubmitted();
        void onError(String message);
    }

    private final View view;
    private final ReviewDAO reviewDAO;
    private final Handler mainHandler;

    public ReviewPresenter(Context context, View view) {
        Context appContext = context.getApplicationContext();
        this.view = view;
        this.reviewDAO = new ReviewDAO(appContext);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void loadReviews(final int productId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Review> reviews = reviewDAO.getReviewsByProduct(productId);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onReviewsLoaded(reviews);
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onError("Lỗi tải đánh giá: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void submitReview(final int userId, final int productId, final int rating, final String comment) {
        if (rating < 1 || rating > 5) {
            view.onError("Vui lòng chọn đánh giá từ 1 đến 5 sao");
            return;
        }
        if (!Validator.isNotEmpty(comment)) {
            view.onError("Vui lòng nhập bình luận");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean alreadyReviewed = reviewDAO.hasUserReviewed(userId, productId);
                    if (alreadyReviewed) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                view.onError("Bạn đã đánh giá sản phẩm này rồi");
                            }
                        });
                        return;
                    }

                    String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date());

                    Review review = new Review();
                    review.setUserId(userId);
                    review.setProductId(productId);
                    review.setRating(rating);
                    review.setComment(comment.trim());
                    review.setCreatedAt(createdAt);

                    final long id = reviewDAO.insertReview(review);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (id > 0) {
                                view.onReviewSubmitted();
                            } else {
                                view.onError("Gửi đánh giá thất bại");
                            }
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.onError("Lỗi: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void checkCanReview(final int userId, final int productId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean alreadyReviewed = reviewDAO.hasUserReviewed(userId, productId);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (alreadyReviewed) {
                            view.onError("ALREADY_REVIEWED");
                        }
                    }
                });
            }
        }).start();
    }

    public void getAverageRating(final int productId, final OnAverageRatingCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final float avg = reviewDAO.getAverageRating(productId);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(avg);
                    }
                });
            }
        }).start();
    }

    public interface OnAverageRatingCallback {
        void onResult(float averageRating);
    }
}

