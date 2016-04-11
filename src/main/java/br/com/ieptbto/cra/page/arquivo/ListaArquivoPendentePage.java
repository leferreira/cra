package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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

import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.AutorizacaoCancelamentoMediator;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.relatorio.RelatorioUtil;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.PeriodoDataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Thasso Araújo
 *
 */
public class ListaArquivoPendentePage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	DesistenciaProtestoMediator desistenciaProtestoMediator;
	@SpringBean
	CancelamentoProtestoMediator cancelamentoMediator;
	@SpringBean
	AutorizacaoCancelamentoMediator autorizacaoMediator;

	private Arquivo arquivo;

	public ListaArquivoPendentePage(Arquivo arquivo) {
		this.arquivo = arquivo;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		listaArquivos();
		listaArquivosDesistenciaProtesto();
		listaArquivosCancelamentoProtesto();
		listaAutorizacaoCancelamento();
	}

	private void listaArquivos() {
		add(new ListView<Remessa>("dataTableRemessa", getArquivo().getRemessas()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(downloadArquivoTXT(remessa));
				item.add(relatorioArquivo(remessa));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoPage(remessa));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));

				if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
					remessa.setInstituicaoOrigem(instituicaoMediator.carregarInstituicaoPorId(remessa.getInstituicaoOrigem()));
					remessa.setInstituicaoDestino(instituicaoMediator.carregarInstituicaoPorId(remessa.getInstituicaoDestino()));
					item.add(new Label("instituicao", remessa.getInstituicaoOrigem().getNomeFantasia()));
					item.add(new Label("envio", "CRA"));
					item.add(new Label("destino", remessa.getInstituicaoDestino().getNomeFantasia()));
					item.add(downloadAnexos(remessa));
				} else {
					remessa.setInstituicaoDestino(instituicaoMediator.carregarInstituicaoPorId(remessa.getInstituicaoDestino()));
					remessa.setInstituicaoOrigem(instituicaoMediator.carregarInstituicaoPorId(remessa.getInstituicaoOrigem()));
					item.add(new Label("instituicao", remessa.getInstituicaoDestino().getNomeFantasia()));
					item.add(new Label("envio", remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia()));
					item.add(new Label("destino", "CRA"));
					item.add(new Label("downloadAnexos", StringUtils.EMPTY));
				}
				item.add(new Label("pendente", PeriodoDataUtil.diferencaDeDiasEntreData(remessa.getDataRecebimento().toDate(), new Date())));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(remessa.getArquivo().getHoraEnvio())));
				item.add(new Label("status", remessa.getStatusRemessa().getLabel().toUpperCase()).setMarkupId(remessa.getStatusRemessa().getLabel()));
			}

			private Link<Remessa> downloadArquivoTXT(final Remessa remessa) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						try {
							File file = remessaMediator.baixarRemessaTXT(getUser(), remessa);
							IResourceStream resourceStream = new FileResourceStream(file);

							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, remessa.getArquivo().getNomeArquivo()));
						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
						} catch (Exception e) {
							getFeedbackPanel().error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
						}
					}
				};
			}

			private Link<Remessa> downloadAnexos(final Remessa remessa) {
				List<Anexo> anexos = remessaMediator.verificarAnexosRemessa(remessa);
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
							getFeedbackPanel().error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
						}
					}
				};

				if (anexos != null) {
					if (anexos.isEmpty()) {
						linkAnexos.setOutputMarkupId(false);
						linkAnexos.setVisible(false);
					}
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
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_"
									+ remessa.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));
						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
							e.printStackTrace();
						}
					}
				};
			}
		});
	}

	private void listaArquivosDesistenciaProtesto() {
		add(new ListView<DesistenciaProtesto>("dataTableDesistencia", getArquivo().getRemessaDesistenciaProtesto().getDesistenciaProtesto()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<DesistenciaProtesto> item) {
				final DesistenciaProtesto desistenciaProtesto = item.getModelObject();
				item.add(downloadArquivoTXT(desistenciaProtesto));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
					}
				};
				linkArquivo.add(new Label("nomeArquivo", desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(desistenciaProtesto.getRemessaDesistenciaProtesto().getCabecalho().getDataMovimento())));

				item.add(new Label("instituicao", desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("envio", desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getInstituicaoRecebe().getNomeFantasia()));
				item.add(new Label("destino", instituicaoMediator.getCartorioPorCodigoIBGE(desistenciaProtesto.getCabecalhoCartorio().getCodigoMunicipio()).getNomeFantasia()));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getHoraEnvio())));
				item.add(new Label("status", verificaDownload(desistenciaProtesto).getLabel().toUpperCase()).setMarkupId(verificaDownload(desistenciaProtesto).getLabel()));
				item.add(new Label("dias", PeriodoDataUtil.diferencaDeDiasEntreData(desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getDataEnvio().toDate(), new Date())));
			}

			private Link<Remessa> downloadArquivoTXT(final DesistenciaProtesto desistenciaProtesto) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = desistenciaProtestoMediator.baixarDesistenciaTXT(getUser(), desistenciaProtesto);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
					}
				};
			}

			private StatusRemessa verificaDownload(DesistenciaProtesto desistencia) {
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					return StatusRemessa.ENVIADO;
				}
				if (desistencia.getDownload().equals(false)) {
					return StatusRemessa.AGUARDANDO;
				}
				return StatusRemessa.RECEBIDO;
			}
		});
	}

	private void listaArquivosCancelamentoProtesto() {
		add(new ListView<CancelamentoProtesto>("dataTableCancelamento", getArquivo().getRemessaCancelamentoProtesto().getCancelamentoProtesto()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<CancelamentoProtesto> item) {
				final CancelamentoProtesto cancelamento = item.getModelObject();
				item.add(downloadArquivoTXT(cancelamento));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
					}
				};
				linkArquivo.add(new Label("nomeArquivo", cancelamento.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(cancelamento.getRemessaCancelamentoProtesto().getCabecalho().getDataMovimento())));
				item.add(new Label("instituicao", cancelamento.getRemessaCancelamentoProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("envio", cancelamento.getRemessaCancelamentoProtesto().getArquivo().getInstituicaoRecebe().getNomeFantasia()));
				item.add(new Label("destino", instituicaoMediator.getCartorioPorCodigoIBGE(cancelamento.getCabecalhoCartorio().getCodigoMunicipio()).getNomeFantasia()));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(cancelamento.getRemessaCancelamentoProtesto().getArquivo().getHoraEnvio())));
				item.add(new Label("status", verificaDownload(cancelamento).getLabel().toUpperCase()).setMarkupId(verificaDownload(cancelamento).getLabel()));
				item.add(new Label("dias", PeriodoDataUtil.diferencaDeDiasEntreData(cancelamento.getRemessaCancelamentoProtesto().getArquivo().getDataEnvio().toDate(), new Date())));
			}

			private Link<Remessa> downloadArquivoTXT(final CancelamentoProtesto cancelamento) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = cancelamentoMediator.baixarCancelamentoTXT(getUser(), cancelamento);
						IResourceStream resourceStream = new FileResourceStream(file);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, cancelamento.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
					}
				};
			}

			private StatusRemessa verificaDownload(CancelamentoProtesto cancelamento) {
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					return StatusRemessa.ENVIADO;
				}
				if (cancelamento.getDownload().equals(false)) {
					return StatusRemessa.AGUARDANDO;
				}
				return StatusRemessa.RECEBIDO;
			}
		});
	}

	private void listaAutorizacaoCancelamento() {
		add(new ListView<AutorizacaoCancelamento>("dataTableAutorizacao", getArquivo().getRemessaAutorizacao().getAutorizacaoCancelamento()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<AutorizacaoCancelamento> item) {
				final AutorizacaoCancelamento ac = item.getModelObject();
				item.add(downloadArquivoTXT(ac));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
					}
				};
				linkArquivo.add(new Label("nomeArquivo", ac.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(ac.getRemessaAutorizacaoCancelamento().getCabecalho().getDataMovimento())));
				item.add(new Label("instituicao", ac.getRemessaAutorizacaoCancelamento().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("envio", ac.getRemessaAutorizacaoCancelamento().getArquivo().getInstituicaoRecebe().getNomeFantasia()));
				item.add(new Label("destino", instituicaoMediator.getCartorioPorCodigoIBGE(ac.getCabecalhoCartorio().getCodigoMunicipio()).getNomeFantasia()));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(ac.getRemessaAutorizacaoCancelamento().getArquivo().getHoraEnvio())));
				item.add(new Label("status", verificaDownload(ac).getLabel().toUpperCase()).setMarkupId(verificaDownload(ac).getLabel()));
				item.add(new Label("dias", PeriodoDataUtil.diferencaDeDiasEntreData(ac.getRemessaAutorizacaoCancelamento().getArquivo().getDataEnvio().toDate(), new Date())));

			}

			private Link<Remessa> downloadArquivoTXT(final AutorizacaoCancelamento ac) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = autorizacaoMediator.baixarAutorizacaoTXT(getUser(), ac);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, ac.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
					}
				};
			}

			private StatusRemessa verificaDownload(AutorizacaoCancelamento ac) {
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					return StatusRemessa.ENVIADO;
				}
				if (ac.getDownload().equals(false)) {
					return StatusRemessa.AGUARDANDO;
				}
				return StatusRemessa.RECEBIDO;
			}
		});
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
