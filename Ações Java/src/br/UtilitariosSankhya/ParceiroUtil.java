package br.UtilitariosSankhya;

import java.math.BigDecimal;
import java.util.Collection;

import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ParceiroUtil {

	public Collection<DynamicVO> getParceiro(BigDecimal codparc) throws Exception {
		
		Collection<DynamicVO> filtroUsuarioVO = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.PARCEIRO, "this.CODPARC = ?", codparc));
		
		return (Collection<DynamicVO>) filtroUsuarioVO;
	}
}