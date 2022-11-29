package br.UtilitariosSankhya;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collection;

import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public abstract class Autenticacao {
	public String getOperador(String tabela, BigDecimal pkRegistro, String usuario, String senha, boolean retornaBase64)
			throws Exception {

		String token = "";
		String usuarioBase64 = "";

		Collection<DynamicVO> filtroOperador = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(new FinderWrapper(tabela, "this.CODEMP = ?", pkRegistro));

		for (DynamicVO linha : filtroOperador) {
			if (linha.asString("AD_SENHA") == null || linha.asString("AD_OPERADOR") == null) {
				throw new NullPointerException("Usuário CDL Serasa não está configurado para a empresa ");
			}
			token = String.valueOf(linha.asString("AD_OPERADOR") + ":" + linha.asString("AD_SENHA"));
		}
		return usuarioBase64 = Base64.getEncoder().encodeToString(token.getBytes());
	}
}
