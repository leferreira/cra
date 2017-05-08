package br.com.ieptbto.cra.page.instrumentoProtesto;

import br.com.ieptbto.cra.component.LabelValorMonetario;
import br.com.ieptbto.cra.component.dataTable.CraLinksPanel;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoProtestoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class InstrumentoProtestoPage extends BasePage<InstrumentoProtesto> {

	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private InstrumentoProtestoMediator instrumentoMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;

	private static final long serialVersionUID = 1L;
	private TextField<String> codigoInstrumento;
	private TextField<String> protocoloCartorio;
	private DropDownChoice<Municipio> codigoIbge;

	private ListView<InstrumentoProtesto> listInstrumentos;

	public InstrumentoProtestoPage() {
		adicionarComponentes();
	}
	
	public InstrumentoProtestoPage(String message) {
		success(message);
		adicionarComponentes();
	}
	
	@Override
	protected void adicionarComponentes() {
		verificarEtiquetasGeradasNaoConfimadas();
		add(formEntradaManualInstrumentos());
		add(formCodigoDeBarraInstrumentos());
		add(listInstrumentosLancados());
	}

	private void verificarEtiquetasGeradasNaoConfimadas() {
		if (instrumentoMediator.verificarEtiquetasGeradasNaoConfimadas()) {
			warn("Existem intrumentos com Slips geradas porém não foram confirmados! Por favor vá a página Gerar Slips e em seguida clique em confirmar...");
		}
	}

	private Form<Retorno> formEntradaManualInstrumentos() {
		Form<Retorno> formManual = new Form<Retorno>("formManual") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					String numeroProtocolo = protocoloCartorio.getModelObject();
					String codigoIBGE = Municipio.class.cast(codigoIbge.getDefaultModelObject()).getCodigoIBGE();

					InstrumentoProtesto instrumentoProtesto = instrumentoMediator.salvarInstrumentoProtesto(getUser(), numeroProtocolo, codigoIBGE);
                    listInstrumentos.getModelObject().add(0, instrumentoProtesto);
					protocoloCartorio.getModel().setObject(StringUtils.EMPTY);
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
				    logger.info(e.getMessage(), e);
					error(e.getMessage());
				}
			}
		};
		formManual.add(codigoIbgeCartorio());
		formManual.add(numeroProtocoloCartorio());
		formManual.add(new Button("addManual"));
		return formManual;
	}

	private Form<Retorno> formCodigoDeBarraInstrumentos() {
		Form<Retorno> formCodigo = new Form<Retorno>("formCodigo") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					String numeroProtocolo = codigoInstrumento.getModelObject().substring(8, 18);
                    String codigoIbge = codigoInstrumento.getModelObject().substring(1, 8);

                    InstrumentoProtesto instrumentoProtesto = instrumentoMediator.salvarInstrumentoProtesto(getUser(), numeroProtocolo, codigoIbge);
                    listInstrumentos.getModelObject().add(0, instrumentoProtesto);
                    codigoInstrumento.getModel().setObject(StringUtils.EMPTY);
                } catch (InfraException ex) {
                    codigoInstrumento.getModel().setObject(StringUtils.EMPTY);
				    error(ex.getMessage());
                } catch (Exception e) {
                    logger.info(e.getMessage(), e);
                    error(e.getMessage());
                }
			}
		};
		formCodigo.add(campoCodigoDeBarra());
		formCodigo.add(new Button("addCodigo"));
		return formCodigo;
	}

	private ListView<InstrumentoProtesto> listInstrumentosLancados() {
		return listInstrumentos = new ListView<InstrumentoProtesto>("instrumentos", new ArrayList<InstrumentoProtesto>()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<InstrumentoProtesto> item) {
				final InstrumentoProtesto instrumentoProtesto = item.getModelObject();
                String municipio = instrumentoProtesto.getTituloRetorno().getRemessa().getInstituicaoOrigem().getMunicipio().getNomeMunicipio();
                if (municipio.length() > 20) {
                    municipio = municipio.substring(0, 19);
                }

                item.add(new Label("ordem", item.getIndex() + 1));
                item.add(new Label("numeroTitulo", instrumentoProtesto.getTituloRetorno().getTitulo().getNumeroTitulo()));
                item.add(new Label("protocolo", instrumentoProtesto.getTituloRetorno().getNumeroProtocoloCartorio()));
				item.add(new Label("pracaProtesto", municipio.toUpperCase()));
				item.add(new CraLinksPanel("linkHistorico", instrumentoProtesto.getTituloRetorno().getTitulo(), instrumentoProtesto.getTituloRetorno().getTitulo().getNomeDevedor()));
				item.add(new Label("portador", instrumentoProtesto.getTituloRetorno().getTitulo().getCodigoPortador()));
				item.add(new Label("especie", instrumentoProtesto.getTituloRetorno().getTitulo().getEspecieTitulo()));
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", instrumentoProtesto.getTituloRetorno().getTitulo().getValorTitulo()));
				item.add(linkRemover(instrumentoProtesto));
			}

			private Link<Void> linkRemover(final InstrumentoProtesto instrumentoProtesto) {
                return new Link<Void>("remover") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {

                        try {
                            instrumentoMediator.removerInstrumento(instrumentoProtesto);
                            listInstrumentos.getModelObject().remove(instrumentoProtesto);

                        } catch (InfraException ex) {
                            logger.info(ex.getMessage(), ex);
                            error(ex.getMessage());
                        } catch (Exception ex) {
                            logger.info(ex);
                            error("Não foi possível remover o instrumento te protesto! Favor entrar em contato com a CRA...");
                        }
                    }
                };
            }
		};
	}

	private TextField<String> campoCodigoDeBarra() {
		return codigoInstrumento = new TextField<String>("codigoInstrumento", new Model<String>());
	}

	private DropDownChoice<Municipio> codigoIbgeCartorio() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		codigoIbge = new DropDownChoice<Municipio>("codigoIbge", new Model<Municipio>(), 
				municipioMediator.getMunicipiosTocantins(), renderer);
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

	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return null;
	}
}