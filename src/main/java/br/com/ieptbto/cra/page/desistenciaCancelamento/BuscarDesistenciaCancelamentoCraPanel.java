package br.com.ieptbto.cra.page.desistenciaCancelamento;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class BuscarDesistenciaCancelamentoCraPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BuscarDesistenciaCancelamentoCraPanel.class);

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;

	private ArrayList<TipoArquivoEnum> tiposArquivo;

	private IModel<Arquivo> model;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	private DropDownChoice<Municipio> comboMunicipio;

	public BuscarDesistenciaCancelamentoCraPanel(String id, IModel<Arquivo> model, Instituicao instituicao) {
		super(id, model);
		this.model = model;
		this.tiposArquivo = new ArrayList<TipoArquivoEnum>();
		adicionarComponentes();
	}

	private void adicionarComponentes() {
		add(comboTipoArquivos());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(nomeArquivo());
		add(comboPortador());
		add(pracaProtesto());
		add(botaoEnviar());

	}

	private Component botaoEnviar() {
		return new Button("botaoBuscar") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				Arquivo arquivo = model.getObject();
				ArrayList<TipoArquivoEnum> tipoArquivos = getTiposArquivo();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				Municipio municipio = null;

				try {
					if (arquivo.getNomeArquivo() == null && dataEnvioInicio.getDefaultModelObject() == null) {
						throw new InfraException("Por favor, informe o 'Nome do Arquivo' ou 'Intervalo de datas'!");
					} else if (arquivo.getNomeArquivo() != null) {
						if (arquivo.getNomeArquivo().length() < 5) {
							throw new InfraException("Por favor, informe ao menos 5 caracteres!");
						}
					}

					if (dataEnvioInicio.getDefaultModelObject() != null) {
						if (dataEnvioFinal.getDefaultModelObject() != null) {
							dataInicio = DataUtil.stringToLocalDate(dataEnvioInicio.getDefaultModelObject().toString());
							dataFim = DataUtil.stringToLocalDate(dataEnvioFinal.getDefaultModelObject().toString());
							if (!dataInicio.isBefore(dataFim))
								if (!dataInicio.isEqual(dataFim))
									throw new InfraException("A data de início deve ser antes da data fim.");
						} else
							throw new InfraException("As duas datas devem ser preenchidas.");
					}

					if (comboMunicipio.getDefaultModelObject() != null) {
						municipio = Municipio.class.cast(comboMunicipio.getDefaultModelObject());
					}
					setResponsePage(new ListaDesistenciaCancelamentoPage(arquivo, tipoArquivos, municipio, dataInicio, dataFim));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível buscar os arquivos ! \n Entre em contato com a CRA ");
				}
			}
		};
	}

	private TextField<String> nomeArquivo() {
		return new TextField<String>("nomeArquivo");
	}

	private CheckBoxMultipleChoice<TipoArquivoEnum> comboTipoArquivos() {
		List<TipoArquivoEnum> listaTipos = new ArrayList<TipoArquivoEnum>();
		listaTipos.add(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO);
		listaTipos.add(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO);
		listaTipos.add(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO);
		CheckBoxMultipleChoice<TipoArquivoEnum> tipos = new CheckBoxMultipleChoice<TipoArquivoEnum>("tipoArquivos", new Model<ArrayList<TipoArquivoEnum>>(tiposArquivo), listaTipos);
		tipos.setLabel(new Model<String>("Tipo do Arquivo"));
		return tipos;
	}

	private TextField<LocalDate> dataEnvioInicio() {
		dataEnvioInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
		return dataEnvioInicio;
	}

	private TextField<LocalDate> dataEnvioFinal() {
		return dataEnvioFinal = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	}

	private DropDownChoice<Instituicao> comboPortador() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		DropDownChoice<Instituicao> comboPortador = new DropDownChoice<Instituicao>("instituicaoEnvio", instituicaoMediator.getInstituicoesFinanceirasEConvenios(), renderer);
		return comboPortador;
	}

	private DropDownChoice<Municipio> pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		comboMunicipio = new DropDownChoice<Municipio>("municipio", new Model<Municipio>(), municipioMediator.getMunicipiosTocantins(), renderer);
		return comboMunicipio;
	}

	public ArrayList<TipoArquivoEnum> getTiposArquivo() {
		return tiposArquivo;
	}
}