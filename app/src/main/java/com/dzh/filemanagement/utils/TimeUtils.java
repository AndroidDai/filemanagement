package com.dzh.filemanagement.utils;

/**
 * Created by 38304 on 2018/2/1.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Bao on 2016/3/27.
 */
public class TimeUtils {
    /**
     * 时间戳转为时间(年月日，时分秒)
     *
     * @param cc_time 时间戳
     * @return
     */
    public static String getWorkTime(long cc_time) {
        String re_StrTime = null;
        //同理也可以转为其它样式的时间格式.例如："yyyy/MM/dd HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        // 例如：cc_time=1291778220

        re_StrTime = sdf.format(new Date(cc_time));
        return re_StrTime;
    }

}
