package br.com.ieptbto.cra.page.instrumentoDeProtesto;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
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

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoDeProtestoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.relatorio.SlipUtils;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class InstrumentoDeProtestoPage extends BasePage<Retorno> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(InstrumentoDeProtestoPage.class);

	@SpringBean
	InstrumentoDeProtestoMediator instrumentoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private Retorno tituloProtestado;
	private ListView<Retorno> titulosSlips;
	private List<Retorno> listaRetornoParaSlip = new ArrayList<Retorno>();

	private TextField<String> codigoInstrumento;
	private TextField<String> protocoloCartorio;
	private DropDownChoice<Municipio> codigoIbge;

	public InstrumentoDeProtestoPage() {
		adicionarFormularioCodigo();
		adicionarFormularioManual();

		add(carregarListaSlips());
		titulosSlips.setReuseItems(true);

		add(botaoGerarListaSlip());
		add(botaoGerarEtiquetas());
	}

	private void adicionarFormularioManual() {
		Form<Retorno> formManual = new Form<Retorno>("formManual") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				Retorno tituloProtestado = null;
				try {
					String numeroProtocolo = protocoloCartorio.getModelObject();
					String codigoIBGE = Municipio.class.cast(codigoIbge.getDefaultModelObject()).getCodigoIBGE();

					tituloProtestado = instrumentoMediator.buscarTituloProtestado(numeroProtocolo, codigoIBGE);

					if (tituloProtestado != null) {
						if (!listaRetornoParaSlip.contains(tituloProtestado)) {
							listaRetornoParaSlip.add(tituloProtestado);
							codigoIbge.clearInput();
						} else
							error("A lista já contem o título!");
					} else {
						error("Titulo não encontrado!");
					}
					protocoloCartorio.clearInput();
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível encontrar o título! Entre em contato com a CRA.");
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

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				Retorno tituloProtestado = null;

				try {
					String codigoIbge = codigoInstrumento.getModelObject().substring(1, 8);
					String protocolo = codigoInstrumento.getModelObject().substring(8, 18);
					tituloProtestado = instrumentoMediator.buscarTituloProtestado(protocolo, codigoIbge);

					if (tituloProtestado != null) {
						if (!listaRetornoParaSlip.contains(tituloProtestado)) {
							listaRetornoParaSlip.add(tituloProtestado);
							codigoInstrumento.clearInput();
						} else
							error("A lista já contem o título!");
					} else {
						error("Titulo não encontrado!");
					}
					codigoInstrumento.clearInput();
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível encontrar o título! Entre em contato com a CRA.");
				}
			}
		};
		formCodigo.add(campoCodigoDeBarra());
		formCodigo.add(new Button("addCodigo"));
		add(formCodigo);
	}

	@SuppressWarnings("rawtypes")
	private ListView<Retorno> carregarListaSlips() {
		return titulosSlips = new ListView<Retorno>("instrumentos", listaRetornoParaSlip) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Retorno> item) {
				final Retorno retorno = item.getModelObject();
				item.add(new Label("numeroTitulo", retorno.getTitulo().getNumeroTitulo()));
				item.add(new Label("protocolo", retorno.getNumeroProtocoloCartorio()));
				item.add(new Label("pracaProtesto", retorno.getTitulo().getPracaProtesto()));

				Link linkHistorico = new Link("linkHistorico") {
					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(retorno.getTitulo()));
					}
				};
				linkHistorico.add(new Label("nomeDevedor", retorno.getTitulo().getNomeDevedor()));
				item.add(linkHistorico);

				item.add(new Label("portador", instituicaoMediator.getInstituicaoPorCodigoPortador(retorno.getTitulo().getCodigoPortador())
				        .getNomeFantasia()));
				item.add(new Label("especie", retorno.getTitulo().getEspecieTitulo()));
				item.add(new LabelValorMonetario("valorTitulo", retorno.getTitulo().getValorTitulo()));
				item.add(new LabelValorMonetario("valorSaldo", retorno.getTitulo().getSaldoTitulo()));
			}

		};
	}

	private TextField<String> campoCodigoDeBarra() {
		return codigoInstrumento = new TextField<String>("codigoInstrumento", new Model<String>());
	}

	private DropDownChoice<Municipio> codigoIbgeCartorio() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		return codigoIbge = new DropDownChoice<Municipio>("codigoIbge", new Model<Municipio>(), municipioMediator.listarTodos(), renderer);
	}

	private TextField<String> numeroProtocoloCartorio() {
		return protocoloCartorio = new TextField<String>("protocoloCartorio", new Model<String>());
	}

	@SuppressWarnings("rawtypes")
	private Component botaoGerarListaSlip() {
		return new Link("gerarLista") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				List<InstrumentoProtesto> listaSlip = new ArrayList<InstrumentoProtesto>();
				JasperPrint jasperPrint;

				try {
					instrumentoMediator.processarInstrumentos(listaRetornoParaSlip);
					listaSlip = instrumentoMediator.buscarInstrumentosParaSlip();
					jasperPrint = getSlipUtils().gerarSlipLista(listaSlip);

					getResponse().write(JasperExportManager.exportReportToPdf(jasperPrint));

				} catch (JRException e) {
					e.printStackTrace();
					error("Não foi possível processar os instrumentos de protesto! Entre em contato com a CRA.");
				}
			}

		};
	}

	private Component botaoGerarEtiquetas() {
		Component button = new Button("gerarEtiquetas") {
			/***/
			private static final long serialVersionUID = 1L;

			public void onSubmit() {

				try {
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível processar os instrumentos de protesto! Entre em contato com a CRA.");
				}
			}
		};
		return button;
	}

	private SlipUtils getSlipUtils() {
		return new SlipUtils();
	}

	@Override
	protected IModel<Retorno> getModel() {
		return new CompoundPropertyModel<Retorno>(tituloProtestado);
	}
}
