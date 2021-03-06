package com.example.zclass;

import static com.example.zclass.online.service.UpdateUser.update_onl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.zclass.offline.OptionActivity;
import com.example.zclass.offline.aidltest.MYyActivity;
import com.example.zclass.offline.dao.CourseDao;
import com.example.zclass.offline.pojo.Course;
import com.example.zclass.offline.view.TimeTableView;
import com.example.zclass.online.Dao.User;
import com.example.zclass.online.Activity.Class_OnlineActivity;
import com.example.zclass.online.Dialog.Dialog_Signin;
import com.example.zclass.online.Dialog.Dialog_Signup;
import com.example.zclass.online.Dialog.LoadingDialog;
import com.example.zclass.online.Activity.MyInfoActivity;
import com.example.zclass.online.service.HttpClientUtils;
import com.example.zclass.online.service.UpdateUser;
import com.example.zclass.online.tool.BaseActivity;
import com.example.zclass.online.tool.SPUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.RequiresApi;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    String TAG="MainActivity";
    private final RxPermissions rxPermissions=new RxPermissions(this);
    //??????????????????
    private boolean hasPermissions = false;
    //????????????
    public static User user_info;
    public static Boolean result=false;
    private CourseDao courseDao = new CourseDao(this);
    private TimeTableView timeTable;
    private SharedPreferences sp;

    private MediaPlayer mMediaPlayer;
    private MediaPlayer myMediaPlayer;
    private SharedPreferences saved_prefs;
    public static AssetFileDescriptor afd = null;
    private boolean SoundEnabled = true;
    private AudioManager amanager = null;
    BottomNavigationView mNaviView;
    Dialog_Signin sign_Dialog;
    Dialog_Signup signup_Dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkVersion();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        timeTable = findViewById(R.id.timeTable);
        timeTable.addListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryListener();
            }
        });
        user_info =new User();
        mNaviView=findViewById(R.id.bottom_navigation);
        mNaviView.setOnItemSelectedListener(new NavigationViewlistener());
        qd();
    }
    /**
     * ????????????
     */
    private void checkVersion() {
        //Android6.0???????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //???????????????Fragment????????????this??????getActivity()
            //????????????
            FragmentManager fragmentManager=this.getFragmentManager();
            rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {//????????????
                            //showMsg("???????????????");
                            hasPermissions=true;
                        } else {//????????????
                            showMsg("???????????????");
                            hasPermissions=false;
                        }
                    });

        } else {
            //Android6.0??????
            //showMsg("????????????????????????");
        }
    }
    class NavigationViewlistener implements NavigationBarView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent=null;
            switch (item.getItemId()){
                case R.id.page_0:
                    intent=new Intent(MainActivity.this, MYyActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.page_1:
                    return true;
                case R.id.page_2:
                    //?????????????????????
                   if(user_info.getFlag_login()==1){
                        intent=new Intent(MainActivity.this, Class_OnlineActivity.class);
                        intent.putExtra("user",user_info);
                        startActivity(intent);
                        MainActivity.this.finish();
                        return true;
                    }else {
                        login(MainActivity.this,Class_OnlineActivity.class);
                        Log.e("MainActivity","login??????");
                        return false;
                    }
                case R.id.page_3:

                    result=false;
                    if(user_info.getFlag_login()==1){
                        intent=new Intent(MainActivity.this, MyInfoActivity.class);
                        intent.putExtra("user",user_info);
                        startActivity(intent);
                        MainActivity.this.finish();

                        return true;
                    }else{
                        login(MainActivity.this,MyInfoActivity.class);
                        Log.e("MainActivity","login??????");

                        return false;
                    }
            }
            return result;
        }
    }
    public void login(Context context,Class cl){
        String url_login=BaseActivity.BaseUrl+"LoginServlet";

        sign_Dialog =new Dialog_Signin(context,R.style.upuser);
        sign_Dialog.setTitle("??????")
                .setUsername(SPUtils.getString("userid",null,context))
                .setPassword(SPUtils.getString("password",null,context))
                .setsignin("??????", new Dialog_Signin.IonsigninListener() {
                    @Override
                    public void onsignin(Dialog dialog) {

                        //???????????? ??????
                        //sign_Dialog.hide();
                        Dialog dialog_lod = LoadingDialog.createLoadingDialog(context);
                        dialog_lod.show();

                        String user_id =sign_Dialog.getUsername();
                        String user_password =sign_Dialog.getPassword();

                        user_info.setUserid(user_id);
                        user_info.setPassword(user_password);

                        if( "".equals(user_id) ||"".equals(user_password)){
                            Toast.makeText(getApplicationContext(), "????????????????????????!",
                                    Toast.LENGTH_SHORT).show();
                            sign_Dialog.show();
                            //pd.cancel();
                            dialog_lod.cancel();
                        }else {
                            SPUtils.putString("userid",user_id,context);
                            SPUtils.putString("password",user_password,context);
                            if(user_info.getFlag_login()==1){

                                //pd.cancel();
                                dialog_lod.cancel();

                                sign_Dialog.hide();
                                //?????????cl
                                user_info.setFlag_login(1);
                                Intent intent=new Intent(context, cl);
                                intent.putExtra("user",user_info);
                                startActivity(intent);
                                MainActivity.this.finish();
                                result=true;
                            }else{
                                //update_onl();
                                HashMap<String, String> stringHashMap=new HashMap<String,String>();
                                stringHashMap.put(User.USERID, user_id);
                                stringHashMap.put(User.PASSWORD, user_password);
                                stringHashMap.put(User.METHOD,"login");
                                stringHashMap.put(User.WAY,"signin");

                                Log.e(TAG,"-------------????????????----------");
                                HttpClientUtils.post(url_login, HttpClientUtils.maptostr(stringHashMap), new HttpClientUtils.OnRequestCallBack() {
                                    @Override
                                    public void onSuccess(String json) {
                                        //?????????cl
                                        if("Ok".equals(json)){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //pd.cancel();
                                                    dialog_lod.cancel();

                                                    Toast.makeText(getApplicationContext(), "????????????!",
                                                            Toast.LENGTH_SHORT).show();

                                                    sign_Dialog.hide();
                                                }
                                            });
                                            update_onl();
                                            user_info.setFlag_login(1);
                                            Intent intent=new Intent(context, cl);
                                            intent.putExtra("user",user_info);
                                            startActivity(intent);
                                            MainActivity.this.finish();
                                            result =true;
                                            Log.e("MainActivity","??????");
                                        }else{
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //pd.cancel();
                                                    dialog_lod.cancel();

                                                    Toast.makeText(getApplicationContext(), "????????????????????????!",
                                                            Toast.LENGTH_SHORT).show();
                                                    sign_Dialog.show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onError(String errorMsg) {
                                        Log.e(TAG,"-----------"+errorMsg);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //pd.cancel();
                                                dialog_lod.cancel();

                                                Toast.makeText(getApplicationContext(), "???????????????!",
                                                        Toast.LENGTH_SHORT).show();
                                                sign_Dialog.show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                }).setsignup("??????", new Dialog_Signin.IonsignupListener(){
            @Override
            public void onsignup(Dialog dialog) {
                sign_Dialog.hide();
                //?????????????????????


                signup_Dialog =new Dialog_Signup(context,R.style.upuser);
                signup_Dialog.setTitle("??????").setUserid("userid").setPassword("password")
                        .setsubmit("??????", new Dialog_Signup.IonsubmitListener() {
                            @Override
                            public void onsubmit(Dialog dialog) {

                                //??????
                                //signup_Dialog.hide();
                                Dialog dialog_lod =LoadingDialog.createLoadingDialog(context);
                                dialog_lod.show();

                                String user_id =signup_Dialog.getUserid();
                                String user_name =signup_Dialog.getUsername();
                                String user_password =signup_Dialog.getPassword();

                                user_info.setUserid(user_id);
                                user_info.setUsername(user_name);
                                user_info.setPassword(user_password);

                                if( "".equals(user_id) ||"".equals(user_password)||"".equals(user_name)){
                                    Toast.makeText(getApplicationContext(), "????????????????????????!",
                                            Toast.LENGTH_SHORT).show();
                                    signup_Dialog.show();
                                    //pd.cancel();
                                    dialog_lod.cancel();
                                }else {
                                    //update_onl();
                                    HashMap<String, String> stringHashMap=new HashMap<String,String>();
                                    stringHashMap.put(User.USERID, user_info.getUserid());
                                    stringHashMap.put(User.USERNAME,user_info.getUsername());
                                    stringHashMap.put(User.PASSWORD, user_info.getPassword());
                                    stringHashMap.put(User.METHOD,"login");
                                    stringHashMap.put(User.WAY,"signup");

                                    HttpClientUtils.post(url_login, HttpClientUtils.maptostr(stringHashMap), new HttpClientUtils.OnRequestCallBack() {
                                        @Override
                                        public void onSuccess(String json) {
                                            if("Ok".equals(json)){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog_lod.cancel();

                                                        Toast.makeText(getApplicationContext(), "????????????!",
                                                                Toast.LENGTH_SHORT).show();

                                                        signup_Dialog.hide();
                                                    }
                                                });//?????????cl
                                                update_onl();
                                                user_info.setFlag_login(1);
                                                Intent intent=new Intent(context, cl);
                                                intent.putExtra("user",user_info);
                                                startActivity(intent);
                                                MainActivity.this.finish();
                                                result =true;
                                                Log.e("MainActivity","??????");
                                            }else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog_lod.cancel();

                                                        Toast.makeText(getApplicationContext(), "??????id??????!",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                        }

                                        @Override
                                        public void onError(String errorMsg) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //pd.cancel();
                                                    dialog_lod.cancel();

                                                    Toast.makeText(getApplicationContext(), "???????????????!",
                                                            Toast.LENGTH_SHORT).show();
                                                    signup_Dialog.show();
                                                }
                                            });
                                        }
                                    });
                                }
                            }


                        }).show();
            }
        }).show();
    }


    private List<Course> acquireData() {
        List<Course> courses = new ArrayList<>();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        if (sp.getBoolean("isFirstUse", true)) {//????????????
            sp.edit().putBoolean("isFirstUse", false).apply();
        }else {
            courses = courseDao.listAll();
        }
        return courses;
    }

    /**
     * ??????
     */
    public void categoryListener() {
        Intent intent = new Intent(this, OptionActivity.class);
        startActivity(intent);
    }
    //**************************??????????????????********************************
    private void startAlarm() {
        mMediaPlayer = MediaPlayer.create(this, getSystemDefultRingtoneUri());
        mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    private void stopAlarm() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mMediaPlayer.stop();
            }
        }, 20000);

    }

    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_RINGTONE);
    }


    public void stopmusic() {
        this.saved_prefs = getSharedPreferences("RealSilent", 0);// ??????????????????????????????
        try {
            afd = getAssets().openFd("test.mp3");
            myMediaPlayer = new MediaPlayer();
            myMediaPlayer.reset();
            myMediaPlayer.setDataSource(afd.getFileDescriptor(),
                    afd.getStartOffset(), afd.getLength());
            myMediaPlayer.prepare();
            myMediaPlayer.start();
            myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    myMediaPlayer.reset();
                    try {
                        myMediaPlayer.setDataSource(afd.getFileDescriptor(),
                                afd.getStartOffset(), afd.getLength());
                    } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        myMediaPlayer.prepare();
                    } catch (IllegalStateException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    myMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        amanager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (SoundEnabled) {
            SharedPreferences.Editor localEditor = saved_prefs.edit();

            localEditor.putInt("last_media_volume", amanager.getStreamVolume(AudioManager.STREAM_MUSIC));
            localEditor.commit();
            amanager.getStreamVolume(AudioManager.STREAM_MUSIC);
            amanager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

        } else {
            int i = saved_prefs.getInt("last_media_volume", 0);
            amanager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
        }
        SoundEnabled = !SoundEnabled;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void startmusic() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int min = mAudioManager.getStreamMinVolume(AudioManager.STREAM_SYSTEM);
        int max= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        int value = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        int predict = max/2;
        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);



        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,predict,  0 );  //tempVolume:???????????????


        //mAudioManager.setStreamVolume( AudioManager.STREAM_MUSIC,10,0); //????????????

    }

    //****************************??????????????????*************************************
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void gettime() {
        OptionActivity q = new OptionActivity();
        SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
        long date = config.getLong("date", 0);
        int newday = config.getInt("day",0);
        int newmonth = config.getInt("month",0);
        int newyear = config.getInt("year",0);
        java.util.Calendar calendar1 = java.util.Calendar.getInstance();
        calendar1.set(newyear, newmonth, newday, 0, 0, 0);
        Date time1 = calendar1.getTime();

        int STU = 0;
        Calendar calendar = Calendar.getInstance();

        //?????????????????????
//???
        int year = calendar.get(Calendar.YEAR);
        //???
        int month = calendar.get(Calendar.MONTH) + 1;
        //???
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //??????????????????
//??????
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //??????
        int minute = calendar.get(Calendar.MINUTE);
        //???
        int second = calendar.get(Calendar.SECOND);
        java.util.Calendar calendar2 = java.util.Calendar.getInstance();
        calendar2.set(year, month, day, 0, 0, 0);
        Date time2 = calendar2.getTime();
         long l = (new Date().getTime() - time1.getTime()) / (1000 * 3600 * 24 * 7) + 1;


        String mYear, mMonth, mDay, mWay;
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(calendar.get(Calendar.YEAR)); // ??????????????????
        mMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);// ??????????????????
        mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));// ?????????????????????????????????
        mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            STU = 7;
        } else if ("2".equals(mWay)) {
            STU = 1;
        } else if ("3".equals(mWay)) {
            STU = 2;
        } else if ("4".equals(mWay)) {
            STU = 3;
        } else if ("5".equals(mWay)) {
            STU = 4;
        } else if ("6".equals(mWay)) {
            STU = 5;
        } else if ("7".equals(mWay)) {
            STU = 6;
        }
        //OptionActivity q = new OptionActivity();
        CourseDao c1 = new CourseDao(this);
        List<Course> cs = courseDao.query2(STU);
        String result = "";
       // Toast.makeText(MainActivity.this,""+newyear+" "+newmonth+" "+newday+"??????"+l,Toast.LENGTH_SHORT).show();
        for(Course course1 : cs)
        {
            if(course1.getDay()==STU)
            {
                /*


                Toast.makeText(MainActivity.this,"fhfg"+Integer.parseInt(course1.getWeekType()),Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this,"sdf"+course1.getWeekType(),Toast.LENGTH_SHORT).show();
                if(course1.getStartWeek()<=l) {
                    if((Integer.parseInt(course1.getWeekType()) ==2&&l%2==1)||Integer.parseInt(course1.getWeekType())==1) {
                        if((Integer.parseInt(course1.getWeekType()) ==3&&l%2==0)||Integer.parseInt(course1.getWeekType())==1) {
                            if (hour == 12 && minute == 31) {
                                startAlarm();
                                stopAlarm();
                            }
                            if (hour == 12&&minute==32)
                                stopmusic();
                            if(hour == 12 && minute == 33)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    startmusic();
                                }}}
                }
*/
                switch(course1.getSection()) {
                    case 1:
                        if (course1.getStartWeek() <= l) {
                            if ((Integer.parseInt(course1.getWeekType()) == 2 && l % 2 == 1)||Integer.parseInt(course1.getWeekType())==1) {
                                if ((Integer.parseInt(course1.getWeekType()) == 3 && l % 2 == 0)||Integer.parseInt(course1.getWeekType())==1) {
                                    if (hour == 7 && minute > 45) {
                                        startAlarm();
                                        stopAlarm();

                                    }
                                    if (hour == 8)
                                        stopmusic();
                                    if (hour == 9 && minute == 40)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                            startmusic();
                                        }
                                }
                            }
                        }
                    case 3:
                        if (course1.getStartWeek() <= l) {
                            if((Integer.parseInt(course1.getWeekType()) ==2&&l%2==1)||Integer.parseInt(course1.getWeekType())==1) {
                                if((Integer.parseInt(course1.getWeekType()) ==3&&l%2==0)||Integer.parseInt(course1.getWeekType())==1) {
                            if (hour == 9 && minute > 55) {
                                startAlarm();
                                stopAlarm();
                            }
                            if (hour == 10)
                                stopmusic();
                            if (hour == 12 && minute == 1)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    startmusic();
                                }
                        }
                }
                        }
                    case 5:
                        if(course1.getStartWeek()<=l) {
                            if ((Integer.parseInt(course1.getWeekType()) == 2 && l % 2 == 1)||Integer.parseInt(course1.getWeekType())==1) {
                                if((Integer.parseInt(course1.getWeekType()) ==3&&l%2==0)||Integer.parseInt(course1.getWeekType())==1) {
                                if (hour == 1 && minute > 45) {
                                    startAlarm();
                                    stopAlarm();
                                }
                                if (hour == 2)
                                    stopmusic();
                                if (hour == 3 && minute == 40)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                        startmusic();
                                    }
                            }
                        }
                        }
                    case 7:
                        if(course1.getStartWeek()<=l) {
                            if((Integer.parseInt(course1.getWeekType()) ==2&&l%2==1)||Integer.parseInt(course1.getWeekType())==1) {
                                if((Integer.parseInt(course1.getWeekType()) ==3&&l%2==0)||Integer.parseInt(course1.getWeekType())==1) {
                            if (hour == 3 && minute > 45) {
                                startAlarm();
                                stopAlarm();
                            }
                            if (hour == 4)
                                stopmusic();
                            if(hour == 5 && minute == 40)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    startmusic();
                                }}}
                        }
                    case 9:
                        if(course1.getStartWeek()<=l) {
                            if((Integer.parseInt(course1.getWeekType()) ==2&&l%2==1)||Integer.parseInt(course1.getWeekType())==1) {
                                if((Integer.parseInt(course1.getWeekType()) ==3&&l%2==0)||Integer.parseInt(course1.getWeekType())==1) {
                            if (hour == 6 && minute == 45) {
                                startAlarm();
                                stopAlarm();
                            }
                            if (hour == 7)
                                stopmusic();
                            if(hour == 8 && minute == 40)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    startmusic();
                                }}}
                        }






                }
            }else{

            }
        }


        //****************************??????????????????*************************************

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void alock() {
        AlarmManager alarmMgr = null;
        PendingIntent alarmIntent = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public void qd(){
        final Handler handler = new Handler();
        Thread thread =new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                gettime();
//Toast.makeText(MainActivity.this,"\n"+"??????SimpleDateFormat??????24??????????????????\n"+"sdf.format(new Date())",Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, 50000);// 50???????????????
            }
        });
        thread.setDaemon(true);
        handler.postDelayed(thread, 50000);// ??????????????????????????????
    }
    protected void onStart() {
        UpdateUser.update_dl(getIntent());
        mNaviView.setSelectedItemId(R.id.page_1);
        //??????????????????
        long date = sp.getLong("date", new Date().getTime());
        timeTable.loadData(acquireData(), new Date(date));
        Log.i("test", new Date(date).toString());
        super.onStart();
    }
    //???????????????
    //??????Do not disturb??????,????????????????????????
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onDestroy() {
        if (sign_Dialog != null) { sign_Dialog.cancel();sign_Dialog=null;}
        if (signup_Dialog != null) { signup_Dialog.cancel();signup_Dialog=null;}
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int min = mAudioManager.getStreamMinVolume(AudioManager.STREAM_SYSTEM);
        int max= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        int value = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        int predict = max/2;
        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,predict,  0 );  //tempVolume:???????????????

        super.onDestroy();
    }
    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
