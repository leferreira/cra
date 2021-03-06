package br.com.ieptbto.cra.page.relatorio;

import br.com.ieptbto.cra.beans.ArquivoBean;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.NivelDetalhamentoRelatorio;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RelatorioArquivosInputPanel extends Panel {

	private static final long serialVersionUID = 1L;

	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private List<Instituicao> listaInstituicoes;
	private DropDownChoice<Instituicao> comboInstituicao;

	public RelatorioArquivosInputPanel(String id, IModel<ArquivoBean> model) {
		super(id, model);
		add(dateFieldDataInicio());
		add(dateFieldDataFinal());
		add(tipoRelatorio());
		add(tipoArquivo());
		add(tipoInstituicao());
		add(instituicaoCartorio());
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

	private RadioChoice<TipoArquivoFebraban> tipoArquivo() {
		List<TipoArquivoFebraban> listaTipos = new ArrayList<TipoArquivoFebraban>();
		listaTipos.add(TipoArquivoFebraban.REMESSA);
		listaTipos.add(TipoArquivoFebraban.CONFIRMACAO);
		listaTipos.add(TipoArquivoFebraban.RETORNO);
		RadioChoice<TipoArquivoFebraban> radioTipoArquivo = new RadioChoice<TipoArquivoFebraban>("tipoArquivo", listaTipos);
		radioTipoArquivo.setLabel(new Model<String>("Tipo do Arquivo"));
		radioTipoArquivo.setRequired(true);
		return radioTipoArquivo;
	}

	private RadioChoice<NivelDetalhamentoRelatorio> tipoRelatorio() {
		List<NivelDetalhamentoRelatorio> choices = Arrays.asList(NivelDetalhamentoRelatorio.values());
		RadioChoice<NivelDetalhamentoRelatorio> radioTipoRelatorio = new RadioChoice<NivelDetalhamentoRelatorio>("tipoRelatorio", choices);
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
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		comboInstituicao = new DropDownChoice<Instituicao>("instituicao", getListaInstituicoes(), renderer);
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