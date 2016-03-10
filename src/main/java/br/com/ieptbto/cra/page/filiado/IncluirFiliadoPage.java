package br.com.ieptbto.cra.page.filiado;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.SetorFiliado;
import br.com.ieptbto.cra.enumeration.EnumerationSimNao;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
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
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class IncluirFiliadoPage extends BasePage<Filiado> {

    /**/
    private static final long serialVersionUID = 1L;

    @SpringBean
    private MunicipioMediator municipioMediator;
    @SpringBean
    private InstituicaoMediator InstituicaoMediator;
    @SpringBean
    private FiliadoMediator filiadoMediator;
    private Filiado filiado;
    private List<SetorFiliado> setoresFiliado;
    private SetorFiliado setorPadraoCra;

    public IncluirFiliadoPage() {
	this.filiado = new Filiado();
	this.setoresFiliado = new ArrayList<SetorFiliado>();
	;
	this.filiado.setSetoresFiliado(getSetoresFiliado());
	this.setoresFiliado.add(getSetorPadraoCra());

	carregarCampos();
    }

    public IncluirFiliadoPage(Filiado filiado) {
	this.filiado = filiado;
	this.setoresFiliado = filiadoMediator.buscarSetoresFiliado(filiado);
	this.filiado.setSetoresFiliado(getSetoresFiliado());

	carregarCampos();
    }

    private void carregarCampos() {
	Form<?> form = new Form<Filiado>("form", getModel()) {

	    /**/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSubmit() {
		Filiado novoFiliado = getModelObject();
		boolean existeSetorAtivo = false;

		try {
		    for (SetorFiliado setor : getSetoresFiliado()) {
			if (setor.isSituacaoAtivo() == true) {
			    existeSetorAtivo = true;
			    break;
			}
		    }
		    if (existeSetorAtivo == false) {
			throw new InfraException("Não é possível salvar os dados do filiado! Ao menos um setor deve estar ativo!");
		    }

		    if (filiado.getId() != 0) {
			filiadoMediator.alterarFiliado(novoFiliado);
		    } else {
			filiadoMediator.salvarFiliado(novoFiliado);
		    }
		    setResponsePage(new ListaFiliadoPage("Os dados do novo filiado foi salvo com sucesso !"));
		} catch (InfraException ex) {
		    error(ex.getMessage());
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

	WebMarkupContainer divSetoresFiliados = new WebMarkupContainer("divSetoresFiliados");
	divSetoresFiliados.add(new SetorFiliadoInputPanel("setorPanel", getModel(), getSetoresFiliado()));
	divSetoresFiliados.add(listaSetoresFiliado());
	if (getUser().getInstituicao().getPermitidoSetoresConvenio().equals(EnumerationSimNao.NAO)
		&& getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
	    divSetoresFiliados.setVisible(false);
	}
	add(divSetoresFiliados);
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

    private ListView<SetorFiliado> listaSetoresFiliado() {
	return new ListView<SetorFiliado>("listaSetor", getSetoresFiliado()) {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void populateItem(ListItem<SetorFiliado> item) {
		SetorFiliado setor = item.getModelObject();
		item.add(new Label("contador", item.getIndex() + 1));
		item.add(new Label("descricao", setor.getDescricao()));
		item.add(new Label("ativo", verificarSituacao(setor.isSituacaoAtivo())));
		item.add(alterarSituacao(setor));
	    }

	    private String verificarSituacao(Boolean ativo) {
		if (ativo.equals(true))
		    return "Sim";
		return "Não";
	    }

	    private Link<SetorFiliado> alterarSituacao(final SetorFiliado setor) {
		return new Link<SetorFiliado>("alterar") {

		    /***/
		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {

			try {
			    if (setor.isSituacaoAtivo() == true) {
				setor.setSituacaoAtivo(false);
			    } else {
				setor.setSituacaoAtivo(true);
			    }

			} catch (InfraException e) {
			    System.out.println(e.getMessage());
			    getFeedbackPanel().error(e.getMessage());
			} catch (Exception e) {
			    System.out.println(e.getMessage());
			    getFeedbackPanel().error("Não foi possível remover o setor da empresa filiada! Entre em contato com o CRA !");
			}
		    }
		};
	    }
	};
    }

    public SetorFiliado getSetorPadraoCra() {
	this.setorPadraoCra = new SetorFiliado();
	this.setorPadraoCra.setDescricao("GERAL");
	this.setorPadraoCra.setSetorPadraoFiliado(true);
	this.setorPadraoCra.setSituacaoAtivo(true);
	return setorPadraoCra;
    }

    public List<SetorFiliado> getSetoresFiliado() {
	if (setoresFiliado == null) {
	    setoresFiliado = new ArrayList<SetorFiliado>();
	}
	return setoresFiliado;
    }

    public Filiado getFiliado() {
	return filiado;
    }

    @Override
    protected IModel<Filiado> getModel() {
	return new CompoundPropertyModel<Filiado>(filiado);
    }
}
