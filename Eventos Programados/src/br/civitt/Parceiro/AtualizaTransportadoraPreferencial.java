package br.civitt.Parceiro;
import br.civitt.Utilitarios.ParceiroUtil;
import br.civitt.Utilitarios.*;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Collection;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class AtualizaTransportadoraPreferencial implements EventoProgramavelJava{
	ParceiroUtil cliente = new ParceiroUtil();
	private EntityFacade dwf;
	private JdbcWrapper jdbc = null; 
	
	private enum Nordeste {
		AL, CE, MA, PA, PB, PE, PI, RN, SE;
		BigDecimal getTransportadora() {
			return BigDecimal.valueOf(11298);
		}	
	}
	public enum Sudeste {
		RJ, SP;
		BigDecimal getTransportadora() {
			return BigDecimal.valueOf(4691);
		}
	}
	public enum Sul {
		PR, RS, SC;
		BigDecimal getTransportadora() {
			return BigDecimal.valueOf(16940);
		}
	}
	public enum CentroOeste {
		MT, MS, TO;
		BigDecimal getTransportadora() {
			return BigDecimal.valueOf(16940);
		}
	}
	public enum Norte {
		AC, AP, AM, RO, RR;
		BigDecimal getTransportadora() {
			return BigDecimal.valueOf(11298);
		}
	}
	
	public enum OutrosUF {
		DF, GO, MG;
		BigDecimal getTransportadora() {
			return BigDecimal.valueOf(10352);
		}
	}
	
	@Override
	public void afterDelete(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterUpdate(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeCommit(TransactionContext contexto) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeDelete(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeInsert(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeUpdate(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub
		DynamicVO parVo = (DynamicVO) contexto.getVo();
	    ModifingFields modFields = contexto.getModifingFields();
	    	
    	try {
    		if (modFields.isModifing("CODCID") && modFields.getNewValue("CODCID") != modFields.getOldValue("CODCID")) {
    			cliente.setCodparc(parVo.asBigDecimal("CODPARC"));
    			cliente.setCodCid(parVo.asBigDecimal("CODCID"));
    			cliente.setTipPessoa(parVo.asString("TIPPESSOA"));
    			
    			alteraTransportadora();
    		}
			
			
		} catch (Exception erro) {
			throw new Exception("ERRO: " + erro.toString());
		}
	}
	public void alteraTransportadora () throws Exception {
    	
    	try {
    		final ContextoAcao contexto = null;
    			/*Filtro de parceiro*/
    			this.dwf = EntityFacadeFactory.getDWFFacade(); 
    			FinderWrapper finderComplemento = new FinderWrapper(DynamicEntityNames.COMPLEMENTO_PARCEIRO, "CODPARC = ?", new Object[] {cliente.getCodparc()});
	        	Collection<PersistentLocalEntity> finderComplementoCPLE = this.dwf.findByDynamicFinder(finderComplemento);
	        	for (PersistentLocalEntity linha  : finderComplementoCPLE) 
	        	{
	        		
	        		jdbc = this.dwf.getJdbcWrapper();
		            jdbc.openSession();
	        		EntityVO finderComplementoEVO = linha.getValueObject();
	        		DynamicVO complementoVO = (DynamicVO) finderComplementoEVO;
	        		
	        		if (cliente.getTipPessoa().equals("F")) {
	        			complementoVO.setProperty("CODPARCTRANSP", BigDecimal.valueOf(8));
					}
	        		
	        		NativeSql sql = new NativeSql(jdbc);
	        		sql.setNamedParameter("P_CODCID", cliente.getCodCid());
	        		sql.appendSql("SELECT UFS.UF FROM TSIUFS UFS INNER JOIN TSICID CID ON CID.UF = UFS.CODUF WHERE CID.CODCID = :P_CODCID");
	        		ResultSet result = sql.executeQuery();
	        		
	        		while (result.next()) {
	        			 
						if (result.getString("UF").equals("ES")) {
							complementoVO.setProperty("CODPARCTRANSP", BigDecimal.valueOf(2310));
						} else if (result.getString("UF").equals("BA")) {
							complementoVO.setProperty("CODPARCTRANSP", BigDecimal.valueOf(10797));
						} else {
								for (Nordeste linhaUF : Nordeste.values()) {
									if (result.getString("UF").equals(linhaUF.toString())) {
										complementoVO.setProperty("CODPARCTRANSP", linhaUF.getTransportadora());
									}
								}
								for (Sudeste linhaUF : Sudeste.values()) {
									if (result.getString("UF").equals(linhaUF.toString())) {
										complementoVO.setProperty("CODPARCTRANSP", linhaUF.getTransportadora());
									} 
								}
								for (OutrosUF linhaUF : OutrosUF.values()) {
									if (result.getString("UF").equals(linhaUF.toString())) {
										complementoVO.setProperty("CODPARCTRANSP", linhaUF.getTransportadora());
									}
								}
								for (Sul linhaUF : Sul.values()) {
									if (result.getString("UF").equals(linhaUF.toString())) {
										complementoVO.setProperty("CODPARCTRANSP", linhaUF.getTransportadora());
									}
								}
								for (CentroOeste linhaUF : CentroOeste.values()) {
									if (result.getString("UF").equals(linhaUF.toString())) {
										complementoVO.setProperty("CODPARCTRANSP", linhaUF.getTransportadora());
									}
								}
								for (Norte linhaUF : Norte.values()) {
									if (result.getString("UF").equals(linhaUF.toString())) {
										complementoVO.setProperty("CODPARCTRANSP", linhaUF.getTransportadora());
									}
								}
						}
					}
	        		linha.setValueObject((EntityVO) complementoVO); // salva linha
	        		contexto.setMensagemRetorno("Foi vinculoado a transportadora" + complementoVO.asBigDecimal("CODPARCTRANSP"));
	        	}
		} catch (Exception erro) {
			throw new Exception("Erro ao tentar salvar transportadora preferencial, motivo: " + erro.toString());
		}
    	

    	
    	
	}
}
			
