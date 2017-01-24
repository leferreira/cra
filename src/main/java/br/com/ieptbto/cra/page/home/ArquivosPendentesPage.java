package br.com.ieptbto.cra.page.home;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

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

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.view.ViewArquivosPendentes;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.AutorizacaoCancelamentoMediator;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosAutorizacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosDesistenciaPage;
import br.com.ieptbto.cra.relatorio.RelatorioUtil;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.PeriodoDataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Thasso Araújo
 *
 */
public class ArquivosPendentesPage extends BasePage<Arquivo> {

	/***/
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
	private List<ViewArquivosPendentes> arquivosPendentes;

	public ArquivosPendentesPage(Usuario usuario, List<ViewArquivosPendentes> arquivosPendentes) {
		this.usuario = usuario;
		this.arquivosPendentes = arquivosPendentes;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(listaArquivos());
	}

	private ListView<ViewArquivosPendentes> listaArquivos() {
		return new ListView<ViewArquivosPendentes>("dataTableArquivosPendentes", arquivosPendentes) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ViewArquivosPendentes> item) {
				final ViewArquivosPendentes arquivo = item.getModelObject();
				final TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoEnum(arquivo.getTipo_Arquivo());

				item.add(downloadArquivoTXT(tipoArquivo, arquivo));
				item.add(relatorioArquivo(tipoArquivo, arquivo));
				item.add(downloadAnexos(arquivo));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {
							setResponsePage(new TitulosArquivoPage(arquivo.getIdRemessa_Remessa()));
						} else if (TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)) {
							DesistenciaProtesto dp = desistenciaMediator.buscarDesistenciaPorPK(arquivo.getIdDesistencia_DesistenciaProtesto());
							setResponsePage(new TitulosDesistenciaPage(dp));
						} else if (TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
							CancelamentoProtesto cp = cancelamentoMediator.buscarCancelamentoPorPK(arquivo.getIdCancelamento_CancelamentoProtesto());
							setResponsePage(new TitulosCancelamentoPage(cp));
						} else if (TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
							AutorizacaoCancelamento ac = autorizacaoMediator.buscarAutorizacaoPorPK(arquivo.getIdAutorizacao_AutorizacaoCancelamento());
							setResponsePage(new TitulosAutorizacaoCancelamentoPage(ac));
						}
					}
				};
				linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo_Arquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(arquivo.getDataEnvio_Arquivo())));
				item.add(new Label("instituicao", arquivo.getNomeFantasia_Instituicao()));
				item.add(new Label("envio", "CRA"));
				item.add(new Label("destino", arquivo.getNomeMunicipio_Municipio()));
				item.add(new Label("dias", PeriodoDataUtil.diferencaDeDiasEntreData(arquivo.getDataRecebimento_Arquivo(), new Date())));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(arquivo.getHoraEnvio_Arquivo())));

				WebMarkupContainer divInfo = new WebMarkupContainer("divInfo");
				divInfo.add(new AttributeAppender("id", StatusDownload.getStatus(arquivo.getStatusDownload()).getLabel()));
				divInfo.add(new Label("status", arquivo.getStatusDownload()));
				item.add(divInfo);
			}

			private Link<Remessa> downloadArquivoTXT(final TipoArquivoFebraban tipoArquivo, final ViewArquivosPendentes arquivo) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = null;

						try {
							if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {
								Remessa remessa = remessaMediator.buscarRemessaPorPK(arquivo.getIdRemessa_Remessa());
								file = downloadMediator.baixarRemessaTXT(usuario, remessa);
							} else if (TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)) {
								DesistenciaProtesto dp = desistenciaMediator.buscarDesistenciaPorPK(arquivo.getIdDesistencia_DesistenciaProtesto());
								file = downloadMediator.baixarDesistenciaTXT(usuario, dp);
							} else if (TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
								CancelamentoProtesto cp = cancelamentoMediator.buscarCancelamentoPorPK(arquivo.getIdCancelamento_CancelamentoProtesto());
								file = downloadMediator.baixarCancelamentoTXT(usuario, cp);
							} else if (TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
								AutorizacaoCancelamento ac = autorizacaoMediator.buscarAutorizacaoPorPK(arquivo.getIdAutorizacao_AutorizacaoCancelamento());
								file = downloadMediator.baixarAutorizacaoTXT(usuario, ac);
							}

							IResourceStream resourceStream = new FileResourceStream(file);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, arquivo.getNomeArquivo_Arquivo()));
						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							logger.info(e.getMessage(), e);
							error("Não foi possível baixar o arquivo! Favor entrar em contato com a CRA...");
						}
					}
				};
			}

			private Link<Remessa> downloadAnexos(final ViewArquivosPendentes arquivo) {
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

			private Link<Remessa> relatorioArquivo(final TipoArquivoFebraban tipoArquivo, final ViewArquivosPendentes arquivo) {
				Link<Remessa> linkRelatorio = new Link<Remessa>("gerarRelatorio") {

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
							error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
							e.printStackTrace();
						}
					}
				};
				if (!tipoArquivo.equals(TipoArquivoFebraban.REMESSA)) {
					linkRelatorio.setOutputMarkupId(false);
					linkRelatorio.setVisible(false);
				}
				return linkRelatorio;
			}
		};
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}