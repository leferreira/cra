package br.com.ieptbto.cra.page.base;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.mediator.RemessaMediator;

/**
 * 
 * @author Lefer
 *
 * @param <T>
 */
@AuthorizeInstantiation(value = "USER")
public class HomePage<T extends AbstractEntidade<T>> extends BasePage<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	RemessaMediator remessaMediator;

	public HomePage() {
		super();
		listaConfirmacoesPendentes();
//		listaRetornosPendentes();
	}

	private ListView<Remessa> listaConfirmacoesPendentes(){
		ListView<Remessa> listView = new ListView<Remessa>("listViewMunicipio", remessaMediator.confirmacoesPendentes(getUser().getInstituicao())) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(new Label("arquivo", remessa.getArquivo()));
				item.add(new Label("instituicao", remessa.getInstituicaoDestino().getNomeFantasia()));
			}
		};
		return listView;
	}
	
	public HomePage(PageParameters parameters) {
		error(parameters.get("error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitulo() {
		return "CRA - Central de Remessa de Arquivos";
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}

}