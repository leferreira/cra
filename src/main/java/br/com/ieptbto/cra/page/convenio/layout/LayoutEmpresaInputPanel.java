package br.com.ieptbto.cra.page.convenio.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.enumeration.CampoLayout;
import br.com.ieptbto.cra.mediator.FiliadoMediator;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
public class LayoutEmpresaInputPanel extends Panel {

	@SpringBean
	private FiliadoMediator filiadoMediator;

	private DropDownChoice<Filiado> comboEmpresas;
	private TextField<Integer> campoOrdem;
	private TextField<Integer> campoPosicaoInicio;
	private TextField<Integer> campoPosicaoFim;
	private DropDownChoice<CampoLayout> comboCampos;
	private LayoutFiliado layoutFiliado;
	private List<LayoutFiliado> listaLayoutFiliado;

	private WebMarkupContainer divResultado;

	public LayoutEmpresaInputPanel(String id, IModel<?> model) {
		super(id, model);
		layoutFiliado = new LayoutFiliado();
		addComponent();
	}

	private void addComponent() {
		add(getComboEmpresas());

		WebMarkupContainer divCampo = getDivCampo();
		add(divCampo);

		divCampo.add(getCampoOrdem());
		divCampo.add(getCampoPosicaoInicio());
		divCampo.add(getCampoPosicaoFim());
		divCampo.add(getComboCampos());
		divCampo.add(getButaoAdd());

		divResultado = getDivResultado();
		divResultado.add(getTabelaResultado());
		divResultado.setOutputMarkupId(true);
		add(divResultado);

	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		// divResultado.setVisible(!getCamposFiliado().isEmpty());
	}

	private Component getTabelaResultado() {
		return new ListView<LayoutFiliado>("tabelaLayout", getCamposFiliado()) {

			@Override
			protected void populateItem(ListItem<LayoutFiliado> item) {
				final LayoutFiliado layout = item.getModelObject();
				item.add(new Label("campo", layout.getCampo().getLabel()));
				item.add(new Label("ordem", layout.getOrdem()));
				item.add(new Label("posicaoInicio", layout.getPosicaoInicio()));
				item.add(new Label("posicaoFim", layout.getPosicaoFim()));
				item.add(getButtonRemove(layout));
			}

			private Component getButtonRemove(final LayoutFiliado layout) {
				return new Link<Retorno>("remover") {
					@Override
					public void onClick() {
						getCamposFiliado().remove(layout);
					}
				};
			}
		};
	}

	private List<LayoutFiliado> getCamposFiliado() {
		if (listaLayoutFiliado == null) {
			listaLayoutFiliado = new ArrayList<LayoutFiliado>();
		}
		return listaLayoutFiliado;
	}

	private AjaxButton getButaoAdd() {
		AjaxButton button = new AjaxButton("add") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				layoutFiliado = new LayoutFiliado();
				Integer posicaoFim = (Integer) campoPosicaoFim.getDefaultModelObject();
				Integer posicaoInicio = (Integer) campoPosicaoInicio.getDefaultModelObject();
				Integer ordem = (Integer) campoOrdem.getDefaultModelObject();
				CampoLayout campo = comboCampos.getConvertedInput();
				Filiado empresa = comboEmpresas.getConvertedInput();
				layoutFiliado.setCampo(campo);
				layoutFiliado.setFiliado(empresa);
				layoutFiliado.setOrdem(ordem);
				layoutFiliado.setPosicaoFim(posicaoFim);
				layoutFiliado.setPosicaoInicio(posicaoInicio);
				getCamposFiliado().add(layoutFiliado);
				divResultado.setVisible(true);
				target.add(divResultado);
			}

		};

		return button;
	}

	private WebMarkupContainer getDivResultado() {
		WebMarkupContainer divCampo = new WebMarkupContainer("divResultado");
		divCampo.setOutputMarkupId(true);
		return divCampo;
	}

	private DropDownChoice<CampoLayout> getComboCampos() {
		ChoiceRenderer<CampoLayout> renderer = new ChoiceRenderer<CampoLayout>("label");
		if (comboCampos == null) {
			comboCampos = new DropDownChoice<CampoLayout>("campo", Arrays.asList(CampoLayout.values()), renderer);
			comboCampos.setLabel(new Model<String>("Campo"));
			comboCampos.setOutputMarkupId(true);
			comboCampos.setRequired(true);
		}
		return comboCampos;
	}

	private TextField<Integer> getCampoPosicaoFim() {
		if (campoPosicaoFim == null) {
			campoPosicaoFim = new TextField<Integer>("posicaoFim");
			campoPosicaoFim.setOutputMarkupId(true);
		}
		return campoPosicaoFim;
	}

	private TextField<Integer> getCampoPosicaoInicio() {
		if (campoPosicaoInicio == null) {
			campoPosicaoInicio = new TextField<Integer>("posicaoInicio");
			campoPosicaoInicio.setOutputMarkupId(true);
		}
		return campoPosicaoInicio;
	}

	private TextField<Integer> getCampoOrdem() {
		campoOrdem = new TextField<Integer>("ordem");
		campoOrdem.setOutputMarkupId(true);
		return campoOrdem;
	}

	private WebMarkupContainer getDivCampo() {
		WebMarkupContainer divCampo = new WebMarkupContainer("divCampo");
		divCampo.setOutputMarkupId(true);
		return divCampo;
	}

	private DropDownChoice<Filiado> getComboEmpresas() {
		ChoiceRenderer<Filiado> renderer = new ChoiceRenderer<Filiado>("razaoSocial");
		comboEmpresas = new DropDownChoice<Filiado>("filiado", filiadoMediator.buscarTodosFiliados(), renderer);
		comboEmpresas.setLabel(new Model<String>("Empresa"));
		comboEmpresas.setOutputMarkupId(true);
		comboEmpresas.setRequired(true);

		return comboEmpresas;
	}
}
