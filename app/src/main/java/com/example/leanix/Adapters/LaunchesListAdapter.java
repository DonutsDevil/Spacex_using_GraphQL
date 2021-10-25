package com.example.leanix.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.leanix.Model.Launch;
import com.example.leanix.R;
import com.example.leanix.Utils.Utils;
import com.example.leanix.Views.BookmarkFragment;
import com.example.leanix.Views.LaunchesListFragment;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LaunchesListAdapter extends RecyclerView.Adapter<LaunchesListAdapter.Holder> {
    private static final String TAG = "LaunchesListAdapter";
    private final Context mContext;
    private final OnLaunchClickListener mLaunchCardClick;
    private final List<Launch> mLaunchList;
    private List<Launch> mBookMarkList;
    private Fragment mFragment;
    private boolean isBookMarkFragment;

    public LaunchesListAdapter(Context context, OnLaunchClickListener launchCardClick,
                               List<Launch> launchList, Fragment fragment) {
        mContext = context;
        mLaunchCardClick = launchCardClick;
        mBookMarkList = new ArrayList<>();
        mLaunchList = launchList;
        if (fragment instanceof BookmarkFragment) {
            mBookMarkList = mLaunchList;
            isBookMarkFragment = true;
        }else {
            isBookMarkFragment = false;
        }
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_launch_list_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Launch launch;
        if (isBookMarkFragment){
            launch = mBookMarkList.get(position);
        }else {
            launch = mLaunchList.get(position);
        }
        holder.setBookMarkIv(launch, false,position);
        holder.setMissionName(launch.getShortMissionName());
        holder.mLaunchDate.setText(launch.getLaunchDate());
        holder.setMissionPatchImage(launch.getSmallMissionPatchImageURL());
    }

    @Override
    public int getItemCount() {
        return mLaunchList.size();
    }

    public void updateBookmarkList(List<Launch> newBookmarkList) {
        this.mBookMarkList = newBookmarkList;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialCardView mLaunchCard;
        private final ImageView mMissionPatchImageIv;
        private final TextView mMissionNameTv;
        private final TextView mLaunchDate;
        private final ImageView mBookMarkIv;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mLaunchCard = itemView.findViewById(R.id.launchCard);
            mMissionPatchImageIv = itemView.findViewById(R.id.launchPatchImage);
            mMissionNameTv = itemView.findViewById(R.id.missionNameTv);
            mLaunchDate = itemView.findViewById(R.id.launchDateTv);
            mBookMarkIv = itemView.findViewById(R.id.bookMarkIv);

            mLaunchCard.setOnClickListener(this);
            mBookMarkIv.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.launchCard : mLaunchCardClick.onLaunchCardClick(getAdapterPosition());
                    break;
                case R.id.bookMarkIv :  {
                    int position = getAdapterPosition();
                    Launch launch = mLaunchList.get(position);
                    setBookMarkIv(launch,true,position);
                    break;
                }
            }
        }

        private void setMissionPatchImage(String url) {
            RequestOptions options = new RequestOptions()
                    .error(R.drawable.noimage);
            Glide.with(mContext)
                    .setDefaultRequestOptions(options)
                    .load(url)
                    .placeholder(R.drawable.noimage)
                    .into(mMissionPatchImageIv);
        }

        private void setMissionName(String missionName) {
            mMissionNameTv.setText(missionName);
        }

        /**
         * @param selectedLaunch tells the selectedLaunch weather it needs to be checked, or uncheck bookmark
         * @param toAddInList Is true when we click to mark the bookmark.
         *                      This will add position to bookmarkList
         *                    Is false when we just load the card,
         *                      the bookmark is check or uncheck if the position is there in bookmarklist
         * */
        private void setBookMarkIv(Launch selectedLaunch, boolean toAddInList, int position) {
            if (mBookMarkList.contains((selectedLaunch))) {
                if(toAddInList) {
                    mBookMarkList.remove(selectedLaunch);
                    if (isBookMarkFragment) {
                        notifyDataSetChanged();
                    }
                    selectedLaunch.setIsBookmark(0);
                    mLaunchCardClick.removeLaunchFromBookmarkList(selectedLaunch);
                    mBookMarkIv.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.ic_bookmark_unfilled));
                }else{
                    mBookMarkIv.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.ic_bookmark_filled));
                }
            }else {
                if(toAddInList) {
                    mBookMarkList.add(selectedLaunch);
                    selectedLaunch.setIsBookmark(Utils.BOOKMARK_KEY);
                    mLaunchCardClick.addLaunchToBookmarkList(selectedLaunch);
                    mBookMarkIv.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.ic_bookmark_filled));
                }else{
                    mBookMarkIv.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.ic_bookmark_unfilled));
                }
            }
        }
    }

    public List<Launch> getBookMarkList() {
        return mBookMarkList;
    }

    public interface OnLaunchClickListener {
        void onLaunchCardClick(int position);
        void addLaunchToBookmarkList(Launch launch);
        void removeLaunchFromBookmarkList(Launch launch);
    }
}
