package br.com.ieptbto.cra.page.desistenciaCancelamento;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.bean.ArquivoFormBean;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;

/**
 * @author Thasso Araújo
 *
 */
public class BuscarDesistenciaCancelamentoInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;

	private List<Instituicao> listaInstituicoes;
	private DropDownChoice<Instituicao> dropDownInstituicao;
	private Label labelMunicipio;
	private TipoInstituicaoCRA tipoInstituicao;

	public BuscarDesistenciaCancelamentoInputPanel(String id, IModel<ArquivoFormBean> model, Usuario usuario) {
		super(id, model);
		this.tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();

		adicionarCampos();
	}

	private void adicionarCampos() {
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
		listaTipos.add(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO);
		listaTipos.add(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO);
		listaTipos.add(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO);
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

	private DropDownChoice<Municipio> dropDownCartorio() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		DropDownChoice<Municipio> dropDownCartorio = new DropDownChoice<Municipio>("municipio", municipioMediator.getMunicipiosTocantins(), renderer);
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