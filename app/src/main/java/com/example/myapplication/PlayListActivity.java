package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayListActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener{

    MyRecyclerViewAdapter adapter;
    ArrayList<String> playListList = new ArrayList<String>();
    ArrayList<String> playListNames;
    ArrayList<Integer> playListLoc;
    ArrayList<String> allMusicNames;
    ArrayList<Integer> allMusicLoc;
    RecyclerView recyclerView;
    Boolean isInPlaylist = false;
    MusicService ms = new MusicService();
    int pos = -1;
    boolean isAllSong = false;
    boolean isloaded = false;

    Button backtomain;
    Button delete;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list2);


        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, conn, 0);

        backtomain =  (Button) findViewById(R.id.BACKTOMAIN);
        delete = (Button) findViewById(R.id.DELETE);




        Bundle n = getIntent().getExtras();
        assert n != null;
        playListList.add("All Music");
        playListList.add("PlayList");


        allMusicNames = n.getStringArrayList("allMusic");
        allMusicLoc = n.getIntegerArrayList("allMusicLoc");
        isloaded = true;


        playListNames = n.getStringArrayList("playListNames");
        playListLoc = n.getIntegerArrayList("playListLoc");
//        Log.d("asd", playListNames.get(0));


        recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, playListList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);




        backtomain.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                backToMain();
            }
        });

        delete.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                deleteSong();
            }
        });

    }

    public void backToMain()
    {
        isInPlaylist = false;
        isAllSong = false;
        this.finish();
        delete.setVisibility(View.INVISIBLE);
    }

    public void deleteSong()
    {
        if(pos >= 0)
        {
            playListNames.remove(pos);
            playListNames.trimToSize();
            playListLoc.remove(pos);
            playListLoc.trimToSize();

            adapter = new MyRecyclerViewAdapter(this, playListNames);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);

        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        if(isInPlaylist == false) {
            if (position == 0) {
                adapter = new MyRecyclerViewAdapter(this, allMusicNames);
                recyclerView.setAdapter(adapter);
                adapter.setClickListener(this);
                ms.notRandomPlay();
                isInPlaylist = true;
                isAllSong = true;
            }
            if (position == 1) {
                //ms.setToPlaylist();
                adapter = new MyRecyclerViewAdapter(this, playListNames);
                recyclerView.setAdapter(adapter);
                adapter.setClickListener(this);
                isInPlaylist = true;
                isAllSong = false;
            }
        }
        else
        {
            if(isAllSong)
            {
                if (position == pos) {
                    ms.pointer = position;
                    ms.clickStart();
                    pos = -1;
                    backToMain();
                } else {
                    pos = position;
                }
            }
            else {
                delete.setVisibility(View.VISIBLE);
                adapter = new MyRecyclerViewAdapter(this, playListNames);
                recyclerView.setAdapter(adapter);
                adapter.setClickListener(this);
                if (position == pos) {
                    String[] tempNames = new String[playListNames.size()];
                    int[] tempLocs = new int[playListNames.size()];
                    for (int i = 0; i < playListNames.size(); i++) {
                        tempNames[i] = playListNames.get(i);
                        tempLocs[i] = playListLoc.get(i);
                    }
                    ms.setToPlaylist(tempNames, tempLocs);
                    ms.pointer = position;
                    ms.clickStart();
                    pos = -1;
                    backToMain();
                } else {
                    pos = position;
                }
            }
        }




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
