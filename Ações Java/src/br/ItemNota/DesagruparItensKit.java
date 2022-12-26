package br.ItemNota;

import java.math.BigDecimal;
import java.util.Collection;
import br.UtilitariosSankhya.CabecalhoNotaUtil;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class DesagruparItensKit implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		Registro[] itens = contexto.getLinhas();
		if (itens.length == 0) {
			throw new Exception("Selecione um produto");
		}
		final boolean confirmaOperacao = contexto.confirmarSimNao("Deseja continuar?",
				"Foi selecionado " + itens.length + " registro (s) para despregar do item.", 0);

		if (!confirmaOperacao) {
			contexto.setMensagemRetorno("Operação cancelada");
		} else {
			BigDecimal nunota = BigDecimal.ZERO;
			for (Registro produtoSelecionado : itens) {
				nunota = new BigDecimal(produtoSelecionado.getCampo("NUNOTA").toString());

				if (produtoSelecionado.getCampo("CODVOL").equals("KT")) {
					// Método busca os itens do Kit
					Collection<DynamicVO> itemKitVo = getItensComponsicaoKit(
							new BigDecimal(produtoSelecionado.getCampo("NUNOTA").toString()),
							new BigDecimal(produtoSelecionado.getCampo("SEQUENCIA").toString()));
					
					for (DynamicVO buscaKitItem : itemKitVo) {
						ajusteItemKit(buscaKitItem);
						excluirKit(produtoSelecionado);
						//excluirItensKit(buscaKitItem);
							
					}

				} else {
					String mensagem = String.format("O item %s não é um kit",
							produtoSelecionado.getCampo("CODPROD").toString());
					throw new Exception(mensagem);
				}
			}			
			contexto.setMensagemRetorno("Operação finalizada com sucesso!");
		}
	}

	public Collection<DynamicVO> getItensComponsicaoKit(BigDecimal nunota, BigDecimal sequenciaOrigem)
			throws Exception {
		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
		String expressaoFiltro = "this.NUNOTA = " + nunota + " AND this.NUNOTA IN (SELECT VAR.NUNOTA "
				+ "FROM TGFVAR VAR " + "WHERE VAR.NUNOTA = this.NUNOTA " + "AND VAR.SEQUENCIA = this.SEQUENCIA "
				+ "AND VAR.SEQUENCIAORIG = " + sequenciaOrigem + ")";

		Collection<DynamicVO> filtroItemVO = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.ITEM_NOTA, expressaoFiltro));

		return (Collection<DynamicVO>) filtroItemVO;
	}

	public void ajusteItemKit(DynamicVO itemVo) throws Exception {
		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
		FinderWrapper filtroItem = new FinderWrapper(DynamicEntityNames.ITEM_NOTA, "this.NUNOTA = ? AND this.SEQUENCIA = ?", new Object[] {itemVo.asBigDecimal("NUNOTA"), itemVo.asBigDecimal("SEQUENCIA")});
		Collection<PersistentLocalEntity> finderFinanceiroCPLE = dwf.findByDynamicFinder(filtroItem);
    	
			for (PersistentLocalEntity linha  : finderFinanceiroCPLE) 
	    	{	
				EntityVO finderItemEVO = linha.getValueObject();
	    		DynamicVO dyitemVO = (DynamicVO) finderItemEVO;
	    		
	    		dyitemVO.setProperty("SEQUENCIA", itemVo.asBigDecimal("SEQUENCIA"));
	    		dyitemVO.setProperty("USOPROD", "R");
	    		dyitemVO.setProperty("QTDFORMULA", null);
	    		dyitemVO.setProperty("ATUALESTTERC", "N");
	    		dyitemVO.setProperty("TERCEIROS", "N");
	    		linha.setValueObject((EntityVO) dyitemVO);		
	    	}
	}

	public void excluirItensKit(DynamicVO itemVo) throws Exception {
		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
		/************************************************************************* 
		 * Deverá remover os itens dos kit da TGFVAR pois ao excluir o item
		 * ainda permanece na tabela.
		 **************************************************************************/
		FinderWrapper finderItemVar = new FinderWrapper(DynamicEntityNames.ITEM_NOTA, "NUNOTA = "
				+ itemVo.asBigDecimal("NUNOTA") + " AND SEQUENCIA = " + itemVo.asBigDecimal("SEQUENCIA"));
		dwf.removeByCriteria(finderItemVar);		
	}

	public void excluirKit(Registro item) throws Exception {
		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
		try {
			FinderWrapper finderItem = new FinderWrapper(DynamicEntityNames.ITEM_NOTA,
					"NUNOTA = " + new BigDecimal(item.getCampo("NUNOTA").toString()) + " AND SEQUENCIA = "
							+ new BigDecimal(item.getCampo("SEQUENCIA").toString()));

			dwf.removeByCriteria(finderItem);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
