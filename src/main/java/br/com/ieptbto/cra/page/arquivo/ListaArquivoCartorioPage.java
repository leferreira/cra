package br.com.ieptbto.cra.page.arquivo;

import br.com.ieptbto.cra.beans.ArquivoBean;
import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoCampo51;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.report.RelatorioUtil;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class ListaArquivoCartorioPage extends BasePage<Arquivo> {

	@SpringBean
	private RemessaMediator remessaMediator;
	@SpringBean
	private DownloadMediator downloadMediator;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;

	private static final long serialVersionUID = 1L;
	private Usuario usuario;
	private Arquivo arquivo;
	private ArquivoBean arquivoBean;

	public ListaArquivoCartorioPage(ArquivoBean arquivoBean, Usuario usuario) {
		this.arquivo = new Arquivo();
		this.arquivoBean = arquivoBean;
		this.usuario = getUser();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		listaArquivos();
	}

	private void listaArquivos() {
		add(new ListView<Remessa>("dataTableRemessa", buscarArquivos()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(downloadArquivoTXT(remessa));
				item.add(relatorioArquivo(remessa));

				item.add(new Label("sequencialCabecalho", remessa.getCabecalho().getNumeroSequencialRemessa()));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoPage(remessa));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));

				if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.REMESSA)) {
					String instituicao = remessa.getInstituicaoOrigem().getNomeFantasia();
					item.add(new Label("instituicao", instituicao));
					item.add(new Label("envio", remessa.getArquivo().getInstituicaoRecebe().getNomeFantasia()));
					item.add(new Label("destino", remessa.getInstituicaoDestino().getNomeFantasia()));
					item.add(downloadAnexos(remessa));
				} else {
					String instituicao = remessa.getInstituicaoDestino().getNomeFantasia();
					item.add(new Label("instituicao", instituicao));
					item.add(new Label("envio", remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia()));
					item.add(new Label("destino", remessa.getArquivo().getInstituicaoRecebe().getNomeFantasia()));
					item.add(new Label("downloadAnexos", StringUtils.EMPTY));
				}
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(remessa.getArquivo().getHoraEnvio())));
				WebMarkupContainer divInfo = new WebMarkupContainer("divInfo");
				divInfo.add(new AttributeAppender("id", remessa.getStatusDownload().getLabel()));
				divInfo.add(new Label("status", remessa.getStatusDownload().getLabel().toUpperCase()));
				item.add(divInfo);
			}

			private Link<Remessa> downloadArquivoTXT(final Remessa remessa) {
				return new Link<Remessa>("downloadArquivo") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						try {
							File file = downloadMediator.baixarRemessaTXT(getUser(), remessa);
							IResourceStream resourceStream = new FileResourceStream(file);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, remessa.getArquivo().getNomeArquivo()));
						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
						} catch (Exception e) {
							logger.info(e.getMessage(), e);
							getFeedbackPanel().error("Não foi possível fazer o download do arquivo ! Favor entrar em contato com a CRA...");
						}
					}
				};
			}

			private Link<Remessa> downloadAnexos(final Remessa remessa) {
				Anexo anexo = null;
				if (remessa.getInstituicaoOrigem().getTipoCampo51().equals(TipoCampo51.DOCUMENTOS_COMPACTADOS)) {
					anexo = remessaMediator.verificarAnexosRemessa(remessa);
				}

				Link<Remessa> linkAnexos = new Link<Remessa>("downloadAnexos") {

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

			private Link<Remessa> relatorioArquivo(final Remessa remessa) {
				return new Link<Remessa>("gerarRelatorio") {

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
							getFeedbackPanel().error(ex.getMessage());
						} catch (Exception e) {
							getFeedbackPanel().error("Não foi possível gerar o relatório do arquivo. Favor entrar em contato com a CRA...");
							logger.info(e.getMessage(), e);
						}
					}
				};
			}
		});
	}

	public IModel<List<Remessa>> buscarArquivos() {
		return new LoadableDetachableModel<List<Remessa>>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected List<Remessa> load() {
				ArquivoBean bean = getArquivoFormBean();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;

				if (bean.getDataInicio() != null) {
					dataInicio = new LocalDate(bean.getDataInicio());
				}
				if (bean.getDataFim() != null) {
					dataFim = new LocalDate(bean.getDataFim());
				}
				return remessaMediator.buscarRemessas(usuario, bean.getNomeArquivo(), dataInicio, dataFim, bean.getTipoInstituicao(), bean.getBancoConvenio(),
						bean.getCartorio(), bean.getTiposArquivos(), bean.getSituacoesArquivos());
			}
		};
	}

	public ArquivoBean getArquivoFormBean() {
		return arquivoBean;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}