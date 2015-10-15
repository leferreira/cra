package br.com.ieptbto.cra.page.convenio.layout;

import java.util.Arrays;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.enumeration.CampoLayout;
import br.com.ieptbto.cra.mediator.FiliadoMediator;

/**
 * 
 * @author Lefer
 *
 */

public class LayoutEmpresaInputPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private FiliadoMediator filiadoMediator;

	public LayoutEmpresaInputPanel(String id, IModel<?> model) {
		super(id, model);
		addComponent();
	}

	private void addComponent() {
		DropDownChoice<Filiado> comboEmpresas = getComboEmpresas();
		add(comboEmpresas);

		WebMarkupContainer divCampo = getDivCampo();
		add(divCampo);

		TextField<Integer> campoOrdem = getCampoOrdem();
		divCampo.add(campoOrdem);

		TextField<Integer> campoPosicaoInicio = getCampoPosicaoInicio();
		divCampo.add(campoPosicaoInicio);

		TextField<Integer> campoPosicaoFim = getCampoPosicaoFim();
		divCampo.add(campoPosicaoFim);

		DropDownChoice<CampoLayout> comboCampos = getComboCampos();
		divCampo.add(comboCampos);

		WebMarkupContainer divResultado = getDivResultado();
		add(divResultado);

	}

	private WebMarkupContainer getDivResultado() {
		WebMarkupContainer divCampo = new WebMarkupContainer("divResultado");
		divCampo.setOutputMarkupId(true);
		return divCampo;
	}

	private DropDownChoice<CampoLayout> getComboCampos() {
		ChoiceRenderer<CampoLayout> renderer = new ChoiceRenderer<CampoLayout>("label");
		DropDownChoice<CampoLayout> combo = new DropDownChoice<CampoLayout>("campo", Arrays.asList(CampoLayout.values()), renderer);
		combo.setLabel(new Model<String>("Campo"));
		combo.setOutputMarkupId(true);
		combo.setRequired(true);
		return combo;
	}

	private TextField<Integer> getCampoPosicaoFim() {
		TextField<Integer> field = new TextField<Integer>("posicaoFim");
		field.setOutputMarkupId(true);
		return field;
	}

	private TextField<Integer> getCampoPosicaoInicio() {
		TextField<Integer> field = new TextField<Integer>("posicaoInicio");
		field.setOutputMarkupId(true);
		return field;
	}

	private TextField<Integer> getCampoOrdem() {
		TextField<Integer> field = new TextField<Integer>("ordem");
		field.setOutputMarkupId(true);
		return field;
	}

	private WebMarkupContainer getDivCampo() {
		WebMarkupContainer divCampo = new WebMarkupContainer("divCampo");
		divCampo.setOutputMarkupId(true);
		return divCampo;
	}

	private DropDownChoice<Filiado> getComboEmpresas() {
		ChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		DropDownChoice<Filiado> combo = new DropDownChoice<Filiado>("filiado", filiadoMediator.buscarTodosFiliados(), renderer);
		combo.setLabel(new Model<String>("Empresa"));
		combo.setOutputMarkupId(true);
		combo.setRequired(true);

		return combo;
	}
}
