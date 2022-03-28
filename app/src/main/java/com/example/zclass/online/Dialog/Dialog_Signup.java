package com.example.zclass.online.Dialog;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zclass.R;

public class Dialog_Signup extends Dialog implements View.OnClickListener {

    private String title;
    private String userid;
    private String username;
    private String password;
    private String submit;
    private TextView mETuserid;
    private TextView mETusername;
    private TextView mETpassword;
    private Dialog_Signup.IonsubmitListener submitListener;

    public String getPassword() {
        mETpassword = findViewById(R.id.password);
        return mETpassword.getText().toString();
    }

    public Dialog_Signup setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUserid() {
        mETuserid = findViewById(R.id.userid);
        return mETuserid.getText().toString();
    }
    public String getUsername() {
        mETusername = findViewById(R.id.username);
        return mETusername.getText().toString();
    }

    public Dialog_Signup setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    public Dialog_Signup(@NonNull Context context, int themeResId) {
        super(context,themeResId);
    }

    public Dialog_Signup setTitle(String title) {
        this.title = title;
        return this;
    }

    public Dialog_Signup setsubmit(String submit, Dialog_Signup.IonsubmitListener Listener) {
        this.submit = submit;
        this.submitListener=Listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signup);
        /*
         * 将对话框的大小按屏幕大小的百分比设置
         */
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        Point size =new Point();
        d.getSize(size);
        p.width =(int)(size.x *0.8);//设置dialog的宽度为当前手机屏幕的宽度*0.8
        getWindow().setAttributes(p);

        TextView mTvsubmit = findViewById(R.id.submit);
        TextView mTvtitle = findViewById(R.id.title_dialog);
        mETuserid = findViewById(R.id.userid);
        mETusername=findViewById(R.id.username);
        mETpassword = findViewById(R.id.password);
        if(!TextUtils.isEmpty(title)){
            mTvtitle.setText(title);
        }
        if(!TextUtils.isEmpty(submit)){
            mTvsubmit.setText(submit);
        }
        mTvsubmit.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit:
                if(submitListener!=null){
                    submitListener.onsubmit(this);
                }
                break;
        }
    }

    public interface IonsubmitListener{
        void onsubmit(Dialog dialog);
    }
}