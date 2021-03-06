package com.example.zclass.online.Activity;

import static com.example.zclass.online.tool.BaseActivity.setrTV;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.zclass.MainActivity;
import com.example.zclass.R;
import com.example.zclass.offline.aidltest.MYyActivity;
import com.example.zclass.online.Dao.User;
import com.example.zclass.online.Dialog.Dialog_upUser;
import com.example.zclass.online.Dialog.LoadingDialog;
import com.example.zclass.online.service.HttpClientUtils;
import com.example.zclass.online.service.UpdateUser;
import com.example.zclass.online.tool.BaseActivity;
import com.example.zclass.online.tool.BitmapUtils;
import com.example.zclass.online.tool.CameraUtils;
import com.example.zclass.online.tool.SPUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG ="MyInfoActivity";

    private TextView mTVuid;
    private TextView mTVusex;
    private TextView mTVupassw;
    private TextView mTVuname;
    private TextView mTVuprofess;
    private TextView mTVuschool;
    private TextView mTVuphone;

    private final RxPermissions rxPermissions=new RxPermissions(this);
    //??????????????????
    private boolean hasPermissions = false;
    //????????????
    private BottomSheetDialog bottomSheetDialog;
    //????????????
    private View bottomView;
    //???????????????????????????
    private File outputImagePath;
    //??????????????????
    public static final int TAKE_PHOTO = 1;
    //??????????????????
    public static final int SELECT_PHOTO = 2;

    //????????????
    private ShapeableImageView ivHead;
    //Base64
    private String base64Pic;
    //??????????????????????????????Bitmap
    private Bitmap orc_bitmap;
    //Glide????????????????????????
    private RequestOptions requestOptions = RequestOptions.circleCropTransform()
            .diskCacheStrategy(DiskCacheStrategy.NONE)//??????????????????
            .skipMemoryCache(true);//??????????????????

    BottomNavigationView mNaviView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo);
        findview();
        //????????????
        String imageUrl = SPUtils.getString("imageUrl",null,this);
        if(imageUrl != null){
            Glide.with(this).load(imageUrl).apply(requestOptions).into(ivHead);
        }else {
            BaseActivity.iconDO(ivHead,MainActivity.user_info.getUserid());
        }
        checkVersion();
        mNaviView=findViewById(R.id.bottom_navigation);

        mNaviView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.page_0:
                        Intent intent4=new Intent(MyInfoActivity.this, MYyActivity.class);
                        startActivity(intent4);
                        return true;
                    case R.id.page_1:
                        Intent intent1=new Intent(MyInfoActivity.this, MainActivity.class);
                        intent1.putExtra("user",MainActivity.user_info);
                        startActivity(intent1);
                        MyInfoActivity.this.finish();
                        return true;
                    case R.id.page_2:
                        Intent intent=new Intent(MyInfoActivity.this, Class_OnlineActivity.class);
                        intent.putExtra("user",MainActivity.user_info);
                        startActivity(intent);
                        MyInfoActivity.this.finish();
                        return true;
                    case R.id.page_3:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        mNaviView.setSelectedItemId(R.id.page_3);
        super.onStart();
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
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.info_userid:
                break;
            case R.id.info_sex:
                String[] sexArry = new String[]{"??????", "??????", "??????"};// ????????????
                HashMap<String, String> stringHashMap2=new HashMap<String,String>();
                stringHashMap2.put(User.WAY,"upsex");
                //toast(User.SEX,mTVusex.getText().toString(),stringHashMap2);
                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);// ??????????????????
                builder3.setSingleChoiceItems(sexArry, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {// which?????????????????????
                        mTVusex.setText(sexArry[which]);

                        HashMap<String, String> stringHashMap=new HashMap<String,String>();
                        stringHashMap.put(User.USERID, MainActivity.user_info.getUserid());
                        stringHashMap.put(User.WAY,"upsex");
                        stringHashMap.put(User.SEX,sexArry[which]);
                        stringHashMap.put(User.METHOD,"login");

                        Dialog dialog1 = LoadingDialog.createLoadingDialog(MyInfoActivity.this);
                        dialog1.show();
                        HttpClientUtils.post(BaseActivity.BaseUrl+"LoginServlet", HttpClientUtils.maptostr(stringHashMap), new HttpClientUtils.OnRequestCallBack() {
                            @Override
                            public void onSuccess(String json) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog1.hide();
                                        if("Ok".equals(json)){
                                            Toast.makeText(getApplicationContext(), "????????????!", Toast.LENGTH_SHORT).show();
                                            MainActivity.user_info.setSex(sexArry[which]);
                                            mTVusex.setText(sexArry[which]);
                                        }else {
                                            Toast.makeText(getApplicationContext(), "????????????!",
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
                                        dialog1.hide();
                                        Toast.makeText(getApplicationContext(), "????????????!/n"+errorMsg,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        dialog.dismiss();// ??????????????????item??????????????????????????????????????????
                    }
                });
                builder3.show();// ??????????????????

                break;
            case R.id.info_passw:
                HashMap<String, String> stringHashMap3=new HashMap<String,String>();
                stringHashMap3.put(User.WAY,"uppass");
                toast(User.PASSWORD,mTVupassw.getText().toString(),stringHashMap3);
                break;
            case R.id.info_username:
                HashMap<String, String> stringHashMap4=new HashMap<String,String>();
                stringHashMap4.put(User.WAY,"upname");
                toast(User.USERNAME,mTVuname.getText().toString(),stringHashMap4);
                break;
            case R.id.info_school:
                HashMap<String, String> stringHashMap5=new HashMap<String,String>();
                stringHashMap5.put(User.WAY,"upschool");
                toast(User.SCHOOL,mTVuschool.getText().toString(),stringHashMap5);
                break;
            case R.id.info_phone:
                HashMap<String, String> stringHashMap6=new HashMap<String,String>();
                stringHashMap6.put(User.WAY,"upphone");
                toast(User.PHONENUMBER,mTVuphone.getText().toString(),stringHashMap6);
                break;
            case R.id.info_profess:
                HashMap<String, String> stringHashMap7=new HashMap<String,String>();
                stringHashMap7.put(User.WAY,"upprof");
                toast(User.PROFESS,mTVuprofess.getText().toString(),stringHashMap7);
                break;
        }
    }
    /**
     * ????????????
     *
     */
    public void changeAvatar(View view) {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomView = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
        bottomSheetDialog.setContentView(bottomView);
        bottomSheetDialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet)
                .setBackgroundColor(Color.TRANSPARENT);
        TextView tvTakePictures = bottomView.findViewById(R.id.tv_take_pictures);
        TextView tvOpenAlbum = bottomView.findViewById(R.id.tv_open_album);
        TextView tvCancel = bottomView.findViewById(R.id.tv_cancel);

        //??????
        tvTakePictures.setOnClickListener(v -> {
            takePhoto();
            bottomSheetDialog.cancel();
        });
        //????????????
        tvOpenAlbum.setOnClickListener(v -> {
            openAlbum();
            bottomSheetDialog.cancel();
        });
        //??????
        tvCancel.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
        });
        //??????????????????
        bottomSheetDialog.show();
    }

    /**
     * ??????
     */
    private void takePhoto() {
        if (!hasPermissions) {
            showMsg("??????????????????");
            checkVersion();
            return;
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        outputImagePath = new File(getExternalCacheDir(),
                filename + ".jpg");
        Intent takePhotoIntent = CameraUtils.getTakePhotoIntent(this, outputImagePath);
        // ??????????????????????????????Activity???????????????TAKE_PHOTO
        startActivityForResult(takePhotoIntent, TAKE_PHOTO);
    }

    /**
     * ????????????
     */
    private void openAlbum() {
        if (!hasPermissions) {
            showMsg("??????????????????");
            checkVersion();
            return;
        }
        startActivityForResult(CameraUtils.getSelectPhotoIntent(), SELECT_PHOTO);
    }

    /**
     * ?????????Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //???????????????
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //????????????
                    SPUtils.remove("imageUrl",this);
                    displayImage(outputImagePath.getAbsolutePath());
                    HttpClientUtils.uploadic("icon", "1.jpg"
                            , outputImagePath.getAbsolutePath(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showMsg("????????????");
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showMsg("????????????");
                                }
                            });
                        }
                    });
                }
                break;
            //?????????????????????
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    String imagePath = null;
                    //???????????????????????????
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        //4.4?????????????????????????????????????????????
                        imagePath = CameraUtils.getImageOnKitKatPath(data, this);
                    } else {
                        imagePath = CameraUtils.getImageBeforeKitKatPath(data, this);
                    }
                    //????????????
                    SPUtils.remove("imageUrl",this);
                    displayImage(imagePath);
                    HttpClientUtils.uploadic("icon", "1.jpg"
                            , imagePath, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showMsg("????????????");
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showMsg("????????????");
                                        }
                                    });
                                }
                            });
                }
                break;
            default:
                break;
        }

    }

    /**
     * ??????????????????????????????
     */
    private void displayImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {

            //????????????
            SPUtils.putString("imageUrl",imagePath,this);

            //????????????
            Glide.with(this).load(imagePath).apply(requestOptions).into(ivHead);

            //????????????
            orc_bitmap = CameraUtils.compression(BitmapFactory.decodeFile(imagePath));
            //???Base64
            base64Pic = BitmapUtils.bitmapToBase64(orc_bitmap);

        } else {
            showMsg("??????????????????");
        }
    }
    public void toast(String KEY, String text, HashMap<String,String> stringHashMap){
        HashMap<String, String> stringHashMap2=new HashMap<String,String>();
        stringHashMap2.put(User.USERID, MainActivity.user_info.getUserid());
        stringHashMap2.put(User.METHOD,"login");
        stringHashMap2.putAll(stringHashMap);

        Dialog dialog = LoadingDialog.createLoadingDialog(MyInfoActivity.this);

        Dialog_upUser Dialod_upsex = new Dialog_upUser(MyInfoActivity.this,R.style.upuser);
        Dialod_upsex.setKEY(KEY);
        Dialod_upsex.setText(text).setsubmit("LoginServlet",stringHashMap2, new Dialog_upUser.IonsaveListener() {
            @Override
            public void submit() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });
            }
            @Override
            public void success(String json) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                        if("Ok".equals(json)){
                            Toast.makeText(getApplicationContext(), "????????????!",
                                    Toast.LENGTH_SHORT).show();
                            switch (stringHashMap.get("way")){
                                case "upsex":
                                    MainActivity.user_info.setSex(Dialod_upsex.getText());
                                    mTVusex.setText(Dialod_upsex.getText());
                                    break;
                                case "uppass":
                                    MainActivity.user_info.setPassword(Dialod_upsex.getText());
                                    mTVupassw.setText(Dialod_upsex.getText());
                                    break;
                                case "upname":
                                    MainActivity.user_info.setUsername(Dialod_upsex.getText());
                                    mTVuname.setText(Dialod_upsex.getText());
                                    break;
                                case "upschool":
                                    MainActivity.user_info.setSchool(Dialod_upsex.getText());
                                    mTVuschool.setText(Dialod_upsex.getText());
                                    break;
                                case "upphone":
                                    MainActivity.user_info.setPhonenumber(Dialod_upsex.getText());
                                    mTVuphone.setText(Dialod_upsex.getText());
                                    break;
                                case "upprof":
                                    MainActivity.user_info.setProfess(Dialod_upsex.getText());
                                    mTVuprofess.setText(Dialod_upsex.getText());
                                    break;
                            }
                            Dialod_upsex.cancel();
                        }else {
                            Toast.makeText(getApplicationContext(), "????????????!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void error(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                        Toast.makeText(getApplicationContext(), "????????????!/n"+error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).show();
    }
    private void findview(){
        ivHead = findViewById(R.id.iv_head);
        BaseActivity.iconDO(ivHead,MainActivity.user_info.getUserid());

        mTVuid=findViewById(R.id.info_userid);
        setrTV(mTVuid);
        mTVuid.setText(MainActivity.user_info.getUserid());
        mTVusex=findViewById(R.id.info_sex);
        setrTV(mTVusex);
        mTVusex.setOnClickListener(this);
        mTVusex.setText(MainActivity.user_info.getSex());
        mTVupassw=findViewById(R.id.info_passw);
        setrTV(mTVupassw);
        mTVupassw.setOnClickListener(this);
        mTVupassw.setText(MainActivity.user_info.getPassword());
        mTVuname=findViewById(R.id.info_username);
        setrTV(mTVuname);
        mTVuname.setOnClickListener(this);
        mTVuname.setText(MainActivity.user_info.getUsername());
        mTVuprofess=findViewById(R.id.info_profess);
        setrTV(mTVuprofess);
        mTVuprofess.setOnClickListener(this);
        mTVuprofess.setText(MainActivity.user_info.getProfess());
        mTVuschool=findViewById(R.id.info_school);
        mTVuschool.setText(MainActivity.user_info.getSchool());
        setrTV(mTVuschool);
        mTVuschool.setOnClickListener(this);
        mTVuphone=findViewById(R.id.info_phone);
        mTVuphone.setText(MainActivity.user_info.getPhonenumber());
        setrTV(mTVuphone);
        mTVuphone.setOnClickListener(this);
    }
    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
