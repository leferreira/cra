package br.com.ieptbto.cra.page.base;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.arquivo.ListaArquivosPendentesPage;
import br.com.ieptbto.cra.page.relatorio.RelatorioArquivosPage;
import br.com.ieptbto.cra.page.relatorio.RelatorioInstituicoesCartoriosPage;
import br.com.ieptbto.cra.page.titulo.TitulosArquivoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.PeriodoDataUtil;

/**
 * 
 * @author Lefer
 *
 * @param <T>
 */
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HomePage<T extends AbstractEntidade<T>> extends BasePage<T> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private RemessaMediator remessaMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;
	private Arquivo arquivo;

	public HomePage() {
		super();
		this.arquivo = remessaMediator.arquivosPendentes(getUser().getInstituicao());
		
		carregarHomePage();
	}
	
	public HomePage(PageParameters parameters) {
		this.arquivo = remessaMediator.arquivosPendentes(getUser().getInstituicao());

		error(parameters.get("error"));
		carregarHomePage();
	}

	private void carregarHomePage() {
		linkAcessoRapidoGerarRelatorios();
		labelOrigemDestino(); 
		labelQuantidadeRemessasPendentes();
		labelQuantidadeCancelamentos();
		linkAcesseNossoSiteIEPTB();
		downloadOficioCorregedoria();
		linkConfirmacoesPendentes(); 
		listaConfirmacoesPendentes();
		linkCancelamentosPendentes();
		listaDesistenciaPendentes();
		listaCancelamentoPendentes();
		listaAutorizacaoCancelamentoPendentes();
	}
	
	private void linkAcessoRapidoGerarRelatorios() {
		if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			add(new Link<Arquivo>("acessoRapidoRelatorios"){

				/***/
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					setResponsePage(new RelatorioArquivosPage());
				}
				
			});
		} else {
			add(new Link<Arquivo>("acessoRapidoRelatorios"){

				/***/
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					setResponsePage(new RelatorioInstituicoesCartoriosPage());
				}
				
			});
		}
	}

	private void labelOrigemDestino() {
		if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA) ||
				getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			add(new Label("labelRemessas", "DESTINO"));
			add(new Label("labelCancelamentos", "DESTINO"));
		} else {
			add(new Label("labelRemessas", "ORIGEM"));
			add(new Label("labelCancelamentos", "ORIGEM"));
		}
	}
	
	private void labelQuantidadeRemessasPendentes() {
		int quantidade = 0;
		if (getConfirmacoesPendentes() != null) {
			quantidade = getConfirmacoesPendentes().size();
		}
		add(new Label("qtdRemessas", quantidade));
	}
	
	private void labelQuantidadeCancelamentos() {
		int quantidade = 0;
		if (getDesistenciaPendentes() != null) {
			quantidade = quantidade + getDesistenciaPendentes().size();
		}
		if (getCancelamentoPendentes() != null) {
			quantidade = quantidade + getCancelamentoPendentes().size();
		}
		if (getAutorizacaoCancelamentoPendentes() != null) {
			quantidade = quantidade + getAutorizacaoCancelamentoPendentes().size();
		}
		add(new Label("qtdCancelamentos", quantidade));
	}

	private void linkAcesseNossoSiteIEPTB() {
		add(new ExternalLink("acesseNossoSite", "http://www.ieptbto.com.br/"));
	}
	
	private void downloadOficioCorregedoria(){
		add(new Link<T>("donwloadOficio") {
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				File file = new File(ConfiguracaoBase.DIRETORIO_BASE + "Oficio_Corregedoria.pdf");
				IResourceStream resourceStream = new FileResourceStream(file);
	
				getRequestCycle().scheduleRequestHandlerAfterCurrent(
				        new ResourceStreamRequestHandler(resourceStream, file.getName()));
				
			}
		});
	}
	
	private void linkConfirmacoesPendentes() {
		add(new Link<Remessa>("arquivosConfirmacoesPendetes") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ListaArquivosPendentesPage(getUser(), getConfirmacoesPendentes()));
			}
		});
	}
	

	private void listaConfirmacoesPendentes() {
		add(new ListView<Remessa>("listConfirmacoes", getConfirmacoesPendentes()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoPage(remessa));
					}
				};
				linkArquivo.add(new Label("arquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
					String instituicao = remessa.getInstituicaoOrigem().getNomeFantasia();
					item.add(new Label("instituicao", instituicao.toUpperCase()));
					item.add(downloadAnexos(remessa));
				} else if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)){
					item.add(new Label("instituicao", municipioMediator.buscaMunicipioPorCodigoIBGE(remessa.getCabecalho().getCodigoMunicipio()).getNomeMunicipio().toUpperCase()));
					item.add(downloadAnexos(remessa));
				} else {
					item.add(new Label("instituicao", municipioMediator.buscaMunicipioPorCodigoIBGE(remessa.getCabecalho().getCodigoMunicipio()).getNomeMunicipio().toUpperCase()));
					item.add(new Label("downloadAnexos", StringUtils.EMPTY));
				}
				
				item.add(new Label("pendente", PeriodoDataUtil.diferencaDeDiasEntreData(remessa.getDataRecebimento().toDate(), new Date())));
				item.add(downloadArquivoTXT(remessa));
			}
			
			private Link<Remessa> downloadArquivoTXT(final Remessa remessa) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						try {
							File file = remessaMediator.baixarRemessaTXT(getUser(), remessa);
							IResourceStream resourceStream = new FileResourceStream(file);
	
							getRequestCycle().scheduleRequestHandlerAfterCurrent(
							        new ResourceStreamRequestHandler(resourceStream, remessa.getArquivo().getNomeArquivo()));
						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
						} catch (Exception e) {
							getFeedbackPanel().error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
						}
					}
				};
			}
			
			private Link<Remessa> downloadAnexos(final Remessa remessa) {
				List<Anexo> anexos = remessaMediator.verificarAnexosRemessa(remessa);
				Link<Remessa> linkAnexos = new Link<Remessa>("downloadAnexos") {
					
					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						
						try { 
							File file = remessaMediator.processarArquivosAnexos(getUser(), remessa);
							IResourceStream resourceStream = new FileResourceStream(file);
							
							getRequestCycle().scheduleRequestHandlerAfterCurrent(
									new ResourceStreamRequestHandler(resourceStream, file.getName()));
						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
						} catch (Exception e) {
							getFeedbackPanel().error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
						}
					}
				};
				
				if (anexos != null) {
					if (anexos.isEmpty()) {
						linkAnexos.setOutputMarkupId(false);
						linkAnexos.setVisible(false);
					}
				}
				return linkAnexos;
			}
		});
	}

	private void linkCancelamentosPendentes() {
		add(new Link<DesistenciaProtesto>("arquivosCancelamentosPendetes") {
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ListaArquivosPendentesPage(getDesistenciaPendentes(), getCancelamentoPendentes(), getAutorizacaoCancelamentoPendentes()));
			}
		});
	}
	
	private void listaCancelamentoPendentes() {
		add(new ListView<CancelamentoProtesto>("listaCancelamentos", getCancelamentoPendentes()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<CancelamentoProtesto> item) {
				final CancelamentoProtesto remessa = item.getModelObject();
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
					}
				};
				linkArquivo.add(new Label("desistencia", remessa.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA) ||
						getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					item.add(new Label("banco", municipioMediator.buscaMunicipioPorCodigoIBGE(remessa.getCabecalhoCartorio().getCodigoMunicipio()).getNomeMunicipio().toUpperCase()));
				} else if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
					String nomeFantasia = remessa.getRemessaCancelamentoProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia();
					item.add(new Label("banco", nomeFantasia.toUpperCase()));
				}
				
				item.add(new Label("dias", PeriodoDataUtil.diferencaDeDiasEntreData(remessa.getRemessaCancelamentoProtesto().getArquivo().getDataEnvio().toDate(), new Date())));
				item.add(downloadArquivoTXT(remessa));
			}
			
			private Link<Remessa> downloadArquivoTXT(final CancelamentoProtesto remessa) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = remessaMediator.baixarCancelamentoTXT(getUser(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, remessa.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
					}
				};
			}
		});
	}
	
	private void listaDesistenciaPendentes() {
		add(new ListView<DesistenciaProtesto>("listaDesistencias", getDesistenciaPendentes()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<DesistenciaProtesto> item) {
				final DesistenciaProtesto remessa = item.getModelObject();
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
					}
				};
				linkArquivo.add(new Label("desistencia", remessa.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA) ||
						getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					item.add(new Label("banco", municipioMediator.buscaMunicipioPorCodigoIBGE(remessa.getCabecalhoCartorio().getCodigoMunicipio()).getNomeMunicipio().toUpperCase()));
				} else if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
					String nomeFantasia = remessa.getRemessaDesistenciaProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia();
					item.add(new Label("banco", nomeFantasia.toUpperCase()));
				}
				
				item.add(new Label("dias", PeriodoDataUtil.diferencaDeDiasEntreData(remessa.getRemessaDesistenciaProtesto().getArquivo().getDataEnvio().toDate(), new Date())));
				item.add(downloadArquivoTXT(remessa));
			}
			
			private Link<Remessa> downloadArquivoTXT(final DesistenciaProtesto desistenciaProtesto) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = remessaMediator.baixarDesistenciaTXT(getUser(), desistenciaProtesto);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
					}
				};
			}
		});
	}

	private void listaAutorizacaoCancelamentoPendentes() {
		add(new ListView<AutorizacaoCancelamento>("listaAutorizacao", getAutorizacaoCancelamentoPendentes()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<AutorizacaoCancelamento> item) {
				final AutorizacaoCancelamento remessa = item.getModelObject();
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
					}
				};
				linkArquivo.add(new Label("desistencia", remessa.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA) ||
						getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					item.add(new Label("banco", municipioMediator.buscaMunicipioPorCodigoIBGE(remessa.getCabecalhoCartorio().getCodigoMunicipio()).getNomeMunicipio().toUpperCase()));
				} else if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
					String nomeFantasia = remessa.getRemessaAutorizacaoCancelamento().getArquivo().getInstituicaoEnvio().getNomeFantasia();
					item.add(new Label("banco", nomeFantasia.toUpperCase()));
				}
				
				item.add(new Label("dias", PeriodoDataUtil.diferencaDeDiasEntreData(remessa.getRemessaAutorizacaoCancelamento().getArquivo().getDataEnvio().toDate(), new Date())));
				item.add(downloadArquivoTXT(remessa));
			}
			
			private Link<Remessa> downloadArquivoTXT(final AutorizacaoCancelamento ac) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = remessaMediator.baixarAutorizacaoTXT(getUser(), ac);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, ac.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
					}
				};
			}
		});
	}
	
	private List<DesistenciaProtesto> getDesistenciaPendentes() {
		return arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto();
	}
	
	private List<CancelamentoProtesto> getCancelamentoPendentes() {
		return arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto();
	}
	
	private List<AutorizacaoCancelamento> getAutorizacaoCancelamentoPendentes() {
		return arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento();
	}
	
	private List<Remessa> getConfirmacoesPendentes() {
		return arquivo.getRemessas();
	}

	public Arquivo getArquivo() {
		return arquivo;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getTitulo() {
		return "CRA - Central de Remessa de Arquivos";
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}
}