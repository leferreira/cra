package br.com.ieptbto.cra.page.desistenciaCancelamento;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class BuscarDesistenciaCancelamentoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	private Arquivo arquivo;
	private Form<Arquivo> form;
	private Instituicao instituicao;

	public BuscarDesistenciaCancelamentoPage() {
		this.arquivo = new Arquivo();
		this.instituicao = getUser().getInstituicao();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioDevulucaoCancelamento();
		panelDevulucaoCancelamento();
	}

	private void formularioDevulucaoCancelamento() {
		form = new Form<Arquivo>("form", getModel());
	}

	private void panelDevulucaoCancelamento() {
		if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			form.add(new BuscarDesistenciaCancelamentoCraPanel("cancelamentoDevolvidoInputPanel", getModel(), getInstituicao()));
		} else if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
			form.add(new BuscarDesistenciaCancelamentoCartorioPanel("cancelamentoDevolvidoInputPanel", getModel(), getInstituicao(), getUser()));
		} else if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			form.add(new BuscarDesistenciaCancelamentoInstituicaoPanel("cancelamentoDevolvidoInputPanel", getModel(), getInstituicao()));
		}
		add(form);
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}