package com.oilpalm3f.mainapp.common;

import com.oilpalm3f.mainapp.utils.DateFormats;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by siva on 04/09/17.
 */

//To get the Financial Years date
public class FiscalDate {

    private static final int FIRST_FISCAL_MONTH  = Calendar.APRIL;

    private Calendar  calendarDate;

    public FiscalDate(Calendar calendarDate) {
        this.calendarDate = calendarDate;
    }

    public FiscalDate(Date date) {
        this.calendarDate = Calendar.getInstance();
        this.calendarDate.setTime(date);
    }

    public int getFiscalMonth() {
        int month = calendarDate.get(Calendar.MONTH);
        int result = ((month - FIRST_FISCAL_MONTH - 1) % 12) + 1;
        if (result < 0) {
            result += 12;
        }
        return result;
    }

    public int getFiscalYear() {
        int month = calendarDate.get(Calendar.MONTH);
        int year = calendarDate.get(Calendar.YEAR);
        return (month >= FIRST_FISCAL_MONTH) ? year : year - 1;
    }

    public int getCalendarMonth() {
        return calendarDate.get(Calendar.MONTH);
    }

    public int getCalendarYear() {
        return calendarDate.get(Calendar.YEAR);
    }

    public void main() {
        displayFinancialDate(Calendar.getInstance());
        displayFinancialDate(setDate(2013, 1, 1));
        displayFinancialDate(setDate(2012, 6, 25));
    }

    private static Calendar setDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    private static Calendar setDay(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_YEAR, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }
    public void displayFinancialDate(Calendar calendar) {
        FiscalDate fiscalDate = new FiscalDate(calendar);
        int year = fiscalDate.getFiscalYear();
        System.out.println("##### Current Date : " + calendar.getTime().toString());
        System.out.println("##### Fiscal Years : " + year + "-" + (year + 1));
        System.out.println("##### Fiscal Month : " + fiscalDate.getFiscalMonth());
        System.out.println(" ");
    }

    public String getFinancialYear(Calendar calendar) {
        FiscalDate fiscalDate = new FiscalDate(calendar);
        int year = fiscalDate.getFiscalYear();
        return  year + "-" + (year + 1);
    }

    public String financialYearStartMonth(Calendar calendar) {
        FiscalDate fiscalDate = new FiscalDate(calendar);
        int year = fiscalDate.getFiscalYear();
        Calendar convertedCalendar = setDate(year, 3, 1);
        return DateFormats.getDate(convertedCalendar.getTime());
    }

    public String financialYearDay(Calendar calendar){
        FiscalDate fiscalday = new FiscalDate(calendar);
        int year = fiscalday.getFiscalYear();
        Calendar convertedCalendar = setDay(year,3,1);
        return DateFormats.getDate(convertedCalendar.getTime());
    }

    public String financialYearEndMonth(Calendar calendar) {
        FiscalDate fiscalDate = new FiscalDate(calendar);
        int year = fiscalDate.getFiscalYear();
        Calendar convertedCalendar = setDate(year + 1, 2, 31);
        return DateFormats.getDate(convertedCalendar.getTime());
    }
}
