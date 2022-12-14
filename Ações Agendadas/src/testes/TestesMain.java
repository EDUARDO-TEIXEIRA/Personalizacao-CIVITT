package testes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestesMain {

	public static void main(String[] args) throws Exception {
		URL url = new URL("https://deployment.transpofrete.com.br/api//importacaoXML/upload");
		String boundary = "--------------------------" + System.currentTimeMillis(); 
		System.out.println("Teste: " + boundary);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Authorization", "Bearer eBY9YydOYRH8cv3mT03gntbUWImFPsG8R0h4feVcaD");
		connection.setRequestProperty("Cache-Control", "no-cache");
		
		connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
		connection.setRequestProperty("Content-Length", "<calculated when request is sent>");
		connection.setRequestProperty("Host", "<calculated when request is sent>");
		connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
		connection.setRequestProperty("Accept", "*/*");		
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		connection.setRequestProperty("Connection", "keep-alive");
		//Carrega o arquivo binário
		
        try (OutputStream outputStream = connection.getOutputStream()){
			outputStream.write(null);
			outputStream.close();
		} catch (Exception erro) {
			
		}

		int responseCode = connection.getResponseCode();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		 if (responseCode == HttpURLConnection.HTTP_OK && 
			 responseCode == HttpURLConnection.HTTP_CREATED || 
			 responseCode == HttpURLConnection.HTTP_ACCEPTED) {
			 throw new Exception("Erro de conexão, veja mais informações: ");
		 }
				 
		/*if (1==1) {
			throw new Exception(MensagemExceptionUtil.getMensagemResponseCode(connection.getResponseCode()) 
					+ "<br>" + connection.getErrorStream().toString()
			);
		}*/
		
	}
}
