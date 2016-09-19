package br.com.ieptbto.cra.page.relatorio.titulo;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import br.com.ieptbto.cra.bean.RelatorioFormBean;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class RelatorioTitulosPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	private TituloRemessa titulo;
	private FileUploadField fileUploadField;

	public RelatorioTitulosPage() {
		this.titulo = new TituloRemessa();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		fileUploadPlanilhaPendenciasCra();
		carregarFormulario();
	}

	private void carregarFormulario() {
		RelatorioFormBean relatorioTitulosBean = new RelatorioFormBean();
		RelatorioTitulosForm form =
				new RelatorioTitulosForm("form", new CompoundPropertyModel<RelatorioFormBean>(relatorioTitulosBean), fileUploadField);
		form.add(new RelatorioTitulosInputPanel("relatorioTitulosInputPanel",
				new CompoundPropertyModel<RelatorioFormBean>(relatorioTitulosBean), fileUploadField));
		add(form);
	}

	private FileUploadField fileUploadPlanilhaPendenciasCra() {
		fileUploadField = new FileUploadField("planilhaPendenciaCra", new ListModel<FileUpload>());
		fileUploadField.setLabel(new Model<String>("Anexo de Arquivo"));
		return fileUploadField;
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}
}