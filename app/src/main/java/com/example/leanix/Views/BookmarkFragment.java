package com.example.leanix.Views;

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

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leanix.Adapters.LaunchesListAdapter;
import com.example.leanix.Model.Launch;
import com.example.leanix.R;
import com.example.leanix.Utils.Utils;
import com.example.leanix.ViewModel.MainActivityViewModel;

import java.util.List;

public class BookmarkFragment extends Fragment implements
        LaunchesListAdapter.OnLaunchClickListener {
    private static final String TAG = "BookmarkFragment";
    // UI
    private RecyclerView mBookmarkRv;
    private TextView mNoBookmarkTv;

    // VAR
    private MainActivityViewModel mSharedModel;
    private LaunchesListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private String mRecyclerViewType;
    private Parcelable recyclerViewSate;
    private NavController navController;

    public BookmarkFragment() {
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
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerViewType = Utils.getListViewOption(getActivity());
        navController = Navigation.findNavController(view);
        mSharedModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mSharedModel.getListViewType().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String viewType) {
                mRecyclerViewType = viewType;
            }
        });

        initView(view);
        showBookmarkRv();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity)getActivity();
        if (activity != null) {
            activity.showUpButton();
            activity.setToolbarTitle("Bookmarks");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerViewSate = mLayoutManager.onSaveInstanceState();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.launch_list,menu);

        menu.getItem(1).setVisible(false);
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
        int internetStatus = Utils.getConnectivityStatusString(getActivity());
        switch (id) {
            case R.id.sortByMissionName: {
                if (internetStatus == Utils.NO_INTERNET_CONNECTIVITY){
                    List<Launch> sortedList = mSharedModel.getSortedLaunchList(mAdapter.getBookMarkList(),
                            Utils.SORT_BY_MISSION_NAME_BOOKMARK_LIST);
                    mAdapter.updateBookmarkList(sortedList);
                }
                mSharedModel.sortLaunchList(Utils.SORT_BY_MISSION_NAME_BOOKMARK_LIST);
                return true;
            }
            case R.id.sortByLaunchDate: {
                if (internetStatus == Utils.NO_INTERNET_CONNECTIVITY){
                    List<Launch> sortedList = mSharedModel.getSortedLaunchList(mAdapter.getBookMarkList(),
                            Utils.SORT_BY_LAUNCH_DATE_BOOKMARK_LIST);
                    mAdapter.updateBookmarkList(sortedList);
                }
                mSharedModel.sortLaunchList(Utils.SORT_BY_LAUNCH_DATE_BOOKMARK_LIST);
                return true;
            }
            case R.id.listViewType: {
                if (mRecyclerViewType.equals(Utils.LIST_VIEW_VALUE)) {
                    Utils.setListViewOption(getActivity(),Utils.GRID_VIEW_VALUE);
                    mSharedModel.setListViewType(Utils.GRID_VIEW_VALUE);
                    mLayoutManager = new GridLayoutManager(getActivity(), 2);
                    item.setTitle("List View");
                }else if(mRecyclerViewType.equals(Utils.GRID_VIEW_VALUE)) {
                    Utils.setListViewOption(getActivity(),Utils.LIST_VIEW_VALUE);
                    mSharedModel.setListViewType(Utils.LIST_VIEW_VALUE);
                    mLayoutManager = new
                            LinearLayoutManager(getActivity(),
                            LinearLayoutManager.VERTICAL,false);
                    item.setTitle("Grid View");
                }
                mBookmarkRv.setLayoutManager(mLayoutManager);
                return true;
            }
            case android.R.id.home:
                navController.navigateUp();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView(View view) {
        mBookmarkRv = view.findViewById(R.id.bookmarkRv);
        mNoBookmarkTv = view.findViewById(R.id.noBookmarksTv);
    }

    private void showBookmarkRv() {
        if (mRecyclerViewType.equals(Utils.LIST_VIEW_VALUE)) {
            mLayoutManager = new
                    LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL,false);
        }else if(mRecyclerViewType.equals(Utils.GRID_VIEW_VALUE)) {
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
        }
        mSharedModel.getBookmarkList().observe(getViewLifecycleOwner(), new Observer<List<Launch>>() {
            @Override
            public void onChanged(List<Launch> bookmarkList) {
                if (bookmarkList!=null) {
                    if (bookmarkList.size() == 0) {
                        showNoBookmarksFound();
                    }else{
                        hideNoBookmarkFound();
                    }
                    setBookmarkRv(bookmarkList);
                }else {
                    showNoBookmarksFound();
                }
            }
        });
    }

    private void setBookmarkRv(List<Launch> bookmarkList) {
        mBookmarkRv.setLayoutManager(mLayoutManager);
        mAdapter = new LaunchesListAdapter(getActivity(),this,bookmarkList,this);
        mBookmarkRv.setAdapter(mAdapter);
        mBookmarkRv.setHasFixedSize(true);
        if (recyclerViewSate != null) {
            mLayoutManager.onRestoreInstanceState(recyclerViewSate);
        }
    }

    private void showNoBookmarksFound(){
        mNoBookmarkTv.setVisibility(View.VISIBLE);
        mBookmarkRv.setVisibility(View.GONE);
    }

    private void hideNoBookmarkFound() {
        mNoBookmarkTv.setVisibility(View.GONE);
        mBookmarkRv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLaunchCardClick(int position) {
        Launch launch = mAdapter.getBookMarkList().get(position);
        mSharedModel.setSelectedLaunch(launch);
        navController.navigate(R.id.action_bookmarkFragment_to_launchDetailsFragment);
    }

    @Override
    public void addLaunchToBookmarkList(Launch launch) {
        mSharedModel.insertBookmark(launch);
    }

    @Override
    public void removeLaunchFromBookmarkList(Launch launch) {
        mSharedModel.deleteBookmark(launch);
    }
}