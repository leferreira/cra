package br.com.ieptbto.cra.page.relatorio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.bean.ArquivoFormBean;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoRelatorio;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

public class RelatorioArquivosInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private List<Instituicao> listaInstituicoes;
	private DropDownChoice<Instituicao> comboInstituicao;

	public RelatorioArquivosInputPanel(String id, IModel<ArquivoFormBean> model) {
		super(id, model);

		adicionarCampos();
	}

	private void adicionarCampos() {
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(tipoRelatorio());
		add(tipoArquivo());
		add(tipoInstituicao());
		add(instituicaoCartorio());
	}

	private TextField<LocalDate> dataEnvioInicio() {
		TextField<LocalDate> dataEnvioInicio = new TextField<LocalDate>("dataInicio");
		dataEnvioInicio.setLabel(new Model<String>("Período de datas"));
		dataEnvioInicio.setRequired(true);
		dataEnvioInicio.setMarkupId("date");
		return dataEnvioInicio;
	}

	private TextField<LocalDate> dataEnvioFinal() {
		TextField<LocalDate> dataEnvioFinal = new TextField<LocalDate>("dataFim");
		dataEnvioFinal.setMarkupId("date1");
		return dataEnvioFinal;
	}

	private RadioChoice<TipoArquivoEnum> tipoArquivo() {
		List<TipoArquivoEnum> listaTipos = new ArrayList<TipoArquivoEnum>();
		listaTipos.add(TipoArquivoEnum.REMESSA);
		listaTipos.add(TipoArquivoEnum.CONFIRMACAO);
		listaTipos.add(TipoArquivoEnum.RETORNO);
		RadioChoice<TipoArquivoEnum> radioTipoArquivo = new RadioChoice<TipoArquivoEnum>("tipoArquivo", listaTipos);
		radioTipoArquivo.setLabel(new Model<String>("Tipo do Arquivo"));
		radioTipoArquivo.setRequired(true);
		return radioTipoArquivo;
	}

	private RadioChoice<TipoRelatorio> tipoRelatorio() {
		List<TipoRelatorio> choices = Arrays.asList(TipoRelatorio.values());
		RadioChoice<TipoRelatorio> radioTipoRelatorio = new RadioChoice<TipoRelatorio>("tipoRelatorio", choices);
		radioTipoRelatorio.setLabel(new Model<String>("Tipo Relatório"));
		radioTipoRelatorio.setRequired(true);
		return radioTipoRelatorio;
	}

	private DropDownChoice<TipoInstituicaoCRA> tipoInstituicao() {
		List<TipoInstituicaoCRA> choices = new ArrayList<TipoInstituicaoCRA>();
		choices.add(TipoInstituicaoCRA.CARTORIO);
		choices.add(TipoInstituicaoCRA.CONVENIO);
		choices.add(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA);
		final DropDownChoice<TipoInstituicaoCRA> tipoInstituicao = new DropDownChoice<TipoInstituicaoCRA>("tipoInstituicao",
				new Model<TipoInstituicaoCRA>(), choices, new ChoiceRenderer<TipoInstituicaoCRA>("label"));
		tipoInstituicao.add(new OnChangeAjaxBehavior() {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				TipoInstituicaoCRA tipo = tipoInstituicao.getModelObject();

				if (tipoInstituicao.getModelObject() != null) {
					if (tipo.equals(TipoInstituicaoCRA.CONVENIO)) {
						comboInstituicao.setChoiceRenderer(new ChoiceRenderer<Instituicao>("nomeFantasia"));
						getListaInstituicoes().clear();
						getListaInstituicoes().addAll(instituicaoMediator.getConvenios());
					} else if (tipo.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
						comboInstituicao.setChoiceRenderer(new ChoiceRenderer<Instituicao>("nomeFantasia"));
						getListaInstituicoes().clear();
						getListaInstituicoes().addAll(instituicaoMediator.getInstituicoesFinanceiras());
					} else if (tipo.equals(TipoInstituicaoCRA.CARTORIO)) {
						comboInstituicao.setChoiceRenderer(new ChoiceRenderer<Instituicao>("municipio.nomeMunicipio"));
						getListaInstituicoes().clear();
						getListaInstituicoes().addAll(instituicaoMediator.getCartorios());
					}

					comboInstituicao.setEnabled(true);
					comboInstituicao.setRequired(true);
				} else {
					comboInstituicao.setEnabled(false);
					comboInstituicao.setRequired(false);
					getListaInstituicoes().clear();
				}
				target.add(comboInstituicao);
			}
		});
		tipoInstituicao.setLabel(new Model<String>("Tipo Instituição"));
		tipoInstituicao.setRequired(true);
		return tipoInstituicao;
	}

	private DropDownChoice<Instituicao> instituicaoCartorio() {
		comboInstituicao = new DropDownChoice<Instituicao>("instituicao", getListaInstituicoes(), new ChoiceRenderer<Instituicao>("nomeFantasia"));
		comboInstituicao.setLabel(new Model<String>("Instituição"));
		comboInstituicao.setOutputMarkupId(true);
		comboInstituicao.setEnabled(false);
		return comboInstituicao;
	}

	public List<Instituicao> getListaInstituicoes() {
		if (listaInstituicoes == null) {
			listaInstituicoes = new ArrayList<Instituicao>();
		}
		return listaInstituicoes;
	}
}