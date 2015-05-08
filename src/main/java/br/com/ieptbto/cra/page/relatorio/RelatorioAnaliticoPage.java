package br.com.ieptbto.cra.page.relatorio;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class RelatorioAnaliticoPage extends BasePage<Remessa>{

	/***/
	private static final long serialVersionUID = 1L;
	private Remessa remessa;
	private Form<Remessa> form;
	private Instituicao instituicao;
	
	public RelatorioAnaliticoPage() {
		this.remessa  = new Remessa();
		this.instituicao = getUser().getInstituicao();
		
		form = new Form<Remessa>("form", getModel());
		if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals("CRA")){
			form.add(new RelatorioCraPanel("relatorioPanel", getModel(), getInstituicao()));
		} else if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals("Cartório")){
			form.add(new RelatorioCartorioPanel("relatorioPanel", getModel(), getInstituicao()));
		} else {
			form.add(new RelatorioBancoPanel("relatorioPanel", getModel(),getInstituicao()));
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
