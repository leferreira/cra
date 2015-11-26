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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.enumeration.CampoLayout;
import br.com.ieptbto.cra.enumeration.TipoArquivoLayoutEmpresa;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
public class LayoutEmpresaInputPanel extends Panel {

	@SpringBean
	private InstituicaoMediator instituicaoMediator;

	private DropDownChoice<Instituicao> comboEmpresas;
	private TextField<Integer> campoOrdem;
	private TextField<Integer> campoPosicaoInicio;
	private TextField<Integer> campoPosicaoFim;
	private DropDownChoice<CampoLayout> comboCampos;
	private LayoutFiliado layoutFiliado;
	private List<LayoutFiliado> listaLayoutFiliado;
	private WebMarkupContainer divResultado;
	private List<CampoLayout> campos;
	private DropDownChoice<TipoArquivoLayoutEmpresa> comboTipoArquivo;

	private TextField<String> campoDescricao;

	public LayoutEmpresaInputPanel(String id, IModel<?> model) {
		super(id, model);
		layoutFiliado = new LayoutFiliado();
		addComponent();
	}

	private void addComponent() {
		add(getComboEmpresas());
		add(getComboTipoArquivoLayout());

		WebMarkupContainer divCampo = getDivCampo();
		add(divCampo);

		divCampo.add(getDescricaoCampo());
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

	private Component getDescricaoCampo() {
		campoDescricao = new TextField<String>("descricaoCampo");
		campoDescricao.setOutputMarkupId(true);
		return campoDescricao;
	}

	private Component getComboTipoArquivoLayout() {
		ChoiceRenderer<TipoArquivoLayoutEmpresa> renderer = new ChoiceRenderer<TipoArquivoLayoutEmpresa>("label");
		if (comboTipoArquivo == null) {
			comboTipoArquivo = new DropDownChoice<TipoArquivoLayoutEmpresa>("tipoArquivo", getTipoArquivoLayoutEmpresa(), renderer);
			comboTipoArquivo.setLabel(new Model<String>("Tipo Arquivo"));
			comboTipoArquivo.setOutputMarkupId(true);
			comboTipoArquivo.setRequired(true);
		}
		return comboTipoArquivo;
	}

	private Component getTabelaResultado() {
		return new ListView<LayoutFiliado>("tabelaLayout", getCamposFiliado()) {

			@Override
			protected void populateItem(ListItem<LayoutFiliado> item) {
				final LayoutFiliado layout = item.getModelObject();
				item.add(new Label("descricao", layout.getDescricaoCampo()));
				item.add(new Label("campo", layout.getCampo().getLabel()));
				item.add(new Label("ordem", layout.getOrdem()));
				item.add(new Label("posicaoInicio", layout.getPosicaoInicio()));
				item.add(new Label("posicaoFim", layout.getPosicaoFim()));
				item.add(getButtonRemove(layout));
			}

			private Component getButtonRemove(final LayoutFiliado layout) {
				return new AjaxButton("remover") {
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						getCamposFiliado().remove(layout);
						target.add(divResultado);

					}
				};
			}
		};
	}

	public List<LayoutFiliado> getCamposFiliado() {
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

				if (verificarCampos(layoutFiliado)) {
					target.add(this.getPage());
					return;
				}

				for (LayoutFiliado campoFiliado : getCamposFiliado()) {
					if (campoFiliado.getOrdem() == layoutFiliado.getOrdem()) {
						warn("O campo ordem não pode ser repetido.");
						target.add(this.getPage());
						return;
					}
				}

				// divResultado.add(comboCampos);

				getCamposFiliado().add(layoutFiliado);
				divResultado.setVisible(true);
				target.add(divResultado);
				target.add(this.getPage());
			}

