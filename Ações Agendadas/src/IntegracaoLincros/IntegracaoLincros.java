package IntegracaoLincros;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import Util.MensagemExceptionUtil;
import Util.PreferenciaSistemaUtil;

public abstract class IntegracaoLincros {
	public static void conexaoHTTP(byte[] arquivozip) throws Exception {
		int contentLength = arquivozip.length;
		String boundary = "--------------------------" + System.currentTimeMillis();
		URL url = new URL("https://deployment.transpofrete.com.br/api//importacaoXML/upload");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", "Bearer " + PreferenciaSistemaUtil.getOperador());
		connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
		connection.setRequestProperty("Content-Length", Integer.toString(contentLength));
		connection.setRequestProperty("Host", "deployment.transpofrete.com.br");
		connection.setDoOutput(true);
		
		try (OutputStream os = connection.getOutputStream();) {
			String encodedFile = Base64.getEncoder().encodeToString(arquivozip); 
			os.write(encodedFile.getBytes());
			os.flush();
		}

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {				
			throw new Exception(MensagemExceptionUtil.getMensagemResponseCode(connection.getResponseCode())			 
			);
		} 
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
			StringBuilder retornoAPI = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				retornoAPI.append(responseLine.trim());
			}
				
			System.out.println("Retorno API: " + retornoAPI  + ", status code: " + connection.getResponseCode());
			br.close();
		} 
	}
	
}
