package com.example.ankit.studentzoneassienment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.ankit.studentzoneassienment.model.YoutubeDataModel;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Youtube extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private Tracker mTracker;
    YouTubePlayer mYoutubePlayer;
    YouTubePlayerView youtubeplayerview;
    int in=0;
    long t=0;
    long time;


    YoutubeDataModel mListData = new YoutubeDataModel();
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        new RequestYoutubeAPI().execute();

        youtubeplayerview = findViewById(R.id.youtubeplayer);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        if (!b) {
            String code = getIntent().getStringExtra("code");
            youTubePlayer.loadVideo(code);
        }
        mYoutubePlayer = youTubePlayer;
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    protected void onStart() {
        String type = getIntent().getStringExtra("type");
        if(type.equals("live") || type.equals("live noti"))
        {
            Log.i("yqwas",mListData.getChannelId());
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Video")
                    .setAction("tried playing")
                    .setLabel("video")
                    .build());
            if(type.equals("live noti"))
            {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Video")
                        .setAction("tried playing notification")
                        .setLabel(mListData.getChannelId())
                        .build());
            }
        }
        Log.i("yo","onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i("yo","onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("yo","onPause");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        Log.i("yo","onSaveInstanceState");
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onStop() {
        Log.i("yo","onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try{
            long millis = mYoutubePlayer.getCurrentTimeMillis();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Video")
                    .setAction("play duration")
                    .setLabel(mListData.getTitle())
                    .setValue(seconds)
                    .build());
        }catch (Exception e) {
            Log.i("yo", "error destroy");
        }
        super.onDestroy();
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {
            Bundle bundle = new Bundle();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    try {
                        long millis = mYoutubePlayer.getCurrentTimeMillis();
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
                        long totalmillis = mYoutubePlayer.getDurationMillis();
                        long totalseconds = TimeUnit.MILLISECONDS.toSeconds(totalmillis);
                        t =seconds-time;
                        String type = getIntent().getStringExtra("type");
                        if(type.equals("live noti")&& in == 0)
                        {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Video")
                                    .setAction("start notification")
                                    .setLabel( mListData.getChannelId())
                                    .build());
                        }
                        if(seconds == 1 && in==0)
                        {
                            in=1;
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Video")
                                    .setAction("start")
                                    .setLabel(mListData.getTitle())
                                    .build());
                        }
                        if(t ==5 && seconds==time+ t)
                        {
                            Log.d("yo", "run: "+seconds);
                            time = seconds;
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Video")
                                    .setAction("total play duration")
                                    .setLabel(mListData.getTitle())
                                    .setValue(5)
                                    .build());
                            handler.postDelayed(this, 500);
                        }else{
                            handler.postDelayed(this, 500);
                        }
                    }catch (Exception e)
                    {
                        Log.i("timeerror",e+"");
                    }
                }
            }, 999);
          //new CurrentTIme().execute(mYoutubePlayer);
            Log.i("yo","onPlaying");
        }

        @Override
        public void onPaused() {
            Log.i("yo","onPaused");
        }

        @Override
        public void onStopped() {
            Log.i("yo","onStopped");
        }

        @Override
        public void onBuffering(boolean b) {
            Log.i("yo","onBuffering");
        }

        @Override
        public void onSeekTo(int i) {
            i/=1000;
            time=i-t;
            Log.i("yo","onSeekTo"+i);
        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {
            Log.i("yo","onLoading");
        }

        @Override
        public void onLoaded(String s) {
            Log.i("yo","onLoaded");
        }

        @Override
        public void onAdStarted() {
            Log.i("yo","onAdStarted");
        }

        @Override
        public void onVideoStarted() {
            Log.i("yo","onVideoStarted");
        }

        @Override
        public void onVideoEnded() {
            Log.i("yo","onVideoEnded");
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            Log.i("yo","onError");
        }
    };


    @SuppressLint("StaticFieldLeak")
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {

        ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(Youtube.this);
            progress.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            String code = getIntent().getStringExtra("code");
            String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id="+code+"&maxResults=20&key="+Config.DEVELOPER_KEY+"";
            HttpGet httpGet = new HttpGet(url);
            Log.e("URL", url);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                String json = EntityUtils.toString(httpEntity);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    mListData = parseVideoListFromResponse(jsonObject);
                    Log.i("ankir",mListData.getVideo_id());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                youtubeplayerview.initialize(Config.DEVELOPER_KEY, Youtube.this);
                progress.setMessage("Loading.... :) ");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.hide();
            }
        }
    }

    public YoutubeDataModel parseVideoListFromResponse(JSONObject jsonObject) {
        YoutubeDataModel youtubeObject = new YoutubeDataModel();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.has("id")) {
                        String video_id = json.getString("id");
                        JSONObject jsonSnippet = json.getJSONObject("snippet");
                        String title = jsonSnippet.getString("title");
                        String description = jsonSnippet.getString("description");
                        String publishedAt = jsonSnippet.getString("publishedAt");
                        String channelId = jsonSnippet.getString("channelId");
                        String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("standard").getString("url");
                        youtubeObject.setTitle(title);
                        youtubeObject.setDescription(description);
                        youtubeObject.setPublishedAt(publishedAt);
                        youtubeObject.setThumbnail(thumbnail);
                        youtubeObject.setVideo_id(video_id);
                        youtubeObject.setChannelId(channelId);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return youtubeObject;
    }



}



