package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class MonitorarTitulosPage extends BasePage<TituloRemessa>{

	/***/
	private static final long serialVersionUID = 1L;
	private TituloRemessa titulo;
	private Instituicao instituicao;

	public MonitorarTitulosPage() {
		this.titulo = new TituloRemessa();
		this.instituicao = getUser().getInstituicao();
		
		adicionarPanels();
	}

	private void adicionarPanels() {
		Form<TituloRemessa> form = new Form<TituloRemessa>("form", getModel());	
		
		if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)){
			form.add(new MonitorarTitulosCraPanel("titulosInputPanel", getModel()));
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)){
			form.add(new MonitorarTitulosCartorioPanel("titulosInputPanel", getModel()));
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			form.add(new MonitorarTitulosInstituicaoPanel("titulosInputPanel", getModel()));
		}
		add(form);
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}
}
