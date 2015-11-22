package br.com.ieptbto.cra.page.cra;

import java.util.List;

import org.apache.log4j.Logger;
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

import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfirmacaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosArquivoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */

@SuppressWarnings( "serial" )
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ConfirmacaoPage extends BasePage<Confirmacao> {

	private static final Logger logger = Logger.getLogger(ConfirmacaoPage.class);
	
	@SpringBean
	private ConfirmacaoMediator confirmacaoMediator;
	private Confirmacao confirmacao;
	private List<Remessa> confirmacoesParaEnvio;
	
	public ConfirmacaoPage(){
		this.confirmacao = new Confirmacao();
		carregarGuiaConfirmacao();
	}
	
	private void carregarGuiaConfirmacao(){
		setConfirmacoesParaEnvio(confirmacaoMediator.buscarConfirmacoesPendentesDeEnvio());
		Form<Confirmacao> formConfirmacao = new Form<Confirmacao>("formConfirmacao"){
			@Override
            protected void onSubmit(){
				
				try{
					if (!getConfirmacoesParaEnvio().isEmpty())
						confirmacaoMediator.gerarConfirmacoes(getUser(), getConfirmacoesParaEnvio());
					else 
						throw new InfraException("Não há confirmações pendentes para envio.");
					
					setResponsePage(new ConfirmacaoLabel());
				} catch (InfraException e) {
					logger.error(e.getMessage(), e);
					error(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível gerar a confirmação! Entre em contato com a CRA.");
				}
            }
        };
        formConfirmacao.add(carregarListaConfirmacao());
        formConfirmacao.add(new Button("botaoConfirmacao"));
        add(formConfirmacao);
	}
	
	
	private ListView<Remessa> carregarListaConfirmacao(){
		return new ListView<Remessa>("confirmacao", getConfirmacoesParaEnvio()){
				@Override
	            protected void populateItem(ListItem<Remessa> item){
					final Remessa retorno = item.getModelObject();
					item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
					item.add(new Label("horaEnvio", DataUtil.localTimeToString(retorno.getArquivo().getHoraEnvio())));
					item.add(new Label("instituicaoOrigem.nomeFantasia", retorno.getInstituicaoOrigem().getNomeFantasia()));
					item.add(new Label("instituicaoDestino.nomeFantasia", retorno.getInstituicaoDestino().getNomeFantasia()));
					Link<Remessa> linkArquivo = new Link<Remessa>("linkArquivo") {

						public void onClick() {
			            	setResponsePage(new TitulosArquivoPage(retorno));  
			            }
			        };
			        linkArquivo.add(new Label("arquivo.nomeArquivo", retorno.getArquivo().getNomeArquivo()));
			        item.add(linkArquivo);
            }
        };
	}
	
	public List<Remessa> getConfirmacoesParaEnvio() {
		return confirmacoesParaEnvio;
	}

	public void setConfirmacoesParaEnvio(List<Remessa> confirmacoesParaEnvio) {
		this.confirmacoesParaEnvio = confirmacoesParaEnvio;
	}
	
	@Override
	protected IModel<Confirmacao> getModel() {
		return new CompoundPropertyModel<Confirmacao>(confirmacao);
	}
}
