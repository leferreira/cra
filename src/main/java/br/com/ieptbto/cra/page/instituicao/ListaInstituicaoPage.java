package br.com.ieptbto.cra.page.instituicao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ListaInstituicaoPage extends BasePage<Instituicao> {

	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private Instituicao instituicao;

	public ListaInstituicaoPage() {
		instituicao = new Instituicao();
		adicionarComponentes();
	}

	public ListaInstituicaoPage(String mensagem) {
		instituicao = new Instituicao();
		success(mensagem);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		botaoNovaInstituicao();
		listaInstituicoes();

	}

	private void botaoNovaInstituicao() {
		add(new Link<Instituicao>("botaoNovo") {

			public void onClick() {
				setResponsePage(new IncluirInstituicaoPage());
			}
		});
	}

	private void listaInstituicoes() {
		add(new ListView<Instituicao>("listViewInstituicao", listarInstituicoes()) {

			@Override
			protected void populateItem(ListItem<Instituicao> item) {
				final Instituicao instituicaoLista = item.getModelObject();

				Link<Instituicao> linkAlterar = new Link<Instituicao>("linkAlterar") {

					public void onClick() {
						setResponsePage(new IncluirInstituicaoPage(instituicaoLista));
					}
				};
				linkAlterar.add(new Label("nomeFantasia", instituicaoLista.getNomeFantasia()));
				item.add(linkAlterar);
				item.add(new Label("codigoCompensacao", instituicaoLista.getCodigoCompensacao()));
				item.add(new Label("tipoInstituicao", instituicaoLista.getTipoInstituicao().getTipoInstituicao().getLabel()));
				item.add(new Label("responsavel", instituicaoLista.getResponsavel()));
				item.add(new Label("email", instituicaoLista.getEmail()));
				item.add(new LabelValorMonetario<BigDecimal>("valorConfirmacao", instituicaoLista.getValorConfirmacao()));
				if (instituicaoLista.isSituacao()) {
					item.add(new Label("situacao", "Sim"));
				} else {
					item.add(new Label("situacao", "Não"));
				}
			}
		});
	}

	public IModel<List<Instituicao>> listarInstituicoes() {
		return new LoadableDetachableModel<List<Instituicao>>() {

			@Override
			protected List<Instituicao> load() {
				return instituicaoMediator.listarTodasInstituicoes();
			}
		};
	}

	@Override
	protected IModel<Instituicao> getModel() {
		return new CompoundPropertyModel<Instituicao>(instituicao);
	}
}
