package br.com.ieptbto.cra.page.tipoInstituicao;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@SuppressWarnings("serial")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class IncluirTipoInstituicaoPage extends BasePage<TipoInstituicao> {

	private TipoInstituicao tipoInstituicao;
	private TipoInstituicaoForm form;
	
	public IncluirTipoInstituicaoPage(){
		tipoInstituicao = new TipoInstituicao();
		setForm();
	}
	
	public IncluirTipoInstituicaoPage(TipoInstituicao tipoInstituicao){
		this.tipoInstituicao = tipoInstituicao;
		setForm();
	}
	
	public void setForm(){
		form = new TipoInstituicaoForm("form", getModel());
		form.add(new TipoInstituicaoInputPanel("tipoInputPanel", getModel(), tipoInstituicao));
		add(form);
	}
	
	@Override
	protected IModel<TipoInstituicao> getModel() {
		return new CompoundPropertyModel<TipoInstituicao>(tipoInstituicao);
	}

}
