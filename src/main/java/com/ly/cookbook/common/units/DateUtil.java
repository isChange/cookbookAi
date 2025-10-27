package com.ly.cookbook.common.units;

import cn.hutool.core.date.DatePattern;

import com.ly.cookbook.exception.emun.AssertEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;


/**
 * Description: 时间工具类
 */
@Slf4j
public class DateUtil {

    public static String YYYY = "yyyy";
    public static String YYYY_MM = "yyyy-MM";
    public static String YYYYMMDD = "yyyyMMdd";
    public static String MM_DD_NAME = "MM月dd日";
    public static String HH_MM_SS = "HH:mm:ss";
    public static String YYYY_MM_DD_1 = "yyyy-MM-dd";
    public static String YYYY_MM_DD_2 = "yyyy/MM/dd";
    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";


    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};


    /**
     * Description: Date日期转换
     *
     * @param dateStr
     * @return Date
     */
    public static Date parseDate(String dateStr) {
        if (StringUtils.isNotBlank(dateStr)) {
            try {
                return org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, parsePatterns);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Description: 获取当前时间字符串
     */
    public static String newTimeString(){
        return newTimeString(null);
    }

    /**
     * Description: 获取当前时间字符串,默认时间格式为yyyyMMddHHmmss
     */
    public static String newTimeString(String format){
        Date date=new Date();
        if(StringUtils.isEmpty(format)){
            format= DatePattern.PURE_DATETIME_PATTERN;
        }
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Description: LocalDateTime转换
     *
     * @param localDateTimeStr
     * @param parse
     * @return LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(String localDateTimeStr, String parse) {
        if (StringUtils.isNotBlank(localDateTimeStr)) {
            if (StringUtils.isBlank(parse)) {
                for (String parsePattern : parsePatterns) {
                    try {
                        return LocalDateTime.parse(localDateTimeStr, DateTimeFormatter.ofPattern(parsePattern));
                    } catch (DateTimeParseException e) {
                    } catch (RuntimeException ex) {
                        log.error("LocalDateTime日期转换异常", ex);
                    }
                }
                AssertUtil.putMeg(AssertEnum.LOCAL_DATETIME_CONVERT_ERROR);
            } else {
                return LocalDateTime.parse(localDateTimeStr, DateTimeFormatter.ofPattern(parse));
            }
        }
        return null;
    }

    /**
     * Description: LocalDate日期转换
     *
     * @param localDateStr
     * @return LocalDate
     */
    public static LocalDate parseLocalDate(String localDateStr, String parse) {
        if (StringUtils.isNotBlank(localDateStr)) {
            if (StringUtils.isBlank(parse)) {
                for (String parsePattern : parsePatterns) {
                    try {
                        return LocalDate.parse(localDateStr, DateTimeFormatter.ofPattern(parsePattern));
                    } catch (DateTimeParseException e) {
                    } catch (RuntimeException ex) {
                        log.error("LocalDate日期转换异常", ex);
                    }
                }
                AssertUtil.putMeg(AssertEnum.LOCAL_DATE_CONVERT_ERROR);
            } else {
                return LocalDate.parse(localDateStr, DateTimeFormatter.ofPattern(parse));
            }
        }
        return null;
    }


    /**
     * 格式化 LocalDateTime
     */
    public static final String parseLocalDateTimeToStr(String format, LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern(format).format(localDateTime);
    }

    /**
     * 格式化 LocalDate
     */
    public static final String parseLocalDateToStr(String format, LocalDate localDate) {
        return DateTimeFormatter.ofPattern(format).format(localDate);
    }

    /**
     * 格式化 LocalDateTime YYYY_MM_DD_HH_MM_SS
     */
    public static final String parseLocalDateTime(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS).format(localDateTime);
    }

    /**
     * 格式化 LocalDate YYYY_MM_DD
     */
    public static final String parseLocalDate(LocalDate localDate) {
        return DateTimeFormatter.ofPattern(YYYY_MM_DD_1).format(localDate);
    }

    /**
     * 获取 LocalDateTime 当前时间
     */
    public static final String nowLocalDateTime() {
        return DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS).format(LocalDateTime.now());
    }

    /**
     * 获取 LocalDate 当前时间
     */
    public static final String nowLocalDate() {
        return DateTimeFormatter.ofPattern(YYYY_MM_DD_1).format(LocalDate.now());
    }

    /**
     * 获取最近一周时间： 从现在某个时间点计算，然后减 6 天
     */
    public static LocalDate nearOneWeek(LocalDate localDate) {
        return localDate.minusDays(6);
    }

    /**
     * 获取本月第一天
     */
    public static LocalDate firstDayOfMonth(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 近三个月： 从某个时间点计算，然后月份 减3，天数减 1
     */
    public static LocalDate nearThreeMonth(LocalDate date) {
        return date.minusMonths(3).plusDays(1);
    }

    /**
     * 时间戳转换成 LocalDateTime
     */
    public static LocalDateTime timestampToLocalDateTime(Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 计算相差多少个小时
     */
    public static Long differHour(String localDateStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(localDateStr, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
        Duration duration = Duration.between(localDateTime, LocalDateTime.now());
        return duration.toHours();
    }

}
