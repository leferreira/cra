package br.com.ieptbto.cra.page.instrumentoProtesto;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoDeProtestoMediator;
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
	InstrumentoDeProtestoMediator instrumentoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	
	private InstrumentoProtesto instrumento;
	private List<InstrumentoProtesto> instrumentos;

	public BuscarInstrumentoProtestoPage() {
		this.instrumento = new InstrumentoProtesto();
		add(adicionarFormulario());
		add(carregarListaSlips());
	}
	
	private Form<InstrumentoProtesto> adicionarFormulario() {
		return new Form<InstrumentoProtesto>("form") {

			@Override
			protected void onSubmit() {
				try { 
				} catch (InfraException ex) {
					error(ex.getMessage());
				}
			}
		};
	}

	private ListView<InstrumentoProtesto> carregarListaSlips() {
		return new ListView<InstrumentoProtesto>("instrumentos", getInstrumentos()) {

			@Override
			protected void populateItem(ListItem<InstrumentoProtesto> item) {
				final InstrumentoProtesto instrumento = item.getModelObject();
			}
		};
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