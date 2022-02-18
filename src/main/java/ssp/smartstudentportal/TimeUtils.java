package ssp.smartstudentportal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtils {
    public static String getTimeDifference(String dateTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(dateTime);
        long time = date.getTime();
        long currentTime = System.currentTimeMillis();
        long difference = currentTime - time;
        if(difference < 60000){
            return "Just now";
        }else if(difference < 3600000){
            return (difference/60000)+" minutes ago";
        }else if (difference < 86400000){
            return (difference/3600000)+" hours ago";
        }else if (difference < 604800000){
            return (difference/86400000)+" days ago";
        }else if (difference < 2419200000L) {
            return (difference / 604800000) + " weeks ago";
        }else{
            return difference/2419200000L+" months ago";
        }
    }
    public static ZonedDateTime convertirAFecha(String fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(fecha, formatter);

        ZonedDateTime resultado = date.atStartOfDay(ZoneId.systemDefault());
        return resultado;
    }
}
