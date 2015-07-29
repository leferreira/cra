package br.com.ieptbto.cra.webservice.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.mediator.RemessaMediator;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ConfirmacaoService extends CraWebService {

	@Autowired
	private RemessaMediator remessaMediator;

	public String processar(String nomeArquivo, Usuario usuario) {
		ArquivoVO arquivoVO = null;
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		if (getUsuario() == null) {
			return setResposta(arquivoVO, nomeArquivo);
		}

		if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.equals(getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao())) {
			arquivoVO = remessaMediator.buscarArquivos(getNomeArquivo());
		} else if (TipoInstituicaoCRA.CARTORIO.equals(getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao())) {
			arquivoVO = remessaMediator.buscarRemessaParaCartorio(getUsuario().getInstituicao(), getNomeArquivo());
		}

		if (getNomeArquivo() == null) {
			return setResposta(arquivoVO, nomeArquivo);
		}
		return setResposta(arquivoVO, getNomeArquivo());
	}
}
