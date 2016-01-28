package br.com.ieptbto.cra.page.relatorio.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
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
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioCartorioPanel extends Panel  {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioCartorioPanel.class);
	
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;
	@SpringBean
	private ArquivoMediator arquivoMediator;
	
	private Arquivo arquivo;
	private Remessa remessa;
	private Usuario usuario;
	
	private List<Instituicao> listaInstituicoes;
	private RadioChoice<SituacaoTituloRelatorio> tipoRelatorio;
	private DropDownChoice<Instituicao> comboPortador;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	
	public RelatorioCartorioPanel(String id, IModel<Arquivo> model, Usuario user) {
		super(id, model);
		this.remessa = new Remessa();
		this.arquivo = model.getObject();
		this.usuario = user;

		carregarFormularioRelatorioArquivo();
		carregarFormularioRelatorioTitulos();
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
		formTitulo.add(tipoInstituicao());
		formTitulo.add(comboPortadorCartorio());
		add(formTitulo);
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
	
	private DropDownChoice<TipoInstituicaoCRA> tipoInstituicao() {
		IChoiceRenderer<TipoInstituicaoCRA> renderer = new ChoiceRenderer<TipoInstituicaoCRA>("label");
		List<TipoInstituicaoCRA> choices = new ArrayList<TipoInstituicaoCRA>();
		choices.add(TipoInstituicaoCRA.CONVENIO);
		choices.add(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA);
		final DropDownChoice<TipoInstituicaoCRA> tipoInstituicao = new DropDownChoice<TipoInstituicaoCRA>("tipoInstituicao", new Model<TipoInstituicaoCRA>(), choices, renderer);
		tipoInstituicao.add(new OnChangeAjaxBehavior() {
			
			/***/
			private static final long serialVersionUID = 1L;
			
			@Override
            protected void onUpdate(AjaxRequestTarget target){
				TipoInstituicaoCRA tipo = tipoInstituicao.getModelObject();
				
				if (tipoInstituicao.getModelObject() != null) {
					if (tipo.equals(TipoInstituicaoCRA.CONVENIO)) {
						getListaInstituicoes().clear();
						getListaInstituicoes().addAll(instituicaoMediator.getConvenios());
					} else if (tipo.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
						getListaInstituicoes().clear();
						getListaInstituicoes().addAll(instituicaoMediator.getInstituicoesFinanceiras());
					}
					comboPortador.setEnabled(true);
					comboPortador.setRequired(true);
				} else {
					comboPortador.setEnabled(false);
					comboPortador.setRequired(false);
	            	getListaInstituicoes().clear();
				}
            	target.add(comboPortador);
            }
        });
		tipoInstituicao.setLabel(new Model<String>("Tipo Instituição"));
		tipoInstituicao.setRequired(true);
		return tipoInstituicao;
	}
	
	private DropDownChoice<Instituicao> comboPortadorCartorio() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		comboPortador = new DropDownChoice<Instituicao>("instituicaoOrigem", getListaInstituicoes() , renderer);
		comboPortador.setLabel(new Model<String>("Portador"));
		comboPortador.setOutputMarkupId(true);
		comboPortador.setEnabled(false);
		return comboPortador;
	}
	
	public Arquivo getArquivo() {
		return arquivo;
	}

	public Remessa getRemessa() {
		return remessa;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public List<Instituicao> getListaInstituicoes() {
		if (listaInstituicoes == null) {
			listaInstituicoes = new ArrayList<Instituicao>();
		}
		return listaInstituicoes;
	}
}