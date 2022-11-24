package br.UtilitariosSankhya;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collection;

import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EmpresasUtil {
	public String getOperadorBase64(BigDecimal codemp, String campoUsuario, String campoSenha) throws Exception {
		String token = "";
		String usuarioBase64 = "";

		Collection<DynamicVO> filtroOperador = (Collection<DynamicVO>) 
				EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(
				new FinderWrapper(DynamicEntityNames.EMPRESA, "this.CODEMP = ?", codemp));
		
		for (DynamicVO linha : filtroOperador) 
		{
			if (linha.asString(campoUsuario) == null || linha.asString(campoSenha) == null) {
				throw new NullPointerException("Usuário não está está configurado para a empresa " + codemp + ".");
			}
			token = String.valueOf(linha.asString("AD_OPERADOR") + ":" + linha.asString("AD_SENHA"));
		}
		return usuarioBase64 = Base64.getEncoder().encodeToString(token.getBytes());
	}
}
