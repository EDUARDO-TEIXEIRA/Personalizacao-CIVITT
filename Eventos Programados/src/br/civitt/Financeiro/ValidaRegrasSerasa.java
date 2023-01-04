package br.civitt.Financeiro;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class ValidaRegrasSerasa implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent contexto) throws Exception {
		
	}

	@Override
	public void afterInsert(PersistenceEvent contexto) throws Exception {

	}

	@Override
	public void afterUpdate(PersistenceEvent contexto) throws Exception {
		DynamicVO finVO = (DynamicVO) contexto.getVo();
		ModifingFields modFields = contexto.getModifingFields();
		
		if ((modFields.isModifing("NURENEG") && modFields.getNewValue("NURENEG") != null)) {
			if (finVO.getProperty("AD_SERASA") != null) {
				if (finVO.getProperty("AD_SERASA").equals("S")) {
					throw new Exception("Títulos negativados ao Serasa não podem ser renegociados.");
				}
			}
		}
	}

	@Override
	public void beforeCommit(TransactionContext contexto) throws Exception {
		
	}

	@Override
	public void beforeDelete(PersistenceEvent contexto) throws Exception {
		
	}

	@Override
	public void beforeInsert(PersistenceEvent contexto) throws Exception {
		
	}

	@Override
	public void beforeUpdate(PersistenceEvent contexto) throws Exception {
		
	}

}
