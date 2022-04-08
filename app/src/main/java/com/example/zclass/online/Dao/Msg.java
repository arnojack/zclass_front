package com.example.zclass.online.Dao;
import java.util.Date;

/*
 我们需要单例保存的数据为：
 1.消息的内容；
 2.消息的类型：发送还是接收；
 3.消息创建时间
 */
public class Msg {
    private String content;
    private String name;
    private int type;
    private String time;
    public final static int TYPE_RECEIVED=0;
    public final static int TYPE_SENT=1;
    public Msg(String content,int type){
        this.content =content;
        this.type = type;
        this.time = timeData();
    }

    public Msg() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public String getTime() {
        return time;
    }
    /*
    写一个获取时间的方法
     */
    public String timeData(){
        Date date = new Date();
        String timeData = String.format("%tH",date)
                +String.format("%tM",date)
                +String.format("%tS",date);
        return timeData;

    }
}