package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.bean.ArquivoFormBean;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoVisualizacaoArquivos;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

/**
 * @author Thasso Araújo
 *
 */
public class BuscarArquivoInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private InstituicaoMediator instituicaoMediator;

	private List<Instituicao> listaInstituicoes;
	private DropDownChoice<Instituicao> dropDownInstituicao;
	private TipoVisualizacaoArquivos tipoVisualizacaoAtual;
	private Label labelMunicipio;
	private DropDownChoice<Instituicao> dropDownCartorio;
	private TipoInstituicaoCRA tipoInstituicao;

	public BuscarArquivoInputPanel(String id, IModel<ArquivoFormBean> model, Usuario usuario) {
		super(id, model);
		this.tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();

		adicionarCampos();
	}

	private void adicionarCampos() {
		add(radioTipoVisualizacao());
		add(textFieldNomeArquivo());
		add(dateFieldDataInicio());
		add(dateFieldDataFinal());
		add(comboTipoArquivos());
		add(checkSituacaoArquivos());
		add(labelBancoCovenio());
		add(dropDownTipoInstituicao());
		add(dropDownBancoConvenios());
		add(labelMunicipio());
		add(dropDownCartorio());
	}

	private RadioChoice<TipoVisualizacaoArquivos> radioTipoVisualizacao() {
		IChoiceRenderer<TipoVisualizacaoArquivos> renderer = new ChoiceRenderer<TipoVisualizacaoArquivos>("label");
		final RadioChoice<TipoVisualizacaoArquivos> radioVisualizacao = new RadioChoice<TipoVisualizacaoArquivos>("tipoVisualizacaoArquivos",
				new Model<TipoVisualizacaoArquivos>(), Arrays.asList(TipoVisualizacaoArquivos.values()), renderer);

		if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.equals(tipoInstituicao) || TipoInstituicaoCRA.CONVENIO.equals(tipoInstituicao)) {
			tipoVisualizacaoAtual = TipoVisualizacaoArquivos.ARQUIVOS_CARTORIOS;
			ArquivoFormBean.class.cast(getDefaultModel().getObject()).setTipoVisualizacaoArquivos(TipoVisualizacaoArquivos.ARQUIVOS_BANCOS_CONVENIOS);
			radioVisualizacao.setVisible(false);
		} else if (TipoInstituicaoCRA.CARTORIO.equals(tipoInstituicao)) {
			tipoVisualizacaoAtual = TipoVisualizacaoArquivos.ARQUIVOS_BANCOS_CONVENIOS;
			ArquivoFormBean.class.cast(getDefaultModel().getObject()).setTipoVisualizacaoArquivos(TipoVisualizacaoArquivos.ARQUIVOS_CARTORIOS);
			radioVisualizacao.setVisible(false);
		} else {
			tipoVisualizacaoAtual = TipoVisualizacaoArquivos.ARQUIVOS_BANCOS_CONVENIOS;
			ArquivoFormBean.class.cast(getDefaultModel().getObject()).setTipoVisualizacaoArquivos(TipoVisualizacaoArquivos.ARQUIVOS_CARTORIOS);
			radioVisualizacao.setDefaultModel(new Model<TipoVisualizacaoArquivos>(TipoVisualizacaoArquivos.ARQUIVOS_CARTORIOS));
		}
		radioVisualizacao.setLabel(new Model<String>("Tipo de Visualização"));
		radioVisualizacao.setOutputMarkupId(true);
		radioVisualizacao.add(new AjaxEventBehavior("onchange") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {

				if (TipoVisualizacaoArquivos.ARQUIVOS_CARTORIOS.equals(tipoVisualizacaoAtual)) {
					ArquivoFormBean.class.cast(getDefaultModel().getObject()).setTipoVisualizacaoArquivos(TipoVisualizacaoArquivos.ARQUIVOS_CARTORIOS);
					tipoVisualizacaoAtual = TipoVisualizacaoArquivos.ARQUIVOS_BANCOS_CONVENIOS;
					dropDownCartorio.setEnabled(true);
					labelMunicipio.setEnabled(true);
				} else if (TipoVisualizacaoArquivos.ARQUIVOS_BANCOS_CONVENIOS.equals(tipoVisualizacaoAtual)) {
					ArquivoFormBean.class.cast(getDefaultModel().getObject()).setTipoVisualizacaoArquivos(TipoVisualizacaoArquivos.ARQUIVOS_BANCOS_CONVENIOS);
					tipoVisualizacaoAtual = TipoVisualizacaoArquivos.ARQUIVOS_CARTORIOS;
					dropDownCartorio.setEnabled(false);
					labelMunicipio.setEnabled(false);
				}
				target.add(dropDownCartorio);
				target.add(labelMunicipio);
			}

		});
		return radioVisualizacao;
	}

	private TextField<String> textFieldNomeArquivo() {
		return new TextField<String>("nomeArquivo");
	}

	private DateTextField dateFieldDataInicio() {
		DateTextField dataEnvioInicio = new DateTextField("dataInicio", "dd/MM/yyyy");
		dataEnvioInicio.setLabel(new Model<String>("Período de datas"));
		dataEnvioInicio.setMarkupId("date");
		return dataEnvioInicio;
	}

	private DateTextField dateFieldDataFinal() {
		DateTextField dataEnvioFinal = new DateTextField("dataFim", "dd/MM/yyyy");
		dataEnvioFinal.setMarkupId("date1");
		return dataEnvioFinal;
	}

	private CheckBoxMultipleChoice<TipoArquivoEnum> comboTipoArquivos() {
		List<TipoArquivoEnum> listaTipos = new ArrayList<TipoArquivoEnum>();
		listaTipos.add(TipoArquivoEnum.REMESSA);
		listaTipos.add(TipoArquivoEnum.CONFIRMACAO);
		listaTipos.add(TipoArquivoEnum.RETORNO);
		CheckBoxMultipleChoice<TipoArquivoEnum> tipos = new CheckBoxMultipleChoice<TipoArquivoEnum>("tiposArquivos", listaTipos);
		tipos.setLabel(new Model<String>("Tipos de Arquivo"));
		return tipos;
	}

	private CheckBoxMultipleChoice<SituacaoArquivo> checkSituacaoArquivos() {
		CheckBoxMultipleChoice<SituacaoArquivo> situacao =
				new CheckBoxMultipleChoice<SituacaoArquivo>("situacoesArquivos", Arrays.asList(SituacaoArquivo.values()));
		situacao.setLabel(new Model<String>("Situação do Arquivo"));
		return situacao;
	}

	private Label labelBancoCovenio() {
		Label labelBancoCovenio = new Label("labelBancoCovenio", "Banco/Convênio");
		labelBancoCovenio.setOutputMarkupId(true);
		if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.equals(tipoInstituicao) || TipoInstituicaoCRA.CONVENIO.equals(tipoInstituicao)) {
			labelBancoCovenio.setVisible(false);
		}
		return labelBancoCovenio;
	}

	private DropDownChoice<Instituicao> dropDownBancoConvenios() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		dropDownInstituicao = new DropDownChoice<Instituicao>("bancoConvenio", getListaInstituicoes(), renderer);
		dropDownInstituicao.setLabel(new Model<String>("Banco/Convênio"));
		dropDownInstituicao.setOutputMarkupId(true);
		if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.equals(tipoInstituicao) || TipoInstituicaoCRA.CONVENIO.equals(tipoInstituicao)) {
			dropDownInstituicao.setVisible(false);
		}
		return dropDownInstituicao;
	}

	private DropDownChoice<TipoInstituicaoCRA> dropDownTipoInstituicao() {
		IChoiceRenderer<TipoInstituicaoCRA> renderer = new ChoiceRenderer<TipoInstituicaoCRA>("label");
		List<TipoInstituicaoCRA> choices = new ArrayList<TipoInstituicaoCRA>();
		choices.add(TipoInstituicaoCRA.CONVENIO);
		choices.add(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA);
		final DropDownChoice<TipoInstituicaoCRA> dropDowntipoInstituicao = new DropDownChoice<TipoInstituicaoCRA>("tipoInstituicao", choices, renderer);
		dropDowntipoInstituicao.add(new OnChangeAjaxBehavior() {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				TipoInstituicaoCRA tipo = dropDowntipoInstituicao.getModelObject();

				if (dropDowntipoInstituicao.getModelObject() != null) {
					if (tipo.equals(TipoInstituicaoCRA.CONVENIO)) {
						getListaInstituicoes().clear();
						getListaInstituicoes().addAll(instituicaoMediator.getConvenios());
					} else if (tipo.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
						getListaInstituicoes().clear();
						getListaInstituicoes().addAll(instituicaoMediator.getInstituicoesFinanceiras());
					}
					dropDownInstituicao.setEnabled(true);
				} else {
					dropDownInstituicao.setEnabled(false);
					getListaInstituicoes().clear();
				}
				target.add(dropDownInstituicao);
			}
		});
		if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.equals(tipoInstituicao) || TipoInstituicaoCRA.CONVENIO.equals(tipoInstituicao)) {
			dropDowntipoInstituicao.setVisible(false);
		}
		dropDowntipoInstituicao.setLabel(new Model<String>("Tipo Instituição"));
		return dropDowntipoInstituicao;
	}

	private Label labelMunicipio() {
		labelMunicipio = new Label("labelMunicipio", "Município");
		labelMunicipio.setOutputMarkupId(true);
		if (!TipoInstituicaoCRA.CRA.equals(tipoInstituicao)) {
			labelMunicipio.setVisible(false);
		}
		return labelMunicipio;
	}

	private DropDownChoice<Instituicao> dropDownCartorio() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("municipio.nomeMunicipio");
		dropDownCartorio = new DropDownChoice<Instituicao>("cartorio", instituicaoMediator.getCartorios(), renderer);
		dropDownCartorio.setOutputMarkupId(true);
		if (!TipoInstituicaoCRA.CRA.equals(tipoInstituicao)) {
			dropDownCartorio.setVisible(false);
		}
		return dropDownCartorio;
	}

	public List<Instituicao> getListaInstituicoes() {
		if (listaInstituicoes == null) {
			listaInstituicoes = new ArrayList<Instituicao>();
		}
		return listaInstituicoes;
	}
}