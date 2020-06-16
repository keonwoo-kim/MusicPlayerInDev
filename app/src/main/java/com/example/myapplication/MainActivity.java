package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.myapplication.NotificationThing.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {

    TextView title;
    Switch shuffle;
    MusicService ms = new MusicService();
    NotificationManagerCompat notificationManagerCompat;
    ArrayList<ArrayList<String>> playListList = new ArrayList<ArrayList<String>>();
    ArrayList<String> allMusicList = new ArrayList<String>();
    ArrayList<Integer> allMusicLocs = new ArrayList<>();

    ArrayList<String> playList = new ArrayList<String>();
    ArrayList<Integer> playListLoc = new ArrayList<>();
    //ArrayList<String> playListNames = new ArrayList<String>();
    EditText et;
    AlertDialog.Builder alertDialogBuilder;
    BroadcastReceiver br;
    AlertDialog alertDialog;
    Boolean isLoaded;
    Bundle n = new Bundle();

    int total = 0;






    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        br = new EarphoneReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        this.registerReceiver(br,filter);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent1 = new Intent(this, LoadingActivity.class);
        startActivity(intent1);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, conn, 0);


        title = (TextView) findViewById(R.id.musicTitle);
        shuffle = (Switch) findViewById(R.id.shuffle);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        SharedPreferences sf = getSharedPreferences("shuffle",MODE_PRIVATE);

        Boolean state = sf.getBoolean("shuffle", false);
        shuffle.setChecked(state);
        total = sf.getInt("total", 0);
        for(int i = 0; i < total; i++)
        {
            playList.add(sf.getString("song"+i, ""));
            playListLoc.add(sf.getInt("songloc"+i, 0));
        }


        //*********************************************************
        /*
        playListNames.add("All Songs");
        playList.add("All Songs");
        for(int i = 0; i < ms.musicNames.length + ms.musicLoc.length; i++)
        {
            if(i%2 == 0){
            playList.add(ms.musicNames[i/2]);}
            else{
            playList.add(String.valueOf(ms.musicLoc[(i-1)/2]));}
        }
        playList.clear();
        int numPlayList = sf.getInt("numPlayLists", 0);
        for(int i = 0; i < numPlayList; i++)
        {
            for(int j = 0; j < sf.getInt("playList" + j + "num", 0); j++)
            {
                playList.add(sf.getString("playList" + j, ""));
            }
            playListList.add(playList);
            playListNames.add(playList.get(0));
            playList.clear();
        }

         */


        //*********************************************************

        Log.d("listSize", String.valueOf(ms.musicNames.length));



        shuffle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(shuffle.isChecked() == true)
                {
                    ms.randomPlay();
                }
                if(shuffle.isChecked() == false)
                {
                    ms.notRandomPlay();
                }
            }
        });


        /*
        //*********************************

        alertDialogBuilder = new AlertDialog.Builder(this);

        et = new EditText(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(et);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                boolean isDuplicated = false;
                for(int i = 0; i < playListList.size(); i++)
                {
                    if(!playListList.get(i).get(0).contains(et.getText().toString()))
                    {
                        isDuplicated = true;
                        duplicatedMessage();
                    }
                }
                if (!isDuplicated)
                {
                    playListNames.add(et.getText().toString());
                }
            }
        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
        //**********************************************
        */





    }


    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences sp = getSharedPreferences("shuffle", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        Boolean switchState = shuffle.isChecked();
        editor.putBoolean("shuffle", switchState);
        total = playList.size();
        for (int i = 0; i < playList.size(); i++)
        {
            editor.putString("song"+i, playList.get(i));
            editor.putInt("songloc"+i, playListLoc.get(i));
        }
        editor.putInt("total", total);

/*
        //***********************************************
        for (int i = 0; i < playListList.size(); i++) {
            for(int j = 0; j < playListList.get(i).size();){
                if(j%2 == 0) {
                    editor.putString("playList" + i, playListList.get(i).get(j));
                    numberOfSongs++;
                }
                else {
                    editor.putString("playList" + i, playListList.get(i).get(j));
                }
            }
            editor.putInt("playList" + i + "num", numberOfSongs);
            numberOfSongs = 0;
        }

        editor.putInt("numPlayLists", playListList.size());
        //********************************************************

 */
        editor.commit();
    }

    /*
    public void duplicatedMessage()
    {
        Toast.makeText(this, "There is already a playlist named " + et.getText().toString() + ".", Toast.LENGTH_SHORT).show();
    }

     */

        public void sendNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Currently Playing")
                .setContentText(ms.titleBack())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManagerCompat.notify(1, notification);
        }

        public void viewPlayList(View view) {
            Intent intent = new Intent( MainActivity.this, PlayListActivity.class); // 다음 넘어갈 클래스 지정

            if(allMusicList.size()!=ms.musicNames.length) {
                for (int i = 0; i < ms.musicNames.length; i++) {
                    allMusicList.add(ms.musicNames[i]);
                    allMusicLocs.add(ms.musicLoc[i]);
                }}
            n.putStringArrayList("allMusic", allMusicList);
            n.putIntegerArrayList("allMusicLoc", allMusicLocs);






            n.putStringArrayList("playListNames", playList);
            n.putIntegerArrayList("playListLoc", playListLoc);


            intent.putExtras(n);
            //intent.putExtras(n);
            //Log.d("ListInfo", playList.get(0));
            startActivity(intent); // 다음 화면으로 넘어간다
        }

        public void addPlayList(View view) {
            Intent intent = new Intent( MainActivity.this, PlayListActivity.class); // 다음 넘어갈 클래스 지정

            n = new Bundle();
            if(allMusicList.size()!=ms.musicNames.length) {
                for (int i = 0; i < ms.musicNames.length; i++) {
                    allMusicList.add(ms.musicNames[i]);
                    allMusicLocs.add(ms.musicLoc[i]);
                }}
            n.putStringArrayList("allMusic", allMusicList);
            n.putIntegerArrayList("allMusicLoc", allMusicLocs);


            playList.add(ms.musicNames[ms.pointer]);
            playListLoc.add(ms.musicLoc[ms.pointer]);
            //n.putString("playListNames", );
            n.putStringArrayList("playListNames", playList);
            n.putIntegerArrayList("playListLocs", playListLoc);


            intent.putExtras(n);
            //Log.d("ListInfo123", ms.musicNames[ms.pointer]);
            //startActivity(intent); // 다음 화면으로 넘어간다

            //************************



            // show it
            //alertDialog.show();

            //****************

            //startActivity(intent); // 다음 화면으로 넘어간다
        }

        public void clickPlay(View v) {
            ms.clickStart();
            sendNotification();
            title.setText(ms.titleBack());
        }
        public void clickPause(View v)
        {
            ms.clickPause();
        }
        public void clickStop(View v)
        {
            ms.clickStop();
        }
        public void clickNext(View v)
        {
            ms.clickNext();
            sendNotification();
            title.setText(ms.titleBack());
        }
        public void clickPrevious(View v)
        {
            ms.clickPrevious();
            sendNotification();
            title.setText(ms.titleBack());
        }
        public void clickRandom(View v)
        {
            ms.clickRandom();
            shuffle.setChecked(true);
            sendNotification();
            title.setText(ms.titleBack());
        }



    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.ServiceBinder binder= (MusicService.ServiceBinder) iBinder;
            ms = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}
