package br.com.ieptbto.cra.page.centralDeAcoes;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class LogCraPage extends BasePage<LogCra> {

	@SpringBean
	private DownloadMediator downloadMediator;
	
	private static final long serialVersionUID = 1L;
	private LogCra logCra;

	public LogCraPage(LogCra logCra) {
		this.logCra = logCra;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(labelAcao());
		add(textAreaDescricao());
		add(labelTipoLog());
		add(labelDataHora());
		add(labelInstituicao());
		add(labelUsuario());
		add(buttonDownloadRelatorio());
		add(textAreaException());
	}

	private Label labelAcao() {
		return new Label("acao", new Model<String>(logCra.getAcao().getLabel()));
	}

	private Label textAreaDescricao() {
		String descricao = logCra.getDescricao();
		if (StringUtils.isBlank(descricao)) {
			descricao = "";
		}
		Label Label = new Label("descricao", new Model<String>(descricao));
		Label.setEnabled(false);
		Label.setEscapeModelStrings(false);
		return Label;
	}

	private WebMarkupContainer labelTipoLog() {
		WebMarkupContainer divTipoOcorrencia = new WebMarkupContainer("divTipoOcorrencia");
		divTipoOcorrencia.setMarkupId(getModel().getObject().getTipoLog().getIdHtml());
		divTipoOcorrencia.setOutputMarkupId(true);
		divTipoOcorrencia.add(new Label("tipoLog.label", new Model<String>(logCra.getTipoLog().getLabel())));
		return divTipoOcorrencia;
	}

	private Label labelDataHora() {
		return new Label("dataHora", new Model<String>(DataUtil.localDateToString(logCra.getData()) + 
				" ás " + DataUtil.localTimeToString("HH:mm:ss", logCra.getHora())));
	}

	private Label labelInstituicao() {
		return new Label("instituicao", new Model<String>(logCra.getInstituicao()));
	}

	private Label labelUsuario() {
		Label label = new Label("usuario", new Model<String>(logCra.getUsuario()));
		label.setEnabled(false);
		return label;
	}

	private Label textAreaException() {
		Label textArea = null;
		if (getModel().getObject().getExcecao() == null) {
			textArea = new Label("exception", new Model<String>("Não foi registrada nenhuma exceção nesta ação..."));
		} else {
			String trace = getModel().getObject().getExcecao().getMessage() + "\n";
			StackTraceElement[] stackTrace = getModel().getObject().getExcecao().getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				trace = trace.concat(stackTraceElement.toString() + "\n");
			}
			textArea = new Label("exception", new Model<String>(trace));
		}
		textArea.setEnabled(false);
		return textArea;
	}
	
	private Link<Void> buttonDownloadRelatorio() {
		return new Link<Void>("buttonDownloadRelatorio") {

			private static final long serialVersionUID = 1L;

			 @Override
			public void onClick() {
				if (StringUtils.isBlank(logCra.getRelatorio())) {
					warn("O log da ação não registrou nenhum tipo de relatório de resposta...");
					return;
				}
				
				try {
					File file = downloadMediator.downloadRelatorioLogCra(logCra);
					IResourceStream resourceStream = new FileResourceStream(file);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, 
							logCra.getNomeArquivoRelatorio() + ConfiguracaoBase.EXTENSAO_ARQUIVO_XML));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					logger.info(e.getMessage(), e);
					error("Não foi possível fazer o download do arquivo ! Favor entrar em contato com a CRA...");
				}
			}
		};
	}

	@Override
	protected IModel<LogCra> getModel() {
		return new CompoundPropertyModel<LogCra>(logCra);
	}
}
