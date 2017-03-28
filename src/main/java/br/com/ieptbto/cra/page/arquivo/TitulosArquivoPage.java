package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.component.dataTable.CraDataTable;
import br.com.ieptbto.cra.component.dataTable.TituloViewColumns;
import br.com.ieptbto.cra.dataProvider.TituloProvider;
import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.view.ViewTitulo;
import br.com.ieptbto.cra.enumeration.TipoCampo51;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.report.RelatorioUtil;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class TitulosArquivoPage extends BasePage<Remessa> {

	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	DownloadMediator downloadMediator;
	@SpringBean
	TituloMediator tituloMediator;

	private static final long serialVersionUID = 1L;
	private Remessa remessa;

	public TitulosArquivoPage(Remessa remessa) {
		this.remessa = remessaMediator.buscarRemessaPorPK(remessa);
		adicionarComponentes();
	}

	public TitulosArquivoPage(Integer idRemessa) {
		this.remessa = remessaMediator.buscarRemessaPorPK(idRemessa);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(divInformacoesArquivo());
		add(dataTableTitulos());
		add(acoesArquivoMenu());
	}

	private CraDataTable<ViewTitulo> dataTableTitulos() {
		TituloProvider dataProvider = new TituloProvider(tituloMediator.consultarViewTitulosPorRemessa(remessa));
		List<IColumn<ViewTitulo, String>> columns = TituloViewColumns.generateDataTableColumnsFromTituloView();
		return new CraDataTable<ViewTitulo>("table", columns, dataProvider);
	}

	private WebMarkupContainer divInformacoesArquivo() {
		WebMarkupContainer divInformacoes = new WebMarkupContainer("divInformacoes");
		divInformacoes.add(new Label("nomeArquivo", remessa.getArquivo().getNomeArquivo()));
		divInformacoes.add(new Label("dataEnvio", DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));
		divInformacoes.add(new Label("enviadoPor", remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia()));
		divInformacoes.add(new Label("usuario", remessa.getArquivo().getUsuarioEnvio().getNome()));
		divInformacoes.add(new Label("destino", remessa.getInstituicaoDestino().getNomeFantasia()));
		divInformacoes.add(infoDownloadRemessaBloqueado());
		divInformacoes.add(botaoGerarRelatorio());
		divInformacoes.add(botaoDownloadAnexos());
		divInformacoes.add(botaoDownloadArquivoTXT());
		return divInformacoes;
	}

	private WebMarkupContainer infoDownloadRemessaBloqueado() {
		WebMarkupContainer divDownloadRemessaBloqueado = new WebMarkupContainer("divDownloadRemessaBloqueado");
		divDownloadRemessaBloqueado.setOutputMarkupId(true);
		divDownloadRemessaBloqueado.setVisible(false);
		if (remessa.getDevolvidoPelaCRA() == true) {
			divDownloadRemessaBloqueado.setVisible(true);
		}
		return divDownloadRemessaBloqueado;
	}

	private WebMarkupContainer acoesArquivoMenu() {
		return new AcoesArquivoMenu("acoesArquivoMenu", getModel(), getUser());
	}

	private Link<Remessa> botaoDownloadAnexos() {
		Anexo anexo = null;
		if (remessa.getInstituicaoOrigem().getTipoCampo51().equals(TipoCampo51.DOCUMENTOS_COMPACTADOS)) {
			anexo = remessaMediator.verificarAnexosRemessa(remessa);
		}

		Link<Remessa> linkAnexos = new Link<Remessa>("downloadAnexos") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				try {
					File file = remessaMediator.processarArquivosAnexos(getUser(), remessa);
					IResourceStream resourceStream = new FileResourceStream(file);

					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, file.getName()));
				} catch (InfraException ex) {
					getFeedbackPanel().error(ex.getMessage());
				} catch (Exception e) {
					getFeedbackPanel().error("Não foi possível baixar o arquivo ! Favor entrar em contato com a CRA...");
					logger.info(e.getMessage(), e);
				}
			}
		};

		if (anexo == null) {
			linkAnexos.setOutputMarkupId(false);
			linkAnexos.setVisible(false);
		}
		return linkAnexos;
	}

	private Link<Remessa> botaoGerarRelatorio() {
		return new Link<Remessa>("gerarRelatorio") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				try {
					JasperPrint jasperPrint = new RelatorioUtil().relatorioArquivoCartorio(remessa);
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
									"CRA_RELATORIO_" + remessa.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
					e.printStackTrace();
				}
			}
		};
	}

	private Link<Remessa> botaoDownloadArquivoTXT() {
		return new Link<Remessa>("downloadArquivo") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				try {
					File file = downloadMediator.baixarRemessaTXT(getUser(), remessa);
					IResourceStream resourceStream = new FileResourceStream(file);

					getRequestCycle().scheduleRequestHandlerAfterCurrent(
									new ResourceStreamRequestHandler(resourceStream, remessa.getArquivo().getNomeArquivo()));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
				}
			}
		};
	}

	@Override
	protected IModel<Remessa> getModel() {
		return new CompoundPropertyModel<Remessa>(remessa);
	}
}