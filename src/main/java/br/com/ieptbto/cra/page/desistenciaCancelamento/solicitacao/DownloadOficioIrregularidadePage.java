package br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.component.CustomFeedbackPanel;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoSolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.enumeration.regra.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.webpage.AbstractWebPage;

@SuppressWarnings("serial")
public class DownloadOficioIrregularidadePage extends AbstractWebPage<Usuario> {

	protected static final Logger logger = Logger.getLogger(DownloadOficioIrregularidadePage.class);
	
	// Url: DownloadOficioIrregularidade?nossoNumero=MWR6523&tipoSolicitacao=dp&irregularidade=

	@SpringBean
	DownloadMediator downloadMediator;
	private Usuario usuario;
	private CustomFeedbackPanel feedBackPanel;
	private IRequestParameters requestParams;
	
	public DownloadOficioIrregularidadePage() {
		this.usuario = new Usuario();
		this.requestParams = RequestCycle.get().getRequest().getRequestParameters();
		
		downloadOficioDesistenciaCacelamento();
		adicionarComponentes();
	}

	private void adicionarComponentes() {
		add(feedbackPanel());
	}

	private FeedbackPanel feedbackPanel(){
		feedBackPanel = new CustomFeedbackPanel(WID_FEEDBACK);
		feedBackPanel.setOutputMarkupId(true);
		return feedBackPanel;
	}
	
	private void downloadOficioDesistenciaCacelamento() {

		try {
			File file = downloadMediator.baixarOficioDesistenciaCancelamento(getNossoNumero(), getTipoSolicitacao(), getCodigoIrregularidade());
			
			if (file != null) {
				IResourceStream resourceStream = new FileResourceStream(file);
				getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, file.getName()));
			} else {
				info("Não há arquivo de ofício anexo para este Cancelamento/Devolução de protesto...");
			}
		} catch (InfraException ex) {
			error(ex.getMessage());
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			error("Não foi possível fazer o download do arquivo anexo. Favor entrar em contato com a CRA...");
		}
	}

	private CodigoIrregularidade getCodigoIrregularidade() {
		String irregularidade = this.requestParams.getParameterValue("irregularidade").toString();
		if (irregularidade != null) {
			if (StringUtils.isNotBlank(irregularidade)) {
				return CodigoIrregularidade.getIrregularidade(irregularidade);
			}
		}
		return CodigoIrregularidade.IRREGULARIDADE_0;
	}

	private TipoSolicitacaoDesistenciaCancelamento getTipoSolicitacao() {
		String tipoSolicitacao = this.requestParams.getParameterValue("tipoSolicitacao").toString();
		
		try {
			if (StringUtils.isNotBlank(tipoSolicitacao)) {
				TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(tipoSolicitacao.toUpperCase().trim());
				
				if (tipoArquivo.equals(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO)) {
					if (getCodigoIrregularidade() == CodigoIrregularidade.IRREGULARIDADE_0) {
						return TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO;
					}
					return TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO_IRREGULARIDADE;
				} else if (tipoArquivo.equals(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO)) {
					return TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_CANCELAMENTO_PROTESTO;
				} else if (tipoArquivo.equals(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO)) {
					return TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_AUTORIZACAO_CANCELAMENTO;
				} else {
					throw new InfraException("O Tipo da Solicitação informado é inválido, ou não existe. Favor entrar em contato com a CRA...");
				}
			} else {
				throw new InfraException("O Tipo da Solicitação informado é inválido, ou não existe. Favor entrar em contato com a CRA...");
			}
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			throw new InfraException("O Tipo da Solicitação informado é inválido, ou não existe. Favor entrar em contato com a CRA...");
		}
	}
	
	private String getNossoNumero() {
		String nossoNumero = this.requestParams.getParameterValue("nossoNumero").toString();
		if (StringUtils.isBlank(nossoNumero)) {
			return StringUtils.EMPTY;
		}
		return nossoNumero;
	}
	
	public IModel<Usuario> getModel() {
		return new CompoundPropertyModel<Usuario>(usuario);
	}
}