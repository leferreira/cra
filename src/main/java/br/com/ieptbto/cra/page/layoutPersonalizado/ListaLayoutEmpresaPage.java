package br.com.ieptbto.cra.page.layoutPersonalizado;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.mediator.LayoutFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * 
 * @author Lefer
 *
 */

@SuppressWarnings({ "serial" })
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ListaLayoutEmpresaPage extends BasePage<LayoutFiliado> {

	@SpringBean
	LayoutFiliadoMediator layoutFiliadoMediator;
	private IModel<LayoutFiliado> layoutEmpresa;
	private List<LayoutFiliado> listEmpresas;

	public ListaLayoutEmpresaPage() {

		carregarDados();
		WebMarkupContainer divResultado = getDivResultado();
		divResultado.add(getTabelaResultado());
		add(divResultado);
	}

	@Override
	protected void adicionarComponentes() {
		// TODO Auto-generated method stub

	}

	private void carregarDados() {
		listEmpresas = layoutFiliadoMediator.buscarEmpresasComLayout();
	}

	private WebMarkupContainer getDivResultado() {
		WebMarkupContainer divCampo = new WebMarkupContainer("divResultado");
		divCampo.setOutputMarkupId(true);
		return divCampo;
	}

	private Component getTabelaResultado() {
		return new ListView<LayoutFiliado>("tabelaLayout", getListEmpresas()) {

			@Override
			protected void populateItem(ListItem<LayoutFiliado> item) {
				final LayoutFiliado layout = item.getModelObject();
				item.add(new Label("empresa", layout.getEmpresa().getNomeFantasia()));
				item.add(new Label("tipoArquivo", layout.getLayoutArquivo().getLabel()));
			}
		};
	}

	public List<LayoutFiliado> getListEmpresas() {
		return listEmpresas;
	}

	@Override
	protected IModel<LayoutFiliado> getModel() {
		return new CompoundPropertyModel<LayoutFiliado>(layoutEmpresa);
	}

}
