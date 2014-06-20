package cl.wafle.mediaplayer.views;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cl.wafle.mediaplayer.R;
import cl.wafle.mediaplayer.asynctasks.GetRadios;
import cl.wafle.mediaplayer.services.ServiceRadio;
import cl.wafle.mediaplayer.utils.Constants;


public class Index extends SherlockActivity {

    private final String TAG = Index.class.getSimpleName();

    private ServiceRadio mServiceRadio;
    private boolean isBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServiceRadio = ((ServiceRadio.RadioBinder) iBinder).getService();
            isBound = true;
            Log.i(TAG, "onServiceConnected");
            if(mServiceRadio.isRunning()){
                Log.i(TAG, "oonServiceConnected Service is Running");
                changeWhenIsPlaying();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "ReceivingBroadcast");

            if(intent.getAction().equals(Constants.receiver_mediaplayer_status)) {
                //Do something with the string
                Log.i(TAG, Constants.receiver_mediaplayer_status);
                if(intent.hasExtra(Constants.receiver_mediaplayer_status)){
                    String status = intent.getStringExtra(Constants.receiver_mediaplayer_status);
                    tvSignalStatus.setText(status);

                    if(status.equals(Constants.STATUS_LIVE)){
                        changeWhenIsPlaying();
                    }else{
                        changeWhenIsNotPlaying();
                    }
                }
            }

            if(intent.getAction().equals(Constants.receiver_mediaplayer_content)){
                Log.i(TAG, Constants.receiver_mediaplayer_content);

                String artist = null;
                String trackname = null;
                String mediaName = null;

                String stringTvSignalTitle = "";

                if(intent.hasExtra(Constants.receiver_mediaplayer_content_medianame)){
                    mediaName = intent.getStringExtra(Constants.receiver_mediaplayer_content_medianame);
                }

                if(intent.hasExtra(Constants.receiver_mediaplauer_content_artist)){
                    artist = intent.getStringExtra(Constants.receiver_mediaplauer_content_artist);
                }

                if(intent.hasExtra(Constants.receiver_mediaplayer_content_trackname)){
                    trackname = intent.getStringExtra(Constants.receiver_mediaplayer_content_trackname);
                }

                if(artist == null && trackname == null){
                    stringTvSignalTitle = mediaName;
                }else if(artist != null && trackname != null){
                    stringTvSignalTitle = artist +" - "+trackname;
                }

                tvSignalTitle.setText(stringTvSignalTitle);
            }
        }
    };

    //UI
    @InjectView(R.id.btnActionPlayPause) ImageButton btnActionPlayPause;
    @InjectView(R.id.tvSignalTitle) TextView tvSignalTitle;
    @InjectView(R.id.tvSignalStatus) TextView tvSignalStatus;
    @InjectView(R.id.lvSignalList) ListView lvSignalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Obteniendo librerías
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
            return;

        setContentView(R.layout.activity_index);
        ButterKnife.inject(this);
        // TODO Use "injected" views...

        bindServices();
        registerReceivers();
        setViews();
    }

    @OnClick(R.id.btnActionPlayPause) void clickActionPlayPause() {
        // TODO call server...
        Log.v(TAG, "Button Play Pressed!!");
        if(mServiceRadio != null){
            Log.i(TAG, "mServiceRadio is not null");
            Intent intent = new Intent(this, ServiceRadio.class);
            if(!mServiceRadio.isRunning()){
                Log.v(TAG, "Is Not Running");
                intent.setAction(Constants.service_mediaplayer_play);
                startService(intent);
                changeWhenIsPlaying();
            }else{
                Log.v(TAG, "Is Running");
                intent.setAction(Constants.service_mediaplayer_stop);
                startService(intent);
                changeWhenIsNotPlaying();
            }
        }else {
            Log.i(TAG, "mServiceRadio is null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mServiceRadio == null){
            bindServices();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mServiceRadio != null){
            if(isBound){
                unbindService(mConnection);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeWhenIsPlaying(){
        btnActionPlayPause.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
        tvSignalStatus.setText(mServiceRadio.getStatus());
    }

    public void changeWhenIsNotPlaying(){
        btnActionPlayPause.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
        tvSignalStatus.setText(mServiceRadio.getStatus());
    }

    public void bindServices(){
        Intent intent = new Intent(this, ServiceRadio.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    public void registerReceivers(){
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.receiver_mediaplayer_status);
        intentFilter.addAction(Constants.receiver_mediaplayer_content);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void setViews(){
        new GetRadios(){
            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(Index.this);
                progressDialog.setTitle("Cargando");
                progressDialog.setMessage("Obteniendo Señales");
                progressDialog.setCancelable(false);
            }

            @Override
            protected void onPostExecute(List<String[]> strings) {
                super.onPostExecute(strings);
                if(progressDialog != null){
                    progressDialog.dismiss();
                }

                HashMap<String, String> items;
                ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

                for(int i = 0; i < strings.size(); i++){
                    items = new HashMap<String, String>();

                    items.put("line1", strings.get(i)[0]);
                    items.put("line2", strings.get(i)[1]);
                    list.add( items );

                }

                SimpleAdapter sa = new SimpleAdapter(Index.this,
                        list,
                        android.R.layout.simple_list_item_2,
                        new String[] {"line1", "line2"},
                        new int [] {android.R.id.text1, android.R.id.text2});

                lvSignalList.setAdapter(sa);
            }

        }.execute();

        lvSignalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                Log.i(TAG, "onItemClick => " + text2.getText());
                Intent intent = new Intent(Index.this, ServiceRadio.class);
                intent.setAction(Constants.service_mediaplayer_play);
                intent.putExtra("name", text1.getText());
                intent.putExtra("url", text2.getText());
                startService(intent);
            }
        });
    }


}
