package br.UtilitariosSankhya;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class DatasUtil {

 public String getDifHoras(java.util.Date date, Date dataSolicit) {
        Calendar dtInicio = new GregorianCalendar();
        dtInicio.setTime(date);
        Calendar dtFinal = new GregorianCalendar();
        dtFinal.setTime(dataSolicit);
        
        Long difMillis = dtFinal.getTimeInMillis() - dtInicio.getTimeInMillis();
        Long difHoras = difMillis / (60 * 60 * 1000);
        Long diffSeconds = difMillis / 1000 % 60;
        Long diffMinutes = difMillis / (60 * 1000) % 60;
        long diff = TimeUnit.DAYS.convert(difMillis, TimeUnit.MILLISECONDS);
        
        return "Dias: " + diff + ", " + difHoras + ":" + diffMinutes + ":" + diffSeconds;
    }
}