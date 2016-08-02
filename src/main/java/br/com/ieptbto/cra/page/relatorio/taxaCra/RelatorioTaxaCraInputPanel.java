package br.com.ieptbto.cra.page.relatorio.taxaCra;

import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

public class RelatorioTaxaCraInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	private static final String RELATORIO_PAGOS = "PAGOS";
	private static final String RELATORIO_CANCELADOS = "CANCELADOS";

	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private DropDownChoice<Instituicao> dropDownConvenio;
	private DropDownChoice<Instituicao> dropDownCartorio;

	public RelatorioTaxaCraInputPanel(String id, IModel<RelatorioTaxaCraFormBean> model) {
		super(id, model);

		adicionarCampos();
	}

	private void adicionarCampos() {
		add(dateFieldDataInicio());
		add(dateFieldDataFinal());
		add(dropDownBancoConvenios());
		add(dropDownCartorio());
		add(situcaoTituloRelatorio());
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

	private RadioGroup<String> situcaoTituloRelatorio() {
		RadioGroup<String> radioTipoRelatorio = new RadioGroup<String>("situacaoTituloRelatorio");
		radioTipoRelatorio.setLabel(new Model<String>("Situação dos Títulos"));
		radioTipoRelatorio.add(new Radio<String>("pagos", new Model<String>(RELATORIO_PAGOS)));
		radioTipoRelatorio.add(new Radio<String>("cancelados", new Model<String>(RELATORIO_CANCELADOS)));
		return radioTipoRelatorio;
	}

	private DropDownChoice<Instituicao> dropDownBancoConvenios() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		dropDownConvenio = new DropDownChoice<Instituicao>("convenio", instituicaoMediator.getConvenios(), renderer);
		dropDownConvenio.setLabel(new Model<String>("Convênio"));
		dropDownConvenio.setOutputMarkupId(true);
		return dropDownConvenio;
	}

	private DropDownChoice<Instituicao> dropDownCartorio() {
		dropDownCartorio = new DropDownChoice<Instituicao>("cartorio", instituicaoMediator.getCartorios(),
				new ChoiceRenderer<Instituicao>("municipio.nomeMunicipio"));
		dropDownCartorio.setOutputMarkupId(true);
		dropDownCartorio.setRequired(true);
		return dropDownCartorio;
	}
}