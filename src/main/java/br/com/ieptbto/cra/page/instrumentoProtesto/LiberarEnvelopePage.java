package br.com.ieptbto.cra.page.instrumentoProtesto;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.EnvelopeSLIP;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstrumentoProtestoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings( "serial" )
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class LiberarEnvelopePage extends BasePage<InstrumentoProtesto> {

	private static final Logger logger = Logger.getLogger(LiberarEnvelopePage.class);
	
	@SpringBean
	InstrumentoProtestoMediator instrumentoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	
	private InstrumentoProtesto instrumento;
	private List<EnvelopeSLIP> envelopes;
	private ListView<EnvelopeSLIP> listViewEnvelopes;
	

	public LiberarEnvelopePage() {
		this.instrumento = new InstrumentoProtesto();
		this.envelopes = instrumentoMediator.buscarEnvelopesPendetesLiberacao();
		carregarPage();
	}
	
	public LiberarEnvelopePage(String mensagem) {
		this.instrumento = new InstrumentoProtesto();
		this.envelopes = instrumentoMediator.buscarEnvelopesPendetesLiberacao();
		carregarPage();
		info(mensagem);
	}
	
	private void carregarPage() {
		final CheckGroup<EnvelopeSLIP> grupo = new CheckGroup<EnvelopeSLIP>("group", new ArrayList<EnvelopeSLIP>());
		Form<InstrumentoProtesto> form = new Form<InstrumentoProtesto>("form") {

			@Override
			protected void onSubmit() {
				List<EnvelopeSLIP> envelopesLiberados = new ArrayList<EnvelopeSLIP>();
				
				try{
					if (grupo.getModelObject().isEmpty() || grupo.getModelObject().size() == 0){
						throw new InfraException("Ao menos um envelope deve ser selecionado!");
					} else {
						envelopesLiberados = (List<EnvelopeSLIP>) grupo.getModelObject();
						instrumentoMediator.alterarEnvelopesParaEnviado(envelopesLiberados);
					}
					setResponsePage(new LiberarEnvelopePage("Os envelopes foram liberados com sucesso !"));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível liberar os envelopes! Entre em contato com a CRA.");
				}
			}
		};
		form.add(carregarListaEnvelopes());
		listViewEnvelopes.setReuseItems(true);
		grupo.add(listViewEnvelopes);
		form.add(grupo);
		add(form);
	}

	private ListView<EnvelopeSLIP> carregarListaEnvelopes() {
		return listViewEnvelopes = new ListView<EnvelopeSLIP>("envelopes", getEnvelopes()) {

			@Override
			protected void populateItem(ListItem<EnvelopeSLIP> item) {
				final EnvelopeSLIP envelope = item.getModelObject();
				item.add(new Check<EnvelopeSLIP>("checkbox", item.getModel()));
                item.add(new Label("banco", envelope.getBanco()));
                item.add(new Label("agencia", envelope.getAgenciaDestino()));
                item.add(new Label("municipio", envelope.getMunicipioAgenciaDestino()));
                item.add(new Label("uf", envelope.getUfAgenciaDestino()));
                item.add(new Label("quantidade", envelope.getQuantidadeInstrumentos()));
                item.add(new Label("codigoCRA", envelope.getCodigoCRA()));
			}
		};
	}
	
	private List<EnvelopeSLIP> getEnvelopes() {
		if (envelopes == null) {
			envelopes = new ArrayList<EnvelopeSLIP>();
		}
		return envelopes;
	}
	
	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return new CompoundPropertyModel<InstrumentoProtesto>(instrumento);
	}
}