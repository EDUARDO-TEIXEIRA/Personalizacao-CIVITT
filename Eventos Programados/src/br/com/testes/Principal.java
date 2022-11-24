package br.com.testes;

import java.util.Date;

import br.civitt.Utilitarios.DatasUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Principal {
	private enum DaysOfWeekEnum  {
		SUNDAY,
	    MONDAY,
	    TUESDAY, 
	    WEDNESDAY, 
	    THURSDAY, 
	    FRIDAY, 
	    SATURDAY
	}
public static void main(String[] args) {
	for (DaysOfWeekEnum  day : DaysOfWeekEnum.values()) { 
	    System.out.println(day); 
	}
}
}
