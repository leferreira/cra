package br.com.ieptbto.cra.page.cartorio;

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
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.EmailValidator;

@SuppressWarnings("serial")
public class CartorioInputPanel extends Panel {

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;
	List<String> listaDeMunicipios;
	static TextField<String> campoInstituicao;
	private DropDownChoice<Municipio> combo;
	private Component button;

	public CartorioInputPanel(String id, IModel<Instituicao> model) {
		super(id, model);
		adicionarCampos();
	}

	public CartorioInputPanel(String id) {
		super(id);
		adicionarCampos();
	}

	public void adicionarCampos() {
		add(campoInstituicao());
		add(campoRazaoSocial());
		add(campoCnpj());
		add(campoEmail());
		add(campoContato());
		add(campoEndereco());
		add(campoResponsavel());
		add(campoStatus());
		add(comboMunicipios());
		add(campoBanco());
		add(campoContaCorrente());
		add(campoFavorecido());
		add(campoAgenciaConta());
		add(botaoSalvar());
	}

	private TextField<String> campoInstituicao() {
		campoInstituicao = new TextField<String>("nomeFantasia");
		campoInstituicao.setLabel(new Model<String>("Nome Fantasia"));
		campoInstituicao.setRequired(true);
		campoInstituicao.setOutputMarkupId(true);
		return campoInstituicao;
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

	private TextField<String> campoFavorecido() {
		TextField<String> text = new TextField<String>("favorecido");
		text.setLabel(new Model<String>("Favorecido"));
		return text;
	}

	private TextField<String> campoBanco() {
		TextField<String> text = new TextField<String>("bancoContaCorrente");
		text.setLabel(new Model<String>("Banco"));
		return text;
	}

	private TextField<String> campoContaCorrente() {
		TextField<String> text = new TextField<String>("numContaCorrente");
		text.setLabel(new Model<String>("Conta Corrente"));
		return text;
	}

	private TextField<String> campoAgenciaConta() {
		TextField<String> text = new TextField<String>("agenciaContaCorrente");
		text.setLabel(new Model<String>("Agência"));
		return text;
	}

	private Component campoStatus() {
		List<String> status = Arrays.asList(new String[] { "Ativo", "Não Ativo" });
		return new RadioChoice<String>("status", status);
	}

	private Component comboMunicipios() {

		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		combo = new DropDownChoice<Municipio>("municipio", municipioMediator.listarTodos(), renderer);
		combo.setLabel(new Model<String>("Município"));
		combo.setOutputMarkupId(true);
		combo.setRequired(true);
		return combo;
	}

	private Component botaoSalvar() {
		button = new Button("botaoSalvar") {
		};
		return button;
	}
}
