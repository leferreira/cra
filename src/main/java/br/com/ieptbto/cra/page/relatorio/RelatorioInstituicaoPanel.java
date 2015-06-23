package br.com.ieptbto.cra.page.relatorio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioInstituicaoPanel extends Panel{

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioInstituicaoPanel.class);
	private static final List<String> TIPO_ARQUIVOS = Arrays.asList(new String[] { "B", "C", "R" });
	
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	RelatorioMediator relatorioMediator;
	
	@SuppressWarnings("unused")
	private String tipoRelatorio;
	private TextField<LocalDate> fieldDataInicio;
	private TextField<LocalDate> fieldDataFim;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Instituicao portador;
	private String tipoArquivo;
	
	public RelatorioInstituicaoPanel(String id, IModel<?> model, Instituicao instituicao) {
		super(id, model);
		this.portador = instituicao;
		add(comboTipoArquivos());
		add(dataEnvioInicio());
		add(comboTipoRelatorio());
		add(dataEnvioFinal());
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
					
					try {
						JasperPrint jasperPrint = relatorioMediator.novoRelatorioSintetico(portador, tipoArquivo, dataInicio, dataFim);
						getResponse().write(JasperExportManager.exportReportToPdf(jasperPrint));
					
					}catch (JRException e) {
						logger.error(e.getMessage(), e);
						error("Não foi possível gerar o relatório. \n Entre em contato com a CRA!");
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						error("Não foi possível gerar o relatório. A busca não retornou resultados neste período! ");
					}
				}
		});
	}
	
	private Component comboTipoArquivos() {
		return new RadioChoice<String>("tipoArquivo", new PropertyModel<String>(this, "tipoArquivo"), TIPO_ARQUIVOS).setRequired(true);
	}
	
	private Component comboTipoRelatorio() {
		List<String> tipoRelatorio = new ArrayList<String>();
		tipoRelatorio.add("Por Arquivo");
		tipoRelatorio.add("Por Título");
		return new RadioChoice<String>("tipoRelatorio", new PropertyModel<String>(this, "tipoRelatorio"), tipoRelatorio).setRequired(true);
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
}