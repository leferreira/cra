package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

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
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosArquivoPage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class ListaArquivosPage extends BasePage<Arquivo> {

	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	RelatorioMediator relatorioMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	private Arquivo arquivo;
	private List<Remessa> remessas;

	public ListaArquivosPage(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim,
	        ArrayList<TipoArquivoEnum> tiposArquivo, ArrayList<StatusRemessa> situacoes) {
		this.arquivo = arquivo;
		this.remessas = remessaMediator.buscarRemessas(arquivo, municipio, dataInicio, dataFim, tiposArquivo, getUser(), situacoes);
		add(carregarListaArquivos());
	}

	private ListView<Remessa> carregarListaArquivos() {
		return new ListView<Remessa>("dataTableRemessa", getRemessas()) {

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(downloadArquivoTXT(remessa));
				item.add(relatorioArquivo(remessa));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoPage(remessa));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));
				
				if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
					item.add(new Label("instituicao", remessa.getInstituicaoOrigem().getNomeFantasia()));
					item.add(new Label("envio", remessa.getArquivo().getInstituicaoRecebe().getNomeFantasia()));
					item.add(new Label("destino", remessa.getInstituicaoDestino().getNomeFantasia()));
				} else {
					item.add(new Label("instituicao", remessa.getInstituicaoDestino().getNomeFantasia()));
					item.add(new Label("envio", remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia()));
					item.add(new Label("destino", remessa.getArquivo().getInstituicaoRecebe().getNomeFantasia()));
				}
				
				item.add(new LabelValorMonetario<BigDecimal>("valor", remessa.getRodape().getSomatorioValorRemessa()));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(remessa.getArquivo().getHoraEnvio())));
				item.add(new Label("status", remessa.getStatusRemessa().getLabel().toUpperCase()).setMarkupId(remessa.getStatusRemessa().getLabel()));
			}

			private Link<Remessa> downloadArquivoTXT(final Remessa remessa) {
				return new Link<Remessa>("downloadArquivo") {

					@Override
					public void onClick() {
						File file = remessaMediator.baixarRemessaTXT(getUser().getInstituicao(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				};
			}
			
			private Link<Remessa> relatorioArquivo(final Remessa remessa) {
				return new Link<Remessa>("gerarRelatorio") {

					@Override
					public void onClick() {
						TipoArquivoEnum tipoArquivo = remessa.getArquivo().getTipoArquivo().getTipoArquivo();
						JasperPrint jasperPrint = null;

						try {
							if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
								JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioRemessa.jrxml"));
								jasperPrint = relatorioMediator.relatorioRemessa(jasperReport ,remessa, getUser().getInstituicao());
							} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
								JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioConfirmação.jrxml"));
								jasperPrint = relatorioMediator.relatorioConfirmacao(jasperReport, remessa, getUser().getInstituicao());
							} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
								JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioRetorno.jrxml"));
								jasperPrint = relatorioMediator.relatorioRetorno(jasperReport ,remessa, getUser().getInstituicao());
							}
							
							File pdf = File.createTempFile("report", ".pdf");
							JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
							IResourceStream resourceStream = new FileResourceStream(pdf);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(
							        new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_" + remessa.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));
						} catch (InfraException ex) { 
							error(ex.getMessage());
						} catch (Exception e) { 
							error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
							e.printStackTrace();
						}
					}
				};
			}
		};
	}

	public List<Remessa> getRemessas() {
		return remessas;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
