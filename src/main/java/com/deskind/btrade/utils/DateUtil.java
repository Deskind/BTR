package com.deskind.btrade.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	public static String getGMTDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		return simpleDateFormat.format(new Date());
	}
}
