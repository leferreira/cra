package br.com.ieptbto.cra.webservice.dao;

import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.mediator.CentralNacionalProtestoMediator;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CentralNacionalProtestoService extends CnpWebService {

	@Autowired
	private CentralNacionalProtestoMediator centralNacionalProtestoMediator;

	public String processar(Usuario usuario) {
		ArquivoCnpVO arquivoCnp = new ArquivoCnpVO();
		setUsuario(usuario);
		
		try {
			if (getUsuario() == null) {
				return usuarioInvalido();
			}
			if (getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
				return usuarioNaoPermitidoConsultaArquivoCNP();
			}
			if (new LocalTime().isAfter(getHoraInicioServicoConsulta()) && new LocalTime().isBefore(getHoraFimServicoConsulta())) { 
				return servicoNaoDisponivelForaDoHorarioConsulta(usuario);		
			}
			
			arquivoCnp = centralNacionalProtestoMediator.gerarArquivoNacional();
			if (arquivoCnp.getRemessasCnpVO().isEmpty()) {
				return naoHaRemessasParaArquivoCnp(usuario);
			}
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex.getCause());
			return gerarMensagem(gerarMensagemErroProcessamento(), CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagem(new ArquivoCnpVO(),CONSTANTE_CNP_XML);
	}
}
