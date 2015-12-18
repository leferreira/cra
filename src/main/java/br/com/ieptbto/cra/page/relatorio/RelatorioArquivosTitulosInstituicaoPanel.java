package br.com.ieptbto.cra.page.relatorio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
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
import br.com.ieptbto.cra.enumeration.TipoRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class RelatorioArquivosTitulosInstituicaoPanel extends Panel  {

	private static final Logger logger = Logger.getLogger(RelatorioArquivosTitulosCraPanel.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	ArquivoMediator arquivoMediator;
	
	private Arquivo arquivo;
	private Remessa remessa;
	private Instituicao instituicao;
	private Usuario user;
	private List<Instituicao> listaInstituicoes;
	private RadioChoice<TipoRelatorio> tipoRelatorio;
	private DropDownChoice<Instituicao> comboInstituicao;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	
	public RelatorioArquivosTitulosInstituicaoPanel(String id, IModel<Arquivo> model, Instituicao instituicao, Usuario user) {
		super(id, model);
		this.setInstituicao(instituicao);
		this.arquivo = model.getObject();
		this.remessa = new Remessa();
		this.user = user;
		this.listaInstituicoes = new ArrayList<Instituicao>();
		listaInstituicoes.add(getUser().getInstituicao());
		listaInstituicoes.add(instituicaoMediator.buscarCRA());
		
		Form<Arquivo> formArquivo = new Form<Arquivo>("formArquivo", new CompoundPropertyModel<Arquivo>(arquivo)){
			
			@Override
			protected void onSubmit() {
				Arquivo arquivoBuscado = getModelObject();
				
				try {
					if (arquivoBuscado.getNomeArquivo().length() < 12 && arquivoBuscado.getNomeArquivo().length() > 13) {
						throw new InfraException("Por favor, informe o nome do arquivo corretamente !");
					}
					setResponsePage(new RelatorioArquivosListaPage(arquivoBuscado));
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
		formArquivo.add(new Button("submitFormArquivo"));
		add(formArquivo);
		
		Form<Remessa> formTitulo = new Form<Remessa>("formTitulo", new CompoundPropertyModel<Remessa>(remessa)){
			
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
					TipoRelatorio situacaoTitulos = tipoRelatorio.getModelObject();
					setResponsePage(new RelatorioTitulosPage(situacaoTitulos, getRemessa().getInstituicaoOrigem(), dataInicio, dataFim));
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
		formTitulo.add(new Button("submitFormTitulo"));
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

	private RadioChoice<TipoRelatorio> tipoRelatorio() {
		List<TipoRelatorio> choices = Arrays.asList(TipoRelatorio.values());
		tipoRelatorio = new RadioChoice<TipoRelatorio>("tipoRelatorio", new Model<TipoRelatorio>(), choices); 
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
	
	public Instituicao getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public Remessa getRemessa() {
		return remessa;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public void setListaInstituicoes(List<Instituicao> listaInstituicoes) {
		this.listaInstituicoes = listaInstituicoes;
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

	public void setUser(Usuario user) {
		this.user = user;
	}
}