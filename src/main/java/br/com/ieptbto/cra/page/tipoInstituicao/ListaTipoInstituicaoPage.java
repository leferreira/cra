package br.com.ieptbto.cra.page.tipoInstituicao;

import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
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

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ListaTipoInstituicaoPage extends BasePage<TipoInstituicao> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	TipoInstituicaoMediator tipoInstituicaoMediator;

	private TipoInstituicao tipoInstituicao;

	public ListaTipoInstituicaoPage() {
		this.tipoInstituicao = new TipoInstituicao();
		adicionarComponentes();
	}

	public ListaTipoInstituicaoPage(String mensagem) {
		this.tipoInstituicao = new TipoInstituicao();
		success(mensagem);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarListaTipos();

	}

	private void carregarListaTipos() {
		add(new ListView<TipoInstituicao>("listViewTipos", tipoInstituicaoMediator.listarTipos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TipoInstituicao> item) {
				final TipoInstituicao tipoLista = item.getModelObject();

				Link<TipoInstituicao> linkAlterar = new Link<TipoInstituicao>("linkAlterar") {
					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new IncluirTipoInstituicaoPage(tipoLista));
					}
				};
				linkAlterar.add(new Label("tipoInstituicao", tipoLista.getTipoInstituicao().getLabel()));
				item.add(linkAlterar);
			}
		});
	}

	@Override
	protected IModel<TipoInstituicao> getModel() {
		return new CompoundPropertyModel<TipoInstituicao>(tipoInstituicao);
	}
}
