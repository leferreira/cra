package br.com.ieptbto.cra.page.home;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.view.ViewArquivosPendentes;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.enumeration.regra.TipoInstituicaoSistema;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.AutorizacaoCancelamentoMediator;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosAutorizacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosDesistenciaPage;
import br.com.ieptbto.cra.util.PeriodoDataUtil;

public class ArquivosPendentesPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ArquivosPendentesPanel.class);

	@SpringBean
	ArquivoMediator arquivoMediator;
	@SpringBean
	DownloadMediator downloadMediator;
	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	DesistenciaProtestoMediator desistenciaMediator;
	@SpringBean
	CancelamentoProtestoMediator cancelamentoMediator;
	@SpringBean
	AutorizacaoCancelamentoMediator autorizacaoMediator;

	private List<ViewArquivosPendentes> arquivosPendentes;
	private Usuario usuario;

	public ArquivosPendentesPanel(String id, Usuario usuario) {
		super(id);
		this.usuario = usuario;
		this.arquivosPendentes = arquivoMediator.consultarViewArquivosPendentes(usuario.getInstituicao());
		add(linkArquivosPendentes());
		add(listaConfirmacoesPendentes());
		add(listaDesistenciaCancelamentoAutorizacaoPendentes());
	}

	private Link<Remessa> linkArquivosPendentes() {
		return new Link<Remessa>("arquivosPendentes") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ArquivosPendentesPage(usuario, arquivosPendentes));
			}
		};
	}

	private ListView<ViewArquivosPendentes> listaConfirmacoesPendentes() {
		List<ViewArquivosPendentes> remessas = getArquivosRemessas();
		add(new Label("totalRemessas", remessas.size()));

		return new ListView<ViewArquivosPendentes>("listaRemessas", remessas) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ViewArquivosPendentes> item) {
				final ViewArquivosPendentes arquivo = item.getModelObject();
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoPage(arquivo.getIdRemessa_Remessa()));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo_Arquivo()));
				item.add(linkArquivo);

				TipoInstituicaoSistema tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
				if (tipoInstituicao.equals(TipoInstituicaoSistema.CARTORIO)) {
					item.add(new Label("instituicao", arquivo.getNomeFantasia_Instituicao()));
				} else if (tipoInstituicao.equals(TipoInstituicaoSistema.CRA) || tipoInstituicao.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
							|| tipoInstituicao.equals(TipoInstituicaoSistema.CONVENIO)) {
					String municipio = arquivo.getNomeMunicipio_Municipio();
					if (municipio.length() > 20) {
						municipio = municipio.substring(0, 19);
					}
					item.add(new Label("instituicao", municipio.toUpperCase()));
				}
				
				item.add(new Label("dias", PeriodoDataUtil.diferencaDeDiasEntreData(arquivo.getDataRecebimento_Arquivo(), new Date())));
				item.add(downloadAnexos(arquivo));
				item.add(downloadArquivoTXT(arquivo));
			}

			private Link<Remessa> downloadArquivoTXT(final ViewArquivosPendentes arquivo) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						Remessa remessa = remessaMediator.buscarRemessaPorPK(arquivo.getIdRemessa_Remessa());

						try {
							File file = downloadMediator.baixarRemessaTXT(usuario, remessa);
							IResourceStream resourceStream = new FileResourceStream(file);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, remessa.getArquivo().getNomeArquivo()));
						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							logger.info(e.getMessage(), e);
							error("Não foi possível baixar o arquivo! Favor entrar em contato com a CRA...");
						}
					}
				};
			}

			private Link<Remessa> downloadAnexos(final ViewArquivosPendentes arquivo) {
				Link<Remessa> linkAnexos = new Link<Remessa>("downloadAnexos") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						Remessa remessa = remessaMediator.buscarRemessaPorPK(arquivo.getIdRemessa_Remessa());

						try {
							File file = remessaMediator.processarArquivosAnexos(usuario, remessa);
							IResourceStream resourceStream = new FileResourceStream(file);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, file.getName()));
						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							logger.info(e.getMessage(), e);
							error("Não foi possível baixar os anexos! Favor entrar em contato com a CRA...");
						}
					}
				};
				if (!arquivo.isDocumentosAnexos_Anexo()) {
					linkAnexos.setOutputMarkupId(false);
					linkAnexos.setVisible(false);
				}
				return linkAnexos;
			}
		};
	}

	private ListView<ViewArquivosPendentes> listaDesistenciaCancelamentoAutorizacaoPendentes() {
		List<ViewArquivosPendentes> desistenciasCancelamentos = getArquivosDesistenciasCancelamentos();
		add(new Label("totalDesistenciasCancelamentos", desistenciasCancelamentos.size()));

		return new ListView<ViewArquivosPendentes>("listaDesistenciasCancelamentos", desistenciasCancelamentos) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ViewArquivosPendentes> item) {
				final ViewArquivosPendentes arquivo = item.getModelObject();
				final TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoEnum(arquivo.getTipo_Arquivo());

				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						
						if (TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)) {
							DesistenciaProtesto dp = desistenciaMediator.buscarDesistenciaPorPK(arquivo.getIdDesistencia_DesistenciaProtesto());
							setResponsePage(new TitulosDesistenciaPage(dp));
						} else if (TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
							CancelamentoProtesto cp = cancelamentoMediator.buscarCancelamentoPorPK(arquivo.getIdCancelamento_CancelamentoProtesto());
							setResponsePage(new TitulosCancelamentoPage(cp));
						} else if (TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
							AutorizacaoCancelamento ac = autorizacaoMediator.buscarAutorizacaoPorPK(arquivo.getIdAutorizacao_AutorizacaoCancelamento());
							setResponsePage(new TitulosAutorizacaoCancelamentoPage(ac));
						}
					}
				};
				linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo_Arquivo()));
				item.add(linkArquivo);

				TipoInstituicaoSistema tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
				if (tipoInstituicao.equals(TipoInstituicaoSistema.CRA) || tipoInstituicao.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
								|| tipoInstituicao.equals(TipoInstituicaoSistema.CONVENIO)) {
					String municipio = arquivo.getNomeMunicipio_Municipio();
					if (municipio.length() > 20) {
						municipio = municipio.substring(0, 19);
					}
					item.add(new Label("instituicao", municipio.toUpperCase()));
				} else if (tipoInstituicao.equals(TipoInstituicaoSistema.CARTORIO)) {
					item.add(new Label("instituicao", arquivo.getNomeFantasia_Instituicao()));
				}
				item.add(new Label("dias", PeriodoDataUtil.diferencaDeDiasEntreData(arquivo.getDataRecebimento_Arquivo(), new Date())));
				item.add(downloadArquivoTXT(tipoArquivo, arquivo));
			}

			private Link<Remessa> downloadArquivoTXT(final TipoArquivoFebraban tipoArquivo, final ViewArquivosPendentes arquivo) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = null;

						try {
							if (TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)) {
								DesistenciaProtesto dp = desistenciaMediator.buscarDesistenciaPorPK(arquivo.getIdDesistencia_DesistenciaProtesto());
								file = downloadMediator.baixarDesistenciaTXT(usuario, dp);
							} else if (TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
								CancelamentoProtesto cp = cancelamentoMediator.buscarCancelamentoPorPK(arquivo.getIdCancelamento_CancelamentoProtesto());
								file = downloadMediator.baixarCancelamentoTXT(usuario, cp);
							} else if (TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
								AutorizacaoCancelamento ac = autorizacaoMediator.buscarAutorizacaoPorPK(arquivo.getIdAutorizacao_AutorizacaoCancelamento());
								file = downloadMediator.baixarAutorizacaoTXT(usuario, ac);
							}

							IResourceStream resourceStream = new FileResourceStream(file);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, arquivo.getNomeArquivo_Arquivo()));
						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							logger.info(e.getMessage(), e);
							error("Não foi possível baixar o arquivo! Favor entrar em contato com a CRA...");
						}
					}
				};
			}
		};
	}

	private List<ViewArquivosPendentes> getArquivosRemessas() {
		List<ViewArquivosPendentes> arquivosRemessa = new ArrayList<ViewArquivosPendentes>();

		for (ViewArquivosPendentes arquivo : arquivosPendentes) {
			TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoEnum(arquivo.getTipo_Arquivo());

			if (tipoArquivo.equals(TipoArquivoFebraban.REMESSA)) {
				arquivosRemessa.add(arquivo);
			}
		}
		return arquivosRemessa;
	}

	private List<ViewArquivosPendentes> getArquivosDesistenciasCancelamentos() {
		List<ViewArquivosPendentes> arquivosDpCP = new ArrayList<ViewArquivosPendentes>();

		for (ViewArquivosPendentes arquivo : arquivosPendentes) {
			TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoEnum(arquivo.getTipo_Arquivo());

			if (tipoArquivo.equals(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO) || tipoArquivo.equals(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO)
							|| tipoArquivo.equals(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO)) {
				arquivosDpCP.add(arquivo);
			}
		}
		return arquivosDpCP;
	}
}