package br.com.ieptbto.cra.page.titulo;

import org.apache.log4j.Logger;
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

import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class MonitorarTitulosInstituicaoPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MonitorarTitulosInstituicaoPanel.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	
	private TextField<LocalDate> dataEntradaCRA;
	private DropDownChoice<Municipio> comboMunicipio;
	private IModel<TituloRemessa> model;
	
	public MonitorarTitulosInstituicaoPanel(String id, IModel<TituloRemessa> model) {
		super(id, model);
		this.model = model;
		adicionarCampos();
	}
	
	private void adicionarCampos(){
		add(nossoNumero());
		add(numeroTitulo());
		add(dataEntradaCRA());
		add(numeroProtocoloCartorio());
		add(pracaProtesto());
		add(nomeDevedor());
		add(documentoDevedor()); 
		add(new Button("botaoBuscar"){
			/** */
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				TituloRemessa titulo = model.getObject();

				try {
					if (dataEntradaCRA.getDefaultModelObject() != null)
						titulo.setDataOcorrencia(DataUtil.stringToLocalDate(dataEntradaCRA.getDefaultModelObject().toString()));
					if (comboMunicipio.getDefaultModelObject() != null)
						titulo.setPracaProtesto(comboMunicipio.getModelObject().getNomeMunicipio());
					
					setResponsePage(new ListaTitulosPage(titulo));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar a busca ! \n Entre em contato com a CRA ");
				}
			}
		});
	}
	
	private DropDownChoice<Municipio> pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		comboMunicipio = new DropDownChoice<Municipio>("municipio", new Model<Municipio>(), municipioMediator.getMunicipiosTocantins(), renderer);
		return comboMunicipio; 
	}
	
	private TextField<String> nossoNumero() {
		return new TextField<String>("nossoNumero");
	}
	
	private TextField<String> numeroTitulo() {
		return new TextField<String>("numeroTitulo");
	}
	
	private TextField<LocalDate> dataEntradaCRA() {
		return dataEntradaCRA = new TextField<LocalDate>("dataOcorrencia", new Model<LocalDate>());
	}
	
	private TextField<String> nomeDevedor() {
		return new TextField<String>("nomeDevedor");
	}
	
	private TextField<String> documentoDevedor() {
		return new TextField<String>("documentoDevedor");
	}
	
	private TextField<String> numeroProtocoloCartorio() {
		return new TextField<String>("numeroProtocoloCartorio");
	}
}
