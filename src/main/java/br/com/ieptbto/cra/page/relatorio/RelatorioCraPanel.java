package br.com.ieptbto.cra.page.relatorio;

import java.util.Arrays;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.util.DataUtil;


/**
 * @author Thasso Araújo
 *
 */
public class RelatorioCraPanel extends Panel{

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioCraPanel.class);
	private static final List<String> TIPO_ARQUIVOS = Arrays.asList(new String[] { "B", "C", "R" });
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	RelatorioMediator relatorioMediator;
	
	private TextField<LocalDate> fieldDataInicio;
	private TextField<LocalDate> fieldDataFim;
	private DropDownChoice<Instituicao> fieldPortador;
	private DropDownChoice<Municipio> fieldMunicipio;
	
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Municipio pracaProtesto;
	private Instituicao bancoPortador;
	private String tipoArquivo;
	
	public RelatorioCraPanel(String id, IModel<?> model) {
		super(id, model);
		add(comboTipoArquivos());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(comboPortador());
		add(pracaProtesto());
		add(new Button("botaoGerar"){
				/****/
				private static final long serialVersionUID = 1L;
				@Override
				public void onSubmit() {
					
					if (fieldDataInicio.getDefaultModelObject() != null){
						if (fieldDataFim.getDefaultModelObject() != null){
							dataInicio = DataUtil.stringToLocalDate(fieldDataInicio.getDefaultModelObject().toString());
							dataFim = DataUtil.stringToLocalDate(fieldDataFim.getDefaultModelObject().toString());
							if (!dataInicio.isBefore(dataFim))
								if (!dataInicio.isEqual(dataFim))
									error("A data de início deve ser antes da data fim.");
						}else
							error("As duas datas devem ser preenchidas.");
					} 
					
					if (fieldPortador.getDefaultModelObject() != null)
						bancoPortador = (Instituicao)fieldPortador.getDefaultModelObject();
					if (fieldMunicipio.getDefaultModelObject() != null)
						pracaProtesto = (Municipio)fieldMunicipio.getDefaultModelObject();
					
					try {
						if (bancoPortador != null && pracaProtesto == null) { 
							JasperPrint jasperPrint = relatorioMediator.novoRelatorioSintetico(bancoPortador, tipoArquivo, dataInicio, dataFim);
							setResponsePage(new VerRelatorioPage(jasperPrint));

						} else if (bancoPortador == null && pracaProtesto != null){ 
							JasperPrint jasperPrint = relatorioMediator.novoRelatorioSinteticoPorMunicipio(pracaProtesto , tipoArquivo,dataInicio, dataFim );
							setResponsePage(new VerRelatorioPage(jasperPrint));
							
						} else 
							error("Deve ser selecionado o portador ou o município!");
						
					}catch (JRException e) {
						logger.error(e.getMessage(), e);
						error("Não foi possível gerar o relatório. \n Entre em contato com a CRA!");
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						error("Não foi possível realizar esta operação! \n Entre em contato com a CRA ");
					}
				}
		});
	}
	
	private Component comboTipoArquivos() {
		return new RadioChoice<String>("tipoArquivo", new PropertyModel<String>(this, "tipoArquivo"), TIPO_ARQUIVOS).setRequired(true);
	}
	
	private TextField<LocalDate> dataEnvioInicio() {
		fieldDataInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
		fieldDataInicio.setLabel(new Model<String>("intervalo da data do relatório"));
		fieldDataInicio.setRequired(true);
		return fieldDataInicio;
	}
	
	private TextField<LocalDate> dataEnvioFinal() {
		return fieldDataFim = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	}
	
	private DropDownChoice<Instituicao> comboPortador() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		return fieldPortador = new DropDownChoice<Instituicao>("portador", new Model<Instituicao>(),instituicaoMediator.getInstituicoesFinanceiras(), renderer);
	}
	
	private DropDownChoice<Municipio> pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		return fieldMunicipio = new DropDownChoice<Municipio>("municipio", new Model<Municipio>(),municipioMediator.listarTodos(), renderer);
	}
}
