package br.com.ieptbto.cra.page.instituicao;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.util.EmailValidator;

/**
 * @author Thasso Araújo
 *
 */
public class InstituicaoInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	TipoInstituicaoMediator tipoMediator;

	public InstituicaoInputPanel(String id, IModel<Instituicao> model) {
		super(id, model);
		add(campoNomeFantasia());
		add(campoRazaoSocial());
		add(comboTipoInstituicao());
		add(campoCnpj());
		add(campoCodigoCompensacao());
		add(campoEmail());
		add(campoContato());
		add(campoValorConfirmacao());
		add(campoEndereco());
		add(campoResponsavel());
		add(campoAgenciaCentralizadora());
		add(campoStatus());
		add(new Button("botaoSalvar"));
	}

	private TextField<String> campoNomeFantasia() {
		TextField<String> textField = new TextField<String>("nomeFantasia");
		textField.setLabel(new Model<String>("Nome Fantasia"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> campoRazaoSocial() {
		TextField<String> textField = new TextField<String>("razaoSocial");
		textField.setLabel(new Model<String>("Razão Social"));
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> campoCnpj() {
		TextField<String> textField = new TextField<String>("cnpj");
		textField.setLabel(new Model<String>("CNPJ"));
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> campoCodigoCompensacao() {
		TextField<String> textField = new TextField<String>("codigoCompensacao");
		textField.setLabel(new Model<String>("Código Compensação"));
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> campoEmail() {
		TextField<String> textField = new TextField<String>("email");
		textField.setLabel(new Model<String>("Email"));
		textField.add(new EmailValidator());
		return textField;
	}

	private TextField<String> campoContato() {
		TextField<String> textField = new TextField<String>("contato");
		textField.setLabel(new Model<String>("Contato"));
		return textField;
	}

	private TextField<String> campoValorConfirmacao() {
		TextField<String> textField = new TextField<String>("valorConfirmacao");
		textField.setLabel(new Model<String>("Valor Confirmação"));
		return textField;
	}

	private TextArea<String> campoEndereco() {
		TextArea<String> text = new TextArea<String>("endereco");
		text.setLabel(new Model<String>("Endereço"));
		return text;
	}

	private TextField<String> campoResponsavel() {
		TextField<String> text = new TextField<String>("responsavel");
		text.setLabel(new Model<String>("Resposável"));
		return text;
	}

	private TextField<String> campoAgenciaCentralizadora() {
		TextField<String> text = new TextField<String>("agenciaCentralizadora");
		text.setLabel(new Model<String>("Agência Centralizadora"));
		return text;
	}

	private Component campoStatus() {
		List<String> status = Arrays.asList(new String[] { "Ativo", "Não Ativo" });
		return new RadioChoice<String>("status", status);
	}

	private DropDownChoice<TipoInstituicao> comboTipoInstituicao() {
		IChoiceRenderer<TipoInstituicao> renderer = new ChoiceRenderer<TipoInstituicao>("tipoInstituicao.label");
		DropDownChoice<TipoInstituicao> combo = new DropDownChoice<TipoInstituicao>("tipoInstituicao", tipoMediator.listaTipoInstituicao(), renderer);
		combo.setLabel(new Model<String>("Tipo Instituição"));
		combo.setOutputMarkupId(true);
		combo.setRequired(true);
		return combo;
	}
}
