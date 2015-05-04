package br.com.ieptbto.cra.page.relatorio;

import org.apache.wicket.Component;
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

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

public class RelatoriosDeTituloPage extends BasePage<TituloRemessa>{

	/***/
	private static final long serialVersionUID = 1L;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;
	private TituloRemessa titulo;
	private Form<TituloRemessa> form;
	
	LocalDate inicio;
	LocalDate fim;
	TextField<LocalDate> dataInicio;
	TextField<LocalDate> dataFim;
	DropDownChoice<Municipio> municipio;
	DropDownChoice<Instituicao> portador;
	
	public RelatoriosDeTituloPage() {
		this.titulo = new TituloRemessa();
		this.inicio = new LocalDate().withDayOfMonth(1);
		this.fim    = new LocalDate().withDayOfMonth(30);
		form = new Form<TituloRemessa>("form", getModel()){
			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
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
		};		
		adicionarCamposParametros();
		add(form);
	}
	
	private void adicionarCamposParametros(){
		form.add(dataEnvioInicio());
		form.add(dataEnvioFinal());
		form.add(comboPortador());
		form.add(pracaProtesto());
	}
	
	public TextField<LocalDate> dataEnvioInicio() {
		dataInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
		dataInicio.setLabel(new Model<String>("intervalo da data do envio"));
		return dataInicio;
	}
	
	public TextField<LocalDate> dataEnvioFinal() {
		return dataFim = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	}
	
	private Component comboPortador() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		this.portador = new DropDownChoice<Instituicao>("portador", new Model<Instituicao>(),instituicaoMediator.getInstituicoesFinanceiras(), renderer);
		return portador;
	}
	
	private Component pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		this.municipio = new DropDownChoice<Municipio>("municipio", new Model<Municipio>(),municipioMediator.listarTodos(), renderer);
		return municipio;
	}
	
	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}

}
