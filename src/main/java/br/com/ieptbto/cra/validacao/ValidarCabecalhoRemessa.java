package br.com.ieptbto.cra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;

@Service
public class ValidarCabecalhoRemessa {

	@Autowired
	private RegraVerificarCidadeCodigoIBGE regraVerificarCidadeCodigoIBGE;
	@Autowired
	private RegraVerificarCidadeAtiva regraVerificarCidadeAtiva;
	@Autowired
	private RegraVerificarSequencialCabecalho regraVerificarSequencialCabecalho;
	private CabecalhoRemessa cabecalho;
	private List<Exception> erros;

	public void validar(CabecalhoRemessa cabecalho, List<Exception> erros) {
		this.cabecalho = cabecalho;
		this.erros = erros;

		executarRegras();
	}

	private void executarRegras() {
		regraVerificarCidadeCodigoIBGE.executar(getCabecalho(), getErros());
		regraVerificarCidadeAtiva.executar(getCabecalho(), getErros());
		regraVerificarSequencialCabecalho.executar(getCabecalho(), getErros());

	}

	public CabecalhoRemessa getCabecalho() {
		return cabecalho;
	}

	public List<Exception> getErros() {
		return erros;
	}

}
