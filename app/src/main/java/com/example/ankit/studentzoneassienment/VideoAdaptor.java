package com.example.ankit.studentzoneassienment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ankit.studentzoneassienment.model.YoutubeDataModel;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class VideoAdaptor extends ArrayAdapter {

    Context _context;
    int resource;
    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle params;
    ArrayList<YoutubeDataModel> mListData = new ArrayList<>();



    public VideoAdaptor(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        this._context = context;
        this.mListData = objects;
        this.resource = resource;
        params = new Bundle();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(_context);
    }

    public int getCount() {
        return mListData.size();
    }

    public YoutubeDataModel getItem(int i) {
        return mListData.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View v, ViewGroup viewGroup) {
        View view;
        LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = infalInflater.inflate( this.resource,null,true);

        final YoutubeDataModel data =  getItem(i);

        final YouTubeThumbnailView thumbnail = view.findViewById(R.id.thumbnail);

        TextView title = view.findViewById(R.id.title);

        //final String url = "https://img.youtube.com/vi/"+getItem(i)+"/0.jpg";

        Glide.with(_context).load(data.getThumbnail())
                .thumbnail(1f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(thumbnail);

            title.setText(data.getTitle());
            thumbnail.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), Youtube.class);
            intent.putExtra("code",data.getVideo_id());
            intent.putExtra("type","video");
            _context.startActivity(intent);
        });
        return view;
    }
}
