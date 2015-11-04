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
@SuppressWarnings("serial")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER})
public class RelatorioSinteticoPage extends BasePage<Remessa> {

	private Remessa remessa;
	private Instituicao instituicao;
	
	public RelatorioSinteticoPage() {
		this.remessa  = new Remessa();
		this.instituicao = getUser().getInstituicao();
		
		Form<Remessa> form = new Form<Remessa>("form", getModel());
		if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)){
			form.add(new RelatorioSinteticoCraPanel("relatorioPanel", getModel()));
		} else if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)){
			form.add(new RelatorioSinteticoCartorioPanel("relatorioPanel", getModel(), getInstituicao()));
		} else if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			form.add(new RelatorioSinteticoInstituicaoPanel("relatorioPanel", getModel(), getInstituicao()));
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
