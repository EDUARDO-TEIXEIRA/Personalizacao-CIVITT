package br.civitt.Utilitarios;

import java.math.BigDecimal;

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

}
