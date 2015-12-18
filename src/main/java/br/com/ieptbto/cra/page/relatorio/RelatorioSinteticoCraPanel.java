package br.com.ieptbto.cra.page.relatorio;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
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
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.util.DataUtil;


/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class RelatorioSinteticoCraPanel extends Panel{

	private static final Logger logger = Logger.getLogger(RelatorioSinteticoCraPanel.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	RelatorioMediator relatorioGerador;
	private TextField<String> fieldDataInicio;
	private TextField<String> fieldDataFim;
	private DropDownChoice<Instituicao> fieldPortador;
	private DropDownChoice<Municipio> fieldMunicipio;
	private TipoArquivoEnum tipoArquivo;
	
	public RelatorioSinteticoCraPanel(String id, IModel<?> model) {
		super(id, model);
		carregarComponentes();
	}
	
	private void carregarComponentes() {
		add(comboTipoArquivos());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(comboPortador());
		add(pracaProtesto());
		add(new Button("botaoGerar"){
			
			@Override
			public void onSubmit() {
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				Municipio pracaProtesto = null;
				Instituicao bancoPortador = null;
				
				if (fieldDataInicio.getModelObject() != null){
					if (fieldDataFim.getModelObject() != null){
						dataInicio = DataUtil.stringToLocalDate(fieldDataInicio.getModelObject());
						dataFim = DataUtil.stringToLocalDate(fieldDataFim.getModelObject());
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
						JasperPrint jasperPrint = relatorioGerador.relatorioSintetico(bancoPortador, tipoArquivo, dataInicio, dataFim);
						getResponse().write(JasperExportManager.exportReportToPdf(jasperPrint));

					} else if (bancoPortador == null && pracaProtesto != null){ 
						JasperPrint jasperPrint = relatorioGerador.relatorioSinteticoPorMunicipio(pracaProtesto , tipoArquivo,dataInicio, dataFim );
						getResponse().write(JasperExportManager.exportReportToPdf(jasperPrint));
						
					} else 
						error("Deve ser selecionado o portador ou o município!");
					
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
	
	private RadioChoice<TipoArquivoEnum> comboTipoArquivos() {
		List<TipoArquivoEnum> choices =  new ArrayList<TipoArquivoEnum>();
		choices.add(TipoArquivoEnum.REMESSA);
		choices.add(TipoArquivoEnum.CONFIRMACAO);
		choices.add(TipoArquivoEnum.RETORNO);
		RadioChoice<TipoArquivoEnum> radio = new RadioChoice<TipoArquivoEnum>("tipoArquivo", new PropertyModel<TipoArquivoEnum>(this, "tipoArquivo"), choices );
		radio.setRequired(true);
		return radio;
	}
	
	private TextField<String> dataEnvioInicio() {
		fieldDataInicio = new TextField<String>("dataEnvioInicio", new Model<String>());
		fieldDataInicio.setLabel(new Model<String>("intervalo da data do relatório"));
		fieldDataInicio.setRequired(true);
		return fieldDataInicio;
	}
	
	private TextField<String> dataEnvioFinal() {
		return fieldDataFim = new TextField<String>("dataEnvioFinal", new Model<String>());
	}
	
	private DropDownChoice<Instituicao> comboPortador() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		return fieldPortador = new DropDownChoice<Instituicao>("portador", new Model<Instituicao>(),instituicaoMediator.getInstituicoesFinanceiras(), renderer);
	}
	
	private DropDownChoice<Municipio> pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		return fieldMunicipio = new DropDownChoice<Municipio>("municipio", new Model<Municipio>(),municipioMediator.getMunicipiosTocantins(), renderer);
	}
}
