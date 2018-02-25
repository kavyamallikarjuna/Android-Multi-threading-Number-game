package com.example.kavya.myproject4;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import static java.lang.Thread.sleep;

public class FirstPage extends AppCompatActivity {

    //ArrayAdapter<String> arrayAdapter;
    static final int GENERATE_RANDOM_VALUE = 1;
    static final int RESULT_FOR_THREAD1 = 2;
    static final int RESULT_FOR_THREAD2 = 8;
    static final int GENERATE_GUESS_CODE = 3;
    static final int GIVE_RESULT = 4;
    static final int DISPLAY_WINNER = 5;
    static final int RECEIVE_GUESS = 6;
    static final int RECEIVE_EVAL = 7;
    static List<Integer> workthread1 = new ArrayList<>();
    static List<Integer> workthread2 = new ArrayList<>();
    static int[] a = new int[4];
    static int[] a2 = new int[4];
    TextView textview1guess;
    TextView textview2guess;
    Button restart;
    ListView simpleListView, simpleListView1;
    int response1fromUI[] = new int[4];
    int response2fromUI[] = new int[4];
    int guess_fromThread1[] = new int[4];
    int guess_fromThread2[] = new int[4];
    Bundle UIforThread2, UIforThread1;
    Handler PlayerOneHandler, PlayerTwoHandler;
    TextView textviewResult;
    int ka=0;
    String s1 = "";
    String s2 = "";
    String s3 = "";
    Thread thread1;
    Thread thread2;
    int token;
    int maxtry=40;

    List<String> lists2 = new ArrayList<>();
    List<String> lists1 = new ArrayList<>();
    static final Object lock = new Object();

