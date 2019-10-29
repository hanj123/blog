package com.hanj.blog.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormat {


    public static String timeFormat(Date date){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        String formatDate = simpleDateFormat.format(date);

        return formatDate;
    }

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(timeFormat(date));
    }
}
