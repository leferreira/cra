package br.com.ieptbto.cra.page.instrumentoProtesto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoProtestoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.cra.MensagemPage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class InstrumentoProtestoPage extends BasePage<InstrumentoProtesto> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(GerarSlipPage.class);

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	InstrumentoProtestoMediator instrumentoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;

	private InstrumentoProtesto instrumento;
	private TextField<String> codigoInstrumento;
	private TextField<String> protocoloCartorio;
	private DropDownChoice<Municipio> codigoIbge;
	private List<Retorno> retornos;

	public InstrumentoProtestoPage() {
		this.instrumento = new InstrumentoProtesto();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		adicionarFormularioManual();
		adicionarFormularioCodigo();
		listaSlips();
		botaoSalvarInstrumentos();

	}

	private void adicionarFormularioManual() {
		Form<Retorno> formManual = new Form<Retorno>("formManual") {

			/***/
			private static final long serialVersionUID = 1L;

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
								getRetornos().add(0, tituloProtestado);
							} else {
								throw new InfraException("O título já existe na lista!");
							}
						} else {
							throw new InfraException("Este instrumento já foi processado anteriormente!");
						}
					} else {
						throw new InfraException("O título não foi encontrado ou não foi protestado pelo cartório!");
					}

					protocoloCartorio.getModel().setObject(StringUtils.EMPTY);
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error(e.getMessage());
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
				try {
					String codigoIbge = codigoInstrumento.getModelObject().substring(1, 8);
					String protocolo = codigoInstrumento.getModelObject().substring(8, 18);
					Retorno tituloProtestado = instrumentoMediator.buscarTituloProtestado(protocolo, codigoIbge);
					InstrumentoProtesto instrumento = instrumentoMediator.isTituloJaFoiGeradoInstrumento(tituloProtestado);

					if (tituloProtestado != null) {
						if (instrumento == null) {
							if (!getRetornos().contains(tituloProtestado)) {
								getRetornos().add(0, tituloProtestado);
							} else {
								throw new InfraException("O título já existe na lista!");
							}
						} else {
							throw new InfraException("Este instrumento já foi processado anteriormente!");
						}
					} else {
						throw new InfraException("O título não foi encontrado ou não foi protestado pelo cartório!");
					}

					codigoInstrumento.getModel().setObject(StringUtils.EMPTY);
				} catch (InfraException ex) {
					error(ex.getMessage());
				}
			}
		};
		formCodigo.add(campoCodigoDeBarra());
		formCodigo.add(new Button("addCodigo"));
		add(formCodigo);
	}

	private void listaSlips() {
		add(new ListView<Retorno>("instrumentos", getRetornos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Retorno> item) {
				final Retorno retorno = item.getModelObject();
				item.add(new Label("ordem", item.getIndex() + 1));
				item.add(new Label("numeroTitulo", retorno.getTitulo().getNumeroTitulo()));
				item.add(new Label("protocolo", retorno.getNumeroProtocoloCartorio()));
				item.add(new Label("pracaProtesto", retorno.getTitulo().getPracaProtesto()));
				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					/***/
					private static final long serialVersionUID = 1L;

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

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						getRetornos().remove(retorno);
					}
				});
			}
		});
	}

	private void botaoSalvarInstrumentos() {
		add(new Link<InstrumentoProtesto>("botaoSalvarInstrumentos") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				try {
					instrumentoMediator.salvarInstrumentoProtesto(getRetornos(), getUser());
					setRetornos(null);
					setResponsePage(new MensagemPage<InstrumentoProtesto>(InstrumentoProtestoPage.class, "ENTRADA DE INSTRUMENTO DE PROTESTO", "Entrada dos instrumentos realizada com sucesso!"));

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception ex) {
					error("Não foi possível gerar as etiquetas ! Entre em contato com a CRA !");
					logger.error(ex.getMessage(), ex);
				}
			}
		});
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

	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return new CompoundPropertyModel<InstrumentoProtesto>(instrumento);
	}
}