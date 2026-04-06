package com.n2.gameshop.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.n2.gameshop.BuildConfig;
import com.n2.gameshop.R;
import com.n2.gameshop.adapter.AdminProductAdapter;
import com.n2.gameshop.model.Category;
import com.n2.gameshop.model.Product;
import com.n2.gameshop.network.CloudinaryUploader;
import com.n2.gameshop.presenter.AdminPresenter;
import com.n2.gameshop.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ManageProductFragment extends Fragment implements AdminPresenter.ProductView {

    private Spinner spinnerFilterCategory;
    private TextView tvProductCount;
    private MaterialButton btnAddProduct;
    private RecyclerView rvProducts;

    private AdminPresenter presenter;
    private AdminProductAdapter productAdapter;
    private List<Category> allCategories = new ArrayList<Category>();
    private int selectedFilterCategoryId = -1; // -1 = All
    private ActivityResultLauncher<String> imagePickerLauncher;
    private TextInputEditText activeImageInput;
    private TextView activeUploadStatus;
    private ImageView activeImagePreview;
    private MaterialButton activePickButton;
    private volatile boolean isImageUploading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new androidx.activity.result.ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            uploadSelectedImage(uri);
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new AdminPresenter(requireContext());

        spinnerFilterCategory = view.findViewById(R.id.spinnerFilterCategory);
        tvProductCount = view.findViewById(R.id.tvProductCount);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        rvProducts = view.findViewById(R.id.rvProducts);

        productAdapter = new AdminProductAdapter(requireContext(), new ArrayList<Product>());
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvProducts.setAdapter(productAdapter);

        productAdapter.setOnProductActionListener(new AdminProductAdapter.OnProductActionListener() {
            @Override
            public void onEditProduct(Product product) {
                showProductDialog(product, false);
            }

            @Override
            public void onDeleteProduct(Product product) {
                showDeleteConfirmation(product);
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductDialog(null, true);
            }
        });

        // Load categories for filter spinner
        presenter.loadCategoriesForProduct(this);
        presenter.loadProducts(this, -1);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadProducts(this, selectedFilterCategoryId);
    }

    private void setupFilterSpinner() {
        List<String> categoryNames = new ArrayList<String>();
        categoryNames.add("Tất cả danh mục");
        for (Category c : allCategories) {
            categoryNames.add(c.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(), android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterCategory.setAdapter(adapter);

        spinnerFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedFilterCategoryId = -1;
                } else {
                    selectedFilterCategoryId = allCategories.get(position - 1).getId();
                }
                presenter.loadProducts(ManageProductFragment.this, selectedFilterCategoryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no-op
            }
        });
    }

    private void showProductDialog(final Product existingProduct, final boolean isNew) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_product, null);

        final TextInputEditText edtName = dialogView.findViewById(R.id.edtDialogProductName);
        final TextInputEditText edtDesc = dialogView.findViewById(R.id.edtDialogProductDesc);
        final TextInputEditText edtPrice = dialogView.findViewById(R.id.edtDialogProductPrice);
        final TextInputEditText edtImage = dialogView.findViewById(R.id.edtDialogProductImage);
        final MaterialButton btnPickImage = dialogView.findViewById(R.id.btnDialogPickImage);
        final ImageView imgPreview = dialogView.findViewById(R.id.imgDialogProductPreview);
        final TextView tvUploadStatus = dialogView.findViewById(R.id.tvDialogUploadStatus);
        final TextInputEditText edtStock = dialogView.findViewById(R.id.edtDialogProductStock);
        final Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerDialogCategory);
        final Spinner spinnerPlatform = dialogView.findViewById(R.id.spinnerDialogPlatform);
        final CheckBox cbActive = dialogView.findViewById(R.id.cbDialogProductActive);

        activeImageInput = edtImage;
        activeImagePreview = imgPreview;
        activeUploadStatus = tvUploadStatus;
        activePickButton = btnPickImage;
        isImageUploading = false;

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch("image/*");
            }
        });

        // Setup category spinner in dialog
        List<String> catNames = new ArrayList<String>();
        for (Category c : allCategories) {
            catNames.add(c.getName());
        }
        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(
                requireContext(), android.R.layout.simple_spinner_item, catNames);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        // Setup platform spinner in dialog
        ArrayAdapter<String> platAdapter = new ArrayAdapter<String>(
                requireContext(), android.R.layout.simple_spinner_item, Constants.PLATFORMS);
        platAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlatform.setAdapter(platAdapter);

        // Pre-fill if editing
        if (!isNew && existingProduct != null) {
            edtName.setText(existingProduct.getName());
            edtDesc.setText(existingProduct.getDescription());
            edtPrice.setText(String.valueOf((long) existingProduct.getPrice()));
            edtImage.setText(existingProduct.getImageUrl());
            edtStock.setText(String.valueOf(existingProduct.getStock()));
            cbActive.setChecked(existingProduct.isActive());

            if (!TextUtils.isEmpty(existingProduct.getImageUrl())) {
                Glide.with(this)
                        .load(existingProduct.getImageUrl())
                        .placeholder(R.drawable.ic_placeholder_game)
                        .error(R.drawable.ic_placeholder_game)
                        .centerCrop()
                        .into(imgPreview);
                tvUploadStatus.setText("Đang dùng ảnh hiện tại");
            }

            // Select existing category
            for (int i = 0; i < allCategories.size(); i++) {
                if (allCategories.get(i).getId() == existingProduct.getCategoryId()) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }

            // Select existing platform
            for (int i = 0; i < Constants.PLATFORMS.length; i++) {
                if (Constants.PLATFORMS[i].equalsIgnoreCase(existingProduct.getPlatform())) {
                    spinnerPlatform.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(isNew ? "Thêm Sản phẩm" : "Sửa Sản phẩm")
                .setView(dialogView)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isImageUploading) {
                            Toast.makeText(requireContext(), "Đang upload ảnh, vui lòng chờ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Product product = isNew ? new Product() : existingProduct;
                        product.setName(getText(edtName));
                        product.setDescription(getText(edtDesc));
                        product.setImageUrl(getText(edtImage));
                        product.setActive(cbActive.isChecked());

                        // Parse price
                        try {
                            product.setPrice(Double.parseDouble(getText(edtPrice)));
                        } catch (NumberFormatException e) {
                            product.setPrice(0);
                        }

                        // Parse stock
                        try {
                            product.setStock(Integer.parseInt(getText(edtStock)));
                        } catch (NumberFormatException e) {
                            product.setStock(0);
                        }

                        // Set category
                        int catPos = spinnerCategory.getSelectedItemPosition();
                        if (catPos >= 0 && catPos < allCategories.size()) {
                            product.setCategoryId(allCategories.get(catPos).getId());
                        }

                        // Set platform
                        int platPos = spinnerPlatform.getSelectedItemPosition();
                        if (platPos >= 0 && platPos < Constants.PLATFORMS.length) {
                            product.setPlatform(Constants.PLATFORMS[platPos]);
                        }

                        presenter.saveProduct(ManageProductFragment.this, product, isNew);
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                clearActiveUploadViews();
            }
        });

        dialog.show();
    }

    private void uploadSelectedImage(final Uri uri) {
        if (activeImageInput == null || activeUploadStatus == null || activeImagePreview == null) {
            return;
        }
        if (TextUtils.isEmpty(BuildConfig.CLOUDINARY_CLOUD_NAME)
                || TextUtils.isEmpty(BuildConfig.CLOUDINARY_UPLOAD_PRESET)) {
            Toast.makeText(requireContext(),
                    "Thiếu cấu hình Cloudinary trong gradle.properties",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_placeholder_game)
                .error(R.drawable.ic_placeholder_game)
                .centerCrop()
                .into(activeImagePreview);

        isImageUploading = true;
        if (activePickButton != null) {
            activePickButton.setEnabled(false);
        }
        activeUploadStatus.setText("Đang upload ảnh lên Cloudinary...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String uploadedUrl = CloudinaryUploader.uploadImage(
                            requireContext(),
                            uri,
                            BuildConfig.CLOUDINARY_CLOUD_NAME,
                            BuildConfig.CLOUDINARY_UPLOAD_PRESET
                    );

                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isImageUploading = false;
                            if (activePickButton != null) {
                                activePickButton.setEnabled(true);
                            }
                            if (activeImageInput != null) {
                                activeImageInput.setText(uploadedUrl);
                            }
                            if (activeUploadStatus != null) {
                                activeUploadStatus.setText("Upload thành công");
                            }
                        }
                    });
                } catch (final Exception e) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isImageUploading = false;
                            if (activePickButton != null) {
                                activePickButton.setEnabled(true);
                            }
                            if (activeUploadStatus != null) {
                                activeUploadStatus.setText("Upload thất bại");
                            }
                            Toast.makeText(requireContext(), "Lỗi upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void clearActiveUploadViews() {
        activeImageInput = null;
        activeUploadStatus = null;
        activeImagePreview = null;
        activePickButton = null;
        isImageUploading = false;
    }

    private void showDeleteConfirmation(final Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc chắn muốn xoá sản phẩm \"" + product.getName() + "\"?")
                .setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.deleteProduct(ManageProductFragment.this, product.getId());
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    // ===== AdminPresenter.ProductView callbacks =====

    @Override
    public void onProductsLoaded(List<Product> products) {
        productAdapter.updateData(products);
        tvProductCount.setText("Tổng: " + products.size() + " sản phẩm");
    }

    @Override
    public void onCategoriesLoaded(List<Category> categories) {
        this.allCategories = categories != null ? categories : new ArrayList<Category>();
        setupFilterSpinner();
    }

    @Override
    public void onProductSaved(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        presenter.loadProducts(this, selectedFilterCategoryId);
    }

    @Override
    public void onProductDeleted(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        presenter.loadProducts(this, selectedFilterCategoryId);
    }

    @Override
    public void onProductError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
