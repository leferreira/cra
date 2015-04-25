package br.com.ieptbto.cra.page.titulo;

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

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class MonitorarTitulosInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MonitorarTitulosInputPanel.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	
	private TituloRemessa titulo;
	private TextField<LocalDate> dataEntradaCRA;
	private DropDownChoice<Instituicao> comboPortador;
	private IModel<TituloRemessa> model;
	
	public MonitorarTitulosInputPanel(String id, IModel<TituloRemessa> model) {
		super(id, model);
		this.model = model;
		this.titulo = new TituloRemessa();
		adicionarCampos();
	}
	
	private void adicionarCampos(){
		add(nossoNumero());
		add(numeroTitulo());
		add(dataEntradaCRA());
		add(numeroProtocoloCartorio());
		add(comboPortador());
		add(nomeDevedor());
		add(documentoDevedor()); 
		add(botaoEnviar());
	}
	
	private Component comboPortador() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		this.comboPortador = new DropDownChoice<Instituicao>("remessa.instituicaoOrigem",instituicaoMediator.getInstituicoesFinanceiras(), renderer);
		return comboPortador;
	}
	
	public TextField<String> nossoNumero() {
		return new TextField<String>("nossoNumero");
	}
	
	public TextField<String> numeroTitulo() {
		return new TextField<String>("numeroTitulo");
	}
	
	public TextField<LocalDate> dataEntradaCRA() {
		return dataEntradaCRA = new TextField<LocalDate>("dataOcorrencia", new Model<LocalDate>());
	}
	
	public TextField<String> nomeDevedor() {
		return new TextField<String>("nomeDevedor");
	}
	
	public TextField<String> documentoDevedor() {
		return new TextField<String>("documentoDevedor");
	}
	
	public TextField<String> numeroProtocoloCartorio() {
		return new TextField<String>("numeroProtocoloCartorio");
	}
	
	private Component botaoEnviar() {
		return new Button("botaoBuscar") {
			/** */
			private static final long serialVersionUID = 1L;
			public void onSubmit() {
				
				titulo = model.getObject();
				try {
					if (dataEntradaCRA.getDefaultModelObject() != null)
						titulo.setDataOcorrencia(DataUtil.stringToLocalDate(dataEntradaCRA.getDefaultModelObject().toString()));
					if (comboPortador.getDefaultModelObject() != null)
						titulo.setCodigoPortador(titulo.getRemessa().getInstituicaoOrigem().getCodigoCompensacao());
					
					setResponsePage(new ListaTitulosPage(titulo));
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
}
