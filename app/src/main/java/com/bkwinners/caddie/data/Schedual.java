package com.bkwinners.caddie.data;

import com.bkwinners.ksnet.dpt.design.util.GsonUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Schedual implements Serializable {

    public static final String INTENT_KEY_SCHEDUAL = "intent_key_schedual";

    private long day;
    private boolean isCheck = false;
    private String placeName;
    private String courseName;
    private String name;
    private int count;
    private String phoneNumber1;
    private String phoneNumber2;
    private String phoneNumber3;
    private String phoneNumber4;
    private String name1;
    private String name2;
    private String name3;
    private String name4;

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPhoneNumber1() {
        if(phoneNumber1==null||phoneNumber1.equals("null"))
            return "";
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        if(phoneNumber1!=null && phoneNumber1.length()==0){
            this.phoneNumber1 = null;
        }
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        if(phoneNumber2==null||phoneNumber2.equals("null"))
            return "";
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        if(phoneNumber2!=null && phoneNumber2.length()==0){
            this.phoneNumber2 = null;
        }
        this.phoneNumber2 = phoneNumber2;
    }

    public String getPhoneNumber3() {
        if(phoneNumber3==null||phoneNumber3.equals("null"))
            return "";
        return phoneNumber3;
    }

    public void setPhoneNumber3(String phoneNumber3) {
        if(phoneNumber3!=null && phoneNumber3.length()==0){
            this.phoneNumber3 = null;
        }
        this.phoneNumber3 = phoneNumber3;
    }

    public String getPhoneNumber4() {
        if(phoneNumber4==null||phoneNumber4.equals("null"))
            return "";
        return phoneNumber4;
    }

    public void setPhoneNumber4(String phoneNumber4) {
        if(phoneNumber4!=null && phoneNumber4.length()==0){
            this.phoneNumber4 = null;
        }
        this.phoneNumber4 = phoneNumber4;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getName4() {
        return name4;
    }

    public void setName4(String name4) {
        this.name4 = name4;
    }


    @Override
    public String toString() {
        return "{"
                + "\"day\":\"" + day + "\""
                + ", \"isCheck\":\"" + isCheck + "\""
                + ", \"placeName\":\"" + placeName + "\""
                + ", \"courseName\":\"" + courseName + "\""
                + ", \"name\":\"" + name + "\""
                + ", \"count\":\"" + count + "\""
                + ", \"phoneNumber1\":\"" + phoneNumber1 + "\""
                + ", \"phoneNumber2\":\"" + phoneNumber2 + "\""
                + ", \"phoneNumber3\":\"" + phoneNumber3 + "\""
                + ", \"phoneNumber4\":\"" + phoneNumber4 + "\""
                + ", \"name1\":\"" + name1 + "\""
                + ", \"name2\":\"" + name2 + "\""
                + ", \"name3\":\"" + name3 + "\""
                + ", \"name4\":\"" + name4 + "\""
                + "}";
    }

    public static Schedual fromJSONString(String json){
        if(json==null || json.length()==0) return null;
        try {
            Schedual schedual = (Schedual) GsonUtil.fromJson(json,Schedual.class);
            return schedual;
        }catch (Exception e){}
        return null;
    }

    public String getDayToString(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date(day));
    }
}
