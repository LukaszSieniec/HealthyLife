package com.example.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.example.healthylife.R;

public class RegistrationProcessActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton imageButtonBack;
    private static CustomViewPager customViewPager;
    private MyViewPagerRegistrationProcessAdapter myViewPagerRegistrationProcessAdapter;

    private class MyViewPagerRegistrationProcessAdapter extends FragmentStatePagerAdapter{

        private final int NUM_PAGES = 8;

        public MyViewPagerRegistrationProcessAdapter(@NonNull FragmentManager fm) {
            super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch(position) {

                case 0:
                    return new InformationFragment();

                case 1:
                    return new GenderFragment();

                case 2:
                    return new LevelOfActivityFragment();

                case 3:
                    return new WeightFragment();

                case 4:
                    return new MuscleMassFragment();

                case 5:
                    return new HeightFragment();

                case 6:
                    return new DateOfBirthFragment();

                case 7:
                    return new SignUp();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_process);

        imageButtonBack = findViewById(R.id.imageButtonBack);

        customViewPager = findViewById(R.id.customViewPager);
        customViewPager.setPagingEnabled(false);

        myViewPagerRegistrationProcessAdapter = new MyViewPagerRegistrationProcessAdapter(getSupportFragmentManager());
        customViewPager.setAdapter(myViewPagerRegistrationProcessAdapter);

        imageButtonBack.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageButtonBack) {

            if(customViewPager.getCurrentItem() == 0) {

                super.onBackPressed();

            } else {

                customViewPager.setCurrentItem(getItem(-1), false);
            }
        }
    }

    private static int getItem(int i) {
        return customViewPager.getCurrentItem() + i;
    }

    public static void nextPage() {

        customViewPager.setCurrentItem(getItem(+1), false);
    }

    @Override
    public void onBackPressed() {

        if(customViewPager.getCurrentItem() == 0) {

            super.onBackPressed();

        } else {

            customViewPager.setCurrentItem(getItem( - 1), false);
        }
    }
}
