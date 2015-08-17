package br.com.ieptbto.cra.page.instituicao;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
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

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.EmailValidator;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class IncluirInstituicaoPage extends BasePage<Instituicao> {

	private static final Logger logger = Logger.getLogger(IncluirInstituicaoPage.class);
	
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;
	@SpringBean
	private TipoInstituicaoMediator tipoMediator;
	private Instituicao instituicao;

	public IncluirInstituicaoPage() {
		instituicao = new Instituicao();
		setForm();
	}

	public IncluirInstituicaoPage(Instituicao instituicao) {
		this.instituicao = instituicao;
		setForm();
	}

	public void setForm() {
		Form<Instituicao> form = new Form<Instituicao>("form", getModel()){
			
			@Override
			public void onSubmit() {
				Instituicao instituicao = getModelObject();
				
				try {
					if (getModelObject().getId() != 0) {
						Instituicao instituicaoSalvo = instituicaoMediator.alterar(instituicao);
						setResponsePage(new DetalharInstituicaoPage(instituicaoSalvo));
					}else{
						if (instituicaoMediator.isInstituicaoNaoExiste(instituicao)) {
							Instituicao instituicaoSalvo = instituicaoMediator.salvar(instituicao);
							setResponsePage(new DetalharInstituicaoPage(instituicaoSalvo));
						} else {
							error("Instituição não criada, pois já existe!");
						}
					}	
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar esta operação! Entre em contato com a CRA!");
				}
			}
		};
		form.add(campoNomeFantasia());
		form.add(campoRazaoSocial());
		form.add(comboTipoInstituicao());
		form.add(campoCnpj());
		form.add(campoCodigoCompensacao());
		form.add(campoEmail());
		form.add(campoContato());
		form.add(campoValorConfirmacao());
		form.add(campoEndereco());
		form.add(campoResponsavel());
		form.add(campoAgenciaCentralizadora());
		form.add(campoStatus());
		form.add(comboMunicipios());
		form.add(new Button("botaoSalvar"));
		add(form);
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
		IChoiceRenderer<TipoInstituicao> renderer = new ChoiceRenderer<TipoInstituicao>("tipoInstituicao.label");
		DropDownChoice<TipoInstituicao> combo = new DropDownChoice<TipoInstituicao>("tipoInstituicao", tipoMediator.listaTipoInstituicao(), renderer);
		combo.setLabel(new Model<String>("Tipo Instituição"));
		combo.setOutputMarkupId(true);
		combo.setRequired(true);
		return combo;
	}
	
	private Component comboMunicipios() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		DropDownChoice<Municipio> combo = new DropDownChoice<Municipio>("municipio", municipioMediator.listarTodos(), renderer);
		combo.setLabel(new Model<String>("Município"));
		combo.setOutputMarkupId(true);
		combo.setRequired(true);
		return combo;
	}

	@Override
	protected IModel<Instituicao> getModel() {
		return new CompoundPropertyModel<Instituicao>(instituicao);
	}
}
