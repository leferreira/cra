package br.com.ieptbto.cra.page.instituicao;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
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
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.EnumerationSimNao;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.enumeration.TipoCampo51;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.validador.EmailValidator;

public class InstituicaoInputPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	TipoInstituicaoMediator tipoMediator;

	public InstituicaoInputPanel(String id, IModel<Instituicao> model) {
		super(id, model);

		adicionarCampos();
	}

	private void adicionarCampos() {
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
		add(comboMunicipios());
		add(comboTipoCampo51());
		add(comboTipoBatimento());
		add(comboPadraoLayoutXML());
		add(campoPermitidoSetores());
	}

	private DropDownChoice<EnumerationSimNao> campoPermitidoSetores() {
		IChoiceRenderer<EnumerationSimNao> renderer = new ChoiceRenderer<EnumerationSimNao>("label");
		DropDownChoice<EnumerationSimNao> dropDown =
				new DropDownChoice<EnumerationSimNao>("permitidoSetoresConvenio", Arrays.asList(EnumerationSimNao.values()), renderer);
		dropDown.setLabel(new Model<String>("Permitido Setores Convênios"));
		dropDown.setRequired(true);
		return dropDown;
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
		textField.setMarkupId("telefone");
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> campoValorConfirmacao() {
		TextField<String> textField = new TextField<String>("valorConfirmacao");
		textField.setLabel(new Model<String>("Valor Confirmação"));
		textField.setOutputMarkupId(true);
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
		DropDownChoice<TipoInstituicao> combo = new DropDownChoice<TipoInstituicao>("tipoInstituicao", tipoMediator.listaTipoInstituicao(),
				new ChoiceRenderer<TipoInstituicao>("tipoInstituicao.label"));
		combo.setLabel(new Model<String>("Tipo Instituição"));
		combo.setOutputMarkupId(true);
		combo.setRequired(true);
		return combo;
	}

	private DropDownChoice<TipoCampo51> comboTipoCampo51() {
		DropDownChoice<TipoCampo51> combo =
				new DropDownChoice<TipoCampo51>("tipoCampo51", Arrays.asList(TipoCampo51.values()), new ChoiceRenderer<TipoCampo51>("label"));
		combo.setLabel(new Model<String>("Tipo de Informação Campo 51"));
		combo.setOutputMarkupId(true);
		combo.setRequired(true);
		return combo;
	}

	private DropDownChoice<TipoBatimento> comboTipoBatimento() {
		DropDownChoice<TipoBatimento> combo =
				new DropDownChoice<TipoBatimento>("tipoBatimento", Arrays.asList(TipoBatimento.values()), new ChoiceRenderer<TipoBatimento>("label"));
		combo.setLabel(new Model<String>("Tipo Batimento"));
		combo.setOutputMarkupId(true);
		combo.setRequired(true);
		return combo;
	}

	private DropDownChoice<LayoutPadraoXML> comboPadraoLayoutXML() {
		DropDownChoice<LayoutPadraoXML> combo = new DropDownChoice<LayoutPadraoXML>("layoutPadraoXML", Arrays.asList(LayoutPadraoXML.values()),
				new ChoiceRenderer<LayoutPadraoXML>("label"));
		combo.setLabel(new Model<String>("Layout padrão XML"));
		combo.setOutputMarkupId(true);
		combo.setRequired(true);
		return combo;
	}

	private Component comboMunicipios() {
		DropDownChoice<Municipio> combo =
				new DropDownChoice<Municipio>("municipio", municipioMediator.listarTodos(), new ChoiceRenderer<Municipio>("nomeMunicipio"));
		combo.setLabel(new Model<String>("Município"));
		combo.setOutputMarkupId(true);
		combo.setRequired(true);
		return combo;
	}
}