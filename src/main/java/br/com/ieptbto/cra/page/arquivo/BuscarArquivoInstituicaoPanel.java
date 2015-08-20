package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
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
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class BuscarArquivoInstituicaoPanel extends Panel  {

	private static final Logger logger = Logger.getLogger(BuscarArquivoInstituicaoPanel.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	private IModel<Arquivo> model;
//	private DropDownChoice<Municipio> comboMunicipio;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	
	private ArrayList<TipoArquivoEnum> tiposArquivo = new ArrayList<TipoArquivoEnum>();
	private ArrayList<SituacaoArquivo> situacaoArquivo = new ArrayList<SituacaoArquivo>();;
	
	public BuscarArquivoInstituicaoPanel(String id, IModel<Arquivo> model, Instituicao instituicao) {
		super(id, model);
		this.model = model;
		add(comboTipoArquivos());
		add(comboSituacaoArquivos());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(nomeArquivo());
//		add(pracaProtesto());
		add(botaoEnviar());
	}
	
	private Component botaoEnviar() {
		return new Button("botaoBuscar") {

			@Override
			public void onSubmit() {
				Arquivo arquivo = model.getObject();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				Municipio municipio = null;
				
				try {
					if (arquivo.getNomeArquivo() == null && dataEnvioInicio.getDefaultModelObject() == null) {
						throw new InfraException("Por favor, informe o 'Nome do Arquivo' ou 'Intervalo de datas'!");
					} else if (arquivo.getNomeArquivo() != null) {
						if (arquivo.getNomeArquivo().length() < 4) {
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
					
//					if (comboMunicipio.getModelObject() != null){
//						municipio = comboMunicipio.getModelObject();
//					}
					setResponsePage(new ListaArquivosInstituicaoPage(arquivo, municipio, dataInicio, dataFim, getTiposArquivo(), getSituacaoArquivo()));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo ! \n Entre em contato com a CRA ");
				}
			}
		};
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
		IChoiceRenderer<SituacaoArquivo> renderer = new ChoiceRenderer<SituacaoArquivo>("label");
		List<SituacaoArquivo> listaSituacao = Arrays.asList(SituacaoArquivo.values());
		CheckBoxMultipleChoice<SituacaoArquivo> situacao = new CheckBoxMultipleChoice<SituacaoArquivo>("situacaoArquivos",new Model<ArrayList<SituacaoArquivo>>(situacaoArquivo), listaSituacao, renderer);
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

//	private DropDownChoice<Municipio> pracaProtesto() {
//		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
//		this.comboMunicipio = new DropDownChoice<Municipio>("municipio", new Model<Municipio>(),municipioMediator.getMunicipiosTocantins(), renderer);
//		return comboMunicipio;
//	}

	public ArrayList<TipoArquivoEnum> getTiposArquivo() {
		return tiposArquivo;
	}

	public ArrayList<SituacaoArquivo> getSituacaoArquivo() {
		return situacaoArquivo;
	}
}