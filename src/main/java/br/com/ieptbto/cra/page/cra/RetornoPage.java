package br.com.ieptbto.cra.page.cra;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.RetornoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosDoArquivoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings( "serial" )
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class RetornoPage extends BasePage<Retorno> {

	private static final Logger logger = Logger.getLogger(RetornoPage.class);
	
	@SpringBean
	private RetornoMediator retornoMediator;
	private Retorno retorno;
	private List<Remessa> retornosParaEnvio;
	
	public RetornoPage(){
		this.retorno = new Retorno();
		carregarGuiaRetorno();
	}
	
	private void carregarGuiaRetorno(){
		setRetornosParaEnvio(retornoMediator.buscarRetornosConfirmados());
        Form<Retorno> formRetorno = new Form<Retorno>("form"){

        	protected void onSubmit(){
				try{
					if (!getRetornosParaEnvio().isEmpty()){
						retornoMediator.gerarRetornos(getUser(), getRetornosParaEnvio());
					} else 
						throw new InfraException("Não há retornos pendentes para envio.");
					
					setResponsePage(new RetornoLabel());
				} catch (InfraException e) {
					logger.error(e.getMessage(), e);
					error(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar o batimento! Entre em contato com a CRA.");
				}
            }
        };
        formRetorno.add(carregarListaRetornos());
        formRetorno.add(new Button("botaoRetorno"));
        add(formRetorno);
	}
	
	
	private ListView<Remessa> carregarListaRetornos(){
		return new ListView<Remessa>("retornos", getRetornosParaEnvio()){
			@Override
			protected void populateItem(ListItem<Remessa> item){
				final Remessa retorno = item.getModelObject();
				item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
				item.add(new Label("instituicaoOrigem.nomeFantasia", retorno.getInstituicaoOrigem().getNomeFantasia()));
				item.add(new LabelValorMonetario<BigDecimal>("valorPagos", retornoMediator.buscarValorDeTitulosPagos(retorno)));
				Link<Remessa> linkArquivo = new Link<Remessa>("linkArquivo") {
					@Override
					public void onClick() {
						setResponsePage(new TitulosDoArquivoPage(retorno));  
					}
				};
				linkArquivo.add(new Label("arquivo.nomeArquivo", retorno.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(removerConfirmado(retorno));
			}
			
			private Component removerConfirmado(final Remessa retorno) {
				return new Link<Arquivo>("removerConfirmado") {
					@Override
					public void onClick() {
						retornoMediator.removerConfirmado(retorno);
						setResponsePage(new RetornoPage());
					}
				};
			}
		};
	}

	public List<Remessa> getRetornosParaEnvio() {
		return retornosParaEnvio;
	}

	public void setRetornosParaEnvio(List<Remessa> retornosParaEnvio) {
		this.retornosParaEnvio = retornosParaEnvio;
	}

	@Override
	protected IModel<Retorno> getModel() {
		return new CompoundPropertyModel<Retorno>(retorno);
	}
}
