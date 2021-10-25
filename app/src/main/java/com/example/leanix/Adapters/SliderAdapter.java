package com.example.leanix.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.example.leanix.R;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.Holder> {
    private List<String> mImageUrlList;
    private Context mContext;

    public SliderAdapter(List<String> imageUrlList, Context context, ViewPager2 pager2){
        mImageUrlList = imageUrlList;
        mContext = context;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_carousel_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        RequestOptions options = new RequestOptions()
                .error(R.drawable.noimage);
        Glide.with(mContext)
                .setDefaultRequestOptions(options)
                .load(mImageUrlList.get(position))
                .placeholder(R.drawable.noimage)
                .into(holder.mCarouselIv);

    }


    @Override
    public int getItemCount() {
        return mImageUrlList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView mCarouselIv;
        public Holder(@NonNull View itemView) {
            super(itemView);
            mCarouselIv = itemView.findViewById(R.id.carouselIv);
        }
    }
}
