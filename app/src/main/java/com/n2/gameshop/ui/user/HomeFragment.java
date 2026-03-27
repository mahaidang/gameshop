package com.n2.gameshop.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.n2.gameshop.R;
import com.n2.gameshop.adapter.ProductAdapter;
import com.n2.gameshop.model.Category;
import com.n2.gameshop.model.Product;
import com.n2.gameshop.preferences.SessionManager;
import com.n2.gameshop.presenter.ProductPresenter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ProductPresenter.View {

    private TextView tvHomeWelcome, tvHomeEmpty;
    private RecyclerView rvHomeFeatured;
    private ProductPresenter presenter;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new ProductPresenter(requireContext(), this);

        tvHomeWelcome = view.findViewById(R.id.tvHomeWelcome);
        tvHomeEmpty = view.findViewById(R.id.tvHomeEmpty);
        rvHomeFeatured = view.findViewById(R.id.rvHomeFeatured);

        SessionManager session = new SessionManager(requireContext());
        if (session.isLoggedIn()) {
            tvHomeWelcome.setText("Xin chào, " + session.getLoggedInUser().getUsername() + "!");
        }

        productAdapter = new ProductAdapter(requireContext(), new ArrayList<Product>());
        rvHomeFeatured.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvHomeFeatured.setNestedScrollingEnabled(false);
        rvHomeFeatured.setAdapter(productAdapter);

        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }
        });

        presenter.loadAllProducts();
    }

    @Override
    public void onProductsLoaded(List<Product> products) {
        if (products != null && !products.isEmpty()) {
            productAdapter.updateData(products);
            rvHomeFeatured.setVisibility(View.VISIBLE);
            tvHomeEmpty.setVisibility(View.GONE);
        } else {
            rvHomeFeatured.setVisibility(View.GONE);
            tvHomeEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCategoriesLoaded(List<Category> categories) { /* not used */ }

    @Override
    public void onError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}

