package br.com.ieptbto.cra.page.instrumentoProtesto;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.ieptbto.cra.entidade.EtiquetaSLIP;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.mediator.InstrumentoProtestoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings( {"serial"} )
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ListaInstrumentoProtestoPage extends BasePage<InstrumentoProtesto>{

	@Autowired
	InstrumentoProtestoMediator instrumentoProtestoMediator;
	
	private InstrumentoProtesto instrumento;
	private List<EtiquetaSLIP> etiquetas;

	public ListaInstrumentoProtestoPage(String codigoEnvelope, String codigoMunicipio, String numeroProtocolo, LocalDate dataEntrada) {
		this.instrumento = new InstrumentoProtesto();
		this.etiquetas = instrumentoProtestoMediator.buscarEtiquetas(codigoEnvelope, codigoMunicipio, numeroProtocolo, dataEntrada);
				
		add(carregarListaSlips());
	}
	
	private ListView<EtiquetaSLIP> carregarListaSlips() {
		return new ListView<EtiquetaSLIP>("instrumentos", getEtiquetas()) {

			@Override
			protected void populateItem(ListItem<EtiquetaSLIP> item) {
				@SuppressWarnings("unused")
				final EtiquetaSLIP etiquetas = item.getModelObject();
			}
		};
	}

	public List<EtiquetaSLIP> getEtiquetas() {
		if (etiquetas == null) {
			this.etiquetas = new ArrayList<EtiquetaSLIP>();
		}
		return etiquetas; 
	}
	
	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return new CompoundPropertyModel<InstrumentoProtesto>(instrumento);
	}
}
