package br.Testes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.json.JSONObject;

public class TestesValores {

	public static void main(String args[]) throws Exception {
		URL url = new URL("https://ws-tms.lincros.com/api/v3/calculo/calcularNota");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", "Bearer xxx");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Content-Length", "<calculated when request is sent>");
		connection.setRequestProperty("Host", "<calculated when request is sent>");
		connection.setDoOutput(true);

		String bodyResquest = "{\r\n\t\"cnpjUnidade\": \"32463085000130\",\r\n\t\"remetente\": \"32463085000130\",\r\n\t\"peso\": 0.292,\r\n\t\"cubagem\": 0,\r\n\t\"pesoCubado\": 0,\r\n\t\"valor\": 191.42,\r\n\t\"volumes\": 0,\r\n\t\"abono\": 0,\r\n\t\"percentualValorCliente\": 0,\r\n\t\"cepOrigem\": 29168081,\r\n\t\"cepDestino\": 29164370,\r\n\t\"modalidadeFrete\": 0,\r\n\t\"tipoOperacao\": 1,\r\n\t\"transportadora\": \"\",\r\n\t\"placa\": \"\"\r\n}";

		try (OutputStream os = connection.getOutputStream()) {
			byte[] input = bodyResquest.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			System.out.println(response.toString());
			
			JSONObject jsonRetorno = new JSONObject(response.toString());
			System.out.println(jsonRetorno.getString("status"));
		}
	}
}