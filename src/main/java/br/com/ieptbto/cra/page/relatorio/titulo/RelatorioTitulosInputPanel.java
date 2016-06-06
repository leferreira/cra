package br.com.ieptbto.cra.page.relatorio.titulo;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.enumeration.TipoExportacaoRelatorio;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

public class RelatorioTitulosInputPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private FileUploadField fileUploadField;
	private List<Instituicao> listaInstituicoes;
	private DropDownChoice<Instituicao> dropDownInstituicao;

	public RelatorioTitulosInputPanel(String id, IModel<RelatorioTitulosFormBean> model, FileUploadField fileUploadField) {
		super(id, model);
		this.fileUploadField = fileUploadField;

		adicionarCampos();
	}

	private void adicionarCampos() {
		add(fileUploadPlanilhaPendenciasRetornoCra());
		add(dateFieldDataInicio());
		add(dateFieldDataFinal());
		add(radioTipoExportacao());
		add(situcaoTituloRelatorio());
		add(dropDownBancoConvenios());
		add(tipoInstituicao());
		add(dropDownCartorio());
	}

	private FileUploadField fileUploadPlanilhaPendenciasRetornoCra() {
		return fileUploadField;
	}

	private DateTextField dateFieldDataInicio() {
		DateTextField dataEnvioInicio = new DateTextField("dataInicio");
		dataEnvioInicio.setLabel(new Model<String>("Período de datas"));
		dataEnvioInicio.setMarkupId("date");
		return dataEnvioInicio;
	}

	private DateTextField dateFieldDataFinal() {
		DateTextField dataEnvioFinal = new DateTextField("dataFim");
		dataEnvioFinal.setMarkupId("date1");
		return dataEnvioFinal;
	}

	private RadioGroup<String> radioTipoExportacao() {
		RadioGroup<String> radioExportacao = new RadioGroup<String>("tipoExportacao");
		radioExportacao.setLabel(new Model<String>("Tipo Exportação Relatório"));
		radioExportacao.setRequired(true);
		radioExportacao.add(new Radio<String>("pdf", new Model<String>(TipoExportacaoRelatorio.PDF.toString())).setEnabled(false));
		radioExportacao.add(new Radio<String>("csv", new Model<String>(TipoExportacaoRelatorio.CSV.toString())));
		return radioExportacao;
	}

	private RadioGroup<SituacaoTituloRelatorio> situcaoTituloRelatorio() {
		RadioGroup<SituacaoTituloRelatorio> radioSituacaoTituloRelatorio = new RadioGroup<SituacaoTituloRelatorio>("situacaoTituloRelatorio");
		radioSituacaoTituloRelatorio.setLabel(new Model<String>("Situação dos Títulos"));
		radioSituacaoTituloRelatorio
				.add(new Radio<SituacaoTituloRelatorio>("todos", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.GERAL)));
		radioSituacaoTituloRelatorio.add(
				new Radio<SituacaoTituloRelatorio>("semConfirmacao", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.SEM_CONFIRMACAO)));
		radioSituacaoTituloRelatorio.add(
				new Radio<SituacaoTituloRelatorio>("comConfirmacao", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.COM_CONFIRMACAO)));
		radioSituacaoTituloRelatorio
				.add(new Radio<SituacaoTituloRelatorio>("comRetorno", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.COM_RETORNO)));
		radioSituacaoTituloRelatorio
				.add(new Radio<SituacaoTituloRelatorio>("pagos", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.PAGOS)));
		radioSituacaoTituloRelatorio
				.add(new Radio<SituacaoTituloRelatorio>("protestados", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.PROTESTADOS)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("retiradosDevolvidos",
				new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.RETIRADOS_DEVOLVIDOS)));
		radioSituacaoTituloRelatorio.add(
				new Radio<SituacaoTituloRelatorio>("desistencia", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.DESISTÊNCIA_DE_PROTESTO))
						.setEnabled(false));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("cancelamento",
				new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.CANCELAMENTO_DE_PROTESTO)).setEnabled(false));
		radioSituacaoTituloRelatorio.add(
				new Radio<SituacaoTituloRelatorio>("semRetorno", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.AUTORIZACAO_CANCELAMENTO))
						.setEnabled(false));
		return radioSituacaoTituloRelatorio;
	}

	private DropDownChoice<Instituicao> dropDownBancoConvenios() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		dropDownInstituicao = new DropDownChoice<Instituicao>("bancoConvenio", getListaInstituicoes(), renderer);
		dropDownInstituicao.setLabel(new Model<String>("Portador"));
		dropDownInstituicao.setOutputMarkupId(true);
		dropDownInstituicao.setEnabled(false);
		return dropDownInstituicao;
	}

	private DropDownChoice<TipoInstituicaoCRA> tipoInstituicao() {
		List<TipoInstituicaoCRA> choices = new ArrayList<TipoInstituicaoCRA>();
		choices.add(TipoInstituicaoCRA.CONVENIO);
		choices.add(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA);
		final DropDownChoice<TipoInstituicaoCRA> tipoInstituicao =
				new DropDownChoice<TipoInstituicaoCRA>("tipoInstituicao", choices, new ChoiceRenderer<TipoInstituicaoCRA>("label"));
		tipoInstituicao.add(new OnChangeAjaxBehavior() {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				TipoInstituicaoCRA tipo = tipoInstituicao.getModelObject();

				if (tipoInstituicao.getModelObject() != null) {
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
		tipoInstituicao.setLabel(new Model<String>("Tipo Instituição"));
		return tipoInstituicao;
	}

	private DropDownChoice<Instituicao> dropDownCartorio() {
		DropDownChoice<Instituicao> dropDownCartorio = new DropDownChoice<Instituicao>("cartorio", instituicaoMediator.getCartorios(),
				new ChoiceRenderer<Instituicao>("municipio.nomeMunicipio"));
		dropDownCartorio.setOutputMarkupId(true);
		return dropDownCartorio;
	}

	public List<Instituicao> getListaInstituicoes() {
		if (listaInstituicoes == null) {
			listaInstituicoes = new ArrayList<Instituicao>();
		}
		return listaInstituicoes;
	}
}