package br.civitt.Parceiro;

import java.math.BigDecimal;
import java.util.Collection;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class VerificaAlteracaoSerasa implements EventoProgramavelJava  {

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
		EntityFacade dwfEntity = EntityFacadeFactory.getDWFFacade();
		BigDecimal codusulogado = (BigDecimal) JapeSessionContext.getRequiredProperty("usuario_logado");
		Collection<DynamicVO> usuario = dwfEntity.findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.USUARIO, "this.CODUSU = ? ", new Object[] {codusulogado}));
		DynamicVO finVO = (DynamicVO) contexto.getVo();
		ModifingFields modFields = contexto.getModifingFields();
		
		if(finVO.getProperty("TIPPESSOA").equals("F") && modFields.isModifing("AD_IGNORAENVIOSERASA")) {
			throw new Exception("Operação não permitida! A marcação está disponível apenas para pessoas jurídicas. <b>[Ignora envio ao Serasa]</b>.");
		}
		
		if (modFields.isModifing("AD_IGNORAENVIOSERASA")) {
			
			if(finVO.getProperty("TIPPESSOA").equals("F")) {
				throw new Exception("Operação não permitida! A marcação está disponível apenas para pessoas jurídicas. <b>[Ignora envio ao Serasa]</b>.");
			}
				for (DynamicVO linhaUsuarioVO : usuario) {
					if (linhaUsuarioVO.asString("AD_PERM_ALT_EXC_PARC_SERASA") == null) { 
						throw new NullPointerException("O usuário não tem permissão para alterar o campo <b>[Ignora envio ao Serasa]</b>.");
					} else if (linhaUsuarioVO.asString("AD_PERM_ALT_EXC_PARC_SERASA").equals("N")) {
						throw new Exception("O usuário não tem permissão para alterar o campo <b>[Ignora envio ao Serasa]</b>.");
					}	
				}	
		}
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
		
	}

}
