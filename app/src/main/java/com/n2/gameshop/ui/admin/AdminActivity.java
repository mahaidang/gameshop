package com.n2.gameshop.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.n2.gameshop.R;
import com.n2.gameshop.preferences.SessionManager;
import com.n2.gameshop.ui.auth.LoginActivity;

public class AdminActivity extends AppCompatActivity {

	private static final String[] TAB_TITLES = {"Users", "Danh mục", "Sản phẩm", "Đơn hàng"};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		Toolbar toolbar = findViewById(R.id.toolbarAdmin);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("Admin Dashboard");
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logout();
			}
		});

		TabLayout tabLayout = findViewById(R.id.tabLayoutAdmin);
		ViewPager2 viewPager = findViewById(R.id.viewPagerAdmin);

		viewPager.setAdapter(new FragmentStateAdapter(this) {
			@NonNull
			@Override
			public Fragment createFragment(int position) {
				switch (position) {
					case 0:
						return new ManageUserFragment();
					case 1:
						return new ManageCategoryFragment();
					case 2:
						return new ManageProductFragment();
					case 3:
						return new ManageOrderFragment();
					default:
						return new ManageUserFragment();
				}
			}

			@Override
			public int getItemCount() {
				return TAB_TITLES.length;
			}
		});

		new TabLayoutMediator(tabLayout, viewPager,
				new TabLayoutMediator.TabConfigurationStrategy() {
					@Override
					public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
						tab.setText(TAB_TITLES[position]);
					}
				}).attach();
	}

	private void logout() {
		new SessionManager(this).clearSession();
		Intent intent = new Intent(this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}
}
