package br.com.ieptbto.cra.page.tipoArquivo;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class IncluirTipoArquivoPage extends BasePage<TipoArquivo>{

	@SpringBean
	TipoArquivoMediator tipoArquivoMediator;
	
	/***/
	private static final long serialVersionUID = 1L;
	private TipoArquivo tipoArquivo;
	
	private TextField<String> fieldHoraInicio;
	private TextField<String> fieldHorafim;

	public IncluirTipoArquivoPage(TipoArquivo tipoArquivo) {
		setTipoArquivo(tipoArquivo);
		Form<?> form = new Form<TipoArquivo>("form"){

			/***/
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit() {
				
				try {
					if (fieldHoraInicio.getDefaultModelObject() !=null)
						getTipoArquivo().setHoraEnvioInicio(new LocalTime(fieldHoraInicio.getDefaultModelObject()));
					if (fieldHorafim.getDefaultModelObject() !=null)
						getTipoArquivo().setHoraEnvioFim(new LocalTime(fieldHorafim.getDefaultModelObject()));
					
					tipoArquivoMediator.alterarTipoArquivo(getTipoArquivo());
					setResponsePage(new ListaTipoArquivoPage("O Tipo Arquivo [ "+ getTipoArquivo().getTipoArquivo().getLabel() +" ] foi alterado com sucesso !"));
				} catch (Exception e) {
					System.out.println(e.getMessage());
					error("Não foi possível criar o novo Tipo Arquivo ["+ getTipoArquivo().getTipoArquivo() +"] ! Entre em contato com a CRA !");
				}
			}
		};
		form.add(campoTipoArquivo());
		form.add(campoConstante());
		form.add(campoHoraEnvioInicio());
		form.add(campoHoraEnvioFim());
		add(form);
	}
	
	private TextField<String> campoHoraEnvioFim() {
		DateTimeFormatter outputFormat = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter();
		if (getTipoArquivo().getHoraEnvioFim() != null) {
			return fieldHorafim = new TextField<String>("dataEnvioFim", new Model<String>(getTipoArquivo().getHoraEnvioFim().toString(outputFormat)));
		}
		return fieldHorafim = new TextField<String>("dataEnvioFim", new Model<String>());
	}

	private TextField<String> campoHoraEnvioInicio() {
		DateTimeFormatter outputFormat = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter();
		if (getTipoArquivo().getHoraEnvioInicio() != null) {
			return fieldHoraInicio = new TextField<String>("dataEnvioInicio", new Model<String>(getTipoArquivo().getHoraEnvioInicio().toString(outputFormat)));
		}
		return fieldHoraInicio = new TextField<String>("dataEnvioInicio", new Model<String>());
	}

	private TextField<String> campoTipoArquivo(){
		TextField<String> text = new TextField<String>("tipoArquivo.label", new Model<String>(getTipoArquivo().getTipoArquivo().getLabel()));
		text.setEnabled(false);
		return text;
	}
	
	private TextField<String> campoConstante(){
		TextField<String> text = new TextField<String>("tipoArquivo.constante", new Model<String>(getTipoArquivo().getTipoArquivo().getConstante()));
		text.setEnabled(false);
		return text;
	}
	
	@Override
	protected IModel<TipoArquivo> getModel() {
		return new CompoundPropertyModel<TipoArquivo>(tipoArquivo);
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}
}
