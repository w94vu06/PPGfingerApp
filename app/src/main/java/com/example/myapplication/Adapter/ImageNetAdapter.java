package com.example.myapplication.Adapter;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Data.DataImage;
import com.example.myapplication.Holder.ImageHolder;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class ImageNetAdapter  extends BannerAdapter<DataImage, ImageHolder> {
    public ImageNetAdapter(List<DataImage> datas) {
        super(datas);
    }

    @Override
    public ImageHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            BannerUtils.setBannerRound(imageView,20);
//        }
        return new ImageHolder(imageView);
    }

    @Override
    public void onBindView(ImageHolder holder, DataImage data, int position, int size) {
        Glide.with(holder.itemView)
                .load(data.url)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(holder.imageView);
//                .thumbnail(Glide.with(holder.itemView).load(R.drawable.images))
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
    }
}
