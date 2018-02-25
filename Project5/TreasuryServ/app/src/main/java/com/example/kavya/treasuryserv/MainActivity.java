package com.example.kavya.treasuryserv;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.kavya.Project5Common.IMyAidlInterface;

import static com.example.kavya.treasuryserv.MyTreasuryService.set_status;
import static java.lang.Thread.sleep;

public class MainActivity extends Activity {

//    TextView tv;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        tv = findViewById(R.id.tv);
//
//        while (true) {
//            try {
//                sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if (isMyServiceRunning(MyTreasuryService.class)) {
//                tv.setText("Service is running");
//
//            } else
//                tv.setText("Service is not running");
//
//        }
//    }
//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        txt=findViewById(R.id.textview);

    }

    @Override
    protected void onResume() {

        super.onResume();
        //txt=findViewById(R.id.textview);
        txt.setText(set_status);

    }

    @Override
    protected void onDestroy() {
        //txt=findViewById(R.id.textview);
        txt.setText("Service connection destroyed");
        super.onDestroy();
    }
}
