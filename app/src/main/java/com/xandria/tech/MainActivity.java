package com.xandria.tech;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.razorpay.PaymentResultListener;
import com.xandria.tech.activity.user.LoginActivity;
import com.xandria.tech.constants.Categories;
import com.xandria.tech.constants.LoggedInUser;
import com.xandria.tech.fragment.DiscussionFragment;
import com.xandria.tech.fragment.MainFragment;
import com.xandria.tech.fragment.MapsFragment;
import com.xandria.tech.fragment.MyLoanedBooksFragment;
import com.xandria.tech.fragment.OrdersFragment;
import com.xandria.tech.fragment.ProfileFragment;
import com.xandria.tech.fragment.RequestFragment;
import com.xandria.tech.fragment.ReturnedOrdersFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, PaymentResultListener {

    View headerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup the custom tool bar as the app bar so that we can add our navigation menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);
        setUpNavigationDrawer(toolbar);

        // Set MainFragment as the default fragment
        Fragment mainFragment = new MainFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame, mainFragment);
        fragmentTransaction.commit();

        Categories.initCategories();
    }

    void setUpNavigationDrawer(Toolbar toolbar){
        //setting up the navigation drawer
        DrawerLayout drawerLayout = findViewById(R.id.main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //make the drawer respond to clicks
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);

        // Populate user's name and points balance
        TextView nameView = headerView.findViewById(R.id.nav_display_name);
        TextView pointsView = headerView.findViewById(R.id.nav_display_points);

        if (LoggedInUser.getInstance().getCurrentUser() != null){
            nameView.setText(HtmlCompat.fromHtml(
                    getString(R.string.name).concat(" ").concat(LoggedInUser.getInstance().getCurrentUser().getName()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
            pointsView.setText(HtmlCompat.fromHtml(
                    getString(R.string.points).concat(" ").concat(
                            String.valueOf(LoggedInUser.getInstance().getCurrentUser().getPoints())
                    ),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        } else {
            nameView.setVisibility(View.GONE);
            pointsView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;

        switch (id){
            case R.id.nav_orders:
                fragment = new OrdersFragment();
                break;
            case R.id.nav_requests:
                fragment = new RequestFragment();
                break;
            case R.id.nav_discussion:
                fragment = new DiscussionFragment();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, LoginActivity.class);
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                break;
            case R.id.nav_my_ordered_books:
                fragment = new MyLoanedBooksFragment();
                break;
            case R.id.nav_returned_orders:
                fragment = new ReturnedOrdersFragment();
                break;
            case R.id.nav_maps_view:
                fragment = new MapsFragment();
                break;
            default:
                fragment = new MainFragment();
                break;
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        }
        else {
            startActivity(intent);
            finish();
        }
        DrawerLayout drawerLayout = findViewById(R.id.main);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.main);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else if (getFragmentManager().getBackStackEntryCount() != 0) getFragmentManager().popBackStack();
        else super.onBackPressed();
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(MainActivity.this, "Payment Success", Toast.LENGTH_LONG).show();

        FragmentManager fm =  getSupportFragmentManager();
        ProfileFragment profileFragment = (ProfileFragment) fm.findFragmentById(R.id.content_frame);
        if (profileFragment != null) profileFragment.handlePaymentComplete(true);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_LONG).show();

        System.out.println(i + " " + s);
        FragmentManager fm =  getSupportFragmentManager();
        ProfileFragment profileFragment = (ProfileFragment) fm.findFragmentById(R.id.content_frame);
        if (profileFragment != null) profileFragment.handlePaymentComplete(false);
    }
}