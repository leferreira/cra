package br.com.ieptbto.cra.page.instrumentoDeProtesto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.ireport.EtiquetasJRDataSource;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoDeProtestoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class InstrumentoDeProtestoPage extends BasePage<InstrumentoProtesto> {

	@SpringBean
	InstrumentoDeProtestoMediator instrumentoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	private InstrumentoProtesto instrumento;
	private List<Retorno> listaRetornoSlip;
	private TextField<String> codigoInstrumento;
	private TextField<String> protocoloCartorio;
	private DropDownChoice<Municipio> codigoIbge;

	public InstrumentoDeProtestoPage() {
		this.listaRetornoSlip = new ArrayList<Retorno>();
		
		adicionarFormularioCodigo();
		adicionarFormularioManual();
		add(carregarListaSlips());
		add(new Form<InstrumentoProtesto> ("formSlips", getModel()){
		
			@Override
			protected void onSubmit() {
				try {
					JasperPrint jasperPrint = gerarSlipLista(instrumentoMediator.processarInstrumentos(getListaRetornoSlip()));
					getResponse().write(JasperExportManager.exportReportToPdf(jasperPrint));
				} catch (JRException e) {
					e.printStackTrace();
					error("Não foi possível gerar a lista de SLIP ! Entre em contato com a CRA !");
				}
			}
		});
		add(new Form<InstrumentoProtesto> ("formEtiquetas", getModel()){
			@Override
			protected void onSubmit() {
				super.onSubmit();
			}
		});
	}

	private void adicionarFormularioManual() {
		Form<Retorno> formManual = new Form<Retorno>("formManual") {

			@Override
			protected void onSubmit() {
				try { 
					String numeroProtocolo = protocoloCartorio.getModelObject();
					String codigoIBGE = Municipio.class.cast(codigoIbge.getDefaultModelObject()).getCodigoIBGE();
					Retorno tituloProtestado = instrumentoMediator.buscarTituloProtestado(numeroProtocolo, codigoIBGE);
					
					if (tituloProtestado != null) {
						if (!getListaRetornoSlip().contains(tituloProtestado)) {
							getListaRetornoSlip().add(tituloProtestado);
						} else
							throw new InfraException("A lista já contem o título!");
					} else 
						throw new InfraException("Titulo não encontrado!");
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
						if (!getListaRetornoSlip().contains(tituloProtestado)) {
							getListaRetornoSlip().add(tituloProtestado);
						} else
							throw new InfraException("A lista já contem o título!");
					} else 
						throw new InfraException("Titulo não encontrado!");
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
		return new ListView<Retorno>("instrumentos", getListaRetornoSlip()) {

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
						getListaRetornoSlip().remove(retorno);
					}
				});
			}
		};
	}

	private TextField<String> campoCodigoDeBarra() {
		return codigoInstrumento = new TextField<String>("codigoInstrumento", new Model<String>());
	}

	private DropDownChoice<Municipio> codigoIbgeCartorio() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		codigoIbge = new DropDownChoice<Municipio>("codigoIbge", new Model<Municipio>(), municipioMediator.listarTodos(), renderer);
		codigoIbge.setRequired(true);
		codigoIbge.setLabel(new Model<String>("Município"));
		return codigoIbge;
	}

	private TextField<String> numeroProtocoloCartorio() {
		protocoloCartorio = new TextField<String>("protocoloCartorio", new Model<String>());
		protocoloCartorio.setRequired(true);
		protocoloCartorio.setLabel(new Model<String>("Número de Protocolo"));
		return protocoloCartorio;
	}

	public JasperPrint gerarSlipLista(List<InstrumentoProtesto> instrumentos) throws JRException {
		List<EtiquetasJRDataSource> listaEtiquetas = new ArrayList<EtiquetasJRDataSource>();
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("DATA", DataUtil.localDateToString(new LocalDate()));
		
		for (InstrumentoProtesto instrumento: instrumentos) {
			EtiquetasJRDataSource novaEtiqueta = new EtiquetasJRDataSource();
			novaEtiqueta.parseToTituloRemessa(instrumento.getTitulo());
			listaEtiquetas.add(novaEtiqueta);
		}
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(listaEtiquetas);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("teste.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	public List<Retorno> getListaRetornoSlip() {
		return listaRetornoSlip;
	}
	
	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return new CompoundPropertyModel<InstrumentoProtesto>(instrumento);
	}
}