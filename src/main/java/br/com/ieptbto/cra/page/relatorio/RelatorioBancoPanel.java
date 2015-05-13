package br.com.ieptbto.cra.page.relatorio;

import java.util.Arrays;
import java.util.List;

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
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.relatorio.RelatorioUtils;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("unused")
public class RelatorioBancoPanel extends Panel{

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioBancoPanel.class);
	private static final List<String> TIPO_ARQUIVOS = Arrays.asList(new String[] { "B", "C", "R" });
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	RelatorioMediator relatorioMediator;
	
	private WebResponse response;
	private Instituicao instituicao;
	private TextField<LocalDate> fieldDataInicio;
	private TextField<LocalDate> fieldDataFim;
	private DropDownChoice<Municipio> fieldMunicipio;
	private String tipoArquivo;
	
	public RelatorioBancoPanel(String id, IModel<?> model, Instituicao instituicao) {
		super(id, model);
		this.response = (WebResponse) getResponse();
		this.instituicao = instituicao;
		add(comboTipoArquivos());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(pracaProtesto());
		add(new Button("botaoGerar"){
				/****/
				private static final long serialVersionUID = 1L;
				@Override
				public void onSubmit() {
//					LocalDate dataInicio = new LocalDate();
//					LocalDate dataFim =  new LocalDate();
//					
//					if (fieldDataInicio.getDefaultModelObject() != null){
//						if (fieldDataFim.getDefaultModelObject() != null){
//							dataInicio = DataUtil.stringToLocalDate(fieldDataInicio.getDefaultModelObject().toString());
//							dataFim = DataUtil.stringToLocalDate(fieldDataFim.getDefaultModelObject().toString());
//							if (!dataInicio.isBefore(dataFim))
//								if (!dataInicio.isEqual(dataFim))
//									throw new InfraException("A data de início deve ser antes da data fim.");
//						}else
//							throw new InfraException("As duas datas devem ser preenchidas.");
//					} 
//					
//					try {
//						if (fieldMunicipio.getDefaultModelObject() != null) {
//							Municipio municipio = Municipio.class.cast(fieldMunicipio.getDefaultModelObject());
////							relatorioMediator.novoRelatorioAnaliticoBanco(getInstituicao(), getTipoArquivo(), dataInicio, dataFim , municipio);
//						} else {
//							JasperPrint jasperPrint = relatorioMediator.novoRelatorioSintetico(getInstituicao(), getTipoArquivo(), dataInicio, dataFim);
//							getRalatorioUtils().gerarRelatorio(response, jasperPrint);
//						}
//					}catch (JRException e) {
//						logger.error(e.getMessage(), e);
//						error("Não foi possível gerar o relatório. \n Entre em contato com a CRA!");
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//						error("Não foi possível realizar esta operação! \n Entre em contato com a CRA ");
//					}
//					setResponsePage(new RelatorioPage());
				}
		});
	}
	
	private Component comboTipoArquivos() {
		return new RadioChoice<String>("tipoArquivo", new PropertyModel<String>(this, "tipoArquivo"), TIPO_ARQUIVOS).setRequired(true);
	}
	
	private TextField<LocalDate> dataEnvioInicio() {
		fieldDataInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
		fieldDataInicio.setLabel(new Model<String>("intervalo da data do envio"));
		fieldDataInicio.setRequired(true);
		return fieldDataInicio;
	}
	
	private TextField<LocalDate> dataEnvioFinal() {
		return fieldDataFim = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	}
	
	private Component pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		return fieldMunicipio = new DropDownChoice<Municipio>("municipio", new Model<Municipio>(),municipioMediator.listarTodos(), renderer);
	}

	private String getTipoArquivo() {
		return tipoArquivo;
	}

	private Instituicao getInstituicao() {
		return instituicao;
	}
	
	private RelatorioUtils getRalatorioUtils(){
		return new RelatorioUtils();
	}
}