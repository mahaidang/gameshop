package com.n2.gameshop.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.n2.gameshop.R;
import com.n2.gameshop.adapter.CategoryAdminAdapter;
import com.n2.gameshop.model.Category;
import com.n2.gameshop.presenter.AdminPresenter;

import java.util.ArrayList;
import java.util.List;

public class ManageCategoryFragment extends Fragment implements AdminPresenter.CategoryView {

    private ListView lvCategories;
    private TextView tvCategoryCount;
    private MaterialButton btnAddCategory;

    private AdminPresenter presenter;
    private CategoryAdminAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new AdminPresenter(requireContext());

        lvCategories = view.findViewById(R.id.lvCategories);
        tvCategoryCount = view.findViewById(R.id.tvCategoryCount);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);

        categoryAdapter = new CategoryAdminAdapter(requireContext(), new ArrayList<Category>());
        lvCategories.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryActionListener(new CategoryAdminAdapter.OnCategoryActionListener() {
            @Override
            public void onEditCategory(Category category) {
                showCategoryDialog(category, false);
            }

            @Override
            public void onDeleteCategory(Category category) {
                showDeleteConfirmation(category);
            }
        });

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog(null, true);
            }
        });

        presenter.loadCategories(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadCategories(this);
    }

    private void showCategoryDialog(final Category existingCategory, final boolean isNew) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_category, null);

        final TextInputEditText edtName = dialogView.findViewById(R.id.edtDialogCategoryName);
        final TextInputEditText edtDesc = dialogView.findViewById(R.id.edtDialogCategoryDesc);
        final TextInputEditText edtIcon = dialogView.findViewById(R.id.edtDialogCategoryIcon);

        if (!isNew && existingCategory != null) {
            edtName.setText(existingCategory.getName());
            edtDesc.setText(existingCategory.getDescription());
            edtIcon.setText(existingCategory.getIconUrl());
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(isNew ? "Thêm Danh mục" : "Sửa Danh mục")
                .setView(dialogView)
                .setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Category category = isNew ? new Category() : existingCategory;
                        category.setName(getText(edtName));
                        category.setDescription(getText(edtDesc));
                        category.setIconUrl(getText(edtIcon));

                        presenter.saveCategory(ManageCategoryFragment.this, category, isNew);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteConfirmation(final Category category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc chắn muốn xoá danh mục \"" + category.getName() + "\"?\n"
                        + "Các sản phẩm trong danh mục này có thể bị ảnh hưởng.")
                .setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.deleteCategory(ManageCategoryFragment.this, category.getId());
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    // ===== AdminPresenter.CategoryView callbacks =====

    @Override
    public void onCategoriesLoaded(List<Category> categories) {
        categoryAdapter.updateData(categories);
        tvCategoryCount.setText("Tổng: " + categories.size() + " danh mục");
    }

    @Override
    public void onCategorySaved(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        presenter.loadCategories(this);
    }

    @Override
    public void onCategoryDeleted(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        presenter.loadCategories(this);
    }

    @Override
    public void onCategoryError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
