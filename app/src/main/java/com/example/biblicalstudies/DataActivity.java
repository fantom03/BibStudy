package com.example.biblicalstudies;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class DataActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private ImageButton actionButton;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.home_tabLayout);
        drawerLayout = findViewById(R.id.drawer);
        actionButton = findViewById(R.id.nav_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setTitle("CLASSES & LESSONS");
        tabLayout = findViewById(R.id.home_tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("AUDIO"));
        tabLayout.addTab(tabLayout.newTab().setText("DOCS"));

        viewPager = findViewById(R.id.home_view_pager);

        tabLayout.setupWithViewPager(viewPager);

        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new AudioFragment(), "AUDIO");
        pageAdapter.addFragment(new DocFragment(), "DOCS");
        viewPager.setAdapter(pageAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.drawer_navigate:
                Intent i = new Intent(this, HomeDefault.class);
                startActivity(i);
                this.finishAffinity();
                break;
            default:
                return true;
        }
        return true;
    }

    class PageAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> frags = new ArrayList<>();
        private ArrayList<String> title = new ArrayList<>();

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return frags.get(position);
        }


        @Override
        public int getCount() {
            return frags.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return title.get(position);
        }

        public void addFragment(Fragment fm, String title){
            frags.add(fm);
            this.title.add(title);
        }
    }
}
