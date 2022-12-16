package br.civitt.ConexaoLincros;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

import org.json.JSONObject;

import br.civitt.Utilitarios.ArquivosUtil;
import br.civitt.Utilitarios.MensagemExceptionUtil;

public abstract class IntegracaoLincros {
	private static DataOutputStream request;
	public static BigDecimal protocolo;
	private static final String boundary = "*****";
	private static final String crlf = "\r\n";
	private static final String twoHyphens = "--";
	private static BigDecimal nunota;

	public static BigDecimal getNunota() {
		return nunota;
	}

	public static void setNunota(BigDecimal nunota) {
		IntegracaoLincros.nunota = nunota;
	}

	public static void conexaoHTTP(byte[] xml) throws IOException, Exception {
		System.out.println("Iniciando integração com a Lincros do documento " + nunota);

		int contentLength = xml.length;

		URL url = new URL("https://ws-tms.lincros.com/api//importacaoXML/upload");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization",
				"Bearer " + br.civitt.Utilitarios.PreferenciaSistemaUtil.getOperador());
		connection.setRequestProperty("User-Agent", "PostmanRuntime/7.29.2");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		connection.setRequestProperty("Content-Length", Integer.toString(contentLength));
		connection.setRequestProperty("Host", "deployment.transpofrete.com.br");

		connection.setDoOutput(true);
		request = new DataOutputStream(connection.getOutputStream());

		addArquivoRequisicao("arquivo", ArquivosUtil.gerarArquivoXml("NotaNFE_" + nunota, xml));

		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream responseStream = new BufferedInputStream(connection.getInputStream());

			BufferedReader br = new BufferedReader(new InputStreamReader(responseStream));
			String responseLine = null;
			StringBuilder responseJsonAPI = new StringBuilder();
			while ((responseLine = br.readLine()) != null) {
				responseJsonAPI.append(responseLine.trim());
			}
			protocolo = getProtocolo(responseJsonAPI);
			br.close();
			System.out.println("Retorno com sucesso, protocolo da " + nunota + " é " + protocolo);

		}
		connection.disconnect();

		if (responseCode != HttpURLConnection.HTTP_OK) {
			System.out.println(MensagemExceptionUtil.getMensagemResponseCode(connection.getResponseCode()));
		}
	}

	public static BigDecimal getProtocolo(StringBuilder respostaAPI) {
		BigDecimal protocolo = BigDecimal.ZERO;
		JSONObject jsonRetorno = new JSONObject(respostaAPI.toString());
		protocolo = BigDecimal.valueOf(jsonRetorno.getInt("protocolo"));
		if (protocolo.signum() > 0) {
			return protocolo;
		}
		return protocolo;
	}

	public void addFormField(String name, String value) throws IOException {
		request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
		request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + this.crlf);
		request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf);
		request.writeBytes(this.crlf);
		request.writeBytes(value + this.crlf);
		request.flush();
	}

	public static void addArquivoRequisicao(String fieldName, File uploadFile) throws IOException {
		String fileName = uploadFile.getName();
		request.writeBytes(twoHyphens + boundary + crlf);
		request.writeBytes(
				"Content-Disposition: form-data; name=\"" + fieldName + "\";filename=\"" + fileName + "\"" + crlf);
		request.writeBytes(crlf);

		byte[] bytes = Files.readAllBytes(uploadFile.toPath());
		request.write(bytes);
	}

}
