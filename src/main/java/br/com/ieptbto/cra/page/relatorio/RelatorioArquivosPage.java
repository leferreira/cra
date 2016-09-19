package br.com.ieptbto.cra.page.relatorio;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.bean.ArquivoFormBean;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class RelatorioArquivosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	private ArquivoFormBean relatorioArquivosBean;

	public RelatorioArquivosPage() {
		this.relatorioArquivosBean = new ArquivoFormBean();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarFormularioArquivoCartorio();

	}

	private void carregarFormularioArquivoCartorio() {
		RelatorioArquivosForm form = new RelatorioArquivosForm("form", new CompoundPropertyModel<ArquivoFormBean>(relatorioArquivosBean));
		form.add(new RelatorioArquivosInputPanel("relatorioArquivosInputPanel",
				new CompoundPropertyModel<ArquivoFormBean>(relatorioArquivosBean)));
		add(form);
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return null;
	}
}