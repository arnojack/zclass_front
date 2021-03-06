package com.example.zclass.online.Activity;

import static com.example.zclass.online.tool.BaseActivity.BaseUrl;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zclass.MainActivity;
import com.example.zclass.R;
import com.example.zclass.offline.aidltest.MYyActivity;
import com.example.zclass.online.Dao.Cou_Stu;
import com.example.zclass.online.Dao.Course;
import com.example.zclass.online.Dao.Msg;
import com.example.zclass.online.Dao.User;
import com.example.zclass.online.Dialog.Dialog_Creatclass;
import com.example.zclass.online.Dialog.Dialog_Joinclass;
import com.example.zclass.online.Dialog.LoadingDialog;
import com.example.zclass.online.Adapter.ClassAdapter;
import com.example.zclass.online.service.HttpClientUtils;
import com.example.zclass.online.service.JWebSocketClientService;
import com.example.zclass.online.tool.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Class_OnlineActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBt_createdclass,mBt_joinedclass,mBt_pop;
    private PopupWindow mpop;
    public ListView lv;
    private Dialog dialog_lod;

    Dialog_Joinclass joinclass;
    Dialog_Creatclass creatclass;
    BottomNavigationView mNaviView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_online);
        mBt_createdclass=findViewById(R.id.btn_CreatedClass);
        mBt_joinedclass=findViewById(R.id.btn_JoinedClass);
        mBt_pop=findViewById(R.id.btn_pop);
        mBt_createdclass.setOnClickListener(this);
        mBt_joinedclass.setOnClickListener(this);
        mBt_pop.setOnClickListener(this);
        mNaviView=findViewById(R.id.bottom_navigation);

        mNaviView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.page_0:
                        Intent intent4=new Intent(Class_OnlineActivity.this, MYyActivity.class);
                        startActivity(intent4);
                        return true;
                    case R.id.page_1:
                        Intent intent1=new Intent(Class_OnlineActivity.this,MainActivity.class);
                        intent1.putExtra("user",MainActivity.user_info);
                        startActivity(intent1);
                        Class_OnlineActivity.this.finish();
                        return true;
                    case R.id.page_2:
                        return true;
                    case R.id.page_3:
                        Intent intent=new Intent(Class_OnlineActivity.this, MyInfoActivity.class);
                        intent.putExtra("user",MainActivity.user_info);
                        startActivity(intent);
                        Class_OnlineActivity.this.finish();
                        return true;
                }
                return false;
            }
        });

        lv = (ListView) findViewById(R.id.listView1);
        dialog_lod = LoadingDialog.createLoadingDialog(Class_OnlineActivity.this);
        Mylisten_class();

        //test();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                TextView classNa=arg1.findViewById(R.id.item_title);
                TextView classId=arg1.findViewById(R.id.item_class_id);
                TextView clgrade=arg1.findViewById(R.id.item_clgrade);
                TextView class1=arg1.findViewById(R.id.item_class);
                TextView teaname=arg1.findViewById(R.id.item_bottom_left);
                TextView teasex=arg1.findViewById(R.id.item_tea_sex);
                TextView teaid=arg1.findViewById(R.id.item_tea_id);
                Intent intent =new Intent(Class_OnlineActivity.this, Chatroom.class);

                intent.putExtra(User.SEX,teasex.getText().toString());
                Chatroom.roomname=classNa.getText().toString();
                intent.putExtra(Course.COUONID,classId.getText().toString());
                intent.putExtra(Course.COUGRADE,clgrade.getText().toString());
                intent.putExtra(Course.COUCLASS,class1.getText().toString());
                intent.putExtra(Course.TEANAME,teaname.getText().toString());
                intent.putExtra(Course.TEAID,teaid.getText().toString());
                if(JWebSocketClientService.client!=null){
                    Date date=new Date(System.currentTimeMillis());
                    Msg Message=new Msg(Chatroom.roomname,MainActivity.user_info.getUserid(),MainActivity.user_info.getUsername(),
                            "onOpen",MainActivity.user_info.getUsername()+" ?????? "+Chatroom.roomname+" ??????",Msg.TYPE_SENT,date);
                    JWebSocketClientService.sendMsg(JSON.toJSONString(Message));
                }
                startActivity(intent);
            }
        });
        dialog_lod.hide();
    }

    @Override
    protected void onStart() {

        mNaviView.setSelectedItemId(R.id.page_2);
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_CreatedClass:
                //?????????????????????
                Myteach_class();
                MainActivity.user_info.setType(User.TEA);
                break;
            case R.id.btn_JoinedClass:
                //?????????????????????
                Mylisten_class();
                MainActivity.user_info.setType(User.STU);
                break;
            case R.id.btn_pop:
                //?????????
                View popview =getLayoutInflater().inflate(R.layout.class_pop,null);
                mpop =new PopupWindow(popview,250, ViewGroup.LayoutParams.WRAP_CONTENT);
                mpop.setOutsideTouchable(true);
                mpop.setFocusable(true);
                mpop.showAsDropDown(mBt_pop);
                TextView mTV_join=popview.findViewById(R.id.mtv_jion);
                mTV_join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mpop.dismiss();
                        //????????????
                        joinclass = new Dialog_Joinclass(Class_OnlineActivity.this, R.style.upuser);
                        joinclass.setTitle("????????????").setText("?????????????????????").setsubmit("??????", new Dialog_Joinclass.IonsubmitListener() {
                            @Override
                            public void onsubmit(Dialog dialog) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialog dialog1 =LoadingDialog.createLoadingDialog(Class_OnlineActivity.this);
                                        dialog1.show();

                                        if("".equals(joinclass.getText())){
                                            dialog1.hide();
                                            Toast.makeText(getApplicationContext(), "????????????????????????!",
                                                    Toast.LENGTH_SHORT).show();
                                        }else {
                                            Myjoin_class(joinclass.getText());
                                            dialog1.hide();
                                            joinclass.cancel();
                                        }
                                    }
                                });
                            }
                        }).show();
                    }
                });
                TextView mTV_create=popview.findViewById(R.id.mtv_create);
                mTV_create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mpop.dismiss();
                        //????????????
                        creatclass = new Dialog_Creatclass(Class_OnlineActivity.this,R.style.upuser);
                        creatclass.setTitle("????????????").setsubmit("??????", new Dialog_Creatclass.IonsubmitListener() {
                            @Override
                            public void onsubmit(Dialog dialog) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String cou_on_name=creatclass.getCouname();
                                        String cou_class =creatclass.getClassname();
                                        String cou_grade =creatclass.getGrade();
                                        //???????????? ??????
                                        Dialog dialog1 =LoadingDialog.createLoadingDialog(Class_OnlineActivity.this);
                                        dialog1.show();

                                        if("".equals(cou_on_name)||"".equals(cou_class)||"".equals(cou_grade)){
                                            dialog1.hide();
                                            Toast.makeText(getApplicationContext(), "????????????????????????!",
                                                    Toast.LENGTH_SHORT).show();
                                        }else {
                                            Course course =new Course();
                                            course.setCou_on_name(cou_on_name);
                                            course.setCou_class(cou_class);
                                            course.setCou_grade(cou_grade);

                                            course.setTea_userid(MainActivity.user_info.getUserid());
                                            course.setTea_name(MainActivity.user_info.getUsername());
                                            Mycreat_class(course);
                                            dialog1.hide();
                                            creatclass.cancel();
                                        }
                                    }
                                });
                            }
                        }).show();
                    }
                });
                break;
        }
    }

    @Override
    protected void onStop() {
        if(joinclass!=null)joinclass.dismiss();
        if(creatclass!=null)creatclass.dismiss();
        super.onStop();
    }

    public void Myjoin_class(String cou_on_id){
        /*???????????????HashMap????????????????????????*/
        //User user_info =(User) getIntent().getSerializableExtra("user");
        HashMap<String, String> stringHashMap=new HashMap<String,String>();
        stringHashMap.put(Cou_Stu.STUID,MainActivity.user_info.getUserid());
        stringHashMap.put(Cou_Stu.COUONID, cou_on_id);
        stringHashMap.put(Course.METHOD,"Update");
        stringHashMap.put(User.WAY,"join");
        String url=BaseUrl+"CourseServlet";
        dialog_lod.show();

        HttpClientUtils.post(url, HttpClientUtils.maptostr(stringHashMap), new HttpClientUtils.OnRequestCallBack() {
            @Override
            public void onSuccess(String json) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog_lod.hide();
                        if("Ok".equals(json)){
                            Toast.makeText(getApplicationContext(), "????????????!",
                                    Toast.LENGTH_SHORT).show();
                            Mylisten_class();
                        }else {
                            Toast.makeText(getApplicationContext(), "???????????????\n?????????????????????!" + json,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onError(String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog_lod.hide();
                        Toast.makeText(getApplicationContext(), "??????!" + errorMsg,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public void Mycreat_class(Course course){
        /*???????????????HashMap????????????????????????*/
        //User user_info =(User) getIntent().getSerializableExtra("user");
        HashMap<String, String> stringHashMap=new HashMap<String,String>();
        stringHashMap.put(Course.TEAID, course.getTea_userid());
        if(course.getTea_name()!=null)
        stringHashMap.put(Course.TEANAME, course.getTea_name());
        else
            stringHashMap.put(Course.TEANAME, "");
        stringHashMap.put(Course.COUONNAME,course.getCou_on_name());
        stringHashMap.put(Course.COUGRADE,course.getCou_grade());
        stringHashMap.put(Course.COUCLASS,course.getCou_class());
        stringHashMap.put(Course.METHOD,"Update");
        stringHashMap.put(User.WAY,"create");
        String url=BaseUrl+"CourseServlet";
        dialog_lod.show();

        HttpClientUtils.post(url, HttpClientUtils.maptostr(stringHashMap), new HttpClientUtils.OnRequestCallBack() {
            @Override
            public void onSuccess(String json) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog_lod.hide();
                        if("Ok".equals(json)){
                            Toast.makeText(getApplicationContext(), "????????????!",
                                    Toast.LENGTH_SHORT).show();
                            Mylisten_class();
                        }else {
                            Toast.makeText(getApplicationContext(), "??????!" + json,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onError(String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog_lod.hide();
                        Toast.makeText(getApplicationContext(), "??????!" + errorMsg,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public void Mylisten_class(){
        /*???????????????HashMap????????????????????????*/
        //User user_info =(User) getIntent().getSerializableExtra("user");
        HashMap<String, String> stringHashMap=new HashMap<String,String>();
        stringHashMap.put(Cou_Stu.STUID, MainActivity.user_info.getUserid());
        stringHashMap.put(Course.METHOD,"Queue");
        stringHashMap.put(User.WAY,"stuget");
        String url=BaseUrl+"CourseServlet";
        dialog_lod.show();

        HttpClientUtils.post(url, HttpClientUtils.maptostr(stringHashMap), new HttpClientUtils.OnRequestCallBack() {
            @Override
            public void onSuccess(String json) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog_lod.hide();
                        try {
                            ArrayList temp= BaseActivity.jtol_cou(json);
                            lv.setAdapter(new ClassAdapter(Class_OnlineActivity.this, temp));//???ListView???????????????
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
            @Override
            public void onError(String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog_lod.hide();
                        Toast.makeText(getApplicationContext(), "??????" + errorMsg,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public void Myteach_class(){
        /*???????????????HashMap????????????????????????*/
        //User user_info =(User) getIntent().getSerializableExtra("user");
        HashMap<String, String> stringHashMap=new HashMap<String,String>();
        stringHashMap.put(Course.TEAID, MainActivity.user_info.getUserid());
        stringHashMap.put(Course.METHOD,"Queue");
        stringHashMap.put(User.WAY,"teaget");
        String url=BaseUrl+"CourseServlet";
        dialog_lod.show();

        HttpClientUtils.post(url, HttpClientUtils.maptostr(stringHashMap), new HttpClientUtils.OnRequestCallBack() {
            @Override
            public void onSuccess(String json) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog_lod.hide();
                        try {
                            ArrayList temp=BaseActivity.jtol_cou(json);
                            lv.setAdapter(new ClassAdapter(Class_OnlineActivity.this,temp ));//???ListView???????????????
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
            @Override
            public void onError(String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog_lod.hide();
                        Toast.makeText(getApplicationContext(), "??????" + errorMsg,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        try{
            dialog_lod.dismiss();
        }catch (Exception e) {
            System.out.println("myDialog??????????????????");
            // TODO: handle exception
        }
        super.onDestroy();
    }
    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}