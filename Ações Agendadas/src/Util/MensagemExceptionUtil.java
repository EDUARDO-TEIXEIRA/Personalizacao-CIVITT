package Util;

import java.net.HttpURLConnection;

public abstract class MensagemExceptionUtil {
	public static String getHeaderRequisicaoRest(HttpURLConnection objconexao) {
		String mensagem = "<br>"
				+ "<br>--------- DADOS DA REQUISIÇÃO ---------"
				
				+ "<br/>"
				+ "<br>--------- CORPO DA REQUISIÇÃO ---------"
				+ "<br>- Authorization: " + objconexao.getRequestProperty("Authorization")
				+ "<br>- Content-Type: " + objconexao.getRequestProperty("Content-Type")
				+ "<br>- Cache-Control: " + objconexao.getRequestProperty("Cache-Control")
				+ "<br>- Content-Length: " + objconexao.getRequestProperty("Content-Length")
				+ "<br/>"
				;
		return mensagem ;
	}
	
	public static String getMensagemResponseCode(int codigo) {
		String erro = "<b>ERRO:</b> ";
		switch (codigo) {
		case 400:
			erro += "400 - O servidor não pode ou não processará a solicitação devido a algo que é percebido como um erro do cliente";
			break;
		case 401:
			erro += "401 - Não autorizado";
			break;
		case 404:
			erro += "400 - URL não encontrada ";
			break;
		case 415:
			erro += "415 - Mídia não suportada ";
			break;
		default:
			erro = String.format("Erro %d não catalogado: ",codigo) ;
			break;
		}
		erro += ".";
		return erro;
	}

}
