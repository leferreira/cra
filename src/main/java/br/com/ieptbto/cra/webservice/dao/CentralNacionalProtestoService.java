package br.com.ieptbto.cra.webservice.dao;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;
import br.com.ieptbto.cra.enumeration.TipoAcaoLog;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.mediator.CentralNacionalProtestoMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CentralNacionalProtestoService extends CnpWebService {

	@Autowired
	CentralNacionalProtestoMediator centralNacionalProtestoMediator;

	public String processar(Usuario usuario) {
		ArquivoCnpVO arquivoCnp = new ArquivoCnpVO();
		setUsuario(usuario);

		try {
			if (getUsuario() == null) {
				return usuarioInvalido();
			}
			if (!getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
				return usuarioNaoPermitidoConsultaArquivoCNP();
			}
			// if (!new LocalTime().isAfter(getHoraInicioServicoConsulta()) &&
			// !new LocalTime().isBefore(getHoraFimServicoConsulta())) {
			// return servicoNaoDisponivelForaDoHorarioConsulta(usuario);
			// }

			if (centralNacionalProtestoMediator.isArquivoJaDisponibilizadoConsultaPorData(new LocalDate())) {
				arquivoCnp = centralNacionalProtestoMediator.buscarArquivoNacionalPorData(new LocalDate());
			} else {
				arquivoCnp = centralNacionalProtestoMediator.gerarArquivoNacional();
				if (arquivoCnp.getRemessasCnpVO().isEmpty()) {
					return naoHaRemessasParaArquivoCnp(usuario);
				}
			}
			loggerCra.sucess(getUsuario(), TipoAcaoLog.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Arquivo CNP do dia " + DataUtil.localDateToString(new LocalDate()) + ", enviado com sucesso para o IEPTB nacional.");
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex.getCause());
			ex.printStackTrace();
			loggerCra.error(getUsuario(), TipoAcaoLog.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Erro interno ao construir o arquivo da CNP do dia " + DataUtil.localDateToString(new LocalDate()) + ".", ex);
			return gerarMensagem(gerarMensagemErroProcessamento(), CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagem(arquivoCnp, CONSTANTE_CNP_XML);
	}
}