package com.example.introduce_assignement_bhushan;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.introduce_assignement_bhushan.Adapters.UsersTabAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private UsersTabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    FragmentManager fragmentManager;


    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==0)
        {
            super.onBackPressed();
        }else {
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUi();

        fragmentManager = getSupportFragmentManager();
        adapter = new UsersTabAdapter(fragmentManager);
        adapter.addFragment(new UsersFrag(), "Users ");
        adapter.addFragment(new EnrollFrag(), "Enroll ");


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setUi() {
        viewPager = (ViewPager) findViewById(R.id.vpg_users);
        tabLayout = (TabLayout) findViewById(R.id.tbl_users);
    }
}