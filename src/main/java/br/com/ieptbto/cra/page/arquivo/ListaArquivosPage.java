package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosArquivoPage;
import br.com.ieptbto.cra.relatorio.RelatorioUtil;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Thasso Araújo
 *
 */
public class ListaArquivosPage extends BasePage<Arquivo> {

    /***/
    private static final long serialVersionUID = 1L;
    protected static final Logger logger = Logger.getLogger(ListaArquivosPage.class);

    @SpringBean
    private RemessaMediator remessaMediator;
    @SpringBean
    private InstituicaoMediator instituicaoMediator;
    private Arquivo arquivo;
    private List<Remessa> remessas;

    public ListaArquivosPage(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim, ArrayList<TipoArquivoEnum> tiposArquivo, ArrayList<StatusRemessa> situacoes) {
	this.arquivo = arquivo;
	this.remessas = remessaMediator.buscarRemessas(arquivo, municipio, dataInicio, dataFim, tiposArquivo, getUser(), situacoes);
	add(carregarListaArquivos());
    }

    private ListView<Remessa> carregarListaArquivos() {
	return new ListView<Remessa>("dataTableRemessa", getRemessas()) {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void populateItem(ListItem<Remessa> item) {
		final Remessa remessa = item.getModelObject();
		item.add(downloadArquivoTXT(remessa));
		item.add(relatorioArquivo(remessa));

		item.add(new Label("sequencialCabecalho", remessa.getCabecalho().getNumeroSequencialRemessa()));
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

		    /***/
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
