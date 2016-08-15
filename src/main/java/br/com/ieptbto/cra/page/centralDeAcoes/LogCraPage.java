package br.com.ieptbto.cra.page.centralDeAcoes;

import java.util.Arrays;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class LogCraPage extends BasePage<LogCra> {

	/***/
	private static final long serialVersionUID = 1L;

	private LogCra logCra;

	public LogCraPage(LogCra logCra) {
		this.logCra = logCra;

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(textFieldAcao());
		add(textAreaDescricao());
		add(textFieldTipoLog());
		add(textFieldDataHora());
		add(textFieldInstituicao());
		add(textFieldUsuario());
		add(textAreaException());

	}

	private TextField<String> textFieldAcao() {
		TextField<String> textField = new TextField<String>("acao", new Model<String>(logCra.getAcao().getLabel()));
		textField.setEnabled(false);
		return textField;
	}

	private TextArea<String> textAreaDescricao() {
		String descricao = logCra.getDescricao();
		if  (logCra.getDescricao() == null) {
			descricao = "";
		}
		TextArea<String> textArea = new TextArea<String>("descricao", new Model<String>(descricao));
		textArea.setEnabled(false);
		return textArea;
	}

	private WebMarkupContainer textFieldTipoLog() {
		WebMarkupContainer divTipoOcorrencia = new WebMarkupContainer("divTipoOcorrencia");
		divTipoOcorrencia.setMarkupId(getModel().getObject().getTipoLog().getIdHtml());
		divTipoOcorrencia.setOutputMarkupId(true);

		divTipoOcorrencia.add(new Label("tipoLog.label", new Model<String>(logCra.getTipoLog().getLabel())));
		return divTipoOcorrencia;
	}

	private TextField<String> textFieldDataHora() {
		TextField<String> textField = new TextField<String>("dataHora",
				new Model<String>(DataUtil.localDateToString(logCra.getData()) + " ás " + DataUtil.localTimeToString(logCra.getHora())));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> textFieldInstituicao() {
		TextField<String> textField = new TextField<String>("instituicao", new Model<String>(logCra.getInstituicao()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> textFieldUsuario() {
		TextField<String> textField = new TextField<String>("usuario", new Model<String>(logCra.getUsuario()));
		textField.setEnabled(false);
		return textField;
	}

	private TextArea<String> textAreaException() {
		TextArea<String> textArea;
		if (getModel().getObject().getExcecao() == null) {
			textArea = new TextArea<String>("exception", new Model<String>("Não foi registrada nenhuma exceção nesta ação..."));
		} else {
			textArea = new TextArea<String>("exception", new Model<String>(getModel().getObject().getExcecao().getMessage() + "\n"
					+ Arrays.toString(getModel().getObject().getExcecao().getStackTrace())));
		}
		textArea.setEnabled(false);
		return textArea;
	}

	@Override
	protected IModel<LogCra> getModel() {
		return new CompoundPropertyModel<LogCra>(logCra);
	}
}
