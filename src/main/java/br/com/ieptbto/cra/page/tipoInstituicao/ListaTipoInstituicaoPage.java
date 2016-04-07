package br.com.ieptbto.cra.page.tipoInstituicao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
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

import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

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
		info(mensagem);
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
				String todasPermissoes = "";
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

				List<PermissaoEnvio> permissoes = tipoInstituicaoMediator.permissoesPorTipoInstituicao(tipoLista);

				if (!permissoes.isEmpty()) {
					for (PermissaoEnvio p : permissoes) {
						todasPermissoes += p.getTipoArquivo().getTipoArquivo().getConstante() + " ";
					}
					item.add(new Label("permissaoEnvio", todasPermissoes));
				} else {
					item.add(new Label("permissaoEnvio", StringUtils.EMPTY));
				}
				todasPermissoes = "";
			}
		});
	}

	@Override
	protected IModel<TipoInstituicao> getModel() {
		return new CompoundPropertyModel<TipoInstituicao>(tipoInstituicao);
	}
}
