package com.example.ankit.studentzoneassienment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ankit.studentzoneassienment.model.YoutubeDataModel;
import com.example.ankit.studentzoneassienment.util.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

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
import java.util.ArrayList;

public class VideoList extends AppCompatActivity {

    ListView vidolist;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private String TAG = "yo";
    ArrayList<String> url = new ArrayList<>();

    FloatingActionButton fab;
    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();


    private static String CHANNEL_ID = "UC_x5XG1OV2P6uZZ5FSM9Ttw"; //here you should use your channel id for testing purpose you can use this api also
    private static String CHANNLE_GET_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&channelId=" + CHANNEL_ID + "&maxResults=20&key=" + Config.DEVELOPER_KEY + "";

    String code= new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        vidolist = findViewById(R.id.vidolist);
        fab = findViewById(R.id.fab);



        Bundle bundle = getIntent().getExtras();
        try {
            if (bundle != null) {
                String as = bundle.getString("click_action");
                String code = bundle.getString("code");
                Log.i("DATA_SENT", as+"  "+code);
                Intent i;
                if(as.equals("live"))
                {
                    i = new Intent(VideoList.this, Youtube.class);
                    i.putExtra("code", code);
                    i.putExtra("type","live noti");
                    startActivity(i);
                }else if (as.equals("vod"))
                {
                    i = new Intent(VideoList.this, Youtube.class);
                    i.putExtra("code", code);
                    i.putExtra("type","video noti");
                    startActivity(i);
                }
            }
        }catch (Exception e)
        {
            Log.i("DATA_SENT",e.toString());
        }


        new RequestYoutubeAPI().execute();

        VideoAdaptor adaptor = new VideoAdaptor(this,R.layout.list_video,url);
        vidolist.setAdapter(adaptor);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(com.example.ankit.studentzoneassienment.app.Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(com.example.ankit.studentzoneassienment.app.Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(com.example.ankit.studentzoneassienment.app.Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    Log.i("ankit",message);
                    code = message;

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                   /* txtMessage.setText(message);*/
                }
            }
        };

        displayFirebaseRegId();

        fab.setOnClickListener(view -> {
            Intent i = new Intent(VideoList.this, Youtube.class);
            if (!code.isEmpty())
            {i.putExtra("code",code);}
            else{
                i.putExtra("type","live");
                i.putExtra("code","svWHlWi-KtA");
            }

            startActivity(i);
        });

    }


    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(com.example.ankit.studentzoneassienment.app.Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

       /* if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");*/
    }


    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(com.example.ankit.studentzoneassienment.app.Config.REGISTRATION_COMPLETE));

        try {
            String message = getIntent().getStringExtra("message");
            Log.i("ankit", message);
        }catch (Exception e)
        {
            Log.i("qwsa",e.toString());
        }
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(com.example.ankit.studentzoneassienment.app.Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    public void initList(ArrayList<YoutubeDataModel> mListData){
        VideoAdaptor adaptor = new VideoAdaptor(this,R.layout.list_video,mListData);
        vidolist.setAdapter(adaptor);
    }

    //create an asynctask to get all the data from youtube
    @SuppressLint("StaticFieldLeak")
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {
        ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(VideoList.this);
            progress.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNLE_GET_URL);
            Log.e("URL", CHANNLE_GET_URL);
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
                    initList(mListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            progress.setMessage("Loading.... :) ");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.hide();
        }
    }

    public ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.has("id")) {
                        JSONObject jsonID = json.getJSONObject("id");
                        String video_id = "";
                        if (jsonID.has("videoId")) {
                            video_id = jsonID.getString("videoId");
                        }
                        if (jsonID.has("kind")) {
                            if (jsonID.getString("kind").equals("youtube#video")) {
                                YoutubeDataModel youtubeObject = new YoutubeDataModel();
                                JSONObject jsonSnippet = json.getJSONObject("snippet");
                                String title = jsonSnippet.getString("title");
                                String description = jsonSnippet.getString("description");
                                String publishedAt = jsonSnippet.getString("publishedAt");
                                String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");
                                youtubeObject.setTitle(title);
                                youtubeObject.setDescription(description);
                                youtubeObject.setPublishedAt(publishedAt);
                                youtubeObject.setThumbnail(thumbnail);
                                youtubeObject.setVideo_id(video_id);
                                mList.add(youtubeObject);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mList;
    }
}
