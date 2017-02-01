package br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao;

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

public class DownloadOficioIrregularidadePage extends WebPage {

	/***/
	private static final long serialVersionUID = 1L;
	private static final String DP = "DP";
	private static final String CP = "CP";

	@SpringBean
	DownloadMediator downloadMediator;
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
			}
		} catch (InfraException ex) {
		} catch (Exception ex) {
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
		} else {
			return codigoIrregularidade;
		}
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
		return null;
	}
}