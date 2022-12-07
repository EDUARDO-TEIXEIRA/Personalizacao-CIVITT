package br.Testes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class TestesValores {

	public static void main(String args[]) throws Exception {
		Map<BigDecimal, BigDecimal> meuMapa = new HashMap<>();
		meuMapa.put(BigDecimal.valueOf(100), BigDecimal.valueOf(1132));
		meuMapa.put(BigDecimal.valueOf(200), BigDecimal.valueOf(16245));
		meuMapa.put(BigDecimal.valueOf(300), BigDecimal.valueOf(1132));
		
		Map<BigDecimal, List<BigDecimal>> groupedValues = new HashMap<>();
		for (Entry<BigDecimal, BigDecimal> entry : meuMapa.entrySet()) {
			BigDecimal value =  entry.getValue();
			BigDecimal key = entry.getKey();

			List<BigDecimal> indices = groupedValues.getOrDefault(value, new ArrayList<>());
			indices.add(key);
			groupedValues.put(value, indices);
		}
		
		for (Entry<BigDecimal, List<BigDecimal>> entry : groupedValues.entrySet()) {
			System.out.println("Parceiro: "+ entry.getKey() + ", Pedido: " + entry.getValue() + ", Tamanho: " + entry.getKey()); 
		}
		String s = "(287732,25565)";
		String replace = s.replace("(", "").replace(")", "");
		StringTokenizer st = new StringTokenizer(s, ",");
		
		String parte1 = st.nextToken(); // retorna "Olá"
		
		System.out.println("Primeira Parte: " + st.nextToken().getClass());
		

		
	}
}