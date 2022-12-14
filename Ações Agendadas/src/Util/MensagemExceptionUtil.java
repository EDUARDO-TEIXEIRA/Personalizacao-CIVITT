package Util;

public abstract class MensagemExceptionUtil {
	public static String getMensagemResponseCode(int codigo) {
		String erro = "<b>ERRO:</b> ";
		switch (codigo) {
		case 400:
			erro += "404 - O servidor não pode ou não processará a solicitação devido a algo que é percebido como um erro do cliente";
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
