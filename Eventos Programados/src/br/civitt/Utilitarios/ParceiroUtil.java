package br.civitt.Utilitarios;

import java.math.BigDecimal;
import java.util.Collection;

import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ParceiroUtil {
	private BigDecimal codparc;
	private BigDecimal codCid;
	private String tipPessoa;
	private String razaoSocial;
	
	public String getTipPessoa() {
		return tipPessoa;
	}
	public void setTipPessoa(String tipPessoa) {
		this.tipPessoa = tipPessoa;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	
	public BigDecimal getCodparc() {
		return codparc;
	}
	public void setCodparc(BigDecimal codparc) {
		this.codparc = codparc;
	}
	public BigDecimal getCodCid() {
		return codCid;
	}
	public void setCodCid(BigDecimal codCid) {
		this.codCid = codCid;
	}
	public Collection<DynamicVO> getParceiro(BigDecimal codparc) throws Exception {
		Collection<DynamicVO> filtroUsuarioVO = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(
						new FinderWrapper(DynamicEntityNames.PARCEIRO, "this.CODPARC = ?", new Object[] { codparc }));

		return (Collection<DynamicVO>) filtroUsuarioVO;
	}

}