    private Handler UIHandler = new Handler() {
        public int flag1, flag2;

        public void handleMessage(Message msg) {
            synchronized (lock) {
                int what = msg.what;
                switch (what) {
                    //Inserting the random value to textviews
                    case GENERATE_RANDOM_VALUE:
                        if (msg.arg1 == 1)
                            textview1guess.setText((String) msg.obj);
                        else if (msg.arg1 == 2)
                            textview2guess.setText((String) (msg.obj));

                        break;

                    //Getting the bundle from worker thread and passing it back to the thread
                    case RESULT_FOR_THREAD1:
                        Bundle b = msg.getData();
                        String s = "Player Thread2 guessed ", Guesscodetoui = "";

                        s += b.getString("key");
                        Guesscodetoui += b.getString("key");
                        s += " ,";
                        int[] res = (int[]) msg.obj;
                        flag1 = 1;
                        for (int i = 0; i < 4; i++) {
                            if (res[i] != 1)
                                flag1 = 0;
                            s += res[i];
                            response1fromUI[i] = res[i];
                            guess_fromThread1[i] = Integer.parseInt(Guesscodetoui.substring(i, i + 1));
                        }
                        String[]words=s.split(",");
                        String word=words[1];
                        int counter = 0;
                        int counter1=0;
                        int counter4=0;
                        for( int i=0; i<word.length(); i++ ) {
                            if(word.charAt(i) == '-') {
                                counter++;
                            }
                        }
                        for( int i=0; i<word.length(); i++ ) {
                            if(word.charAt(i) == '1') {
                                counter1++;
                            }
                        }
                        counter=counter1-counter;
                        for( int i=0; i<word.length(); i++ ) {
                            if(word.charAt(i) == '0') {
                                counter4++;
                            }
                        }

                        Log.i("Correct positions1",String.valueOf(counter));
                        s+=", number of correct positions guessed:";
                        s+=counter;
                        s+=", number of wrong positions guessed:";
                        s+=counter4;

                        if (msg.arg2 == 1 && flag1 == 0 && word.charAt(0) != '3' && !lists1.contains(s)) {

                            lists1.add(s);
                        }
                        for( int i=0; i<word.length(); i++ ) {
                            if(word.charAt(i) == '3') {
                                flag1=5;
                                thread1.interrupt();
                                thread2.interrupt();
                                UIHandler.removeCallbacksAndMessages(null);
                                PlayerOneHandler.removeCallbacksAndMessages(null);
                                PlayerTwoHandler.removeCallbacksAndMessages(null);
                                PlayerOneHandler.getLooper().quit();
                                PlayerTwoHandler.getLooper().quit();
                                textviewResult.setText("Maximum tries finished");
                                break;
                            }
                            break;
                        }
                        if (flag1 == 0 && msg.arg2 == 1) {

                            try {
                                sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Message T2msg = PlayerTwoHandler.obtainMessage(RECEIVE_EVAL);
                            UIforThread2.putIntArray("response", response1fromUI);
                            UIforThread2.putIntArray("guess", guess_fromThread1);
                            T2msg.setData(UIforThread2);
                            PlayerTwoHandler.sendMessage(T2msg);
                        } else if (flag1 == 1 && msg.arg2 == 1 && word.charAt(0) != '3' && !lists1.contains(s)) {
                            try {
//                                textviewResult.setText(s + "success");
                                sleep(2000);
                                lists1.add(s);
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_thread1, R.id.list_item, lists1);
                                simpleListView1.setAdapter(arrayAdapter);
                                thread1.interrupt();
                                thread2.interrupt();
                                UIHandler.removeCallbacksAndMessages(null);
                                PlayerOneHandler.removeCallbacksAndMessages(null);
                                PlayerTwoHandler.removeCallbacksAndMessages(null);
                                PlayerOneHandler.getLooper().quit();
                                PlayerTwoHandler.getLooper().quit();
                                textviewResult.setText("PLAYER 2 WINS!!!");
                                break;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }

                        if (msg.arg2 == 1 && flag1 == 0 && word.charAt(0) != '3' && lists1.contains(s)) {

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_thread1, R.id.list_item, lists1);
                            simpleListView1.setAdapter(arrayAdapter);
                        }
                        break;

                    //Getting the bundle from worker thread and passing it back to the thread
                    case RESULT_FOR_THREAD2:
                        Bundle b2 = msg.getData();
                        String s2 = "Player Thread1 guessed ", Guesscodetoui2 = "";

                        s2 += b2.getString("key");
                        Guesscodetoui2 += b2.getString("key");
                        s2 += " ,";
                        int[] res2 = (int[]) msg.obj;
                        flag2 = 1;
                        for (int i = 0; i < 4; i++) {
                            if (res2[i] != 1)
                                flag2 = 0;
                            s2 += res2[i];
                            response2fromUI[i] = res2[i];
                            guess_fromThread2[i] = Integer.parseInt(Guesscodetoui2.substring(i, i + 1));
                        }
                        String[]words1=s2.split(",");
                        String word1=words1[1];
                        int counter2 = 0;
                        int counter3=0;
                        int counter5=0;
                        for( int i=0; i<word1.length(); i++ ) {
                            if(word1.charAt(i) == '-') {
                                counter2++;
                            }
                        }
                        for( int i=0; i<word1.length(); i++ ) {
                            if(word1.charAt(i) == '1') {
                                counter3++;
                            }
                        }
                        counter2=counter3-counter2;

                        for( int i=0; i<word1.length(); i++ ) {
                            if(word1.charAt(i) == '0') {
                                counter5++;
                            }
                        }

                        Log.i("Correct positions2",String.valueOf(counter2));
                        s2+=", number of correct positions guessed:";
                        s2+=counter2;
                        s2+=", number of wrong positions guessed:";
                        s2+=counter5;

                        if (msg.arg2 == 2 && flag2 == 0 && word1.charAt(0) != '3'  && !lists2.contains(s2)) {

                            lists2.add(s2);
                        }
                        for( int i=0; i<word1.length(); i++ ) {
                            if(word1.charAt(i) == '3') {
                                flag2=5;
                                thread1.interrupt();
                                thread2.interrupt();
                                UIHandler.removeCallbacksAndMessages(null);
                                PlayerOneHandler.removeCallbacksAndMessages(null);
                                PlayerTwoHandler.removeCallbacksAndMessages(null);
                                PlayerOneHandler.getLooper().quit();
                                PlayerTwoHandler.getLooper().quit();
                                textviewResult.setText("Maximum tries finished");
                                break;
                            }
                            break;
                        }

                        if (flag2 == 0 && msg.arg2 == 2) {

                            try {
                                sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Message T1msg = PlayerOneHandler.obtainMessage(RECEIVE_EVAL);
                            UIforThread1.putIntArray("response", response2fromUI);
                            UIforThread1.putIntArray("guess", guess_fromThread2);
                            T1msg.setData(UIforThread1);
                            PlayerOneHandler.sendMessage(T1msg);
                        } else if (flag2 == 1 && msg.arg2 == 2 && word1.charAt(0) != '3'  && !lists1.contains(s2)) {
                            try {
//
                                sleep(2000);
                                lists2.add(s2);
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_thread2, R.id.list_item2, lists2);
                                simpleListView.setAdapter(arrayAdapter);
                                arrayAdapter.notifyDataSetChanged();
                                thread1.interrupt();
                                thread2.interrupt();
                                UIHandler.removeCallbacksAndMessages(null);
                                PlayerOneHandler.removeCallbacksAndMessages(null);
                                PlayerTwoHandler.removeCallbacksAndMessages(null);
                                PlayerOneHandler.getLooper().quit();
                                PlayerTwoHandler.getLooper().quit();
                                textviewResult.setText("PLAYER 1 WINS!!!");
                                break;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                        if (msg.arg2 == 2 && flag2 == 0 && word1.charAt(0) != '3'  && lists2.contains(s2)) {

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_thread2, R.id.list_item2, lists2);
                            simpleListView.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();
                        }
                        break;

                }
            }
        }
    };

    //On create method of the main activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        textview1guess = (TextView)findViewById(R.id.textView);
        textview2guess = (TextView)findViewById(R.id.textView2);
        textviewResult = (TextView)findViewById(R.id.textView3);
        simpleListView = (ListView)findViewById(R.id.simpleListView);;
        simpleListView1 = (ListView)findViewById(R.id.simpleListView1);;
        UIforThread2 = new Bundle();
        UIforThread1 = new Bundle();
        GameStart();
        restart=(Button)findViewById(R.id.restart);

        restart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                thread1 = new Thread(new Player1());
                thread2 = new Thread(new Player2());
                if(PlayerOneHandler!=null){
                    thread1.interrupt();
                    thread2.interrupt();
                    PlayerOneHandler.getLooper().quit();
                    PlayerOneHandler.removeCallbacksAndMessages(token);
                }
                if(PlayerTwoHandler!=null){
                    thread1.interrupt();
                    thread2.interrupt();
                    PlayerTwoHandler.getLooper().quit();
                    PlayerTwoHandler.removeCallbacksAndMessages(token);
                }
                if(UIHandler!=null){

                    UIHandler.removeCallbacksAndMessages(token);
                }
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

    }
    //Function for starting the game
    public void GameStart() {

        textview1guess.setText("");
        textview2guess.setText("");
        thread1 = new Thread(new Player1());
        thread2 = new Thread(new Player2());
        thread1.start();
        thread2.start();

        for (int i = 0; i < 10; i++) {
            workthread1.add(i);
            workthread2.add(i);
        }


        for (int j = 0; j < 4; j++) {
            a[j] = -1;
            a2[j] = -1;
        }

    }
    //Function to create random number
    public String createRandomNumber(){
        List<Integer> numbers = new ArrayList<>();
        s3="";
        for (int i = 0; i <= 9; i++)
            numbers.add(i);
        Collections.shuffle(numbers);
        for (int i = 0; i <= 3; i++)
            s3 += numbers.get(i);
        return s3;
    }

