package com.n2.gameshop.ui.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.n2.gameshop.R;

import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment productListFragment = new ProductListFragment();
    private final Fragment cartFragment = new CartFragment();
    private final Fragment orderHistoryFragment = new OrderHistoryFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Add all fragments but only show home initially
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, profileFragment, "profile").hide(profileFragment)
                .add(R.id.fragmentContainer, orderHistoryFragment, "orders").hide(orderHistoryFragment)
                .add(R.id.fragmentContainer, cartFragment, "cart").hide(cartFragment)
                .add(R.id.fragmentContainer, productListFragment, "products").hide(productListFragment)
                .add(R.id.fragmentContainer, homeFragment, "home")
                .commit();

        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selected = null;
                        int id = item.getItemId();
                        if (id == R.id.nav_home) {
                            selected = homeFragment;
                        } else if (id == R.id.nav_products) {
                            selected = productListFragment;
                        } else if (id == R.id.nav_cart) {
                            selected = cartFragment;
                        } else if (id == R.id.nav_orders) {
                            selected = orderHistoryFragment;
                        } else if (id == R.id.nav_profile) {
                            selected = profileFragment;
                        }

                        if (selected != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(activeFragment)
                                    .show(selected)
                                    .commit();
                            activeFragment = selected;
                            return true;
                        }
                        return false;
                    }
                });
    }
}
