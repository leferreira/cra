package br.com.ieptbto.cra.page.relatorio;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
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
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class RelatorioSinteticoCartorioPanel extends Panel{

	private static final Logger logger = Logger.getLogger(RelatorioSinteticoCartorioPanel.class);
	
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	RelatorioMediator relatorioGerador;
	private Instituicao cartorio;
	private TextField<String> fieldDataInicio;
	private TextField<String> fieldDataFim;
	private TipoArquivoEnum tipoArquivo;
	
	public RelatorioSinteticoCartorioPanel(String id, IModel<?> model, Instituicao cartorio) {
		super(id, model);
		setCartorio(cartorio);
		carregarComponentes();
	}
		
	private void carregarComponentes() {
		add(comboTipoArquivos());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(new Button("botaoGerar"){

			@Override
			public void onSubmit() {
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				
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
				
				try {
					JasperPrint jasperPrint = relatorioGerador.relatorioSinteticoPorMunicipio(getCartorio().getMunicipio(), tipoArquivo,dataInicio, dataFim );
					getResponse().write(JasperExportManager.exportReportToPdf(jasperPrint));
				
				} catch (JRException e) {
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
		fieldDataInicio.setLabel(new Model<String>("intervalo da data do envio"));
		fieldDataInicio.setRequired(true);
		return fieldDataInicio;
	}
	
	private TextField<String> dataEnvioFinal() {
		return fieldDataFim = new TextField<String>("dataEnvioFinal", new Model<String>());
	}

	public Instituicao getCartorio() {
		return cartorio;
	}

	public void setCartorio(Instituicao cartorio) {
		this.cartorio = cartorio;
	}
}