    //Comparing the guesscode with the random number
    private int[] compareString(String secretCode, String guessedCode) {
        String c1 = secretCode;
        String c2 = guessedCode;
        int[] res = new int[c1.length()];
        if(ka<maxtry) {

            Log.i("secretCode", secretCode);
            Log.i("guessedCode", guessedCode);
            for (int i = 0; i < c1.length(); i++) {
                if (c1.charAt(i) == c2.charAt(i)) {
                    res[i] = 1;


                } else if (secretCode.indexOf(c2.charAt(i)) >= 0 && secretCode.indexOf(c2.charAt(i)) < c2.length()) {
                    res[i] = 0;


                } else {
                    res[i] = -1;

                }
            }
            ka++;
        }
        else{
            res[0]=3;
            res[1]=3;
            res[2]=3;
            res[3]=3;
        }
        return res;
    }

    //Function to guess with positions for thread1
    int[] toGuesswithPositions_forthread1(int[] arr, int[] last) {
        int[] temp = new int[4];

        for (int i = 0; i <= 3; i++) {
            if (arr[i] == 1) {
                a2[i] = last[i];
                workthread2.remove((Integer) (last[i]));
            } else if (arr[i] == -1) {
                workthread2.remove((Integer) last[i]);
            }
        }
        if (workthread2.isEmpty())
            return a2;
        Collections.shuffle(workthread2);
        int ctr = 0;

        for (int i = 0; i <= 3; i++) {
            if (a2[i] != -1)
                temp[i] = a2[i];
            else {
                temp[i] = workthread2.get(ctr);
                Log.i("ctr1",String.valueOf(ctr));
                ctr++;
            }
        }

        return temp;
    }
    //Function to guess with positions for thread2
    int[] toGuesswithPositions_forthread2(int[] arr, int[] last) {

        int[] temp = new int[4];


        for (int i = 0; i <= 3; i++) {
            if (arr[i] == 1) {
                a[i] = last[i];
                workthread1.remove((Integer) (last[i]));
            } else if (arr[i] == -1) {
                workthread1.remove((Integer) last[i]);
            }

        }
        if (workthread1.isEmpty())
            return a;
        Collections.shuffle(workthread1);

        Random random = new Random();
        int ctr = 0;

        //int[] temp = new int[4];
        for (int i = 0; i <= 3; i++) {
            if (a[i] != -1)
                temp[i] = a[i];
            else {
                Log.i("ctr2", String.valueOf(ctr));
                temp[i] = random.nextInt(9-0) + 0;

            }
        }

        return temp;
    }

