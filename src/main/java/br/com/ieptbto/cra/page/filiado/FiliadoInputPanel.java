package br.com.ieptbto.cra.page.filiado;

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

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.EstadoUtils;

public class FiliadoInputPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	InstituicaoMediator InstituicaoMediator;

	public FiliadoInputPanel(String id, IModel<Filiado> model) {
		super(id, model);

		adicionarCampos();
	}

	private void adicionarCampos() {
		add(campoNomeCredor());
		add(campoDocumentoCredor());
		add(campoEnderecoCredor());
		add(campoCepCredor());
		add(campoCidadeCredor());
		add(campoUfCredor());
		add(campoSituacao());
		add(campoInstituicaoConvenio());
	}

	private DropDownChoice<String> campoUfCredor() {
		DropDownChoice<String> textField = new DropDownChoice<String>("uf", EstadoUtils.getEstadosToList());
		textField.setLabel(new Model<String>("UF"));
		textField.setRequired(true);
		return textField;
	}

	private DropDownChoice<Municipio> campoCidadeCredor() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		DropDownChoice<Municipio> comboMunicipio =
				new DropDownChoice<Municipio>("municipio", municipioMediator.getMunicipiosTocantins(), renderer);
		comboMunicipio.setLabel(new Model<String>("Município"));
		comboMunicipio.setRequired(true);
		return comboMunicipio;
	}

	private DropDownChoice<Instituicao> campoInstituicaoConvenio() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		DropDownChoice<Instituicao> comboInstituicaoConvenio =
				new DropDownChoice<Instituicao>("instituicaoConvenio", InstituicaoMediator.getConvenios(), renderer);
		comboInstituicaoConvenio.setLabel(new Model<String>("Instituicao Convênio"));
		comboInstituicaoConvenio.setRequired(true);
		return comboInstituicaoConvenio;
	}

	private TextField<String> campoCepCredor() {
		TextField<String> textField = new TextField<String>("cep");
		textField.setLabel(new Model<String>("CEP"));
		textField.setRequired(true);
		return textField;
	}

	private TextArea<String> campoEnderecoCredor() {
		TextArea<String> textArea = new TextArea<String>("endereco");
		textArea.setLabel(new Model<String>("Endereço"));
		textArea.setRequired(true);
		return textArea;
	}

	private TextField<String> campoDocumentoCredor() {
		TextField<String> textField = new TextField<String>("cnpjCpf");
		textField.setLabel(new Model<String>("CNPJ/CPF"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> campoNomeCredor() {
		TextField<String> textField = new TextField<String>("razaoSocial");
		textField.setLabel(new Model<String>("Razão Social"));
		textField.setRequired(true);
		return textField;
	}

	private Component campoSituacao() {
		List<String> status = Arrays.asList(new String[] { "Ativo", "Não Ativo" });
		return new RadioChoice<String>("situacao", status).setRequired(true);
	}
}
