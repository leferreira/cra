package br.com.ieptbto.cra.page.base;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
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
	private Arquivo confirmacoesPendentes;

	public HomePage() {
		super();
		carregarHomePage();
	}

	private void carregarHomePage() {
		this.usuario = getUser();
		this.confirmacoesPendentes = remessaMediator.confirmacoesPendentes(getUsuario().getInstituicao());
		labelArquivosPendentes();
		add(listaConfirmacoesPendentes());
		add(listaArquivosConfirmacoesPendentes());
		add(listaConfirmacoesPendentesDesistenciaProtesto());
	}

	private void labelArquivosPendentes() {
		if (!getConfirmacoesPendentes().isEmpty()) {
			warn("Você tem [ "+ getConfirmacoesPendentes().size() +" ] arquivo(s) pendentes");
		}
	}

	private Link<Remessa> listaArquivosConfirmacoesPendentes() {
		return new Link<Remessa>("arquivosConfirmacoesPendetes") {

			@Override
			public void onClick() {
				setResponsePage(new ListaArquivosPendentesPage(confirmacoesPendentes));
			}
		};
	}

	private ListView<Remessa> listaConfirmacoesPendentes() {
		return new ListView<Remessa>("listConfirmacoes", getConfirmacoesPendentes()) {

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(new Label("tipoArquivo", remessa.getArquivo().getTipoArquivo().getTipoArquivo().getConstante()));
				
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
						File file = remessaMediator.baixarRemessaTXT(getUser().getInstituicao(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				};
			}
		};
	}

	private ListView<DesistenciaProtesto> listaConfirmacoesPendentesDesistenciaProtesto() {
		return new ListView<DesistenciaProtesto>("listDesistencias", getConfirmacoesPendentesDesistenciaProtesto()) {

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
					item.add(new Label("banco", municipioMediator.buscaMunicipioPorCodigoIBGE(remessa.getCabecalhoCartorio().getCodigoMunicipio()).getNomeMunicipio()));
				} else if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
					String nomeFantasia = remessa.getRemessaDesistenciaProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia();
					item.add(new Label("banco", nomeFantasia));
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
	
	private List<DesistenciaProtesto> getConfirmacoesPendentesDesistenciaProtesto() {
		return confirmacoesPendentes.getRemessaDesistenciaProtesto().getDesistenciaProtesto();
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

	public List<Remessa> getConfirmacoesPendentes() {
		return confirmacoesPendentes.getRemessas();
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}
}