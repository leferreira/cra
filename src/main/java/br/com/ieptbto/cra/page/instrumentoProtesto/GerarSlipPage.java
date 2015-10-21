package br.com.ieptbto.cra.page.instrumentoProtesto;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.EnvelopeSLIP;
import br.com.ieptbto.cra.entidade.EtiquetaSLIP;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoProtestoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class GerarSlipPage extends BasePage<InstrumentoProtesto> {

	private static final Logger logger = Logger.getLogger(GerarSlipPage.class);
	
	@SpringBean
	InstrumentoProtestoMediator instrumentoMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private InstrumentoProtesto instrumento;
	private List<Retorno> retornos;
	private List<InstrumentoProtesto> instrumentosProtesto;
	private List<EnvelopeSLIP> envelopes;
	private List<EtiquetaSLIP> etiquetas;

	public GerarSlipPage() {
		this.instrumento = new InstrumentoProtesto();
		this.instrumentosProtesto = instrumentoMediator.buscarInstrumentosParaSlip();
		this.envelopes = null;
		this.etiquetas = null; 
		add(carregarListaSlips());
		add(botaoGerarEtiquetas());
		add(botaoGerarEnvelopes());
		add(botaoGerarListagem());
	}
	
	private ListView<InstrumentoProtesto> carregarListaSlips() {
		return new ListView<InstrumentoProtesto>("instrumentos", getInstrumentosProtesto()) {

			@Override
			protected void populateItem(ListItem<InstrumentoProtesto> item) {
				final InstrumentoProtesto instrumentoProtesto = item.getModelObject();
				final Retorno retorno = instrumentoProtesto.getTituloRetorno();
				
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
			}
		};
	}

	private Link<InstrumentoProtesto> botaoGerarEtiquetas() {
		return new Link<InstrumentoProtesto>("botaoSlip"){
			
			@Override
			public void onClick() {
				SimpleDateFormat dataPadrao = new SimpleDateFormat("dd_MM_yy");

				try {
					InstrumentoProtestoMediator instrumento = instrumentoMediator.processarInstrumentos(getInstrumentosProtesto() ,getRetornos());
					
					if (instrumento.getEtiquetas().isEmpty()) {
						throw new InfraException("Não foi possível gerar SLIPs. Não há entrada de títulos processados !");
					} 

					getEnvelopes().addAll(instrumento.getEnvelopes());
					getEtiquetas().addAll(instrumento.getEtiquetas());
					
					HashMap<String, Object> parametros = new HashMap<String, Object>();
					JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(instrumento.getEtiquetas());
					JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/SlipEtiqueta.jrxml"));
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
					
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(
					        new ResourceStreamRequestHandler(resourceStream, "CRA_SLIP_" + dataPadrao.format(new Date()).toString()  + ".pdf"));
				
				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception ex) { 
					error("Não foi possível gerar as etiquetas ! Entre em contato com a CRA !");
					logger.error(ex.getMessage(), ex);
				}
			}
		};
	}
	
	private Link<InstrumentoProtesto> botaoGerarEnvelopes() {
		return new Link<InstrumentoProtesto>("botaoEnvelope"){
			
			@Override
			public void onClick() {
				SimpleDateFormat dataPadrao= new SimpleDateFormat("dd_MM_yy");

				try {
					if (getEnvelopes().isEmpty()) {
						throw new InfraException("Não foi possível gerar os envelopes. Não foram processados instrumentos !");
					}

					HashMap<String, Object> parametros = new HashMap<String, Object>();
					JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(getEnvelopes());
					JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/SlipEnvelope.jrxml"));
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
					
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
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
		};
	}
	
	private Link<InstrumentoProtesto> botaoGerarListagem() {
		return new Link<InstrumentoProtesto>("botaoListagem"){
			
			@Override
			public void onClick() {
				SimpleDateFormat dataPadrao= new SimpleDateFormat("dd_MM_yy");

				try {
					if (getEtiquetas().isEmpty()) {
						throw new InfraException("Não foi possível gerar a listagem. Não foram processados instrumentos !");
					}

					HashMap<String, Object> parametros = new HashMap<String, Object>();
					JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(getEtiquetas());
					JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/SlipListagem.jrxml"));
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
					
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(
					        new ResourceStreamRequestHandler(resourceStream, "CRA_LISTAGEM_" + dataPadrao.format(new Date()).toString()  + ".pdf"));

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception ex) { 
					error("Não foi possível a listagem ! Entre em contato com a CRA !");
					logger.error(ex.getMessage(), ex);
				}
			}
		};
	}
	
	public List<InstrumentoProtesto> getInstrumentosProtesto() {
		if (instrumentosProtesto == null) {
			instrumentosProtesto = new ArrayList<InstrumentoProtesto>();
		}
		return instrumentosProtesto;
	}
	
	public List<EnvelopeSLIP> getEnvelopes() {
		if (envelopes == null) {
			envelopes = new ArrayList<EnvelopeSLIP>();
		}
		return envelopes;
	}

	public void setEnvelopes(List<EnvelopeSLIP> envelopes) {
		this.envelopes = envelopes;
	}
	
	public List<EtiquetaSLIP> getEtiquetas() {
		if (etiquetas == null) {
			etiquetas = new ArrayList<EtiquetaSLIP>();
		}
		return etiquetas;
	}
	
	public List<Retorno> getRetornos() {
		if (retornos == null) {
			retornos = new ArrayList<Retorno>();
		}
		return retornos;
	}

	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return new CompoundPropertyModel<InstrumentoProtesto>(instrumento);
	}
}