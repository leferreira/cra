package br.com.ieptbto.cra.page.municipio;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.util.EstadoUtils;

/**
 * @author Thasso Araújo
 *
 */
public class MunicipioInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	public MunicipioInputPanel(String id, IModel<Municipio> model) {
		super(id, model);
		add(campoNomeMunicipio());
		add(campoIBGE());
		add(campoUF());
		add(campoCepInicio());
		add(campoCepFinal());
	}

	private TextField<String> campoNomeMunicipio() {
		TextField<String> textField = new TextField<String>("nomeMunicipio");
		textField.setLabel(new Model<String>("Nome do Município"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> campoIBGE() {
		TextField<String> textField = new TextField<String>("codigoIBGE");
		textField.setLabel(new Model<String>("Código do IBGE"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> campoCepInicio() {
		TextField<String> textField = new TextField<String>("faixaInicialCep");
		textField.setLabel(new Model<String>("Faixa de Cep Inicial"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> campoCepFinal() {
		TextField<String> textField = new TextField<String>("faixaFinalCep");
		textField.setLabel(new Model<String>("Faixa de Cep Final"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	private DropDownChoice<String> campoUF() {
		DropDownChoice<String> textField = new DropDownChoice<String>("uf", EstadoUtils.getEstadosToList());
		textField.setLabel(new Model<String>("Unidade Federal"));
		textField.setRequired(true);
		return textField;
	}
}