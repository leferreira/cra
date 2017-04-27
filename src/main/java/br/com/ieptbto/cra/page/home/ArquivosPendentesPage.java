package br.com.ieptbto.cra.page.home;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.view.*;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.*;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosAutorizacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosDesistenciaPage;
import br.com.ieptbto.cra.report.RelatorioUtil;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.PeriodoDataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
public class ArquivosPendentesPage extends BasePage<Arquivo> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	DownloadMediator downloadMediator;
	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	DesistenciaProtestoMediator desistenciaMediator;
	@SpringBean
	CancelamentoProtestoMediator cancelamentoMediator;
	@SpringBean
	AutorizacaoCancelamentoMediator autorizacaoMediator;

	private Arquivo arquivo;
	private Usuario usuario;
	private List<ViewArquivoPendente> arquivosPendentes;

	public ArquivosPendentesPage(Usuario usuario, List<ViewArquivoPendente> arquivosPendentes) {
		this.usuario = usuario;
		this.arquivosPendentes = arquivosPendentes;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(listaArquivos());
	}

	private ListView<ViewArquivoPendente> listaArquivos() {
		return new ListView<ViewArquivoPendente>("dataTableArquivosPendentes", arquivosPendentes) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ViewArquivoPendente> item) {
				final ViewArquivoPendente object = item.getModelObject();
				
				if (RemessaPendente.class.isInstance(object)) {
					final RemessaPendente arquivo = RemessaPendente.class.cast(object);
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosArquivoPage(arquivo.getIdRemessa_Remessa()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo_Arquivo()));
					item.add(linkArquivo);
					item.add(relatorioArquivo(arquivo));
					item.add(downloadAnexos(arquivo));
					item.add(downloadArquivoTXT(TipoArquivoFebraban.REMESSA, arquivo.getIdRemessa_Remessa(), arquivo.getNomeArquivo_Arquivo()));
					
				} else if (DesistenciaPendente.class.isInstance(object)) {
					final DesistenciaPendente arquivo = DesistenciaPendente.class.cast(object);
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosDesistenciaPage(arquivo.getIdDesistencia_DesistenciaProtesto()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo_Arquivo()));
					item.add(linkArquivo);
					item.add(new Label("gerarRelatorio").setVisible(false));
					item.add(new Label("downloadAnexos").setVisible(false));
					item.add(downloadArquivoTXT(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO, arquivo.getIdDesistencia_DesistenciaProtesto(), 
							arquivo.getNomeArquivo_Arquivo()));
					
				} else if (CancelamentoPendente.class.isInstance(object)) {
					final CancelamentoPendente arquivo = CancelamentoPendente.class.cast(object);
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosCancelamentoPage(arquivo.getIdCancelamento_CancelamentoProtesto()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo_Arquivo()));
					item.add(linkArquivo);
					item.add(new Label("gerarRelatorio").setVisible(false));
					item.add(new Label("downloadAnexos").setVisible(false));
					item.add(downloadArquivoTXT(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO, arquivo.getIdCancelamento_CancelamentoProtesto(), 
							arquivo.getNomeArquivo_Arquivo()));
					
				} else if (AutorizacaoPendente.class.isInstance(object)) {
					final AutorizacaoPendente arquivo = AutorizacaoPendente.class.cast(object);
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosAutorizacaoCancelamentoPage(arquivo.getIdAutorizacao_AutorizacaoCancelamento()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo_Arquivo()));
					item.add(linkArquivo);
					item.add(new Label("gerarRelatorio").setVisible(false));
					item.add(new Label("downloadAnexos").setVisible(false));
					item.add(downloadArquivoTXT(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO, arquivo.getIdAutorizacao_AutorizacaoCancelamento(), 
							arquivo.getNomeArquivo_Arquivo()));
				}
				item.add(new Label("dataEnvio", DataUtil.localDateToString(object.getDataEnvio_Arquivo())));
				item.add(new Label("instituicao", object.getNomeFantasia_Instituicao()));
				item.add(new Label("envio", "CRA"));
				item.add(new Label("destino", object.getNomeFantasia_Cartorio()));
				item.add(new Label("dias", PeriodoDataUtil.diferencaDeDiasEntreData(object.getDataRecebimento_Arquivo(), new Date())));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(object.getHoraEnvio_Arquivo())));

				WebMarkupContainer divInfo = new WebMarkupContainer("divInfo");
				divInfo.add(new AttributeAppender("id", StatusDownload.getStatus(object.getStatusDownload()).getLabel()));
				divInfo.add(new Label("status", object.getStatusDownload()));
				item.add(divInfo);
			}

			private Link<Remessa> downloadArquivoTXT(final TipoArquivoFebraban tipoArquivo, final Integer id, final String nomeArquivo) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = null;

						try {
							if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {
								Remessa remessa = remessaMediator.buscarRemessaPorPK(id);
								file = downloadMediator.baixarRemessaTXT(usuario, remessa);
							} else if (TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)) {
								DesistenciaProtesto dp = desistenciaMediator.buscarDesistenciaPorPK(id);
								file = downloadMediator.baixarDesistenciaTXT(usuario, dp);
							} else if (TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
								CancelamentoProtesto cp = cancelamentoMediator.buscarCancelamentoPorPK(id);
								file = downloadMediator.baixarCancelamentoTXT(usuario, cp);
							} else if (TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
								AutorizacaoCancelamento ac = autorizacaoMediator.buscarAutorizacaoPorPK(id);
								file = downloadMediator.baixarAutorizacaoTXT(usuario, ac);
							}

							IResourceStream resourceStream = new FileResourceStream(file);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, nomeArquivo));
						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							logger.info(e.getMessage(), e);
							error("Não foi possível baixar o arquivo! Favor entrar em contato com a CRA...");
						}
					}
				};
			}

			private Link<Remessa> downloadAnexos(final RemessaPendente arquivo) {
				Link<Remessa> linkAnexos = new Link<Remessa>("downloadAnexos") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						Remessa remessa = remessaMediator.buscarRemessaPorPK(arquivo.getIdRemessa_Remessa());

						try {
							File file = remessaMediator.processarArquivosAnexos(usuario, remessa);
							IResourceStream resourceStream = new FileResourceStream(file);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, file.getName()));
						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							logger.info(e.getMessage(), e);
							error("Não foi possível baixar os anexos! Favor entrar em contato com a CRA...");
						}
					}
				};
				if (!arquivo.isDocumentosAnexos_Anexo()) {
					linkAnexos.setOutputMarkupId(false);
					linkAnexos.setVisible(false);
				}
				return linkAnexos;
			}

			private Link<Remessa> relatorioArquivo(final RemessaPendente arquivo) {
				return new Link<Remessa>("gerarRelatorio") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						Remessa remessa = remessaMediator.buscarRemessaPorPK(arquivo.getIdRemessa_Remessa());

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
							error("Não foi possível gerar o relatório do arquivo! Favor entrar em contato com a CRA...");
							logger.info(e.getMessage(), e);
						}
					}
				};
			}
		};
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}