			private boolean verificarCampos(LayoutFiliado layoutFiliado) {
				final String descricao = (String) campoDescricao.getDefaultModelObject();
				final Integer posicaoFim = (Integer) campoPosicaoFim.getDefaultModelObject();
				final Integer posicaoInicio = (Integer) campoPosicaoInicio.getDefaultModelObject();
				final Integer ordem = (Integer) campoOrdem.getDefaultModelObject();
				final CampoLayout campo = comboCampos.getConvertedInput();
				final Instituicao empresa = comboEmpresas.getConvertedInput();
				final TipoArquivoLayoutEmpresa tipoArquivo = comboTipoArquivo.getConvertedInput();

				if (posicaoFim == null || posicaoInicio == null || ordem == null || campo == null || empresa == null || tipoArquivo == null
		                || descricao == null) {
					warn("Todos os campos devem ser preenchidos");
					return true;
				}

				int i = 1;
				for (LayoutFiliado layout : getListaLayoutFiliado()) {

					if (layout.getCampo().equals(getComboCampos().getDefaultModelObject())) {
						warn("O campo " + layout.getCampo() + " já foi adicionado ao layout. Não deve haver campos repetidos no layout.");
						return true;
					}

					// if (layout.getOrdem() == i) {
		            // i++;
		            // } else {
		            // warn("O campo " + layout.getCampo().getLabel() + " não
		            // está na ordem correta.\n"
		            // + "Todos os campos devem ser inseridos na ordem do
		            // layout.");
		            // return true;
		            // }
				}

				layoutFiliado.setCampo(campo);
				layoutFiliado.setEmpresa(empresa);
				layoutFiliado.setOrdem(ordem);
				layoutFiliado.setPosicaoFim(posicaoFim);
				layoutFiliado.setPosicaoInicio(posicaoInicio);
				layoutFiliado.setTipoArquivo(tipoArquivo);
				layoutFiliado.setDescricaoCampo(descricao);

				return false;
			}

		};

		return button;
	}

	private WebMarkupContainer getDivResultado() {
		WebMarkupContainer divCampo = new WebMarkupContainer("divResultado");
		divCampo.setOutputMarkupId(true);
		return divCampo;
	}

	public DropDownChoice<CampoLayout> getComboCampos() {
		ChoiceRenderer<CampoLayout> renderer = new ChoiceRenderer<CampoLayout>("label");
		if (comboCampos == null) {
			comboCampos = new DropDownChoice<CampoLayout>("campo", getCamposLayout(), renderer);
			comboCampos.setLabel(new Model<String>("Campo"));
			comboCampos.setOutputMarkupId(true);
			comboCampos.setRequired(true);
		}
		return comboCampos;
	}

	public List<CampoLayout> getCamposLayout() {
		if (campos == null) {
			campos = Arrays.asList(CampoLayout.values());
		}
		return campos;
	}

	private List<TipoArquivoLayoutEmpresa> getTipoArquivoLayoutEmpresa() {
		return Arrays.asList(TipoArquivoLayoutEmpresa.values());
	}

	private TextField<Integer> getCampoPosicaoFim() {
		campoPosicaoFim = new TextField<Integer>("posicaoFim");
		campoPosicaoFim.setOutputMarkupId(true);
		return campoPosicaoFim;
	}

	private TextField<Integer> getCampoPosicaoInicio() {
		campoPosicaoInicio = new TextField<Integer>("posicaoInicio");
		campoPosicaoInicio.setOutputMarkupId(true);
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

	private DropDownChoice<Instituicao> getComboEmpresas() {
		ChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("razaoSocial");
		comboEmpresas = new DropDownChoice<Instituicao>("empresa", instituicaoMediator.getInstituicoesFinanceiras(), renderer);
		comboEmpresas.setLabel(new Model<String>("Empresa"));
		comboEmpresas.setOutputMarkupId(true);
		comboEmpresas.setRequired(true);

		return comboEmpresas;
	}

	public List<LayoutFiliado> getListaLayoutFiliado() {
		return listaLayoutFiliado;
	}

	public void setListaLayoutFiliado(List<LayoutFiliado> listaLayoutFiliado) {
		this.listaLayoutFiliado = listaLayoutFiliado;
	}

}
