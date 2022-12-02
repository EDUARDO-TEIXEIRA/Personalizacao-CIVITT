package br.UtilitariosSankhya;

import java.math.BigDecimal;
import java.util.Collection;

import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ItensUtil {
	public Collection<DynamicVO> getItensNota(BigDecimal nunota) throws Exception {
		
		Collection<DynamicVO> filtro = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.CABECALHO_NOTA, "this.NUNOTA = ?", nunota));
		
		return (Collection<DynamicVO>) filtro;
	}
}
