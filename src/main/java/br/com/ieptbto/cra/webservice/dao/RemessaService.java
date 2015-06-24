package br.com.ieptbto.cra.webservice.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.UsuarioMediator;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RemessaService extends CraWebService {

	@Autowired
	private RemessaMediator remessaMediator;
	@Autowired
	private UsuarioMediator usuarioMediator;
	private String arquivoRecebido;
	private List<RemessaVO> remessas;

	/**
	 * 
	 * @param nomeArquivo
	 * @param usuario2
	 * @param arquivoRecebido
	 * @return
	 */
	public String processar(String nomeArquivo, Usuario usuario, String arquivoRecebido) {
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		setArquivoRecebido(arquivoRecebido);

		if (getArquivoRecebido() == null || getUsuario() == null) {
			ArquivoVO arquivo = new ArquivoVO();
			return setResposta(arquivo, nomeArquivo);
		}

		// ConversorArquivoVo.converterParaRemessaVO(arquivoRecebido);

		return gerarMensagem(remessaMediator.processarArquivoXML(getRemessas(), getUsuario(), nomeArquivo), "relatorio");
	}

	public String getArquivoRecebido() {
		return arquivoRecebido;
	}

	public void setArquivoRecebido(String arquivoRecebido) {
		this.arquivoRecebido = arquivoRecebido;
	}

	public List<RemessaVO> getRemessas() {
		return remessas;
	}

	public void setRemessas(List<RemessaVO> remessas) {
		this.remessas = remessas;
	}

}
