package br.com.ieptbto.cra.page.base;

import java.io.File;
import java.util.Date;
import java.util.List;

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
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.arquivo.ListaArquivosPendentesPage;
import br.com.ieptbto.cra.page.titulo.TitulosArquivoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.PeriodoDataUtil;

/**
 * 
 * @author Lefer
 *
 * @param <T>
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HomePage<T extends AbstractEntidade<T>> extends BasePage<T> {

	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	private Usuario usuario;
	private Arquivo arquivo;

	public HomePage() {
		super();
		carregarHomePage();
	}

	private void carregarHomePage() {
		this.usuario = getUser();
		this.arquivo = remessaMediator.confirmacoesPendentes(getUsuario().getInstituicao());
//		carregarComunicadoModal();
		labelOrigemDestino(); 
		labelQuantidadeConfirmacoes();
		labelQuantidadeCancelamentos();

		add(linkAcesseNossoSiteIEPTB());
		add(downloadOficioCorregedoria());
		add(linkConfirmacoesPendentes());
		add(linkCancelamentosPendentes());
		add(listaConfirmacoesPendentes());
		add(listaDesistenciaCancelamentoPendentes());
	}
	
//	private void carregarComunicadoModal() {
//		final ModalWindow modalComunicado = new ModalWindow("modalComunicado");
//		modalComunicado.setPageCreator(new ModalWindow.PageCreator() {
//            /***/
//			private static final long serialVersionUID = 1L;
//
//			@Override
//            public Page createPage() {
//                return new ComunicadoModal(HomePage.this.getPageReference(), modalComunicado);
//            }
//	    });
//		modalComunicado.setResizable(false);
//		modalComunicado.setAutoSize(false);
//		modalComunicado.setInitialWidth(75);
//		modalComunicado.setInitialHeight(560); 
//		modalComunicado.setMinimalWidth(75); 
//		modalComunicado.setMinimalHeight(560);
//        modalComunicado.setWidthUnit("%");
//        modalComunicado.setHeightUnit("px");
//        add(modalComunicado);
//		
//		AjaxLink<?> openModal = new AjaxLink<Void>("showModal"){
//            /***/
//			private static final long serialVersionUID = 1L;
//
//			@Override
//            public void onClick(AjaxRequestTarget target){
//            	modalComunicado.show(target);
//            }
//		};
//		if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
//			openModal.setMarkupId("showModal");
//		}
//		add(openModal);
//	}


	private void labelQuantidadeCancelamentos() {
		int quantidade = 0;
		if (getDesistenciaCancelamentoPendentes() != null) {
			quantidade = getDesistenciaCancelamentoPendentes().size();
		}
		add(new Label("qtdCancelamentos", quantidade));
	}

	private void labelQuantidadeConfirmacoes() {
		int quantidade = 0;
		if (getConfirmacoesPendentes() != null) {
			quantidade = getConfirmacoesPendentes().size();
		}
		add(new Label("qtdRemessas", quantidade));
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
	
	private ExternalLink linkAcesseNossoSiteIEPTB() {
		return new ExternalLink("acesseNossoSite", "http://www.ieptbto.com.br/");
	}
	
	private Link<T> downloadOficioCorregedoria(){
		return new Link<T>("donwloadOficio") {
			@Override
			public void onClick() {
				File file = new File(ConfiguracaoBase.DIRETORIO_BASE + "Oficio_Corregedoria.pdf");
				IResourceStream resourceStream = new FileResourceStream(file);
	
				getRequestCycle().scheduleRequestHandlerAfterCurrent(
				        new ResourceStreamRequestHandler(resourceStream, file.getName()));
				
			}
		};
	}
	
	private Link<Remessa> linkConfirmacoesPendentes() {
		return new Link<Remessa>("arquivosConfirmacoesPendetes") {

			@Override
			public void onClick() {
				setResponsePage(new ListaArquivosPendentesPage(getUser(), getConfirmacoesPendentes()));
			}
		};
	}
	

	private ListView<Remessa> listaConfirmacoesPendentes() {
		return new ListView<Remessa>("listConfirmacoes", getConfirmacoesPendentes()) {

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoPage(remessa));
					}
				};
				linkArquivo.add(new Label("arquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
					String nomeFantasia = remessa.getInstituicaoOrigem().getNomeFantasia();
					item.add(new Label("instituicao", nomeFantasia.toUpperCase()));
				} else {
					item.add(new Label("instituicao", municipioMediator.buscaMunicipioPorCodigoIBGE(remessa.getCabecalho().getCodigoMunicipio()).getNomeMunicipio().toUpperCase()));
				}
				
				item.add(new Label("pendente", PeriodoDataUtil.diferencaDeDiasEntreData(remessa.getDataRecebimento().toDate(), new Date())));
				item.add(downloadArquivoTXT(remessa));
			}
			
			private Link<Remessa> downloadArquivoTXT(final Remessa remessa) {
				return new Link<Remessa>("downloadArquivo") {

					@Override
					public void onClick() {
						try {
							File file = remessaMediator.baixarRemessaTXT(getUser(), remessa);
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
			}
		};
	}

	private Link<DesistenciaProtesto> linkCancelamentosPendentes() {
		return new Link<DesistenciaProtesto>("arquivosCancelamentosPendetes") {
			
			@Override
			public void onClick() {
				setResponsePage(new ListaArquivosPendentesPage(getDesistenciaCancelamentoPendentes()));
			}
		};
	}

	private ListView<DesistenciaProtesto> listaDesistenciaCancelamentoPendentes() {
		return new ListView<DesistenciaProtesto>("listDesistencias", getDesistenciaCancelamentoPendentes()) {

			@Override
			protected void populateItem(ListItem<DesistenciaProtesto> item) {
				final DesistenciaProtesto remessa = item.getModelObject();
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

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

					@Override
					public void onClick() {
						File file = remessaMediator.baixarRemessaTXT(getUser(), desistenciaProtesto);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				};
			}
		};
	}
	
	private List<DesistenciaProtesto> getDesistenciaCancelamentoPendentes() {
		return arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto();
	}
	
	private List<Remessa> getConfirmacoesPendentes() {
		return arquivo.getRemessas();
	}

	public HomePage(PageParameters parameters) {
		error(parameters.get("error"));
		carregarHomePage();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitulo() {
		return "CRA - Central de Remessa de Arquivos";
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}
}