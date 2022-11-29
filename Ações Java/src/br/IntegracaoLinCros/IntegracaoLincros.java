package br.IntegracaoLinCros;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Collection;

import br.UtilitariosSankhya.Autenticacao;

public class IntegracaoLincros {
	
	Autenticacao aux;
		
	public void conexaoLincros () throws Exception {
		
			URL url = new URL("https://deployment.transpofrete.com.br/api/v3/calculo/calcularNota");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer " + aux.getOperador(null, null, null, null, false));
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", "<calculated when request is sent>");
			connection.setRequestProperty("Host", "<calculated when request is sent>");
			connection.setDoInput(true);
			String jsonInputString = "{}";
			
			try(OutputStream os = connection.getOutputStream()) 
			{
			    byte[] input = jsonInputString.getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}
			try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				
				while ((responseLine = br.readLine()) != null) {
					    response.append(responseLine.trim());
				}
				if (1==1) {
				    throw new Exception("Resposta: " + response.toString());

				}
			} catch (Exception erro) {
				throw new Exception("Erro de Conexão: " + erro.toString());
			}
	}
}
