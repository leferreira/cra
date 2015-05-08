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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("unused")
public class RelatorioCartorioPanel extends Panel{

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioCartorioPanel.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	RelatorioMediator relatorioMediator;
	
	private Instituicao instituicao;
	private TextField<LocalDate> fieldDataInicio;
	private TextField<LocalDate> fieldDataFim;
	private Instituicao portador;
	private String tipoArquivo;
	
	public RelatorioCartorioPanel(String id, IModel<?> model, Instituicao instituicao) {
		super(id, model);
		this.instituicao = instituicao;
		add(comboTipoArquivos());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(comboPortador());
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
//							relatorioMediator.novoRelatorioAnaliticoBanco(getInstituicao(), getTipoArquivo(), dataInicio, dataFim, portador);
//							relatorioMediator.novoRelatorioSintetico(getInstituicao(), getTipoArquivo(), dataInicio, dataFim);
//					
//					}catch (JRException e) {
//						logger.error(e.getMessage(), e);
//						error("Não foi possível gerar o relatório. \n Entre em contato com a CRA!");
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//						error("Não foi possível realizar esta operação! \n Entre em contato com a CRA ");
//					}
				}
		});
	}
	
	private Component comboTipoArquivos() {
		List<String> status = Arrays.asList(new String[] { "B", "C", "R" });
		return new RadioChoice<String>("tipoArquivo", new Model<String>(tipoArquivo), status).setRequired(true);
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
	
	private Component comboPortador() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		return new DropDownChoice<Instituicao>("portador", new Model<Instituicao>(),instituicaoMediator.getInstituicoesFinanceiras(), renderer);
	}

	private Instituicao getInstituicao() {
		return instituicao;
	}

	private String getTipoArquivo() {
		return tipoArquivo;
	}
}
