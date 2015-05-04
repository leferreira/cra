package br.com.ieptbto.cra.page.relatorio;

import java.util.Arrays;
import java.util.List;

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
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.DataUtil;

public class RelatorioBancoPanel extends Panel{

	/***/
	private static final long serialVersionUID = 1L;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;
	@SuppressWarnings("unused")
	private Instituicao instituicao;
	
	TextField<LocalDate> dataInicio;
	TextField<LocalDate> dataFim;
	Municipio municipio;
	Instituicao comboPortador;
	String tipoArquivo;
	String nivelDetalhamento;
	
	public RelatorioBancoPanel(String id, IModel<?> model, Instituicao instituicao) {
		super(id, model);
		this.instituicao = instituicao;
		add(nivelDetalhamento());
		add(comboTipoArquivos());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(pracaProtesto());
		add(new Button("botaoGerar"){
				/****/
				private static final long serialVersionUID = 1L;
				@Override
				public void onSubmit() {
					LocalDate inicio = new LocalDate();
					LocalDate fim = new LocalDate();
					if (dataInicio.getDefaultModelObject() != null){
						if (dataFim.getDefaultModelObject() != null){
							inicio = DataUtil.stringToLocalDate(dataInicio.getDefaultModelObject().toString());
							fim = DataUtil.stringToLocalDate(dataFim.getDefaultModelObject().toString());
							if (!inicio.isBefore(fim))
								if (!inicio.isEqual(fim))
									throw new InfraException("A data de início deve ser antes da data fim.");
						}else
							throw new InfraException("As duas datas devem ser preenchidas.");
					} 
					
					info(DataUtil.localDateToString(inicio));
					info(DataUtil.localDateToString(fim));
				}
		});
	}
	
	private Component nivelDetalhamento() {
		List<String> status = Arrays.asList(new String[] { "Analítico", "Sintético" });
		return new RadioChoice<String>("nivelDetalhamento", new Model<String>(nivelDetalhamento), status).setRequired(true);
	}
	
	private Component comboTipoArquivos() {
		List<String> status = Arrays.asList(new String[] { "B", "C", "R" });
		return new RadioChoice<String>("tipoArquivo", new Model<String>(tipoArquivo), status).setRequired(true);
	}
	
	private TextField<LocalDate> dataEnvioInicio() {
		dataInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
		dataInicio.setLabel(new Model<String>("intervalo da data do envio"));
		dataInicio.setRequired(true);
		return dataInicio;
	}
	
	private TextField<LocalDate> dataEnvioFinal() {
		return dataFim = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	}
	
	private Component pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		return new DropDownChoice<Municipio>("municipio", new Model<Municipio>(),municipioMediator.listarTodos(), renderer);
	}

}
