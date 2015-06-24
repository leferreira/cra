package br.com.ieptbto.cra.webservice.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
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
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		ArquivoVO arquivoVO = remessaMediator.buscarArquivos(getNomeArquivo());

		if (getNomeArquivo() == null || getUsuario() == null) {
			return setResposta(arquivoVO, nomeArquivo);
		}

		return setResposta(arquivoVO, getNomeArquivo());
	}

}
