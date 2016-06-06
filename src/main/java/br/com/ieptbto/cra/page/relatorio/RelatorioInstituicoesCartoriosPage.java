package br.com.ieptbto.cra.page.relatorio;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class RelatorioInstituicoesCartoriosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	private Arquivo arquivo;

	public RelatorioInstituicoesCartoriosPage() {
		this.arquivo = new Arquivo();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarPanelInstituicaoCartorio();

	}

	private void carregarPanelInstituicaoCartorio() {
		if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
			add(new RelatorioCartorioPanel("relatorioInstituicoesCartorioPanel", getModel(), getUser()));
		} else if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			add(new RelatorioInstituicaoPanel("relatorioInstituicoesCartorioPanel", getModel(), getUser()));
		}
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}