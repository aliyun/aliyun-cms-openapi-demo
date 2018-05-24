package com.alibaba.cms.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class TimeUtils {
    private static final Logger logger = LoggerFactory.getLogger(TimeUtils.class);

    /**
     * 得到UTC时间，类型为字符串，格式为"YYYY-MM-DDThh:mm:ssZ"
     * 如果获取失败，返回null
     *
     */
    public static String getUTCTimeStr() {
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {
            return format.format(cal.getTimeInMillis());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * @return 返回Rfc822格式的Date字符串 "EEE, dd MMM yyyy HH:mm:ss z"
     * */
    public static String formatRfc822Date(Date date) {
        SimpleDateFormat rfc822DateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return rfc822DateFormat.format(date);
    }
}
