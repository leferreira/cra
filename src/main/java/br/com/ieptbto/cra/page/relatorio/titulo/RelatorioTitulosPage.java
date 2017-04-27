package br.com.ieptbto.cra.page.relatorio.titulo;

import br.com.ieptbto.cra.beans.RelatorioBean;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class RelatorioTitulosPage extends BasePage<TituloRemessa> {

	private static final long serialVersionUID = 1L;
	private Usuario usuario;
	private RelatorioBean relatorioTitulosBean;

	public RelatorioTitulosPage() {
		this.relatorioTitulosBean = new RelatorioBean();
		this.usuario = getUser();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(carregarFormulario());
	}

	private RelatorioTitulosForm carregarFormulario() {
		FileUploadField fileUploadField = new FileUploadField("planilhaPendenciaCra", new ListModel<FileUpload>());
		fileUploadField.setLabel(new Model<String>("Anexo de Arquivo"));
		fileUploadField.setEnabled(false);

		TipoInstituicaoCRA tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
		if (tipoInstituicao == TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA || 
				tipoInstituicao == TipoInstituicaoCRA.CONVENIO) {
			this.relatorioTitulosBean.setTipoInstituicao(tipoInstituicao);
			this.relatorioTitulosBean.setBancoConvenio(usuario.getInstituicao());
		} else if (tipoInstituicao == TipoInstituicaoCRA.CARTORIO) {
			this.relatorioTitulosBean.setCartorio(usuario.getInstituicao());
		} else {
			fileUploadField.setEnabled(true);
		}
		
		IModel<RelatorioBean> model = new CompoundPropertyModel<RelatorioBean>(relatorioTitulosBean);
		RelatorioTitulosForm form = new RelatorioTitulosForm("form", model, fileUploadField);
		form.add(new RelatorioTitulosInputPanel("relatorioTitulosInputPanel", model, fileUploadField, usuario));
		return form;
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return null;
	}
}