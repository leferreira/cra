package br.com.ieptbto.cra.page.instituicao;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.enumeration.TipoCampo51;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.validador.EmailValidator;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.CompoundValidator;

import java.util.Arrays;
import java.util.List;

public class InstituicaoInputPanel extends Panel {

    @SpringBean
    private MunicipioMediator municipioMediator;
    @SpringBean
    private TipoInstituicaoMediator tipoMediator;

    private static final long serialVersionUID = 1L;

    public InstituicaoInputPanel(String id, IModel<Instituicao> model) {
        super(id, model);
        add(comboTipoInstituicao());
        add(campoStatus());
        add(comboMunicipios());
        add(comboTipoCampo51());
        add(comboTipoBatimento());
        add(comboPadraoLayoutXML());

        add(addCheckBox("oficioDesistenciaCancelamentoObrigatorio", "Ofício Desistência e Cancelamento Obrigatório", true));
        add(addCheckBox("layoutRetornoRecebimentoEmpresa", "Layout de Retorno Recebimento Empresa", true));
        add(addCheckBox("administrarEmpresasFiliadas", "Administrar Empresas Filiadas", true));
        add(addCheckBox("setoresConvenio", "Permitir Setores Filiados", true));
        add(addCheckBox("seloDiferido", "Selo Diferido", true));
        add(addCheckBox("verificacaoManual", "Verificação Manual", true));
        add(addCheckBox("taxaCra", "Taxa Cra", true));
        add(addCheckBox("retornoCancelamento", "Retorno de Cancelamento (CP, AC)", false));

        add(addTextField("nomeFantasia", "Nome Fantasia", true));
        add(addTextField("razaoSocial", "Razão Social"));
        add(addTextField("cnpj", "CNPJ"));
        add(addTextField("codigoCompensacao", "Código Compensação"));
        add(addTextField("email", "Email", new EmailValidator()));
        add(addTextField("contato", "Contato"));
        add(addTextField("bairro", "Bairro"));
        add(addTextField("valorConfirmacao", "Valor Confirmação"));
        add(addTextField("responsavel", "Resposável"));
        add(addTextField("agenciaCentralizadora", "Agência Centralizadora"));

        add(addTextArea("endereco", "Endereço"));
    }

    private CheckBox addCheckBox(String id, String label, boolean required) {
        CheckBox checkbox = new CheckBox(id);
        checkbox.setLabel(new Model<String>(label));
        checkbox.setRequired(required);
        return checkbox;
    }

    private TextArea<String> addTextArea(String id, String label) {
        TextArea<String> text = new TextArea<String>(id);
        text.setLabel(new Model<String>(label));
        return text;
    }

    private TextField<String> addTextField(String id, String label, CompoundValidator<String> validador) {
        return addTextField(id, label, false, validador);
    }

    private TextField<String> addTextField(String id, String label) {
        return addTextField(id, label, false);
    }

    private TextField<String> addTextField(String id, String label, boolean required) {
        TextField<String> textField = new TextField<String>(id);
        textField.setLabel(new Model<String>(label));
        textField.setRequired(required);
        textField.setOutputMarkupId(true);
        return textField;
    }

    private TextField<String> addTextField(String id, String label, boolean required, CompoundValidator<String> validador) {
        TextField<String> textField = new TextField<String>(id);
        textField.setLabel(new Model<String>(label));
        textField.setRequired(required);
        textField.add(validador);
        textField.setOutputMarkupId(true);
        return textField;
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
        DropDownChoice<TipoCampo51> combo = new DropDownChoice<TipoCampo51>("tipoCampo51", Arrays.asList(TipoCampo51.values()),
                new ChoiceRenderer<TipoCampo51>("label"));
        combo.setLabel(new Model<String>("Tipo de Informação Campo 51"));
        combo.setOutputMarkupId(true);
        combo.setRequired(true);
        return combo;
    }

    private DropDownChoice<TipoBatimento> comboTipoBatimento() {
        DropDownChoice<TipoBatimento> combo = new DropDownChoice<TipoBatimento>("tipoBatimento", Arrays.asList(TipoBatimento.values()),
                new ChoiceRenderer<TipoBatimento>("label"));
        combo.setLabel(new Model<String>("Tipo Batimento"));
        combo.setOutputMarkupId(true);
        combo.setRequired(true);
        return combo;
    }

    private DropDownChoice<LayoutPadraoXML> comboPadraoLayoutXML() {
        DropDownChoice<LayoutPadraoXML> combo = new DropDownChoice<LayoutPadraoXML>("layoutPadraoXML",
                Arrays.asList(LayoutPadraoXML.values()), new ChoiceRenderer<LayoutPadraoXML>("label"));
        combo.setLabel(new Model<String>("Layout padrão XML"));
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

}