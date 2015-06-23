package br.com.ieptbto.cra.page.relatorio;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER})
public class RelatorioPage extends BasePage<Remessa> {

	/***/
	private static final long serialVersionUID = 1L;
	
	private Remessa remessa;
	private Form<Remessa> form;
	private Instituicao instituicao;
	
	public RelatorioPage() {
		this.remessa  = new Remessa();
		this.instituicao = getUser().getInstituicao();
		form = new Form<Remessa>("form", getModel());
		
		if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)){
			form.add(new RelatorioCraPanel("relatorioPanel", getModel()));
		} else if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)){
			form.add(new RelatorioCartorioPanel("relatorioPanel", getModel(), instituicao));
		} else if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			form.add(new RelatorioInstituicaoPanel("relatorioPanel", getModel(), getInstituicao()));
		}
		add(form);
	}

	
	public Remessa getRemessa() {
		return remessa;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}


	public Instituicao getInstituicao() {
		return instituicao;
	}


	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}

	@Override
	protected IModel<Remessa> getModel() {
		return new CompoundPropertyModel<Remessa>(remessa);
	}
}
