package com.n2.gameshop.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.n2.gameshop.R;
import com.n2.gameshop.adapter.CategoryAdapter;
import com.n2.gameshop.adapter.ProductAdapter;
import com.n2.gameshop.model.Category;
import com.n2.gameshop.model.Product;
import com.n2.gameshop.presenter.ProductPresenter;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment implements ProductPresenter.View {

    private SearchView searchViewProduct;
    private RecyclerView rvCategories;
    private RecyclerView rvProducts;
    private Spinner spinnerPlatform;
    private TextView tvEmptyProduct;

    private ProductPresenter presenter;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;

    private List<Category> allCategories = new ArrayList<Category>();
    private List<Product> currentProducts = new ArrayList<Product>();
    private int selectedCategoryId = -1; // -1 = All
    private String selectedPlatform = "All";
    private String currentSearchKeyword = "";

    private static final String[] PLATFORMS = {"All", "PC", "Console", "Mobile"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new ProductPresenter(requireContext(), this);
        initViews(view);
        setupCategoryRecyclerView();
        setupProductRecyclerView();
        setupPlatformSpinner();
        setupSearchView();

        presenter.loadCategories();
        presenter.loadAllProducts();
    }

    private void initViews(View view) {
        searchViewProduct = view.findViewById(R.id.searchViewProduct);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvProducts = view.findViewById(R.id.rvProducts);
        spinnerPlatform = view.findViewById(R.id.spinnerPlatform);
        tvEmptyProduct = view.findViewById(R.id.tvEmptyProduct);
    }

    private void setupCategoryRecyclerView() {
        categoryAdapter = new CategoryAdapter(requireContext(), new ArrayList<Category>());
        rvCategories.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category, int position) {
                if (position == 0) {
                    selectedCategoryId = -1;
                    loadProducts();
                } else {
                    selectedCategoryId = category.getId();
                    loadProducts();
                }
            }
        });
    }

    private void setupProductRecyclerView() {
        productAdapter = new ProductAdapter(requireContext(), new ArrayList<Product>());
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(productAdapter);

        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }
        });
    }

    private void setupPlatformSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                PLATFORMS
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlatform.setAdapter(spinnerAdapter);

        spinnerPlatform.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPlatform = PLATFORMS[position];
                applyPlatformFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no-op
            }
        });
    }

    private void setupSearchView() {
        searchViewProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchKeyword = query;
                loadProducts();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchKeyword = newText;
                if (newText.isEmpty()) {
                    loadProducts();
                } else {
                    presenter.searchProducts(newText);
                }
                return true;
            }
        });
    }

    private void loadProducts() {
        if (!currentSearchKeyword.isEmpty()) {
            presenter.searchProducts(currentSearchKeyword);
        } else if (selectedCategoryId > 0) {
            presenter.loadProductsByCategory(selectedCategoryId);
        } else {
            presenter.loadAllProducts();
        }
    }

    private void applyPlatformFilter() {
        if ("All".equals(selectedPlatform)) {
            productAdapter.updateData(currentProducts);
        } else {
            List<Product> filtered = new ArrayList<Product>();
            for (Product p : currentProducts) {
                if (selectedPlatform.equalsIgnoreCase(p.getPlatform())) {
                    filtered.add(p);
                }
            }
            productAdapter.updateData(filtered);
        }
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (productAdapter.getItemCount() == 0) {
            tvEmptyProduct.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            tvEmptyProduct.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }
    }

    // ===== MVP View callbacks =====

    @Override
    public void onProductsLoaded(List<Product> products) {
        this.currentProducts = products != null ? products : new ArrayList<Product>();
        applyPlatformFilter();
    }

    @Override
    public void onCategoriesLoaded(List<Category> categories) {
        this.allCategories = categories != null ? categories : new ArrayList<Category>();

        List<Category> withAll = new ArrayList<Category>();
        Category allCategory = new Category(0, "Tất cả", "Tất cả danh mục", "");
        withAll.add(allCategory);
        withAll.addAll(this.allCategories);

        categoryAdapter.updateData(withAll);
    }

    @Override
    public void onError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}

