package br.com.ieptbto.cra.page.batimento;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
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
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfirmacaoMediator;
import br.com.ieptbto.cra.mediator.RetornoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosDoArquivoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

@SuppressWarnings( { "rawtypes","unused" })
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class ConfirmacaoRetornoPage extends BasePage<Batimento> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ConfirmacaoRetornoPage.class);
	
	@SpringBean
	RetornoMediator retornoMediator;
	@SpringBean
	ConfirmacaoMediator confirmacaoMediator;
	
	private Batimento batimento;
	private List<Remessa> retornosParaEnvio;
	private List<Remessa> confirmacoesParaEnvio;
	
	private ListView<Remessa> retornos;
	private ListView<Remessa> confirmacao;

	public ConfirmacaoRetornoPage(){
		this.batimento = new Batimento();
		carregarGuiaConfirmacao();
		carregarGuiaRetorno();
	}
	
	private void carregarGuiaRetorno(){
		setRetornosParaEnvio(retornoMediator.buscarRetornosConfirmados());
        Form formRetorno = new Form("form"){
            /***/
			private static final long serialVersionUID = 1L;
			@Override
            protected void onSubmit(){
				try{
					
					if (!getRetornosParaEnvio().isEmpty()){
						retornoMediator.gerarRetornos(getUser(), getRetornosParaEnvio());
					} else 
						throw new InfraException("Não há retornos pendentes para envio.");
						
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar o batimento! Entre em contato com a CRA.");
				}
            }
        };
        formRetorno.add(carregarListaRetornos());
        formRetorno.add(botaoRetorno());
        add(formRetorno);
	}
	
	
	private ListView<Remessa> carregarListaRetornos(){
		return retornos = new ListView<Remessa>("retornos", getRetornosParaEnvio()){
			/***/
			private static final long serialVersionUID = 1L;
			@Override
			protected void populateItem(ListItem<Remessa> item){
				final Remessa retorno = item.getModelObject();
				item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
				item.add(new Label("instituicaoOrigem.nomeFantasia", retorno.getInstituicaoOrigem().getNomeFantasia()));
				item.add(new LabelValorMonetario("valorPagos", retornoMediator.buscarValorDeTitulosPagos(retorno)));
				Link linkArquivo = new Link("linkArquivo") {
					/***/
					private static final long serialVersionUID = 1L;
					
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
					/***/
					private static final long serialVersionUID = 1L;
					
					@Override
					public void onClick() {
						retornoMediator.removerConfirmado(retorno);
						setResponsePage(new ConfirmacaoRetornoPage());
					}
				};
			}
		};
	}

	private void carregarGuiaConfirmacao(){
		setConfirmacoesParaEnvio(confirmacaoMediator.buscarConfirmacoesPendentesDeEnvio());
		Form formConfirmacao = new Form("formConfirmacao"){
            /***/
			private static final long serialVersionUID = 1L;
			@Override
            protected void onSubmit(){
				
				try{
					
					if (!getConfirmacoesParaEnvio().isEmpty())
						confirmacaoMediator.gerarConfirmacoes(getUser(), getConfirmacoesParaEnvio());
					else 
						throw new InfraException("Não há confirmações pendentes para envio.");
						
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível gerar a confirmação! Entre em contato com a CRA.");
				}
            }
        };
        formConfirmacao.add(carregarListaConfirmacao());
        formConfirmacao.add(botaoConfirmacao());
        add(formConfirmacao);
	}
	
	
	private ListView<Remessa> carregarListaConfirmacao(){
		return confirmacao = new ListView<Remessa>("confirmacao", getConfirmacoesParaEnvio()){
			/***/
        	private static final long serialVersionUID = 1L;
				@Override
	            protected void populateItem(ListItem<Remessa> item){
					final Remessa retorno = item.getModelObject();
					item.add(new Label("arquivo.dataEnvioConfirmacao", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
					item.add(new Label("instituicaoOrigem.nomeFantasiaConfirmacao", retorno.getInstituicaoOrigem().getNomeFantasia()));
					item.add(new Label("instituicaoDestino.nomeFantasiaConfirmacao", retorno.getInstituicaoDestino().getNomeFantasia()));
					Link linkArquivo = new Link("linkArquivoConfirmacao") {
			            /***/
						private static final long serialVersionUID = 1L;

						public void onClick() {
			            	setResponsePage(new TitulosDoArquivoPage(retorno));  
			            }
			        };
			        linkArquivo.add(new Label("arquivo.nomeArquivoConfirmacao", retorno.getArquivo().getNomeArquivo()));
			        item.add(linkArquivo);
            }
        };
	}
	
	public List<Remessa> getRetornosParaEnvio() {
		return retornosParaEnvio;
	}

	public void setRetornosParaEnvio(List<Remessa> retornosParaEnvio) {
		this.retornosParaEnvio = retornosParaEnvio;
	}

	public List<Remessa> getConfirmacoesParaEnvio() {
		return confirmacoesParaEnvio;
	}

	public void setConfirmacoesParaEnvio(List<Remessa> confirmacoesParaEnvio) {
		this.confirmacoesParaEnvio = confirmacoesParaEnvio;
	}
	
	@SuppressWarnings("serial")
	private Component botaoConfirmacao() {
		return new Button("botaoConfirmacao") {
		};
	}
	
	@SuppressWarnings("serial")
	private Component botaoRetorno() {
		return new Button("botaoRetorno") {
		};
	}
	
	@Override
	protected IModel<Batimento> getModel() {
		return new CompoundPropertyModel<Batimento>(batimento);
	}

}
