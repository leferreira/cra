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
import br.com.ieptbto.cra.entidade.EtiquetaSLIP;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
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
	private List<EtiquetaSLIP> etiquetaSLIP;
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
				String numeroProtocolo = null;
				String inputMunicipio = null;
				String codigoBarraMunicipio = null;
				String protocolo = null;
				
				try { 
					numeroProtocolo = protocoloCartorio.getModelObject();
					inputMunicipio = Municipio.class.cast(codigoIbge.getDefaultModelObject()).getCodigoIBGE();

					codigoBarraMunicipio = codigoInstrumento.getModelObject().substring(1, 8);
					protocolo = codigoInstrumento.getModelObject().substring(8, 18);
					
				} catch (InfraException ex) {
					error(ex.getMessage());
				}
			}
		};
		form.add(campoCodigoDeBarra());
		form.add(codigoIbgeCartorio());
		form.add(numeroProtocoloCartorio());
		form.add(campoDataEntrada());
		return form;
	}


	private ListView<EtiquetaSLIP> carregarListaSlips() {
		return new ListView<EtiquetaSLIP>("instrumentos", getEtiquetas()) {

			@Override
			protected void populateItem(ListItem<EtiquetaSLIP> item) {
				final EtiquetaSLIP instrumento = item.getModelObject();
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
	
	private List<EtiquetaSLIP> getEtiquetas() {
		if (etiquetaSLIP == null) {
			etiquetaSLIP = new ArrayList<EtiquetaSLIP>();
		}
		return etiquetaSLIP;
	}
	
	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return new CompoundPropertyModel<InstrumentoProtesto>(instrumento);
	}
}