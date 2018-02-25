package com.example.kavya.treasuryserv;

/**
 * Created by Kavya on 06-12-2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.kavya.Project5Common.IMyAidlInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Thread.sleep;


public class MyTreasuryService extends Service {
    String url1 = "";
    String url2 = "";
    String url3 = "";
    int[] open_mo = new int[12];
    int[] open_mo_2=new int[25];
    int avg;
    public static String set_status="Service not yet bound!";

    final IMyAidlInterface.Stub binder = new IMyAidlInterface.Stub() {
        @Override
        public int[] monthlyCash(int year) throws RemoteException{
            url1 = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=" +
                    "select%20open_mo%20from(select%20open_mo,%20min(day),%20month%20from%20t1%20where%20date%20%3E%20%27" +
                    year + "-01-01%27%20AND%20date%20%3C%20%27" + year + "-12-31%27%20group%20by%20month)";

            Mcash myTask = new Mcash();

            myTask.execute(url1);

            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            status("Service Bound");
            return open_mo;
        }

        @Override
        public int[] dailyCash(int day, int month, int year, int wDays) throws RemoteException {


            url2 = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=select%20open_today%20from" +
                    "%20t1%20where%20account=%22Total%20Operating%20Balance%22%20and%20date%20%3E%20%27" + year + "-" + month + "-" + day + "%27%20limit%20" + wDays + ";";
            Dcash myTask = new Dcash();

            myTask.execute(url2);


            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            status("Service Bound");
            Log.d("openmo2",open_mo_2+"");
            return open_mo_2;
        }

        @Override
        public int yearlyAvg(int year) throws RemoteException {

            url3 = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=select%20avg(open_today)%20from%20t1%20where%20date%20%3E%20%27" + year + "-01-01%27AND%20date%20%3C%20%27" + year + "-12-31%27;";
            Avgcash myTask = new Avgcash();
            myTask.execute(url3);


            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            status("Service Bound");

            return avg;
        }
        @Override
        public String status(String x) throws RemoteException{
            set_status=x;
            return set_status;

        }
    };


    public MyTreasuryService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    private class Mcash extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            Log.d("url", strings[0]);
            String jsonStr = sh.makeServiceCall(strings[0]);
            Log.d("jsonstr", jsonStr + "");


            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray tuples = new JSONArray(jsonStr);


                    // looping through All Contacts
                    for (int i = 0; i < tuples.length(); i++) {
                        JSONObject c = tuples.getJSONObject(i);
                        open_mo[i] = c.getInt("open_mo");
                    }

                } catch (final JSONException e) {
                }
            }

            return null;
        }
    }

    private class Dcash extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            Log.d("url", strings[0]);
            String jsonStr = sh.makeServiceCall(strings[0]);
            Log.d("jsonstr", jsonStr + "");


            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray tuples = new JSONArray(jsonStr);


                    // looping through All Contacts
                    for (int i = 0; i < tuples.length(); i++) {
                        JSONObject c = tuples.getJSONObject(i);
                        open_mo_2[i] = c.getInt("open_today");
                        //Log.d("open_mo", open_mo_2[i] + "");
                    }

                } catch (final JSONException e) {
                }
            }

            return null;
        }
    }

    private class Avgcash extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            Log.d("url", strings[0]);
            String jsonStr = sh.makeServiceCall(strings[0]);
            Log.d("jsonstr", jsonStr + "");


            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray tuples = new JSONArray(jsonStr);


                    // looping through All Contacts
                    JSONObject c = tuples.getJSONObject(0);
                    avg = c.getInt("avg(open_today)");

                    Log.d("avg(open_today)", avg + "");

                } catch (final JSONException e) {
                }
            }

            return null;
        }
    }
}








