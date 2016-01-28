package br.com.ieptbto.cra.page.relatorio.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoRelatorio;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioArquivosPage extends BasePage<Remessa> {
	
	/***/
	private static final long serialVersionUID = 1L;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	private Remessa remessa;
	private List<Instituicao> listaInstituicoes;
	private RadioChoice<TipoRelatorio> tipoRelatorio;
	private DropDownChoice<Instituicao> comboPortador;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	private TipoArquivoEnum tiposArquivoRelatorio;

	public RelatorioArquivosPage() {
		this.remessa = new Remessa();
		
		carregarFormularioArquivoCartorio();
	}

	private void carregarFormularioArquivoCartorio() {
		Form<Remessa> form = new Form<Remessa>("form"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				// TODO Auto-generated method stub
				super.onSubmit();
			}
		};
		form.add(dataEnvioInicio());
		form.add(dataEnvioFinal());
		form.add(tipoRelatorio());
		form.add(tipoArquivo());
		form.add(tipoInstituicao());
		form.add(comboPortadorCartorio());
		add(form);
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
	
	private RadioChoice<TipoArquivoEnum> tipoArquivo() {
		List<TipoArquivoEnum> listaTipos = new ArrayList<TipoArquivoEnum>();
		listaTipos.add(TipoArquivoEnum.REMESSA);
		listaTipos.add(TipoArquivoEnum.CONFIRMACAO);
		listaTipos.add(TipoArquivoEnum.RETORNO);
		RadioChoice<TipoArquivoEnum> tipos = new RadioChoice<TipoArquivoEnum>("tipoArquivos",new Model<TipoArquivoEnum>(tiposArquivoRelatorio), listaTipos);
		tipos.setLabel(new Model<String>("Tipo do Arquivo"));
		tipos.setRequired(true);
		return tipos;
	}

	private RadioChoice<TipoRelatorio> tipoRelatorio() {
		List<TipoRelatorio> choices = Arrays.asList(TipoRelatorio.values());
		tipoRelatorio = new RadioChoice<TipoRelatorio>("tipoRelatorio", new Model<TipoRelatorio>(), choices); 
		tipoRelatorio.setLabel(new Model<String>("Tipo Relatório"));
		tipoRelatorio.setRequired(true);
		return tipoRelatorio;
	}
	
	private DropDownChoice<TipoInstituicaoCRA> tipoInstituicao() {
		IChoiceRenderer<TipoInstituicaoCRA> renderer = new ChoiceRenderer<TipoInstituicaoCRA>("label");
		List<TipoInstituicaoCRA> choices = new ArrayList<TipoInstituicaoCRA>();
		choices.add(TipoInstituicaoCRA.CARTORIO);
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
					} else if (tipo.equals(TipoInstituicaoCRA.CARTORIO)) {
						getListaInstituicoes().clear();
						getListaInstituicoes().addAll(instituicaoMediator.getCartorios());
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
	
	public List<Instituicao> getListaInstituicoes() {
		if (listaInstituicoes == null) {
			listaInstituicoes = new ArrayList<Instituicao>();
		}
		return listaInstituicoes;
	}

	@Override
	protected IModel<Remessa> getModel() {
		return new CompoundPropertyModel<Remessa>(remessa);
	}
}