package com.rgc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Var {
    public int id;
    public String type,val;
    Date timestamp;
    final SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Var(){
        sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    Var(int id, String type, String val, String timestamp){
        this();
        this.id = id;
        this.type = type;
        this.val = val;
        try {
            this.timestamp = sourceFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
