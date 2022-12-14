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
	int [] tiposNegociacao = {1362, 1363, 1364, 
			  1365, 1366, 1367, 
			  1368, 1369, 1370, 
			  1371, 1372, 1373, 
			  1374, 1375, 1376, 1377};
	for (int i = 0; i < tiposNegociacao.length; i++) {
		System.out.println("Número do índice " + tiposNegociacao[i]);
		
	}
}
}
