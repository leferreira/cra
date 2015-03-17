package br.com.ieptbto.cra.page.municipio;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.Municipio;

@SuppressWarnings({ "serial" })
public class MunicipioInputPanel extends Panel {
	
	Component button;

	public MunicipioInputPanel(String id, IModel<Municipio> model){
		super(id, model);
		adicionarCampos();
	}
	
	public MunicipioInputPanel(String id) {
		super(id);
		adicionarCampos();
	}
	
	private void adicionarCampos() {
		add(campoNomeMunicipio());
		add(campoIBGE());
		add(campoUF());
		add(botaoSalvar());
	}
	
	private TextField<String> campoNomeMunicipio() {
		TextField<String> textField = new TextField<String>("nomeMunicipio");
		textField.setLabel(new Model<String>("Nome do Município"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}
	
	private TextField<String> campoIBGE() {
		TextField<String> textField = new TextField<String>("codIBGE");
		textField.setLabel(new Model<String>("Código do IBGE"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}
	
	private TextField<String> campoUF() {
		TextField<String> textField = new TextField<String>("uf");
		textField.setLabel(new Model<String>("Unidade Federal"));
		textField.setOutputMarkupId(true);
		return textField;
	}
	
	private Component botaoSalvar() {
		button = new Button("botaoSalvar") {
		};
		return button;
	}
}