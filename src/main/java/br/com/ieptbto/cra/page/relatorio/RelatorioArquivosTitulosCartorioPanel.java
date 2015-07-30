package br.com.ieptbto.cra.page.relatorio;

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

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class RelatorioArquivosTitulosCartorioPanel extends Panel  {

	private static final Logger logger = Logger.getLogger(RelatorioArquivosTitulosCartorioPanel.class);
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	RemessaMediator remessaMediator;
	
	private IModel<Arquivo> model;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Municipio municipio;
	private Instituicao portador;
	
	private Instituicao instituicaoUsuario;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	private DropDownChoice<Instituicao> comboPortador;
	
	public RelatorioArquivosTitulosCartorioPanel(String id, IModel<Arquivo> model, Instituicao instituicao) {
		super(id, model);
		this.model = model;
		this.instituicaoUsuario = instituicao;
		adicionarCampos();
	}

	private Component botaoEnviar() {
		return new Button("botaoBuscar") {
			
			@Override
			public void onSubmit() {
				Arquivo arquivoBuscado = model.getObject();
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
					
					if (model.getObject().getNomeArquivo() != null) {
						arquivoBuscado = remessaMediator.buscarArquivoPorNome(instituicaoUsuario ,model.getObject().getNomeArquivo());
						if (arquivoBuscado != null) {
//							setResponsePage(new TitulosDoArquivoPage(arquivoBuscado));
						} else {
							throw new InfraException("Arquivo não foi encontrado ou não existe!");
						}
					} else if (comboPortador.getDefaultModelObject() != null) {
						portador = Instituicao.class.cast(comboPortador.getDefaultModelObject());
						setResponsePage(new RelatorioTitulosPage(portador, municipio, dataInicio, dataFim));
					}
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
	
	private void adicionarCampos() {
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(nomeArquivo());
		add(comboPortador());
		add(botaoEnviar());
	}

	private TextField<String> nomeArquivo() {
		return new TextField<String>("nomeArquivo");
	}
	
	private TextField<LocalDate> dataEnvioInicio() {
		dataEnvioInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
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
	
}