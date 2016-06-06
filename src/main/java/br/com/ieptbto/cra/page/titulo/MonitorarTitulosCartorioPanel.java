package br.com.ieptbto.cra.page.titulo;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class MonitorarTitulosCartorioPanel extends Panel {

	private static final Logger logger = Logger.getLogger(MonitorarTitulosCartorioPanel.class);

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	private DropDownChoice<Instituicao> comboPortador;
	private IModel<TituloRemessa> model;

	public MonitorarTitulosCartorioPanel(String id, IModel<TituloRemessa> model) {
		super(id, model);
		this.model = model;
		adicionarCampos();
	}

	private void adicionarCampos() {
		add(nossoNumero());
		add(numeroTitulo());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(numeroProtocoloCartorio());
		add(comboPortador());
		add(nomeDevedor());
		add(documentoDevedor());
		add(nomeSacador());
		add(documentoSacador());
		add(botaoEnviar());
	}

	private Component comboPortador() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		this.comboPortador = new DropDownChoice<Instituicao>("remessa.instituicaoOrigem", instituicaoMediator.getInstituicoesFinanceirasEConvenios(), renderer);
		return comboPortador;
	}

	private TextField<String> nossoNumero() {
		return new TextField<String>("nossoNumero");
	}

	private TextField<String> numeroTitulo() {
		return new TextField<String>("numeroTitulo");
	}

	private TextField<LocalDate> dataEnvioInicio() {
		return dataEnvioInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
	}

	private TextField<LocalDate> dataEnvioFinal() {
		return dataEnvioFinal = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	}

	private TextField<String> nomeDevedor() {
		return new TextField<String>("nomeDevedor");
	}

	private TextField<String> documentoDevedor() {
		return new TextField<String>("numeroIdentificacaoDevedor");
	}

	private TextField<String> nomeSacador() {
		return new TextField<String>("nomeSacadorVendedor");
	}

	private TextField<String> documentoSacador() {
		return new TextField<String>("documentoSacador");
	}

	private TextField<String> numeroProtocoloCartorio() {
		return new TextField<String>("numeroProtocoloCartorio");
	}

	private Component botaoEnviar() {
		return new Button("botaoBuscar") {

			@Override
			public void onSubmit() {
				TituloRemessa titulo = model.getObject();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				Municipio municipio = null;

				try {
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
					if (comboPortador.getModelObject() != null)
						titulo.setCodigoPortador(comboPortador.getModelObject().getCodigoCompensacao());

					setResponsePage(new ListaTitulosPage(dataInicio, dataFim, titulo, municipio));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar a busca ! \n Entre em contato com a CRA ");
				}
			}
		};
	}
}
