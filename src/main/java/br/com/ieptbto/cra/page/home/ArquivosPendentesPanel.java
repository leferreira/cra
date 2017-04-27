package br.com.ieptbto.cra.page.home;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.view.*;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.*;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosAutorizacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosDesistenciaPage;
import br.com.ieptbto.cra.util.PeriodoDataUtil;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArquivosPendentesPanel extends Panel {

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

	private List<ViewArquivoPendente> arquivosPendentes;
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

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ArquivosPendentesPage(usuario, arquivosPendentes));
			}
		};
	}

	private ListView<ViewArquivoPendente> listaConfirmacoesPendentes() {
		List<ViewArquivoPendente> remessas = getArquivosRemessas();
		add(new Label("totalRemessas", remessas.size()));

		return new ListView<ViewArquivoPendente>("listaRemessas", remessas) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ViewArquivoPendente> item) {
				final RemessaPendente arquivo = RemessaPendente.class.cast(item.getModelObject());
				
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoPage(arquivo.getIdRemessa_Remessa()));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo_Arquivo()));
				item.add(linkArquivo);

				TipoInstituicaoCRA tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
				if (tipoInstituicao.equals(TipoInstituicaoCRA.CARTORIO)) {
					item.add(new Label("instituicao", arquivo.getNomeFantasia_Instituicao()));
				} else if (tipoInstituicao.equals(TipoInstituicaoCRA.CRA) || tipoInstituicao.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)
							|| tipoInstituicao.equals(TipoInstituicaoCRA.CONVENIO)) {
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

			private Link<Remessa> downloadArquivoTXT(final RemessaPendente arquivo) {
				return new Link<Remessa>("downloadArquivo") {

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

			private Link<Remessa> downloadAnexos(final RemessaPendente arquivo) {
				Link<Remessa> linkAnexos = new Link<Remessa>("downloadAnexos") {

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

	private ListView<ViewArquivoPendente> listaDesistenciaCancelamentoAutorizacaoPendentes() {
		List<ViewArquivoPendente> desistenciasCancelamentos = getArquivosDesistenciasCancelamentos();
		add(new Label("totalDesistenciasCancelamentos", desistenciasCancelamentos.size()));

		return new ListView<ViewArquivoPendente>("listaDesistenciasCancelamentos", desistenciasCancelamentos) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ViewArquivoPendente> item) {
				final ViewArquivoPendente object = item.getModelObject();
				
				if (DesistenciaPendente.class.isInstance(object)) {
					final DesistenciaPendente arquivo = DesistenciaPendente.class.cast(object);
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosDesistenciaPage(arquivo.getIdDesistencia_DesistenciaProtesto()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo_Arquivo()));
					item.add(linkArquivo);
					item.add(new Link<Remessa>("downloadArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							File file = null;

							try {
								DesistenciaProtesto dp = desistenciaMediator.buscarDesistenciaPorPK(arquivo.getIdDesistencia_DesistenciaProtesto());
								file = downloadMediator.baixarDesistenciaTXT(usuario, dp);

								IResourceStream resourceStream = new FileResourceStream(file);
								getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, arquivo.getNomeArquivo_Arquivo()));
							} catch (InfraException ex) {
								error(ex.getMessage());
							} catch (Exception e) {
								logger.info(e.getMessage(), e);
								error("Não foi possível baixar o arquivo! Favor entrar em contato com a CRA...");
							}
						}
					});
					
				} else if (CancelamentoPendente.class.isInstance(object)) {
					final CancelamentoPendente arquivo = CancelamentoPendente.class.cast(object);
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosCancelamentoPage(arquivo.getIdCancelamento_CancelamentoProtesto()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo_Arquivo()));
					item.add(linkArquivo);
					item.add(new Link<Remessa>("downloadArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							File file = null;

							try {
								CancelamentoProtesto cp = cancelamentoMediator.buscarCancelamentoPorPK(arquivo.getIdCancelamento_CancelamentoProtesto());
								file = downloadMediator.baixarCancelamentoTXT(usuario, cp);

								IResourceStream resourceStream = new FileResourceStream(file);
								getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, arquivo.getNomeArquivo_Arquivo()));
							} catch (InfraException ex) {
								error(ex.getMessage());
							} catch (Exception e) {
								logger.info(e.getMessage(), e);
								error("Não foi possível baixar o arquivo! Favor entrar em contato com a CRA...");
							}
						}
					});
					
				} else if (AutorizacaoPendente.class.isInstance(object)) {
					final AutorizacaoPendente arquivo = AutorizacaoPendente.class.cast(object);
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosAutorizacaoCancelamentoPage(arquivo.getIdAutorizacao_AutorizacaoCancelamento()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo_Arquivo()));
					item.add(linkArquivo);
					item.add(new Link<Remessa>("downloadArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							File file = null;

							try {
								AutorizacaoCancelamento ac = autorizacaoMediator.buscarAutorizacaoPorPK(arquivo.getIdAutorizacao_AutorizacaoCancelamento());
								file = downloadMediator.baixarAutorizacaoTXT(usuario, ac);

								IResourceStream resourceStream = new FileResourceStream(file);
								getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, arquivo.getNomeArquivo_Arquivo()));
							} catch (InfraException ex) {
								error(ex.getMessage());
							} catch (Exception e) {
								logger.info(e.getMessage(), e);
								error("Não foi possível baixar o arquivo! Favor entrar em contato com a CRA...");
							}
						}
					});
				}
				TipoInstituicaoCRA tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
				if (tipoInstituicao.equals(TipoInstituicaoCRA.CRA) || tipoInstituicao.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)
						|| tipoInstituicao.equals(TipoInstituicaoCRA.CONVENIO)) {
					String municipio = object.getNomeMunicipio_Municipio();
					if (municipio.length() > 20) {
						municipio = municipio.substring(0, 19);
					}
					item.add(new Label("instituicao", municipio.toUpperCase()));
				} else if (tipoInstituicao.equals(TipoInstituicaoCRA.CARTORIO)) {
					item.add(new Label("instituicao", object.getNomeFantasia_Instituicao()));
				}
				item.add(new Label("dias", PeriodoDataUtil.diferencaDeDiasEntreData(object.getDataRecebimento_Arquivo(), new Date())));
			}
		};
	}

	private List<ViewArquivoPendente> getArquivosRemessas() {
		List<ViewArquivoPendente> arquivosRemessa = new ArrayList<ViewArquivoPendente>();

		for (ViewArquivoPendente arquivo : arquivosPendentes) {
			TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoPorConstante(arquivo.getTipo_Arquivo());

			if (tipoArquivo.equals(TipoArquivoFebraban.REMESSA)) {
				arquivosRemessa.add(arquivo);
			}
		}
		return arquivosRemessa;
	}

	private List<ViewArquivoPendente> getArquivosDesistenciasCancelamentos() {
		List<ViewArquivoPendente> arquivosDpCP = new ArrayList<ViewArquivoPendente>();

		for (ViewArquivoPendente arquivo : arquivosPendentes) {
			TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoPorConstante(arquivo.getTipo_Arquivo());

			if (tipoArquivo.equals(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO) || tipoArquivo.equals(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO)
							|| tipoArquivo.equals(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO)) {
				arquivosDpCP.add(arquivo);
			}
		}
		return arquivosDpCP;
	}
}