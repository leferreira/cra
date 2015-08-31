package br.com.ieptbto.cra.page.base;

import java.util.Date;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.arquivo.ListaArquivosPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.PeriodoDataUtil;

/**
 * 
 * @author Lefer
 *
 * @param <T>
 */
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HomePage<T extends AbstractEntidade<T>> extends BasePage<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	private Usuario usuario;
	private List<Remessa> confirmacoesPendentes;
	
	public HomePage() {
		super();
		carregarHomePage();
	}
	
	private void carregarHomePage() {
		this.usuario = getUser();
		this.confirmacoesPendentes = remessaMediator.confirmacoesPendentes(getUsuario().getInstituicao());
		labelArquivosPendentes();
		add(listaConfirmacoesPendentes());
		add(listaArquivosConfirmacoesPendentes());
//		listaRetornosPendentes();
	}

	private void labelArquivosPendentes() {
		if (!getConfirmacoesPendentes().isEmpty()) {
			warn("Existe(m) [ "+ getConfirmacoesPendentes().size() +" ] arquivo(s) pendente(s) de confirmação !");
		}
	}

	private Link<Remessa> listaArquivosConfirmacoesPendentes() {
		return new Link<Remessa>("arquivosConfirmacoesPendetes"){
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ListaArquivosPage(getUser()));
			}
		};
	}

	private ListView<Remessa> listaConfirmacoesPendentes(){
		return new ListView<Remessa>("listConfirmacoes", getConfirmacoesPendentes()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(new Label("arquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(new Label("instituicao", remessa.getInstituicaoDestino().getMunicipio().getNomeMunicipio()));
				item.add(new Label("pendente", PeriodoDataUtil.diferencaDeDiasEntreData(remessa.getDataRecebimento().toDate(), new Date())));
			}
		};
	}
	
	public HomePage(PageParameters parameters) {
		error(parameters.get("error"));
		carregarHomePage();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitulo() {
		return "CRA - Central de Remessa de Arquivos";
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public List<Remessa> getConfirmacoesPendentes() {
		return confirmacoesPendentes;
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}
}