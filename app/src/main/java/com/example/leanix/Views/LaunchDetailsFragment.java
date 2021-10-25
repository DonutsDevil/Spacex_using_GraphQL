package com.example.leanix.Views;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.leanix.Adapters.SliderAdapter;
import com.example.leanix.Model.Launch;
import com.example.leanix.R;
import com.example.leanix.ViewModel.MainActivityViewModel;


import java.util.List;

public class LaunchDetailsFragment extends Fragment {
    private static final String TAG = "LaunchDetailsFragment";
    //UI
    private TextView mMissionNameTv;
    private TextView mSiteNameTv;
    private TextView mLaunchDateTv;
    private TextView mRocketNameTv;
    private ImageView mLaunchSuccessIv;
    private TextView mDescriptionTv;

    private LinearLayout dotsLayout;
    private SliderAdapter mSliderAdapter;
    private ViewPager2 mViewPager2;
    private ImageView mNoImageAvailableIv;

    // VAR
    private Launch launch;
    private TextView[] dots;
    private MainActivityViewModel sharedModel;
    private NavController navController;

    public LaunchDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_launch_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        sharedModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        initView(view);
        launch = sharedModel.getSelectedLaunch();
        String missionName = launch.getShortMissionName();
        String siteName = launch.getLongSiteName();
        String date = launch.getLaunchDate();
        Log.d(TAG, "onViewCreated: launch date: "+date);
        String details = launch.getDescription();
        String rocketName = launch.getRocketName();
        boolean wasSuccessLaunch = launch.getWasLaunchSuccessful();
        List<String> carouselImageList = launch.getFlickerImageURLs();
        if (carouselImageList.size() == 0) {
            disableCarouselView();
        }else{
            setCarouselView(carouselImageList);
        }
        setOverViewDetails(missionName,siteName,date,details,wasSuccessLaunch,rocketName);

    }

    private void setCarouselView(List<String> carouselImageList) {
        enableCarouselView();
        dots = new TextView[carouselImageList.size()];
        mSliderAdapter = new SliderAdapter(carouselImageList,getActivity(),mViewPager2);
        mViewPager2.setClipToPadding(false);
        mViewPager2.setClipChildren(false);
        mViewPager2.setAdapter(mSliderAdapter);
        mViewPager2.setOffscreenPageLimit(3);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        mViewPager2.setPageTransformer(compositePageTransformer);
        createDotsIndicator();
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectedIndicator(position);
                super.onPageSelected(position);
            }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity)getActivity();
        if (activity != null) {
            activity.showUpButton();
            activity.setToolbarTitle("Details");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navController.navigateUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectedIndicator(int position) {
        Resources res = getActivity().getResources();
        for(int index = 0 ; index < dots.length; index++) {
            if (index == position) {
                dots[index].setTextColor(res.getColor(R.color.purple_500));
            }else {
                dots[index].setTextColor(res.getColor(android.R.color.darker_gray));
            }
        }
    }

    private void createDotsIndicator() {
        for(int index = 0 ; index < dots.length ; index++) {
            dots[index] = new TextView(getActivity());
            dots[index].setText(Html.fromHtml("&#9679"));
            dots[index].setTextSize(18);
            dotsLayout.addView(dots[index]);
        }
    }

    private void setOverViewDetails(String missionName, String siteName, String date,
                                    String details, boolean wasSuccessLaunch, String rocketName) {
        setSuccessLaunchIcon(wasSuccessLaunch);
        mMissionNameTv.setText(missionName);
        mSiteNameTv.setText(siteName);
        mLaunchDateTv.setText("Launch Date: "+date);
        mDescriptionTv.setText(details == null? "Description not available" : details);
        mRocketNameTv.setText("Rocket Name: "+rocketName);
    }

    private void setSuccessLaunchIcon(boolean wasSuccessLaunch) {
        if (wasSuccessLaunch) {
            mLaunchSuccessIv.setImageDrawable(AppCompatResources.getDrawable(getActivity(),R.drawable.ic_tick));
        }else{
            mLaunchSuccessIv.setImageDrawable(AppCompatResources.getDrawable(getActivity(),R.drawable.ic_cross));
        }
    }

    private void initView(View view) {
        mMissionNameTv = view.findViewById(R.id.missionNameTv);
        mSiteNameTv = view.findViewById(R.id.siteNameTv);
        mLaunchDateTv = view.findViewById(R.id.launchDateTv);
        mRocketNameTv = view.findViewById(R.id.rocketNameTv);
        mLaunchSuccessIv = view.findViewById(R.id.wasSuccessLaunchIv);
        mDescriptionTv = view.findViewById(R.id.missionDescriptionTv);
        mViewPager2 = view.findViewById(R.id.carouselViewPager2);
        dotsLayout = view.findViewById(R.id.dotsContainerLL);
        mNoImageAvailableIv = view.findViewById(R.id.noImageAvailableIv);
    }

    private void disableCarouselView() {
        mNoImageAvailableIv.setVisibility(View.VISIBLE);
        mViewPager2.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void enableCarouselView() {
        mNoImageAvailableIv.setVisibility(View.GONE);
        mViewPager2.setVisibility(View.VISIBLE);
        dotsLayout.setVisibility(View.VISIBLE);
    }

}