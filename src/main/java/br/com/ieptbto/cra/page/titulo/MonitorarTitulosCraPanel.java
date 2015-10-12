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

import br.com.ieptbto.cra.entidade.Instituicao;
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
@SuppressWarnings("serial")
public class MonitorarTitulosCraPanel extends Panel {

	private static final Logger logger = Logger.getLogger(MonitorarTitulosCraPanel.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	private IModel<TituloRemessa> model;
	private TextField<String> dataEntradaCRA;
	private DropDownChoice<Instituicao> comboPortador;
	private DropDownChoice<Municipio> comboMunicipio;
	
	public MonitorarTitulosCraPanel(String id, IModel<TituloRemessa> model) {
		super(id, model);
		this.model = model;
		adicionarCampos();
	}
	
	private void adicionarCampos(){
		add(nossoNumero());
		add(numeroTitulo());
		add(dataEntradaCRA());
		add(numeroProtocoloCartorio());
		add(comboPortador());
		add(pracaProtesto());
		add(nomeDevedor());
		add(documentoDevedor()); 
		add(nomeSacador());
		add(documentoSacador()); 
		add(new Button("botaoBuscar"){

			@Override
			public void onSubmit() {
				TituloRemessa titulo = model.getObject();
				Municipio municipio = null;

				try {
					if (dataEntradaCRA.getModelObject() != null)
						titulo.setDataCadastro(DataUtil.stringToLocalDate(dataEntradaCRA.getModelObject()).toDate());
					if (comboPortador.getModelObject() != null) 
						titulo.setCodigoPortador(comboPortador.getModelObject().getCodigoCompensacao());
					if (comboMunicipio.getModelObject() != null)
						municipio = comboMunicipio.getModelObject();
					
					setResponsePage(new ListaTitulosPage(titulo, municipio));
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
	
	private DropDownChoice<Instituicao> comboPortador() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		this.comboPortador = new DropDownChoice<Instituicao>("remessa.instituicaoOrigem",instituicaoMediator.getInstituicoesFinanceirasEConvenios(), renderer);
		return comboPortador;
	}
	
	private TextField<String> nossoNumero() {
		return new TextField<String>("nossoNumero");
	}
	
	private TextField<String> numeroTitulo() {
		return new TextField<String>("numeroTitulo");
	}
	
	private TextField<String> dataEntradaCRA() {
		return dataEntradaCRA = new TextField<String>("dataOcorrencia", new Model<String>());
	}
	
	private TextField<String> nomeDevedor() {
		return new TextField<String>("nomeDevedor");
	}
	
	private TextField<String> documentoDevedor() {
		return new TextField<String>("numeroIdentificacaoDevedor");
	}
	
	private TextField<String> nomeSacador() {
		return new TextField<String>("nomeSacadorVendedor");
	}
	
	private TextField<String> documentoSacador() {
		return new TextField<String>("documentoSacador");
	}
	
	private TextField<String> numeroProtocoloCartorio() {
		return new TextField<String>("numeroProtocoloCartorio");
	}
}
