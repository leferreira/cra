package br.com.ieptbto.cra.page.filiado;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class ListaFiliadoPage extends BasePage<Filiado>{

	private Filiado filiado;
	
	@SpringBean
	FiliadoMediator filiadoMediator;
	
	public ListaFiliadoPage() {
		carregarListaFiliadoPage();
	}
	
	public ListaFiliadoPage(String mensagem) {
		carregarListaFiliadoPage();
		info(mensagem);
	}
	
	private void carregarListaFiliadoPage(){
		this.filiado = new Filiado();
		add(new Link("botaoNovo") {

			public void onClick() {
				setResponsePage(new IncluirFiliadoPage());
            }
        });
		add(carregarListaFiliados());
	}
	
	private ListView<Filiado> carregarListaFiliados(){
		return new ListView<Filiado>("listViewFiliado", filiadoMediator.buscarListaFiliados(getUser().getInstituicao())) {

			@Override
			protected void populateItem(ListItem<Filiado> item) {
				final Filiado filiado = item.getModelObject();
				
				Link linkAlterar = new Link("linkAlterar") {

					public void onClick() {
						setResponsePage(new IncluirFiliadoPage(filiado));
		            }
				}; 
				linkAlterar.add(new Label("nome", filiado.getRazaoSocial()));
				item.add(linkAlterar);
				
				item.add(new Label("instituicao", filiado.getInstituicaoConvenio().getNomeFantasia()));
				item.add(new Label("cidade", filiado.getMunicipio().getNomeMunicipio()));
				item.add(new Label("uf", filiado.getUf()));
				item.add(new Label("ativo", verificarSituacao(filiado.isAtivo()) ));
			}
		};
	}
	
	private String verificarSituacao(Boolean ativo){ 
		if (ativo.equals(true))
			return "Sim";
		return "Não";
	}
	
	@Override
	protected IModel<Filiado> getModel() {
		return new CompoundPropertyModel<Filiado>(filiado);
	}
}
