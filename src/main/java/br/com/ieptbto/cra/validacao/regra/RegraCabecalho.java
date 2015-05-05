package br.com.ieptbto.cra.validacao.regra;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;

/**
 * 
 * @author Lefer
 *
 */
public class RegraCabecalho extends Regra {

	protected CabecalhoRemessa CabecalhoRemessa;

	public CabecalhoRemessa getCabecalhoRemessa() {
		return CabecalhoRemessa;
	}

	public void setCabecalhoRemessa(CabecalhoRemessa cabecalhoRemessa) {
		CabecalhoRemessa = cabecalhoRemessa;
	}

	@Override
	protected void executar() {
		// TODO Auto-generated method stub

	}

}
