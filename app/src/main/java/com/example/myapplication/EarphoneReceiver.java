package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class EarphoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Toast.makeText(context,
                "이어폰 연결 상태가 변경되었습니다.",
                Toast.LENGTH_LONG).show();
    }
}
