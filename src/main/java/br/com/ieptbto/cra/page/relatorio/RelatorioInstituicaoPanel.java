package br.com.ieptbto.cra.page.relatorio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioInstituicaoPanel extends Panel  {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioInstituicaoPanel.class);
	
	@SpringBean
	private MunicipioMediator municipioMediator;
	@SpringBean
	private ArquivoMediator arquivoMediator;
	private Remessa remessa;
	private Arquivo arquivo;
	private Usuario user;

	private List<Instituicao> listaInstituicoes;
	private RadioChoice<SituacaoTituloRelatorio> tipoRelatorio;
	private DropDownChoice<Instituicao> comboInstituicao;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	
	public RelatorioInstituicaoPanel(String id, IModel<Arquivo> model, Usuario user) {
		super(id, model);
		this.arquivo = model.getObject();
		this.remessa = new Remessa();
		this.user = user;
		
		carregarFormularioRelatorioArquivo();
		carregarFormularioRelatorioTitulos();
	}
	
	private void carregarFormularioRelatorioArquivo() {
		Form<Arquivo> formArquivo = new Form<Arquivo>("formArquivo", new CompoundPropertyModel<Arquivo>(arquivo)){
			
			/***/
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit() {
				Arquivo arquivoBuscado = getModelObject();
				
				try {
					if (arquivoBuscado.getNomeArquivo().length() < 12 && arquivoBuscado.getNomeArquivo().length() > 13) {
						throw new InfraException("Por favor, informe o nome do arquivo corretamente !");
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
		formArquivo.add(nomeArquivo());
		add(formArquivo);
	}
	
	private void carregarFormularioRelatorioTitulos() {
		Form<Remessa> formTitulo = new Form<Remessa>("formTitulo", new CompoundPropertyModel<Remessa>(remessa)){
			
			/***/
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("unused")
			@Override
			protected void onSubmit() {
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				
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
					SituacaoTituloRelatorio situacaoTitulos = tipoRelatorio.getModelObject();

				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar a busca ! \n Entre em contato com a CRA ");
				}
			}
		};
		formTitulo.add(dataEnvioInicio());
		formTitulo.add(dataEnvioFinal());
		formTitulo.add(tipoRelatorio());
		formTitulo.add(comboPortadorCRA());
		add(formTitulo);
	}

	private TextField<String> nomeArquivo() {
		TextField<String> textField = new TextField<String>("nomeArquivo");
		textField.setLabel(new Model<String>("Nome do Arquivo"));
		textField.setRequired(true);
		return textField;
	}
	
	private TextField<LocalDate> dataEnvioInicio() {
		dataEnvioInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
		dataEnvioInicio.setLabel(new Model<String>("Período de datas"));
		dataEnvioInicio.setRequired(true);
		dataEnvioInicio.setMarkupId("date");
		return dataEnvioInicio;
	}
	
	private TextField<LocalDate> dataEnvioFinal() {
		dataEnvioFinal = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
		dataEnvioFinal.setMarkupId("date1");
		return dataEnvioFinal;
	}

	private RadioChoice<SituacaoTituloRelatorio> tipoRelatorio() {
		List<SituacaoTituloRelatorio> choices = Arrays.asList(SituacaoTituloRelatorio.values());
		tipoRelatorio = new RadioChoice<SituacaoTituloRelatorio>("tipoRelatorio", new Model<SituacaoTituloRelatorio>(), choices); 
		tipoRelatorio.setLabel(new Model<String>("Tipo Relatório"));
		tipoRelatorio.setRequired(true);
		return tipoRelatorio;
	}
	
	private DropDownChoice<Instituicao> comboPortadorCRA() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		comboInstituicao = new DropDownChoice<Instituicao>("instituicaoOrigem", getListaInstituicoes() , renderer);
		comboInstituicao.setLabel(new Model<String>("Portador"));
		comboInstituicao.setOutputMarkupId(true);
		return comboInstituicao;
	}
	
	public Arquivo getArquivo() {
		return arquivo;
	}

	public List<Instituicao> getListaInstituicoes() {
		if (listaInstituicoes == null) {
			listaInstituicoes = new ArrayList<Instituicao>();
		}
		return listaInstituicoes;
	}

	public Usuario getUser() {
		return user;
	}
}