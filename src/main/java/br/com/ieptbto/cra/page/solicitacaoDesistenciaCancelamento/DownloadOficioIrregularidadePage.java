package br.com.ieptbto.cra.page.solicitacaoDesistenciaCancelamento;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.TipoSolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.page.cra.MensagemPage;

public class DownloadOficioIrregularidadePage extends WebPage {

	/***/
	private static final long serialVersionUID = 1L;
	private static final String DP = "DP";
	private static final String CP = "CP";

	@SpringBean
	private DownloadMediator downloadMediator;

	private String nossoNumero;
	private String irregularidade;
	private String tipoSolicitacao;

	public DownloadOficioIrregularidadePage() {
		this.nossoNumero = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("nossoNumero").toString();
		this.tipoSolicitacao = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("tipoSolicitacao").toString();
		this.irregularidade = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("irregularidade").toString();

		downloadOficioDesistenciaCacelamento();
	}

	private void downloadOficioDesistenciaCacelamento() {

		try {
			File file = downloadMediator.baixarOficioDesistenciaCancelamento(nossoNumero, getTipoSolicitacao(), getCodigoIrregularidade());

			if (file != null) {
				IResourceStream resourceStream = new FileResourceStream(file);
				getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, file.getName()));

			} else {
				setResponsePage(new MensagemPage<>("Download Ofício Desistência Cancelamento",
						"O título não foi encontrado ou não existe ofício para download!"));
			}
		} catch (InfraException ex) {
			setResponsePage(new MensagemPage<>("Download Ofício Desistência Cancelamento", ex.getMessage()));
		} catch (Exception ex) {
			setResponsePage(new MensagemPage<>("Download Ofício Desistência Cancelamento", ex.getMessage()));
		}
	}

	private CodigoIrregularidade getCodigoIrregularidade() {
		CodigoIrregularidade codigoIrregularidade = null;

		if (this.irregularidade != null) {
			if (StringUtils.isNotBlank(irregularidade)) {
				codigoIrregularidade = CodigoIrregularidade.getIrregularidade(irregularidade);
			}
		}
		if (codigoIrregularidade == null || codigoIrregularidade.equals(CodigoIrregularidade.IRREGULARIDADE_0)) {
			setResponsePage(new MensagemPage<>("Download Ofício Desistência Cancelamento",
					"Parâmetros de Código Irregularidade Informado é inválido!"));
		} else {
			return codigoIrregularidade;
		}
		setResponsePage(new MensagemPage<>("Download Ofício Desistência Cancelamento",
				"Parâmetros de Código Irregularidade Informado é inválido!"));
		return null;
	}

	private TipoSolicitacaoDesistenciaCancelamento getTipoSolicitacao() {
		if (this.tipoSolicitacao != null) {
			if (StringUtils.isNotBlank(tipoSolicitacao) && tipoSolicitacao.trim().equals(DP)) {
				return TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO_IRREGULARIDADE;
			} else if (StringUtils.isNotBlank(tipoSolicitacao) && tipoSolicitacao.equals(CP)) {
				return TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_CANCELAMENTO_PROTESTO;
			}
		}
		setResponsePage(
				new MensagemPage<>("Download Ofício Desistência Cancelamento", "Parâmetros de Tipo de Solicitação Informado é inválido!"));
		return null;
	}
}