package br.com.testes;

import java.util.Date;

import br.civitt.Utilitarios.DatasUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Principal {
public static void main(String[] args) {
	try {
		SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date dataInicio = (Date) dtFormat.parse("28/11/2022 14:52:30");
		Date dataFinal = (Date) dtFormat.parse("31/11/2022 18:30:00");
		DatasUtil data = new DatasUtil();
		System.out.println(data.getDifHoras(dataInicio, dataFinal));
		
	} catch (ParseException erro) {
		System.out.println("Erro ao calcular datas " + erro.toString());
	}
}
}
