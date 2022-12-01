package br.IntegracaoLinCros;

import br.UtilitariosSankhya.MensagemRetornoUtil;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class CalcularFrete implements AcaoRotinaJava {
	MensagemRetornoUtil mensagem = new MensagemRetornoUtil();
	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		Registro[] linhas = contexto.getLinhas();
		if (linhas.length == 0) {
			throw new Exception("Selecione um pedido de vendas");
		}
		
		for (Registro registro : linhas) {
			if (!registro.getCampo("TIPMOV").equals("P")) {
				throw new Exception("Não é possível calcular frete de registros que não sejam pedidos de vendas");
			}
			IntegracaoLincros integracao = new IntegracaoLincros();
			integracao.conexaoLincros(linhas);
			if (integracao.getCnpjReturn() != null) {
				registro.setCampo("CODPARCTRANSP", integracao.getCnpjReturn());
				registro.save();	
			}
		}
	}

}
