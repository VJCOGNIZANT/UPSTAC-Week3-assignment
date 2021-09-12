package org.upgrad.upstac.shared;

import org.upgrad.upstac.exception.AppException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneId.systemDefault;
import static java.util.Date.from;
import static java.util.Optional.ofNullable;

public class DateParser {


    public static LocalDate getDateFromString(String input) {

        try {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return asLocalDate(simpleDateFormat.parse(input));
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException("Invalid Date String" + input);
        }
    }

    public static String getStringFromDate(LocalDate input) {
        //"2018-09-09"
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(asDate(input));


    }

    public static LocalDate asLocalDate(Date input) {
        return ofNullable(input)
                .map(date -> ofEpochMilli(date.getTime()).atZone(systemDefault()).toLocalDate())
                .orElseThrow(() -> new AppException("Invalid Input"));

    }

    public static Date asDate(LocalDate input) {

        return ofNullable(input)
                .map(date -> from(date.atStartOfDay(systemDefault()).toInstant()))
                .orElseThrow(() -> new AppException("Invalid Input"));
    }

}