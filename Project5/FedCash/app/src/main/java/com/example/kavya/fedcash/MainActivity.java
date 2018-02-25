package com.example.kavya.fedcash;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kavya.Project5Common.IMyAidlInterface;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MainActivity extends Activity implements View.OnClickListener {

    Spinner spin;
    int position =0;
    int a;
    IMyAidlInterface mIRemoteService;
    EditText first_year, second_date, two_days, third_year;
    TextView tv;
    Button submit1, button1;
    ArrayList<String> list = new ArrayList<String>();
    private boolean mIsBound;
    private int[] ab;
    private String status="";
    private String j="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spin = findViewById(R.id.spinner);
        first_year = findViewById(R.id.etYear1);
        third_year = findViewById(R.id.etYear3);
        second_date = findViewById(R.id.etDate2);
        two_days = findViewById(R.id.etNumber2);
        submit1 = findViewById(R.id.bSubmit1);

        tv = findViewById(R.id.test);

        //adding list with api calls to populate the spinner
        list.add("Monthly Cash");
        list.add("Daily Cash");
        list.add("Yearly Average");

        //hiding the keyboard after clicking done on keyboard
        first_year.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        //hiding the keyboard after clicking done on keyboard
        two_days.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        //hiding the keyboard after clicking done on keyboard
        third_year.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        //setting on click listener for submit button
        submit1.setOnClickListener(this);

        //creating an adapter and setting it to spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(dataAdapter);

        //on item selected listener
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                position = pos;
                //based on the item selected
                //specific widgets are hidden and other are made visible
                switch (position) {


                    case 0:
                        first_year.setVisibility(View.VISIBLE);
                        third_year.setVisibility(View.GONE);
                        second_date.setVisibility(View.INVISIBLE);
                        two_days.setVisibility(View.INVISIBLE);

                        break;
                    case 1:
                        first_year.setVisibility(View.INVISIBLE);
                        third_year.setVisibility(View.GONE);
                        second_date.setVisibility(View.VISIBLE);
                        two_days.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        first_year.setVisibility(View.INVISIBLE);
                        third_year.setVisibility(View.VISIBLE);
                        second_date.setVisibility(View.INVISIBLE);
                        two_days.setVisibility(View.INVISIBLE);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //creating an intent to bind service
        Intent intent = new Intent(IMyAidlInterface.class.getName());
        //getting package names and class names
        //ResolveInfo info= getPackageManager().resolveService(intent,Context.BIND_AUTO_CREATE);
        ResolveInfo info= getPackageManager().resolveService(intent, PackageManager.GET_META_DATA);
        intent.setComponent(new ComponentName(info.serviceInfo.packageName,info.serviceInfo.name));
        mIsBound= bindService(intent, this.mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsBound) {
            //creating an intent to bind service
            Intent intent = new Intent(IMyAidlInterface.class.getName());
            //getting package names and class names
            //ResolveInfo info= getPackageManager().resolveService(intent,Context.BIND_AUTO_CREATE);
            ResolveInfo info= getPackageManager().resolveService(intent, PackageManager.GET_META_DATA);
            intent.setComponent(new ComponentName(info.serviceInfo.packageName,info.serviceInfo.name));
            mIsBound= bindService(intent, this.mConnection, Context.BIND_AUTO_CREATE);

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this.mConnection);
    }


    void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    @Override
    public void onClick(View view) {
        //if bounded starting the worker thread
        if (mIsBound) {
            Thread t1 = new Thread(new runService(position));
            t1.start();
        }
        else if(!mIsBound){
            Toast.makeText(getApplicationContext(),"Service is not bound",Toast.LENGTH_LONG).show();
        }

    }

    //creating service connection class
    private final ServiceConnection mConnection = new ServiceConnection() {
        //when service connected getting reference of the binder
        //making mIsBound true
        public void onServiceConnected(ComponentName className, IBinder iservice) {

            mIRemoteService = IMyAidlInterface.Stub.asInterface(iservice);

            mIsBound = true;

        }

        //when service is disconnected
        //making service null and mIsBound flase
        public void onServiceDisconnected(ComponentName className) {

            mIRemoteService = null;

            mIsBound = false;

        }
    };

    //worker thread for making api calls
    class runService implements Runnable {
        int p;
        //needs the position of which api call was clicked
        runService(int pos){
            p=pos;
        }

        @Override
        public void run() {
            //if service is bound
            if(mIsBound){
                try {


                    switch (p) {
                        case 0:
                            String result1="";

                            final int s1 =Integer.parseInt(first_year.getText().toString());
                            //year should be between 2006 and 2016
                            if(s1>2005 && s1<2017) {
                                //making api call
                                ab = mIRemoteService.monthlyCash(s1);
                                String status1 = "Service bound and running";
                                final String jaa =mIRemoteService.status(status1);
                                //appending all the values to a string
                                for (int i = 0; i < ab.length; i++) {
                                    result1 += ab[i] + "\n";

                                }
                                final String finalResult = result1;
                                //using UI thread to start intent and sending the results
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {


                                        Intent i = new Intent(MainActivity.this, ResultActivity.class);
                                        i.putExtra("input", "MonthlyCash " + s1);
                                        i.putExtra("out", "MonthlyCashResult " + finalResult);
                                        startActivity(i);
                                    }
                                });
                            }
                            else{
                                //if invalid values entered making a toast
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Please give the year b/w 2006 and 2016",Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                            break;
                        case 1:
                            final int[] b;
                            String ja="";
                            final String[] date;
                            String result2 = "";
                            //getting user input
                            final String s2 = second_date.getText().toString();
                            final String s3 = two_days.getText().toString();
                            int day, month, year, wdays;
                            //using split to get individual values from entire date
                            date=s2.split("/");

                            day = Integer.parseInt(date[1]);
                            month = Integer.parseInt(date[0]);
                            year = Integer.parseInt(date[2]);
                            wdays = Integer.parseInt(s3);
                            //validating date
                            if(year>2005 && year<2017 && month>0 && month<13 && day>0 && day<31 && wdays>4 && wdays<26) {
                                //making the api call
                                b = mIRemoteService.dailyCash(day, month, year, wdays);
                                status = "Service bound and running";
                                ja=mIRemoteService.status(status);
                                //appending all the values to a string
                                for (int i = 0; i < b.length; i++) {
                                    result2 += b[i] + " ";

                                }
                                final String jaa=ja;
                                final String finalResult2 = result2;
                                //using ui thread to start intent and send values
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Intent i = new Intent(MainActivity.this, ResultActivity.class);
                                        i.putExtra("input", "DailyCash  " + s2 + " " + s3);
                                        i.putExtra("out", "DailyCashResult " + finalResult2);
                                        startActivity(i);
                                    }
                                });
                            }
                            else{
                                //if invalid values entered making a toast
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Please input correct date b/w 2006 and 2016",Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                            break;
                        case 2:
                            final int c;
                            final int s4= Integer.parseInt(third_year.getText().toString());
                            //validating input
                            if(s4>2005 && s4<2017) {

                                c = mIRemoteService.yearlyAvg((s4));
                                status = "Service bound and running";
                                j =mIRemoteService.status(status);
//                                try {
//                                    j =mIRemoteService.status(status);
//                                    Toast.makeText(getApplicationContext(), "service is bound", Toast.LENGTH_SHORT).show();
//                                } catch (Exception e) {
//                                    Log.i("j_value",String.valueOf(j));
//                                    e.printStackTrace();
//                                }
                                //using ui thread to create intent and send results
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Intent i = new Intent(MainActivity.this, ResultActivity.class);
                                        i.putExtra("input", "YearlyAverage  " + s4);
                                        i.putExtra("out", "YearlyAverageResult " + c);
                                        startActivity(i);
                                    }
                                });
                            }else{
                                //if invalid values entered making a toast
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Please enter correct input b/w 2006 and 2016",Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

                            break;

                    }
                }catch (RemoteException re){}
            }

            else{
            }

        }
    }

}