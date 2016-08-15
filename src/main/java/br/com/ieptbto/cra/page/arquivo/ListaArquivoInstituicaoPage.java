package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.relatorio.RelatorioUtil;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Thasso Araújo
 *
 */
public class ListaArquivoInstituicaoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	ArquivoMediator arquivoMediator;
	@SpringBean
	RemessaMediator remessaMediator;

	private Arquivo arquivo;
	private List<Arquivo> arquivos;

	public ListaArquivoInstituicaoPage(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim,
			ArrayList<TipoArquivoEnum> tiposArquivo, ArrayList<SituacaoArquivo> situacoes) {
		this.arquivo = arquivo;
		this.arquivos = arquivoMediator.buscarArquivosAvancado(arquivo, getUser(), tiposArquivo, municipio, dataInicio, dataFim, situacoes);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		listaArquivos();
	}

	private void listaArquivos() {
		add(new ListView<Arquivo>("dataTableArquivo", getArquivos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Arquivo> item) {
				final Arquivo arquivo = item.getModelObject();
				item.add(new Label("tipoArquivo", arquivo.getTipoArquivo().getTipoArquivo().constante));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoInstituicaoPage(arquivo));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(arquivo.getDataEnvio())));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(arquivo.getHoraEnvio())));
				item.add(new Label("instituicao", arquivo.getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("destino", arquivo.getInstituicaoRecebe().getNomeFantasia()));
				WebMarkupContainer divInfo = new WebMarkupContainer("divInfo");
				divInfo.add(new AttributeAppender("id", arquivo.getStatusArquivo().getSituacaoArquivo().getLabel()));
				divInfo.add(new Label("status", arquivo.getStatusArquivo().getSituacaoArquivo().getLabel().toUpperCase()));
				item.add(divInfo);
				item.add(downloadArquivoTXT(arquivo));
				item.add(relatorioArquivo(arquivo));
			}

			private Link<Arquivo> downloadArquivoTXT(final Arquivo arquivo) {
				return new Link<Arquivo>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = arquivoMediator.baixarArquivoTXT(getUser().getInstituicao(), arquivo);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle()
								.scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, arquivo.getNomeArquivo()));
					}
				};
			}

			private Link<Arquivo> relatorioArquivo(final Arquivo arquivo) {
				return new Link<Arquivo>("gerarRelatorio") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						try {
							JasperPrint jasperPrint = new RelatorioUtil().relatorioArquivoInstiuicao(arquivo);
							File pdf = File.createTempFile("report", ".pdf");
							JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
							IResourceStream resourceStream = new FileResourceStream(pdf);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
									"CRA_RELATORIO_" + arquivo.getNomeArquivo().replace(".", "_") + ".pdf"));
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

	public List<Arquivo> getArquivos() {
		return arquivos;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
