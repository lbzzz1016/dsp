package com.ruoyi.common.helper;

import com.ruoyi.common.utils.DateUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

/**
 * @author LBZ
 * @create 2022/9/16 - 10:34
 */

/**
 * 时间计算逻辑
 * 1、时间小于8:30，从8:30开始
 * 2、结束时间大于17:30，算到17:30
 * 3、开始时间-结束时间,跨越12:00-13:30午休时间
 * 4、按照一天8小时折算
 * 5、不满半小时按照半小时算
 */


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessHoursHelper {

    //计算开始时间与结束时间小时差，工作小时制
    public static double countHours(Date starTime, Date endTime) {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(starTime);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(endTime);

        //开始时间
        int startYear = calendarStart.get(Calendar.YEAR);
        int startMonth = calendarStart.get(Calendar.MONTH);
        int startDate = calendarStart.get(Calendar.DATE);
        int startHour = calendarStart.get(Calendar.HOUR_OF_DAY);
        int startMinute = calendarStart.get(Calendar.MINUTE);

        //结束时间
        int endYear = calendarEnd.get(Calendar.YEAR);
        int endMonth = calendarEnd.get(Calendar.MONTH);
        int endDate = calendarEnd.get(Calendar.DATE);
        int endHour = calendarEnd.get(Calendar.HOUR_OF_DAY);
        int endMinute = calendarEnd.get(Calendar.MINUTE);

        //endTime =
        int diffDay = (int)Math.floor((endTime.getTime() - starTime.getTime())/(1000 * 24 * 60 * 60));   //间隔天
        double flag = (endTime.getTime() - starTime.getTime()) / (1000 * 3600 * 24.0);
        System.out.println(flag);
        int diffHours = 0;    //间隔小时
        int diffMinutes = 0;    //间隔分钟

        //开始时间在午休时间，以13:00为起
        if (startHour >= 12 && startHour < 13) {
            startHour = 12;
            startMinute = 0;
        }
        //结束时间在午休时间，以13:00为止
        if (endHour >= 12 && endHour < 13) {
            endHour = 12;
            endMinute = 0;
        }

        //开始时间早于8:30，从8:30起算
        if (startHour < 8 || (startHour == 8 && startMinute < 30)) {
            startHour = 8;
            startMinute = 30;
        }
        //结束时间大于17:30，到17:30为止
        if (endHour > 17 || (endHour == 17 && endMinute > 30)) {
            endHour = 17;
            endMinute = 30;
        }


        //结束分钟<开始分钟，向小时借
        if (endMinute < startMinute) {
            endMinute += 60;
            endHour--;
        }
        diffMinutes = endMinute - startMinute;

        System.out.println(diffMinutes);

        if (endHour < startHour) {
            diffDay += 1;
            if (0.0 < flag && flag < 1.0) {
                diffHours += 1;
            }
        }

        if (diffDay > 1) {
            //跨天
            diffHours = 17 - startHour + endHour - 8;
            //如果开始时间小于12点，请假小时数-1
            diffHours = startHour <= 12 ? diffHours - 1 : diffHours;
            //如果开始时间大于13点，请假小时数-1
            diffHours = endHour >= 13 ? diffHours - 1 : diffHours;
            System.out.println(diffHours);
        } else {
            //不跨天
            //开始时间-结束时间跨越午休时间，间隔小时-1
            diffHours = startHour <= 12 && endHour >= 13 ? diffHours + endHour - startHour - 1 : diffHours + endHour - startHour;
            System.out.println(diffHours);
        }
        System.out.println(diffDay);
        double diff = (diffDay > 1 ? diffDay - 1 : diffDay) * 8 + diffHours + diffMinutes / 60.0;
        double currentHours = diff;

        double floor = Math.floor(diff);
        if (floor + 0.5 < currentHours) {
            currentHours = floor + 1;
        } else if (floor + 0.5==currentHours + 0.5){
            currentHours = floor;
        }else {
            currentHours = floor + 0.5;
        }

        return currentHours;
    }

    public static void main(String[] args) {
        ProcessHoursHelper processHoursHelper = new ProcessHoursHelper();
        Date startTime = DateUtils.parseStringToDate("2022-09-16 16:30");
        Date endTime = DateUtils.parseStringToDate("2022-09-17 11:00");
        System.out.println(processHoursHelper.countHours(startTime, endTime));
    }
}
