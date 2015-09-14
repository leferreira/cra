package br.com.ieptbto.cra.page.instrumentoProtesto;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoDeProtestoMediator;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class InstrumentoProtestoPanel extends Panel {

	private static final Logger logger = Logger.getLogger(InstrumentoProtestoPanel.class);
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private InstrumentoDeProtestoMediator instrumentoMediator;
	private List<Retorno> retornos;
	private List<InstrumentoProtesto> instrumentos;
	
	public InstrumentoProtestoPanel(String id, List<Retorno> retornos) {
		super(id);
		setRetornos(retornos);
		adicionarCampos();
	}
	
	private void adicionarCampos() {
		add(carregarListaSlips());
		add(formGerarEtiquetas());
	}
	
	private ListView<Retorno> carregarListaSlips() {
		return new ListView<Retorno>("instrumentos", getRetornos()) {

			@Override
			protected void populateItem(ListItem<Retorno> item) {
				final Retorno retorno = item.getModelObject();
				item.add(new Label("numeroTitulo", retorno.getTitulo().getNumeroTitulo()));
				item.add(new Label("protocolo", retorno.getNumeroProtocoloCartorio()));
				item.add(new Label("pracaProtesto", retorno.getTitulo().getPracaProtesto()));
				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					public void onClick() {
						setResponsePage(new HistoricoPage(retorno.getTitulo()));
					}
				};
				linkHistorico.add(new Label("nomeDevedor", retorno.getTitulo().getNomeDevedor()));
				item.add(linkHistorico);
				item.add(new Label("portador", instituicaoMediator.getInstituicaoPorCodigoPortador(retorno.getTitulo().getCodigoPortador()).getNomeFantasia()));
				item.add(new Label("especie", retorno.getTitulo().getEspecieTitulo()));
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", retorno.getTitulo().getValorTitulo()));
				item.add(new Link<Retorno>("remover") {
					
					@Override
					public void onClick() {
						getRetornos().remove(retorno);
					}
				});
			}
		};
	}
	
	private Form<InstrumentoProtesto> formGerarEtiquetas() {
		Form<InstrumentoProtesto> formEtiquetas = new Form<InstrumentoProtesto> ("formSlip");
		formEtiquetas.add(new Button("botaoSlip"){
			@Override
			public void onSubmit() {
				SimpleDateFormat dataPadrao= new SimpleDateFormat("dd_MM_yy");

				try {
					InstrumentoDeProtestoMediator instrumento = instrumentoMediator.processarInstrumentos(getRetornos());
					
					if (instrumento.getEtiquetas().isEmpty()) {
						throw new InfraException("Não foi possível gerar SLIPs. Não há entrada de títulos processados !");
					}

					HashMap<String, Object> parametros = new HashMap<String, Object>();
					parametros.put("DATA", DataUtil.localDateToString(new LocalDate()));
					JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(instrumento.getEtiquetas());
					JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("Teste.jrxml"));
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
					
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(
					        new ResourceStreamRequestHandler(resourceStream, "CRA_SLIP_" + dataPadrao.format(new Date()).toString()  + ".pdf"));
				
				
					beanCollection = new JRBeanCollectionDataSource(instrumento.getEnvelopes());
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("Teste.jrxml"));
					jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
					
					pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(
					        new ResourceStreamRequestHandler(resourceStream, "CRA_ENVELOPES_" + dataPadrao.format(new Date()).toString()  + ".pdf"));
					
				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception ex) { 
					error("Não foi possível gerar as etiquetas ! Entre em contato com a CRA !");
					logger.error(ex.getMessage(), ex);
				}
			}
		});
		return formEtiquetas;
	}
	
	public List<Retorno> getRetornos() {
		return retornos;
	}

	public void setRetornos(List<Retorno> retornos) {
		this.retornos = retornos;
	}

	public List<InstrumentoProtesto> getInstrumentos() {
		if (instrumentos.isEmpty() || instrumentos == null) {
			throw new InfraException("Não existem instrumentos a serem processados !");
		}
		return instrumentos;
	}

	public void setInstrumentos(List<InstrumentoProtesto> instrumentos) {
		this.instrumentos = instrumentos;
	}
}
