package br.com.ieptbto.cra.page.filiado;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.EstadoUtils;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class IncluirFiliadoPage extends BasePage<Filiado> {

	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	InstituicaoMediator InstituicaoMediator;
	@SpringBean
	FiliadoMediator filiadoMediator;
	private Filiado filiado;
	
	public IncluirFiliadoPage() {
		this.filiado = new Filiado();
		carregarCampos();
	}
	
	public IncluirFiliadoPage(Filiado filiado) {
		this.filiado = filiado;
		carregarCampos();
	}
	
	private void carregarCampos() {
		Form<?> form = new Form<Filiado>("form", getModel()){

			@Override
			protected void onSubmit() {
				Filiado novoFiliado = getModelObject();
				
				try {
					if (filiado.getId() != 0)
						filiadoMediator.alterarFiliado(novoFiliado);
					else {
						filiadoMediator.salvarFiliado(novoFiliado);
					}
					setResponsePage(new ListaFiliadoPage("Os dados do novo filiado foi salvo com sucesso !"));
				} catch (Exception e) {
					System.out.println(e.getMessage());
					error("Não foi possível cadastrar a empresa filiada ! Entre em contato com o IEPTB !");
				}
			}
		};
		form.add(campoNomeCredor());
		form.add(campoDocumentoCredor());
		form.add(campoEnderecoCredor());
		form.add(campoCepCredor());
		form.add(campoCidadeCredor());
		form.add(campoUfCredor());
		form.add(campoSituacao());
		form.add(campoInstituicaoConvenio());
		add(form);
	}
	
	private DropDownChoice<String> campoUfCredor() {
		DropDownChoice<String> textField = new DropDownChoice<String>("uf", EstadoUtils.getEstadosToList());
		textField.setLabel(new Model<String>("UF"));
		textField.setRequired(true);
		return textField;
	}

	private DropDownChoice<Municipio> campoCidadeCredor() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		DropDownChoice<Municipio> comboMunicipio = new DropDownChoice<Municipio>("municipio", municipioMediator.getMunicipiosTocantins(), renderer);
		comboMunicipio.setLabel(new Model<String>("Município"));
		comboMunicipio.setRequired(true);
		return comboMunicipio;
	}
	
	private DropDownChoice<Instituicao> campoInstituicaoConvenio() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		DropDownChoice<Instituicao> comboInstituicaoConvenio = new DropDownChoice<Instituicao>("instituicaoConvenio", InstituicaoMediator.getConvenios(), renderer);
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

	public Filiado getFiliado() {
		return filiado;
	}

	public void setFiliado(Filiado filiado) {
		this.filiado = filiado;
	}	
	
	@Override
	protected IModel<Filiado> getModel() {
		return new CompoundPropertyModel<Filiado>(filiado);
	}
}
