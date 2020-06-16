package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service {
    static Bundle ACTION_KEY;
    MediaPlayer mp;                         // 미디어 클래스 음악재생기
    String[] musicNames = new String[5];                    // 음악 제목 리스트
    int[] musicLoc = new int[5];                         // 음악 메모리 로케이션
    int pointer = 0;                        // 몇번째 음악인지 선정
    Random rand = new Random();             // 랜덤클래스
    TextView title;
    //int ACTION_KEY;



    @Override
    public void onCreate() {
        super.onCreate();


        musicNames = getAllRawResourceNames();
        musicLoc = getAllRawResource();


        mp = MediaPlayer.create(this, musicLoc[0]);

        mp.setLooping(false);

        //**************This doesn't work yet 200612   **********************
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                clickNext();
            }
        });
        // *************************************************

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mp.stop();
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder(); //외교관 리턴- MainActivity 에서 사용 가능
    }

    class ServiceBinder extends Binder {
        //이 MusicService 객체의 참조값을
        //리턴하는 기능 메소드
        MusicService getService(){
            return MusicService.this;
        }
    }





    public String[] getMusicNames()
    {
        return(musicNames);
    }

    public void clickStart()
    {
        mp.stop();
        mp = MediaPlayer.create(this, musicLoc[pointer]);
        mp.start();
    }
    public void clickPause()
    {
        mp.pause();
    }
    public void clickStop()
    {
        mp.stop();                                                             // 로딩되어 있는 노래 멈춤
        mp = MediaPlayer.create(this, musicLoc[pointer]); // 포인터에 있는 노래를 로딩함
    }
    public void clickNext()
{
        if(mp!=null){ mp.stop(); }
        if (pointer < musicLoc.length-1) {
            pointer++;
        }
        else {pointer = 0;}
        mp = MediaPlayer.create(this, musicLoc[pointer]);
        mp.start();
        //title.setText(musicNames[pointer]);
    }
    public void clickPrevious()
    {
        if(mp!=null){ mp.stop(); }
        if (pointer > 0) {
            pointer--;
        }
        else {pointer = musicLoc.length-1;}
        mp = MediaPlayer.create(this, musicLoc[pointer]);
        mp.start();
        //title.setText(musicNames[pointer]);
    }
    public void clickRandom()
    {
        if(mp!=null){ mp.stop(); }

        //pointer = rand.nextInt(musicLoc.length);
        randomPlay();
        mp = MediaPlayer.create(this, musicLoc[pointer]);
        mp.start();
        //title.setText(musicNames[pointer]);
    }

    public String titleBack()
    {
        return musicNames[pointer];
    }




    private String[] getAllRawResourceNames() {
        Field fields[] = R.raw.class.getDeclaredFields() ;
        String[] names = new String[fields.length] ;

        try {
            for( int i=0; i< fields.length; i++ ) {
                Field f = fields[i] ;
                names[i] = f.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return names ;
    }

    private int[] getAllRawResource() {
        Field fields[] = R.raw.class.getDeclaredFields();
        int[] ids = new int[fields.length] ;

        try {
            for( int i=0; i< fields.length; i++ ) {
                ids[i] = getResources().getIdentifier(musicNames[i], "raw", getPackageName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids ;
    }

    public void randomPlay(){
        int set;
        ArrayList temp = new ArrayList();
        ArrayList randomNum = new ArrayList();
        String[] tempName = new String[musicNames.length];
        int[] tempLoc = new int[musicNames.length];

        for (int i = 0; i < musicNames.length; i++)
        {
            temp.add(i);
        }
        for (int i = 0; i < musicNames.length; i++)
        {
            set = rand.nextInt(temp.size());
            randomNum.add(temp.get(set));
            temp.remove(set);
        }
        for (int i = 0; i <musicNames.length; i++)
        {
            tempName[i] = musicNames[(int)randomNum.get(i)];
            tempLoc[i] = musicLoc[(int)randomNum.get(i)];
        }
        musicNames = tempName;
        musicLoc = tempLoc;
    }

    public void notRandomPlay(){
        musicNames = getAllRawResourceNames();
        musicLoc = getAllRawResource();
    }

    public void setToPlaylist(String[] MusicNames, int[] MusicLoc ){
        musicNames = MusicNames;
        musicLoc = MusicLoc;
    }





}

