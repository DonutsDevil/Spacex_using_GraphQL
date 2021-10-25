package com.example.leanix.Views;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leanix.Adapters.LaunchesListAdapter;
import com.example.leanix.Model.Launch;
import com.example.leanix.R;
import com.example.leanix.Utils.Utils;
import com.example.leanix.ViewModel.MainActivityViewModel;


import java.util.List;

public class LaunchesListFragment extends Fragment implements
        LaunchesListAdapter.OnLaunchClickListener, View.OnClickListener {
    private static final String TAG = "LaunchesListFragment";
    //UI
    private RecyclerView mLaunchesRv;
    private NavController navController;
    private ImageView mNoInternetConnectionIv;
    private Button mTryAgainBtn;
    private ProgressBar mProgressbar;
    private Toast mToast;
    private TextView mErrorMessageTv;

    //VAR
    private LaunchesListAdapter mAdapter;
    private MainActivityViewModel sharedModel;
    private List<Launch> launchList;
    private LinearLayoutManager mLayoutManager;
    private String mRecyclerViewType;
    private AlertDialog progressAlertDialog;
    private String toastMessage;

    // Save Recycler View Scroll State Using LinearLayoutManager;
    private Parcelable state;

    public LaunchesListFragment() {
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
        return inflater.inflate(R.layout.fragment_launches_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerViewType = Utils.getListViewOption(getActivity());
        navController = Navigation.findNavController(view);
        sharedModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mLayoutManager = new
                LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        sharedModel.setListViewType(mRecyclerViewType);


        sharedModel.getListViewType().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String viewType) {
                mRecyclerViewType = viewType;
            }
        });

        initView(view);
        showProgressBar();
        setViewDependingOnInternet();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity)getActivity();
        if (activity != null) {
            activity.hideUpButton();
            activity.setToolbarTitle(getActivity().getResources().getString(R.string.app_name));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        state = mLayoutManager.onSaveInstanceState();
        toastMessage = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.launch_list,menu);

        if (mRecyclerViewType.equals(Utils.LIST_VIEW_VALUE)){
            menu.getItem(2).setTitle("Grid View");
        }
        else{
            menu.getItem(2).setTitle("List View");
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sortByMissionName: {
                sharedModel.sortLaunchList(Utils.SORT_BY_MISSION_NAME_LAUNCH_LIST);
                return true;
            }
            case R.id.sortByLaunchDate: {
                sharedModel.sortLaunchList(Utils.SORT_BY_LAUNCH_DATE_LAUNCH_LIST);
                return true;
            }
            case R.id.listViewType: {
                if (mRecyclerViewType.equals(Utils.LIST_VIEW_VALUE)) {
                    Utils.setListViewOption(getActivity(),Utils.GRID_VIEW_VALUE);
                    sharedModel.setListViewType(Utils.GRID_VIEW_VALUE);
                    mLayoutManager = new GridLayoutManager(getActivity(), 2);
                    item.setTitle("List View");
                }else if(mRecyclerViewType.equals(Utils.GRID_VIEW_VALUE)) {
                    Utils.setListViewOption(getActivity(),Utils.LIST_VIEW_VALUE);
                    sharedModel.setListViewType(Utils.LIST_VIEW_VALUE);
                    mLayoutManager = new
                            LinearLayoutManager(getActivity(),
                            LinearLayoutManager.VERTICAL,false);
                    item.setTitle("Grid View");
                }
                mLaunchesRv.setLayoutManager(mLayoutManager);
                return true;
            }
            case R.id.bookmarks: {
                navController.navigate(R.id.action_launchesListFragment_to_bookmarkFragment);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tryAgain) {
            showProgressBar();
            setViewDependingOnInternet();
        }
    }

    private void setViewDependingOnInternet() {
        int internetConnectivityStatus = Utils.getConnectivityStatusString(getActivity());
        if (internetConnectivityStatus == Utils.NO_INTERNET_CONNECTIVITY) {
            hideProgressBar();
            showNoInternetConnection();
        }else {
            setLaunchListRecyclerView();
        }

    }

    private void setLaunchListRecyclerView() {
        showLaunchList();
        sharedModel.getLaunchesList().observe(getViewLifecycleOwner(), new Observer<List<Launch>>() {
            @Override
            public void onChanged(List<Launch> launches) {
                if (launches != null && launches.size() == 1 && launches.get(0).getErrorMessageWhileFetchingAPI() != null) {
                    // Error while fetching data
                    showErrorMessage();
                }else {
                    hideErrorMessage();
                    launchList = launches;
                    setLaunchList();
                    hideProgressBar();
                }
            }
        });

    }

    private void initView(View view) {
        mNoInternetConnectionIv = view.findViewById(R.id.noInternetIv);
        mLaunchesRv = view.findViewById(R.id.launchesRv);
        mTryAgainBtn = view.findViewById(R.id.tryAgain);
        mProgressbar = view.findViewById(R.id.indeterminateBar);
        mErrorMessageTv = view.findViewById(R.id.errorMessageTv);
    }

    private void setLaunchList() {

        if (mRecyclerViewType.equals(Utils.LIST_VIEW_VALUE)) {
            mLayoutManager = new
                    LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL,false);
        }else if(mRecyclerViewType.equals(Utils.GRID_VIEW_VALUE)) {
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
        }

        mLaunchesRv.setLayoutManager(mLayoutManager);
        mAdapter = new LaunchesListAdapter(getActivity(),this,launchList,this);

        sharedModel.getBookmarkList().observe(getViewLifecycleOwner(), new Observer<List<Launch>>() {
            @Override
            public void onChanged(List<Launch> bookmarks) {
                if (bookmarks != null) {
                    mAdapter.updateBookmarkList(bookmarks);
                    Log.d(TAG, "onChanged: bookmark list size: "+bookmarks.size());
                    showToast();
                }
                if (progressAlertDialog != null) {
                    progressAlertDialog.cancel();
                }
            }
        });

        mLaunchesRv.setAdapter(mAdapter);
        if(state != null) {
            mLayoutManager.onRestoreInstanceState(state);
        }
        mLaunchesRv.setHasFixedSize(true);
    }

    private void showToast() {
        if (mToast != null) {
            mToast.cancel();
        }
        if (toastMessage != null) {
            mToast = new Toast(getActivity());
            mToast.setText(toastMessage);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    @Override
    public void onLaunchCardClick(int position) {
        Launch launch = launchList.get(position);
        sharedModel.setSelectedLaunch(launch);
        navController.navigate(R.id.action_launchesListFragment_to_launchDetailsFragment);
    }

    @Override
    public void addLaunchToBookmarkList(Launch launch) {
        AlertDialog.Builder progressAlertBuilder = getAlertDialog();
        progressAlertDialog = progressAlertBuilder.show();
        sharedModel.insertBookmark(launch);
        toastMessage = "Launch Added to Bookmark list";
    }

    @Override
    public void removeLaunchFromBookmarkList(Launch launch) {
        AlertDialog.Builder progressAlertBuilder = getAlertDialog();
        progressAlertDialog = progressAlertBuilder.show();
        sharedModel.deleteBookmark(launch);
        toastMessage = "Launch Removed from Bookmark list";
    }

    private AlertDialog.Builder getAlertDialog(){
        return new AlertDialog.Builder(getActivity())
                .setView(R.layout.alert_progress_cricle)
                .setCancelable(false);
    }

    public void showNoInternetConnection() {
        mNoInternetConnectionIv.setVisibility(View.VISIBLE);
        mTryAgainBtn.setVisibility(View.VISIBLE);
        mTryAgainBtn.setOnClickListener(this);
        mLaunchesRv.setVisibility(View.GONE);
    }

    public void showLaunchList() {
        mNoInternetConnectionIv.setVisibility(View.GONE);
        mTryAgainBtn.setVisibility(View.GONE);
        mLaunchesRv.setVisibility(View.VISIBLE);
    }

    public void showErrorMessage() {
        mNoInternetConnectionIv.setVisibility(View.GONE);
        mTryAgainBtn.setVisibility(View.GONE);
        mLaunchesRv.setVisibility(View.GONE);
        mErrorMessageTv.setVisibility(View.VISIBLE);
        hideProgressBar();
    }

    public void hideErrorMessage() {
        mNoInternetConnectionIv.setVisibility(View.GONE);
        mTryAgainBtn.setVisibility(View.GONE);
        mErrorMessageTv.setVisibility(View.GONE);
        mLaunchesRv.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        mNoInternetConnectionIv.setVisibility(View.GONE);
        mTryAgainBtn.setVisibility(View.GONE);
        mProgressbar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressbar.setVisibility(View.GONE);
    }

}