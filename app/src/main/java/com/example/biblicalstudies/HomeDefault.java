package com.example.biblicalstudies;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class HomeDefault extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private ImageButton actionButton;
    private NavigationView navigationView;
    private FirebaseUser user;
    private Boolean isFloatingBtnClicked = false;
    private boolean isLoggedIn = false;
    private boolean isAdmin = false;

    private static AlertDialog.Builder langDialog;
    private static PageAdapter pageAdapter;



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Fade());
        setContentView(R.layout.activity_main);
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String em;
        if(user!=null){
            if(user.getEmail()!=null && user.getEmail().equals("nathan3gill@gmail.com")){
                isAdmin = true;
                em = user.getEmail();
            }else em = (user.getEmail());
            isLoggedIn = true;
        }else{
            em = ("Guest");
        }

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

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        navigationView = findViewById(R.id.nav_view);
        if(isLoggedIn){
            navigationView.getMenu().getItem(1).setTitle("SIGN OUT");
        }else{
            navigationView.getMenu().getItem(1).setTitle("SIGN IN");
        }
        navigationView.setNavigationItemSelectedListener(this);

        tabLayout.addTab(tabLayout.newTab().setText("LESSONS"));
        tabLayout.addTab(tabLayout.newTab().setText("AUDIO"));

        viewPager = findViewById(R.id.home_view_pager);
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new LessonFragment(
                preferences.getString("currentLanguage", "English")), "LESSONS");
        pageAdapter.addFragment(new AudioFragment(
                preferences.getString("currentLanguage", "English")), "AUDIO");
        viewPager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(viewPager);

        if(isAdmin)
            (findViewById(R.id.floating_area)).setVisibility(RelativeLayout.VISIBLE);

        try{
            TextView textView = navigationView.getHeaderView(0).findViewById(R.id.email);
            textView.setText(em);
        }catch(Exception e){
        }

        ((TextView)findViewById(R.id.select_language)).setText(preferences.getString("currentLanguage", "English"));
        
        langDialog = new AlertDialog.Builder(this);
        langDialog.setTitle("Select Language Input");
        langDialog.setIcon(R.drawable.ic_nav_language_brown);
        final String[] lang = getResources().getStringArray(R.array.language_input);
        langDialog.setSingleChoiceItems(R.array.language_input,
                preferences.getInt("currentIndex", 0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                preferences.edit().putInt("currentIndex", i).commit();
                preferences.edit().putString("currentLanguage", lang[i]).commit();
            }
        });

        langDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(HomeDefault.this, HomeDefault.class);
                finishAffinity();
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        langDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        langDialog.create();
    }

    public void OnClickSelectLanguage(View view){
        actionButton.performClick();
    }

    private void showFABMenu() {
        LinearLayout btn1 = findViewById(R.id.floating_btn1);
        LinearLayout btn2 = findViewById(R.id.floating_btn2);
        LinearLayout btn3 = findViewById(R.id.floating_btn3);

        btn1.setVisibility(View.VISIBLE);
        btn2.setVisibility(View.VISIBLE);
        btn3.setVisibility(View.VISIBLE);

        btn1.animate().translationY(-getResources()
                .getDimension(R.dimen.standard_55)).alpha(1f)
                .setInterpolator(new AnticipateOvershootInterpolator()).setDuration(500);
        btn2.animate().translationY(-getResources()
                .getDimension(R.dimen.standard_105)).alpha(1f)
                .setInterpolator(new AnticipateOvershootInterpolator()).setDuration(500);
        btn3.animate().translationY(-getResources()
                .getDimension(R.dimen.standard_155)).alpha(1f)
                .setInterpolator(new AnticipateOvershootInterpolator()).setDuration(500);


    }

    private void closeFABMenu(){
        final LinearLayout btn1 = findViewById(R.id.floating_btn1);
        final LinearLayout btn2 = findViewById(R.id.floating_btn2);
        final LinearLayout btn3 = findViewById(R.id.floating_btn3);

        btn1.animate().translationY(0).alpha(0).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                btn1.setVisibility(View.GONE);
            }
        });
        btn2.animate().translationY(0).alpha(0).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                btn2.setVisibility(View.GONE);
            }
        });
        btn3.animate().translationY(0).alpha(0).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                btn3.setVisibility(View.GONE);
            }
        });
    }

    public void onClickFloatingButton(View view){

        ImageView blk = findViewById(R.id.block_view);

        FloatingActionButton btn = view.findViewById(R.id.floating_btn);

        if(!isFloatingBtnClicked){
            isFloatingBtnClicked = true;

            blk.setVisibility(ImageView.VISIBLE);
            Animation fadeIn = new AlphaAnimation(0,1f);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(300);
            AnimationSet set = new AnimationSet(false);
            set.addAnimation(fadeIn);
            blk.setAnimation(set);
            blk.setEnabled(false);

            AnimatorSet rotateSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.rotate_45);
            rotateSet.setTarget(btn);
            rotateSet.start();
            showFABMenu();


        }else{
            isFloatingBtnClicked = false;

            Animation fadeOut = new AlphaAnimation(1f,0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(300);
            AnimationSet set = new AnimationSet(false);
            set.addAnimation(fadeOut);
            blk.setAnimation(set);
            blk.setVisibility(ImageView.GONE);

            AnimatorSet rotateSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.rotate_45_reverse);
            rotateSet.setTarget(btn);
            rotateSet.start();

            closeFABMenu();
        }
    }


    public void onClickAddData(View view){
        Intent i = new Intent(this, AddData.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onClickAddAdmin(View view){
        Intent i = new Intent(this,SignIn.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onClickAddVerse(View view){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.drawer_language:
                langDialog.show();
                break;
            case R.id.drawer_signIn:
                if(isLoggedIn){
                    FirebaseAuth.getInstance().signOut();
                    isLoggedIn = false; isAdmin = false;
                    drawerLayout.closeDrawer(GravityCompat.START);
                    (findViewById(R.id.floating_area)).setVisibility(RelativeLayout.GONE);
                    navigationView.getMenu().getItem(1).setTitle("SIGN IN");
                    LoginManager.getInstance().logOut();
                    Intent i = new Intent(this, HomeDefault.class);
                    finishAffinity();
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else{
                    Intent i = new Intent(this, SignIn.class);
                    startActivity(i);
                    finish();
                }
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

        public void clear(){
            frags.clear();
            title.clear();
        }
    }


}
