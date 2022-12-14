package Util;

import java.util.Collection;

import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public abstract class PreferenciaSistemaUtil {
	public static String getOperador() throws Exception {
		String token = "";
		Collection<DynamicVO> filtroOperador = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.EMPRESA, "this.CODEMP = 1"));

		for (DynamicVO linha : filtroOperador) {
			if (linha.asString("AD_TOKEN_LINCROS") == null) {
				throw new NullPointerException("Não foi definido o token de acesso para a LINCROS na empresa 1");
			}
			token = linha.asString("AD_TOKEN_LINCROS");
		}
		return token;
	}
}
