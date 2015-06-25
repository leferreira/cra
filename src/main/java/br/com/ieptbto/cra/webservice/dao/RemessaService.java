package br.com.ieptbto.cra.webservice.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.ConversorArquivoVo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
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
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	private String arquivoRecebido;
	private List<RemessaVO> remessas;
	private ArquivoVO arquivoVO;

	/**
	 * 
	 * @param nomeArquivo
	 * @param usuario2
	 * @param arquivoRecebido
	 * @return
	 */
	public String processar(String nomeArquivo, Usuario usuario, ArquivoVO arquivoRecebido) {
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		setArquivoVO(arquivoRecebido);

		if (getArquivoVO() == null || getUsuario() == null) {
			ArquivoVO arquivo = new ArquivoVO();
			return setResposta(arquivo, nomeArquivo);
		}

		setRemessas(ConversorArquivoVo.converterParaRemessaVO(getArquivoVO()));

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

	public ArquivoVO getArquivoVO() {
		return arquivoVO;
	}

	public void setArquivoVO(ArquivoVO arquivoVO) {
		this.arquivoVO = arquivoVO;
	}

}
