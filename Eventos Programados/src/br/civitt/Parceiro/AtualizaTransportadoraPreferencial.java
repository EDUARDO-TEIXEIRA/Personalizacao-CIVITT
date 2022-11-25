package br.civitt.Parceiro;
import br.civitt.Utilitarios.ParceiroUtil;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Collection;

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
			return BigDecimal.valueOf(14266); // JEOLOG TRANSPORTES
		}	
	}
	public enum Sudeste {
		RJ, SP;
		BigDecimal getTransportadora() {
			return BigDecimal.valueOf(4691); // SOLARE ADMINISTRACAO E CONSULTORIA LTDA - EPP
		}
	}
	public enum Sul {
		PR, RS, SC;
		BigDecimal getTransportadora() {
			return BigDecimal.valueOf(16940); // DIRECIONAL TRANSPORTE E LOGISTICA S/A
		}
	}
	public enum CentroOeste {
		MT, MS, TO;
		BigDecimal getTransportadora() {
			return BigDecimal.valueOf(14434); // MIRA SERVICOS DE TRANSPORTES LTDA.
		}
	}
	public enum Norte {
		AC, AP, AM, RO, RR;
		BigDecimal getTransportadora() {
			return BigDecimal.valueOf(11298); // EXATA CARGO LTDA 
		}
	}
	
	public enum OutrosUF {
		DF, GO, MG;
		BigDecimal getTransportadora() {
			return BigDecimal.valueOf(10352); // TG TRANSPORTES GERAIS E DISTRIBUICAO LTDA
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
    		if (modFields.isModifing("CODTAB") && modFields.getNewValue("CODTAB") != modFields.getOldValue("CODTAB")) {
    			cliente.setCodparc(parVo.asBigDecimal("CODPARC"));
    			cliente.setCodCid(parVo.asBigDecimal("CODCID"));
    			cliente.setTipPessoa(parVo.asString("TIPPESSOA"));
    			
    			alteraTransportadora();
    		}

		} catch (Exception erro) {
			throw new Exception("ERRO: " + erro.toString());
		}
	}
	public void insereDadosComplemento () throws Exception {
		try {
        		this.dwf = EntityFacadeFactory.getDWFFacade();
	            DynamicVO comVO = (DynamicVO) this.dwf.getDefaultValueObjectInstance(DynamicEntityNames.COMPLEMENTO_PARCEIRO);  
	            jdbc = dwf.getJdbcWrapper();
	            jdbc.openSession();
	            
	            comVO.setProperty("CODPARC", cliente.getCodparc());
	            
	            PersistentLocalEntity createEntity = dwf.createEntity(DynamicEntityNames.COMPLEMENTO_PARCEIRO, (EntityVO) comVO);
	            DynamicVO save = (DynamicVO) createEntity.getValueObject();
	            alteraTransportadora ();
	            
		} catch (Exception erro) {
			throw new Exception("Não foi possível inserir os dados de complemento do Parceiro" + erro.toString());
		}
		
	}
	
	public void alteraTransportadora () throws Exception {
    	
    	try {
    			/*Filtro de parceiro*/
    			this.dwf = EntityFacadeFactory.getDWFFacade(); 
    			FinderWrapper finderComplemento = new FinderWrapper(DynamicEntityNames.COMPLEMENTO_PARCEIRO, "CODPARC = ?", new Object[] {cliente.getCodparc()});
	        	Collection<PersistentLocalEntity> finderComplementoCPLE = this.dwf.findByDynamicFinder(finderComplemento);
	        	
	        	if (finderComplementoCPLE.isEmpty()) {
	        		insereDadosComplemento();	
				}
	        	
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
							complementoVO.setProperty("CODPARCTRANSP", BigDecimal.valueOf(2310)); // TRESELES TRANSPORTES DE CARGAS LTDA
						} else if (result.getString("UF").equals("BA")) {
							complementoVO.setProperty("CODPARCTRANSP", BigDecimal.valueOf(10797)); // REBOUCAS TRANSPORTES
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
	        	}
		} catch (Exception erro) {
			throw new Exception("Erro ao tentar salvar transportadora preferencial, motivo: " + erro.toString());
		}
    	

    	
    	
	}
}
			
