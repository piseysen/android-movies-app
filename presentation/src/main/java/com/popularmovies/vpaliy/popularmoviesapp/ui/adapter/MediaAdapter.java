package com.popularmovies.vpaliy.popularmoviesapp.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.popularmovies.vpaliy.domain.model.MediaCover;
import com.popularmovies.vpaliy.popularmoviesapp.R;
import com.popularmovies.vpaliy.popularmoviesapp.bus.RxBus;
import com.popularmovies.vpaliy.popularmoviesapp.bus.events.ExposeDetailsEvent;
import com.popularmovies.vpaliy.popularmoviesapp.ui.utils.Constants;
import com.popularmovies.vpaliy.popularmoviesapp.ui.utils.wrapper.TransitionWrapper;
import java.util.List;
import butterknife.ButterKnife;
import android.support.annotation.NonNull;
import butterknife.BindView;

@SuppressWarnings("WeakerAccess")
public class MediaAdapter extends AbstractMediaAdapter<MediaCover> {

    public MediaAdapter(@NonNull Context context, @NonNull RxBus rxBus){
        super(context,rxBus);
    }

    public void setData(List<MediaCover> data) {
        this.data = data;
    }

    public class MediaViewHolder extends GenericViewHolder {

        @BindView(R.id.media_poster)
        ImageView posterImage;

        @BindView(R.id.media_title)
        TextView mediaTitle;

        MediaViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            posterImage.setOnClickListener(v -> {
                if(!isLocked()) {
                    lock();
                    Bundle args = new Bundle();
                    args.putInt(Constants.EXTRA_ID, at(getAdapterPosition()).getMediaId());
                    TransitionWrapper wrapper = TransitionWrapper.wrap(posterImage, args);
                    rxBus.send(new ExposeDetailsEvent(wrapper));
                    unlockAfter(UNLOCK_TIMEOUT);
                }
            });
        }

        void onBindData(){
            MediaCover cover=at(getAdapterPosition());
            mediaTitle.setText(cover.getMovieTitle());
            Glide.with(itemView.getContext())
                    .load(cover.getPosterPath())
                    .priority(Priority.IMMEDIATE)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.placeholder)
                    .animate(R.anim.fade_in)
                    .into(posterImage);
        }

        private void applyPalette(Palette palette){
            itemView.setBackgroundColor(getDominantColor(palette));
        }

        private int getDominantColor(Palette palette){
            if (palette != null) {
                Palette.Swatch result=palette.getDominantSwatch();
                if(palette.getDarkVibrantSwatch()!=null){
                    result=palette.getDarkVibrantSwatch();
                }
                else if(palette.getDarkMutedSwatch()!=null){
                    result=palette.getDarkMutedSwatch();
                }
                return result.getRgb();
            }
            return Color.WHITE;
        }
    }

    @Override
    public void onBindViewHolder(GenericViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root=inflater.inflate(R.layout.adapter_media_item,parent,false);
        return new MediaViewHolder(root);
    }
}