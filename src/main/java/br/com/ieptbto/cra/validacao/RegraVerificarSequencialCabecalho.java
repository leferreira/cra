package br.com.ieptbto.cra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.ValidacaoErroException;
import br.com.ieptbto.cra.mediator.CabecalhoMediator;
import br.com.ieptbto.cra.validacao.regra.RegraCabecalho;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RegraVerificarSequencialCabecalho extends RegraCabecalho {

	@Autowired
	private CabecalhoMediator cabecalhoMediator;

	public void executar(CabecalhoRemessa cabecalho, List<Exception> erros) {
		setErros(erros);
		setCabecalhoRemessa(cabecalho);
		executar();
	}

	@Override
	protected void executar() {
		if (!cabecalhoMediator.isSequencialValido(getCabecalhoRemessa())) {
			getErros().add(
			        new ValidacaoErroException(getCabecalhoRemessa().getRemessa().getArquivo().getNomeArquivo(),
			                Erro.NUMERO_SEQUENCIAL_CABECALHO_INVALDIO, getCabecalhoRemessa().getNumeroSequencialRemessa()));
			logger.error(Erro.NUMERO_SEQUENCIAL_CABECALHO_INVALDIO.getMensagemErro() + " Sequencial:"
			        + getCabecalhoRemessa().getNumeroSequencialRemessa());
		}
	}

}
