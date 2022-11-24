package br.UtilitariosSankhya;

import java.math.BigDecimal;
import java.util.Collection;

import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ParametroSistemaUtil {
	public String getValorChave(String nomeChave) throws Exception {
		Collection<DynamicVO> filtropreferencias = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade().
				findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.PARAMETRO_SISTEMA, "this.CHAVE = ?", new Object[] {nomeChave}));
		if (filtropreferencias.isEmpty()) {
			throw new Exception("Não foi encontrado url nas preferências do sistema");
		}
		for (DynamicVO linha : filtropreferencias) 
		{
			switch (linha.asString("TIPO")) {
			case "T":
				return linha.asString("TEXTO");
			case "L":
				return linha.asString("LOGICO");
			case "I":
				return linha.asBigDecimal("INTEIRO").toString();
			case "C":
				String texto = linha.asString("TEXTO");
				String[] arrOfStr = texto.split(" ");
				int index = 0;

				for (String posicao : arrOfStr) {
					if (linha.asBigDecimal("INTEIRO") == new BigDecimal(index++)) {
						return posicao; 
					}
				}
				return linha.asBigDecimal("INTEIRO").toString();	
			default:
				break;
			}
		}
		return "Chave não encontrada";
	}
}
