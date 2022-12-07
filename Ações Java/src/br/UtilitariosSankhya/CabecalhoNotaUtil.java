package br.UtilitariosSankhya;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class CabecalhoNotaUtil {
	private EntityFacade dwf;
	private JdbcWrapper jdbc = null;
	private BigDecimal totalNota = BigDecimal.valueOf(0);
	private BigDecimal peso = BigDecimal.valueOf(0);
	private BigDecimal qtdVolume = BigDecimal.valueOf(0);
	private BigDecimal codParc = BigDecimal.valueOf(0);
	
	public BigDecimal getCodParc() {
		return codParc;
	}
	public void setCodParc(BigDecimal codParc) {
		this.codParc = codParc;
	}
	
	public BigDecimal getTotalNota() {
		return totalNota;
	}
	public void setTotalNota(BigDecimal totalNota) {
		this.totalNota = totalNota;
	}
	public BigDecimal getPeso() {
		return peso;
	}
	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}
	public BigDecimal getQtdVolume() {
		return qtdVolume;
	}
	public void setQtdVolume(BigDecimal qtdVolume) {
		this.qtdVolume = qtdVolume;
	}

	/******************************************************************************
	 * Integração com a Lincros
	 * Através do método buscarPedidosAgrupadasCliente é retornado os totalizadores do pedido para que 
	 * seja enviado a integração da Lincros. 
	 ******************************************************************************/
	
	public void buscarPedidosAgrupadasCliente(String criterioConsulta) throws Exception {
		
		this.dwf = EntityFacadeFactory.getDWFFacade();

		jdbc = this.dwf.getJdbcWrapper();
		jdbc.openSession();
		
		NativeSql sql = new NativeSql(jdbc);
		try {
			sql.setNamedParameter("P_NOTAS", criterioConsulta);
			sql.appendSql("SELECT NVL(SUM(VLRNOTA),0) VLRNOTA,"
					           + "NVL(SUM(PESO),0) PESO, "
					           + "NVL(SUM(QTDVOL),0) QTDVOL, "
					           + "CODPARC "
					      + "FROM TGFCAB "
					      + "WHERE NUNOTA IN ");
			sql.appendSql(criterioConsulta);
			sql.appendSql("GROUP BY CODPARC");			
			ResultSet rs = sql.executeQuery();

			while (rs.next()) {
				this.totalNota = rs.getBigDecimal("VLRNOTA");
				this.peso 	  = rs.getBigDecimal("PESO");
				this.qtdVolume = rs.getBigDecimal("QTDVOL");
				this.codParc = rs.getBigDecimal("CODPARC");
			}
			
		} catch (Exception erro) {
			throw new Exception("Erro durante a consulta das notas: <b>ERRO: </b>" + erro.toString());
		} finally {
			jdbc.closeSession();
		}

	}
	
}
