package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class BuscarArquivoCraInstituicaoPage extends BasePage<Arquivo>  {

	private static final Logger logger = Logger.getLogger(BuscarArquivoCraInstituicaoPage.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	private Arquivo arquivo;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	private ArrayList<TipoArquivoEnum> tiposArquivo = new ArrayList<TipoArquivoEnum>();
	private ArrayList<SituacaoArquivo> situacaoArquivo = new ArrayList<SituacaoArquivo>();;
	
	public BuscarArquivoCraInstituicaoPage() {
		this.arquivo = new Arquivo();
		Form<Arquivo> form =  new Form<Arquivo>("form", getModel()){
			
			@Override
			public void onSubmit() {
				Arquivo arquivoBuscado = getModelObject();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				Municipio municipio = null;
				
				try {
					if (arquivoBuscado.getNomeArquivo() == null && dataEnvioInicio.getDefaultModelObject() == null) {
						throw new InfraException("Por favor, informe o 'Nome do Arquivo' ou 'Intervalo de datas'!");
					} else if (arquivoBuscado.getNomeArquivo() != null) {
						if (arquivoBuscado.getNomeArquivo().length() < 4) {
							throw new InfraException("Por favor, informe ao menos 4 caracteres!");
						}
					}
					
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
					
					setResponsePage(new ListaArquivosInstituicaoPage(arquivoBuscado, municipio, dataInicio, dataFim, getTiposArquivo(), getSituacaoArquivo()));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo ! \n Entre em contato com a CRA ");
				}
			}
		};
		form.add(nomeArquivo());
		form.add(comboTipoArquivos());
		form.add(comboSituacaoArquivos());
		form.add(dataEnvioInicio());
		form.add(dataEnvioFinal());
		form.add(comboPortador());
		add(form);
	}
	
	private TextField<String> nomeArquivo() {
		return new TextField<String>("nomeArquivo");
	}
	
	private CheckBoxMultipleChoice<TipoArquivoEnum> comboTipoArquivos() {
		List<TipoArquivoEnum> listaTipos = new ArrayList<TipoArquivoEnum>();
		listaTipos.add(TipoArquivoEnum.REMESSA);
		listaTipos.add(TipoArquivoEnum.CONFIRMACAO);
		listaTipos.add(TipoArquivoEnum.RETORNO);
		CheckBoxMultipleChoice<TipoArquivoEnum> tipos = new CheckBoxMultipleChoice<TipoArquivoEnum>("tipoArquivos",new Model<ArrayList<TipoArquivoEnum>>(tiposArquivo), listaTipos);
		tipos.setLabel(new Model<String>("Tipo do Arquivo"));
		return tipos;
	}
	
	private CheckBoxMultipleChoice<SituacaoArquivo> comboSituacaoArquivos() {
		List<SituacaoArquivo> listaSituacao = Arrays.asList(SituacaoArquivo.values());
		CheckBoxMultipleChoice<SituacaoArquivo> situacao = new CheckBoxMultipleChoice<SituacaoArquivo>("situacaoArquivos",new Model<ArrayList<SituacaoArquivo>>(situacaoArquivo), listaSituacao);
		situacao.setLabel(new Model<String>("Situacao do Arquivo"));
		return situacao;
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

	public ArrayList<TipoArquivoEnum> getTiposArquivo() {
		return tiposArquivo;
	}

	public ArrayList<SituacaoArquivo> getSituacaoArquivo() {
		return situacaoArquivo;
	}
	
	public Arquivo getArquivo() {
		return arquivo;
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}