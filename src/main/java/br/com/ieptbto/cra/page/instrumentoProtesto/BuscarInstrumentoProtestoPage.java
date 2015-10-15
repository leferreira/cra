package br.com.ieptbto.cra.page.instrumentoProtesto;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.DateTextField;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoProtestoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings( {"serial","unused"} )
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class BuscarInstrumentoProtestoPage extends BasePage<InstrumentoProtesto> {

	private static final Logger logger = Logger.getLogger(BuscarInstrumentoProtestoPage.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	InstrumentoProtestoMediator instrumentoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	
	private InstrumentoProtesto instrumento;
	private List<InstrumentoProtesto> instrumentos;
	private DateTextField dataEntrada;
	private TextField<String> codigoInstrumento;
	private TextField<String> protocoloCartorio;
	private DropDownChoice<Municipio> codigoIbge;

	public BuscarInstrumentoProtestoPage() {
		this.instrumento = new InstrumentoProtesto();
		add(adicionarFormulario());
		add(carregarListaSlips());
	}
	
	private Form<InstrumentoProtesto> adicionarFormulario() {
		Form<InstrumentoProtesto> form = new Form<InstrumentoProtesto>("form") {

			@Override
			protected void onSubmit() {
				try { 
					tratarMunicipioProtocolo();
				} catch (InfraException ex) {
					error(ex.getMessage());
				}
			}

			private void tratarMunicipioProtocolo() {
				String numeroProtocolo = protocoloCartorio.getModelObject();
				String codigoIBGE = Municipio.class.cast(codigoIbge.getDefaultModelObject()).getCodigoIBGE();
				Retorno tituloProtestado = instrumentoMediator.buscarTituloProtestado(numeroProtocolo, codigoIBGE);
				InstrumentoProtesto instrumento = instrumentoMediator.isTituloJaFoiGeradoInstrumento(tituloProtestado);
				
				if (tituloProtestado != null) {
					if (instrumento == null) {
//						if (!getRetornos().contains(tituloProtestado)) {
//							getRetornos().add(tituloProtestado);
//						} else {
//							throw new InfraException("O título já existe na lista!");
//						}
					} else {
						throw new InfraException("Este instrumento já foi processado anteriormente!");
					}
				} else {
					throw new InfraException("O título não foi encontrado ou não foi protestado pelo cartório!");
				}
			}
		};
		form.add(campoCodigoDeBarra());
		form.add(codigoIbgeCartorio());
		form.add(numeroProtocoloCartorio());
		form.add(campoDataEntrada());
		return form;
	}


	private ListView<InstrumentoProtesto> carregarListaSlips() {
		return new ListView<InstrumentoProtesto>("instrumentos", getInstrumentos()) {

			@Override
			protected void populateItem(ListItem<InstrumentoProtesto> item) {
				final InstrumentoProtesto instrumento = item.getModelObject();
			}
		};
	}

	private DateTextField campoDataEntrada() {
		return dataEntrada = new DateTextField("date", "dd/MM/yyyy");
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
	
	private List<InstrumentoProtesto> getInstrumentos() {
		if (instrumentos == null) {
			instrumentos = new ArrayList<InstrumentoProtesto>();
		}
		return instrumentos;
	}
	
	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return new CompoundPropertyModel<InstrumentoProtesto>(instrumento);
	}
}