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
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.EnvelopeSLIP;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoDeProtestoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
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
public class InstrumentoProtestoPage extends BasePage<InstrumentoProtesto> {

	private static final Logger logger = Logger.getLogger(InstrumentoProtestoPage.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	InstrumentoDeProtestoMediator instrumentoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	
	private InstrumentoProtesto instrumento;
	private List<Retorno> retornos;
	private List<EnvelopeSLIP> envelopes;
	private TextField<String> codigoInstrumento;
	private TextField<String> protocoloCartorio;
	private DropDownChoice<Municipio> codigoIbge;

	public InstrumentoProtestoPage() {
		this.instrumento = new InstrumentoProtesto();
		this.retornos = null;
		this.envelopes = null;
		adicionarFormularioCodigo();
		adicionarFormularioManual();
		add(carregarListaSlips());
		add(botaoGerarEtiquetas());
		add(botaoGerarEnvelopes());
	}
	
	private void adicionarFormularioManual() {
		Form<Retorno> formManual = new Form<Retorno>("formManual") {

			@Override
			protected void onSubmit() {
				try { 
					String numeroProtocolo = protocoloCartorio.getModelObject();
					String codigoIBGE = Municipio.class.cast(codigoIbge.getDefaultModelObject()).getCodigoIBGE();
					Retorno tituloProtestado = instrumentoMediator.buscarTituloProtestado(numeroProtocolo, codigoIBGE);
					InstrumentoProtesto instrumento = instrumentoMediator.isTituloJaFoiGeradoInstrumento(tituloProtestado);
					
					
					if (tituloProtestado != null) {
						if (instrumento == null) {
							if (!getRetornos().contains(tituloProtestado)) {
								getRetornos().add(tituloProtestado);
							} else {
								throw new InfraException("O título já existe na lista!");
							}
						} else {
							throw new InfraException("Este instrumento já foi processado anteriormente!");
						}
					} else {
						throw new InfraException("O título não foi encontrado ou não foi protestado pelo cartório!");
					}
				} catch (InfraException ex) {
					error(ex.getMessage());
				}
			}
		};
		formManual.add(codigoIbgeCartorio());
		formManual.add(numeroProtocoloCartorio());
		formManual.add(new Button("addManual"));
		add(formManual);
	}

	private void adicionarFormularioCodigo() {
		Form<Retorno> formCodigo = new Form<Retorno>("formCodigo") {

			@Override
			protected void onSubmit() {
				try { 
					String codigoIbge = codigoInstrumento.getModelObject().substring(1, 8);
					String protocolo = codigoInstrumento.getModelObject().substring(8, 18);
					Retorno tituloProtestado = instrumentoMediator.buscarTituloProtestado(protocolo, codigoIbge);
					
					if (tituloProtestado != null) {
						if (instrumento == null) {
							if (!getRetornos().contains(tituloProtestado)) {
								getRetornos().add(tituloProtestado);
							} else {
								throw new InfraException("O título já existe na lista!");
							}
						} else {
							throw new InfraException("Este instrumento já foi processado anteriormente!");
						}
					} else {
						throw new InfraException("O título não foi encontrado ou não foi protestado pelo cartório!");
					}
				} catch (InfraException ex) {
					error(ex.getMessage());
				}
			}
		};
		formCodigo.add(campoCodigoDeBarra());
		formCodigo.add(new Button("addCodigo"));
		add(formCodigo);
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

	private Link<InstrumentoProtesto> botaoGerarEtiquetas() {
		return new Link<InstrumentoProtesto>("botaoSlip"){
			
			@Override
			public void onClick() {
				SimpleDateFormat dataPadrao = new SimpleDateFormat("dd_MM_yy");

				try {
					InstrumentoDeProtestoMediator instrumento = instrumentoMediator.processarInstrumentos(getRetornos());
					
					if (instrumento.getEtiquetas().isEmpty()) {
						throw new InfraException("Não foi possível gerar SLIPs. Não há entrada de títulos processados !");
					}

					getEnvelopes().addAll(instrumento.getEnvelopes());
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
	
	private TextField<String> campoCodigoDeBarra() {
		return codigoInstrumento = new TextField<String>("codigoInstrumento", new Model<String>());
	}

	private DropDownChoice<Municipio> codigoIbgeCartorio() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		codigoIbge = new DropDownChoice<Municipio>("codigoIbge", new Model<Municipio>(), municipioMediator.getMunicipiosTocantins(), renderer);
		codigoIbge.setLabel(new Model<String>("Município"));
		codigoIbge.setRequired(true);
		return codigoIbge;
	}

	private TextField<String> numeroProtocoloCartorio() {
		protocoloCartorio = new TextField<String>("protocoloCartorio", new Model<String>());
		protocoloCartorio.setRequired(true);
		protocoloCartorio.setLabel(new Model<String>("Número de Protocolo"));
		return protocoloCartorio;
	}
	
	public List<Retorno> getRetornos() {
		if (retornos == null) {
			retornos = new ArrayList<Retorno>();
		}
		return retornos;
	}
	
	public void setRetornos(List<Retorno> retornos) {
		this.retornos = retornos;
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
	
	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return new CompoundPropertyModel<InstrumentoProtesto>(instrumento);
	}
}