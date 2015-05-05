package br.com.ieptbto.cra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.ValidacaoErroException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.validacao.regra.RegraCabecalho;

@Service
public class RegraVerificarCidadeAtiva extends RegraCabecalho {

	@Autowired
	private InstituicaoMediator instituicaoMediator;

	protected void executar(CabecalhoRemessa cabecalhoRemessa, List<Exception> erros) {
		setErros(erros);
		setCabecalhoRemessa(cabecalhoRemessa);
	}

	@Override
	protected void executar() {
		Instituicao instituicao = instituicaoMediator.getInstituicaoPorCodigoIBGE(getCabecalhoRemessa().getCodigoMunicipio());
		if (instituicao == null || !instituicao.isSituacao()) {
			getErros().add(
			        new ValidacaoErroException(getCabecalhoRemessa().getRemessa().getArquivo().getNomeArquivo(),
			                Erro.INSTITUICAO_NAO_ATIVA, ""));
			logger.error(Erro.INSTITUICAO_NAO_ATIVA.getMensagemErro());
		}

	}

}
