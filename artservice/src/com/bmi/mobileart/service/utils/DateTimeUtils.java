package com.bmi.mobileart.service.utils;

import java.util.Date;

/**
 * Created by Keith on 2015/4/12.
 */
public class DateTimeUtils {

    public static long diffSecond(Date start, Date end){
        return (end.getTime() - start.getTime()) / 1000;
    }
}