    //Player1 runnable
    public class Player1 implements Runnable {
        int result1[] = new int[4];
        int next1[] = new int[4];
        int guess_fromThread1[] = new int[4];
        private Bundle bundle_RG = new Bundle();
        private Bundle bundle_RE = new Bundle();

        public void run() {

            Looper.prepare();
            PlayerOneHandler = new Handler() {
                public void handleMessage(Message msg) {
                    int what = msg.what;
                    switch (what) {
                        //For receiving guess
                        case RECEIVE_GUESS:
                            synchronized (this) {
                                int[] res = compareString(s1, (String) msg.obj);

                                Message msgUI1 = UIHandler.obtainMessage(RESULT_FOR_THREAD1);

                                msgUI1.obj = res;
                                msgUI1.arg2 = 1;

                                bundle_RG.putString("key", (String) msg.obj);

                                msgUI1.setData(bundle_RG);
                                UIHandler.sendMessage(msgUI1);

                            }
                            break;
                        //For receiving evaluations
                        case RECEIVE_EVAL:
                            synchronized (this) {
                                bundle_RE = msg.getData();
                                result1 = bundle_RE.getIntArray("response");
                                guess_fromThread1 = bundle_RE.getIntArray("guess");
                                next1 = toGuesswithPositions_forthread1(result1, guess_fromThread1);

                                String s = "";
                                for (int i : next1)
                                    s += i;
                                msg = PlayerTwoHandler.obtainMessage(RECEIVE_GUESS);
                                msg.obj = s;
                                PlayerTwoHandler.sendMessage(msg);
                                break;
                            }
                    }
                }
            };
            Message msg = UIHandler.obtainMessage(GENERATE_RANDOM_VALUE);
            List<Integer> numbers = new ArrayList<>();

            //For generating random numbers
            s1 = createRandomNumber();
            msg.obj = s1;
            msg.arg1 = 1;
            UIHandler.sendMessage(msg);

            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //For sending the message to another thread
            msg = PlayerTwoHandler.obtainMessage(RECEIVE_GUESS);
            String s5=createRandomNumber();
            msg.obj = s5;
            PlayerTwoHandler.sendMessage(msg);
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Looper.loop();
        }

    }

    //Player2 runnable
    public class Player2 implements Runnable {

        int result2[] = new int[4];
        int next2[] = new int[4];
        int guess_fromThread2[] = new int[4];
        private Bundle bundle2_RE = new Bundle();
        private Bundle bundle2_RG = new Bundle();

        public void run() {

            Looper.prepare();

            PlayerTwoHandler = new Handler() {
                public void handleMessage(Message msg) {
                    int what = msg.what;
                    switch (what) {
                        //For receiving guess
                        case RECEIVE_GUESS:
                            synchronized (this) {
                                int[] res = compareString(s2, (String) msg.obj);
                                Message msgUI2 = UIHandler.obtainMessage(RESULT_FOR_THREAD2);
                                msgUI2.obj = res;
                                msgUI2.arg2 = 2;
                                bundle2_RG.putString("key", (String) msg.obj);

                                msgUI2.setData(bundle2_RG);
                                UIHandler.sendMessage(msgUI2);

                                break;
                            }
                            //For receiving eval
                        case RECEIVE_EVAL:
                            synchronized (this) {
                                bundle2_RE = msg.getData();
                                result2 = bundle2_RE.getIntArray("response");
                                guess_fromThread2 = bundle2_RE.getIntArray("guess");
                                next2 = toGuesswithPositions_forthread2(result2, guess_fromThread2);

                                String s = "";
                                for (int i : next2)
                                    s += i;
                                msg = PlayerOneHandler.obtainMessage(RECEIVE_GUESS);
                                msg.obj = s;
                                PlayerOneHandler.sendMessage(msg);
                                break;
                            }
                    }

                }
            };

            Message msg = UIHandler.obtainMessage(GENERATE_RANDOM_VALUE);
            List<Integer> numbers = new ArrayList<>();
            //For random number generation
            s2="";
            for (int i = 0; i <= 9; i++)
                numbers.add(i);
            Collections.shuffle(numbers);
            for (int i = 0; i <= 3; i++)
                s2 += numbers.get(i);
            msg.obj = s2;
            msg.arg1 = 2;
            UIHandler.sendMessage(msg);


            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            msg = PlayerOneHandler.obtainMessage(RECEIVE_GUESS);
            String s4=createRandomNumber();
            msg.obj=s4;
            PlayerOneHandler.sendMessage(msg);
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Looper.loop();
        }


    }
}