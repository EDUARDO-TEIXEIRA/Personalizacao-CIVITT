package br.UtilitariosSankhya;

import java.math.BigDecimal;

public class FinanceiroUtil {
	private BigDecimal nuNota;
	private BigDecimal numNota ;
	private BigDecimal nuFin;
	private BigDecimal codEmp;
	private BigDecimal valorDesdobramento;
	private BigDecimal codParc;
	public BigDecimal getCodEmp() {
		return codEmp;
	}
	public void setCodEmp(BigDecimal codEmp) {
		this.codEmp = codEmp;
	}
	public BigDecimal getCodParc() {
		return codParc;
	}
	public void setCodParc(BigDecimal codParc) {
		this.codParc = codParc;
	}
	public BigDecimal getNuNota() {
		return nuNota;
	}
	public void setNuNota(BigDecimal nuNota) {
		this.nuNota = nuNota;
	}
	public BigDecimal getNumNota() {
		return numNota;
	}
	public void setNumNota(BigDecimal numNota) {
		this.numNota = numNota;
	}
	public BigDecimal getNuFin() {
		return nuFin;
	}
	public void setNuFin(BigDecimal nuFin) {
		this.nuFin = nuFin;
	}
	public BigDecimal getValorDesdobramento() {
		return valorDesdobramento;
	}
	public void setValorDesdobramento(BigDecimal valorDesdobramento) {
		this.valorDesdobramento = valorDesdobramento;
	}
}
