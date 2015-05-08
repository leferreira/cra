package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
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
public class BuscarArquivoCraPanel extends Panel  {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BuscarArquivoCraPanel.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	
	private Arquivo arquivo;
	private IModel<Arquivo> model;
	private ArrayList<String> tiposArquivo = new ArrayList<String>();
	private DropDownChoice<Municipio> comboMunicipio;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	private DropDownChoice<Instituicao> comboPortador;
	
	public BuscarArquivoCraPanel(String id, IModel<Arquivo> model, Instituicao instituicao) {
		super(id, model);
		this.model = model;
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
			/** */
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				Municipio municipio = null;
				Instituicao portador =  null;
				arquivo = model.getObject();
				
				try {
					if (dataEnvioInicio.getDefaultModelObject() != null){
						if (dataEnvioFinal.getDefaultModelObject() != null){
							dataInicio = DataUtil.stringToLocalDate(dataEnvioInicio.getDefaultModelObject().toString());
							dataFim = DataUtil.stringToLocalDate(dataEnvioFinal.getDefaultModelObject().toString());
							if (!dataInicio.isBefore(dataFim))
								if (!dataInicio.isEqual(dataFim))
									throw new InfraException("A data de início deve ser antes da data fim.");
						}else
							throw new InfraException("As duas datas devem ser preenchidas.");
					} 
					
					if (comboMunicipio.getDefaultModelObject() != null)
						municipio = Municipio.class.cast(comboMunicipio.getDefaultModelObject());
					
					if (comboPortador.getDefaultModelObject() != null)
						portador = Instituicao.class.cast(comboPortador.getDefaultModelObject());
					
					setResponsePage(new ListaArquivosPage(arquivo, municipio, portador, dataInicio, dataFim, tiposArquivo));
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
	
	private TextField<String> nomeArquivo() {
		return new TextField<String>("nomeArquivo");
	}
	
	private Component comboTipoArquivos() {
		List<String> choices = new ArrayList<String>();
		List<TipoArquivoEnum> enumLista = Arrays.asList(TipoArquivoEnum.values());
		for (TipoArquivoEnum tipo : enumLista) {
			choices.add(tipo.constante);
		}
		CheckBoxMultipleChoice<String> tipos = new CheckBoxMultipleChoice<String>("tipoArquivos",new Model<ArrayList<String>>(tiposArquivo), choices);
		tipos.setLabel(new Model<String>("Tipo do Arquivo"));
		return tipos;
	}
	
	private TextField<LocalDate> dataEnvioInicio() {
		dataEnvioInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
		dataEnvioInicio.setRequired(true);
		dataEnvioInicio.setLabel(new Model<String>("intervalo da data do envio"));
		return dataEnvioInicio;
	}
	
	private TextField<LocalDate> dataEnvioFinal() {
		return dataEnvioFinal = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	}

	private Component comboPortador() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		this.comboPortador = new DropDownChoice<Instituicao>("portador", new Model<Instituicao>(),instituicaoMediator.getInstituicoesFinanceiras(), renderer);
		return comboPortador;
	}
	
	private Component pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		this.comboMunicipio = new DropDownChoice<Municipio>("municipio", new Model<Municipio>(),municipioMediator.listarTodos(), renderer);
		return comboMunicipio;
	}
}