package br.com.ieptbto.cra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.ValidacaoErroException;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.validacao.regra.RegraCabecalho;

@Service
public class RegraVerificarCidadeCodigoIBGE extends RegraCabecalho {

	@Autowired
	private MunicipioMediator municipioMediator;

	public void executar(CabecalhoRemessa cabecalho, List<Exception> erros) {
		setErros(erros);
		setCabecalhoRemessa(cabecalho);
		executar();

	}

	@Override
	protected void executar() {
		Municipio municipio = municipioMediator.buscaMunicipioPorCodigoIBGE(getCabecalhoRemessa().getCodigoMunicipio());
		if (municipio == null) {
			getErros().add(
			        new ValidacaoErroException(getCabecalhoRemessa().getRemessa().getArquivo().getNomeArquivo(),
			                Erro.CODIGO_IBGE_NAO_CADASTRADO, getCabecalhoRemessa().getCodigoMunicipio().toString()));
			logger.error(Erro.CODIGO_IBGE_NAO_CADASTRADO.getMensagemErro());
		}

	}
}
