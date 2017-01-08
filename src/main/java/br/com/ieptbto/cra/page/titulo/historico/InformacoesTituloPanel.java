package br.com.ieptbto.cra.page.titulo.historico;

import java.io.File;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;

/**
 * @author Thasso Araújo
 *
 */
public class InformacoesTituloPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	TituloMediator tituloMediator;
	@SpringBean
	MunicipioMediator municipioMediator;

	private TituloRemessa tituloRemessa;
	private Anexo anexo;
	private Usuario usuario;

	public InformacoesTituloPanel(String id, IModel<TituloRemessa> model, Usuario usuario) {
		super(id, model);
		this.tituloRemessa = model.getObject();
		this.anexo = tituloMediator.buscarAnexo(tituloRemessa);

		add(numeroProtocoloCartorio());
		add(dataProtocolo());
		add(codigoCartorio());
		add(labelAgenciaCodigoCedente());
		add(dataOcorrencia());
		add(irregularidade());
		add(codigoMunicipio());
		add(pracaProtesto());
		add(municipioDestino());
		add(status());
		add(nomeSacadorVendedor());
		add(documentoSacador());
		add(ufSacadorVendedor());
		add(cepSacadorVendedor());
		add(cidadeSacadorVendedor());
		add(enderecoSacadorVendedor());
		add(nomeDevedor());
		add(documentoDevedor());
		add(ufDevedor());
		add(cepDevedor());
		add(cidadeDevedor());
		add(enderecoDevedor());
		add(numeroTitulo());
		add(portador());
		add(agencia());
		add(nossoNumero());
		add(especieTitulo());
		add(dataEmissaoTitulo());
		add(dataVencimentoTitulo());
		add(valorTitulo());
		add(saldoTitulo());
		add(valorCustaCartorio());
		add(valorDemaisDespesas());
		add(numeroControleDevedor());
		add(linkAnexos());
		add(labelAlinea());
		add(campoAlinea());
		add(labelDocumentosAnexos());
	}

	private Component labelAlinea() {
		Label labelAlinea = new Label("labelAlinea", "ALÍNEA");
		labelAlinea.setVisible(false);
		if (tituloRemessa.getComplementoRegistro() != null) {
			if (tituloRemessa.getComplementoRegistro().trim().length() == 2) {
				labelAlinea.setVisible(true);
			}
		}
		return labelAlinea;
	}

	private Label campoAlinea() {
		Label campoAlinea = new Label("campoAlinea", "");
		campoAlinea.setVisible(false);
		if (tituloRemessa.getComplementoRegistro() != null) {
			if (tituloRemessa.getComplementoRegistro().trim().length() == 2) {
				campoAlinea = new Label("campoAlinea", tituloRemessa.getComplementoRegistro().trim());
				campoAlinea.setVisible(true);
			}
		}
		return campoAlinea;
	}

	private Component labelDocumentosAnexos() {
		Label labelDocumentos = new Label("labelDocumentosAnexos", "DOCUMENTOS ANEXOS");
		labelDocumentos.setVisible(false);
		if (anexo != null) {
			labelDocumentos.setVisible(true);
		}
		return labelDocumentos;
	}

	private Link<Void> linkAnexos() {
		Link<Void> linkAnexos = new Link<Void>("linkAnexos") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				try {
					File file = remessaMediator.decodificarAnexoTitulo(usuario, tituloRemessa, anexo);
					IResourceStream resourceStream = new FileResourceStream(file);

					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, file.getName()));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
				}

			}
		};
		if (anexo == null) {
			linkAnexos.setVisible(false);
		}
		return linkAnexos;
	}

	private Label codigoCartorio() {
		Integer codigoCartorio = 0;
		if (tituloRemessa.getConfirmacao() != null) {
			codigoCartorio = tituloRemessa.getConfirmacao().getCodigoCartorio();
		}
		if (tituloRemessa.getRetorno() != null) {
			codigoCartorio = tituloRemessa.getRetorno().getCodigoCartorio();
		}
		return new Label("codigoCartorio", new Model<String>(codigoCartorio.toString()));
	}

	private Label dataOcorrencia() {
		LocalDate dataOcorrencia = tituloRemessa.getRemessa().getCabecalho().getDataMovimento();

		if (tituloRemessa.getConfirmacao() != null) {
			dataOcorrencia = tituloRemessa.getConfirmacao().getDataOcorrencia();
		}
		if (tituloRemessa.getRetorno() != null) {
			dataOcorrencia = tituloRemessa.getRetorno().getDataOcorrencia();
		}
		return new Label("dataOcorrencia", DataUtil.localDateToString(dataOcorrencia));
	}

	private Label irregularidade() {
		CodigoIrregularidade codigoIrregularidade = null;
		String irregularidade = StringUtils.EMPTY;
		if (tituloRemessa.getConfirmacao() != null) {
			irregularidade = tituloRemessa.getConfirmacao().getCodigoIrregularidade();
		}
		if (tituloRemessa.getRetorno() != null) {
			if (tituloRemessa.getRetorno().getTipoOcorrencia()
							.equals(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante())) {
				irregularidade = tituloRemessa.getRetorno().getCodigoIrregularidade();
			}
		}
		codigoIrregularidade = CodigoIrregularidade.getIrregularidade(irregularidade);
		if (codigoIrregularidade == null) {
			return new Label("irregularidade", new Model<String>("00"));
		}
		return new Label("irregularidade", new Model<String>(codigoIrregularidade.getMotivo().toUpperCase()));
	}

	private Label codigoMunicipio() {
		return new Label("codigoMunicipio", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getCodigoMunicipio()));
	}

	private Label numeroProtocoloCartorio() {
		String numeroProtocolo = StringUtils.EMPTY;
		if (tituloRemessa.getConfirmacao() != null) {
			numeroProtocolo = tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio();
		}
		return new Label("numeroProtocoloCartorio", new Model<String>(numeroProtocolo));
	}

	private Label dataProtocolo() {
		String dataProtocolo = StringUtils.EMPTY;
		if (tituloRemessa.getConfirmacao() != null) {
			dataProtocolo = DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataProtocolo());
		}
		return new Label("dataProtocolo", new Model<String>(dataProtocolo));
	}

	private Label pracaProtesto() {
		return new Label("pracaProtesto", new Model<String>(tituloRemessa.getPracaProtesto()));
	}

	private Label municipioDestino() {
		Municipio municipio = tituloRemessa.getRemessa().getInstituicaoDestino().getMunicipio();
		municipio = municipioMediator.carregarMunicipio(municipio);
		return new Label("municipioDestino", new Model<String>(municipio.getNomeMunicipio().toUpperCase()));
	}

	private Label status() {
		return new Label("situacaoTitulo", new Model<String>(tituloRemessa.getSituacaoTitulo()));
	}

	private Label numeroTitulo() {
		return new Label("numeroTitulo", new Model<String>(tituloRemessa.getNumeroTitulo()));
	}

	private Label labelAgenciaCodigoCedente() {
		return new Label("agenciaCodigoCedente", tituloRemessa.getAgenciaCodigoCedente());
	}

	private Label portador() {
		return new Label("portador", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getNomePortador()));
	}

	private Label agencia() {
		return new Label("agencia", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getAgenciaCentralizadora()));
	}

	private Label nossoNumero() {
		return new Label("nossoNumero", new Model<String>(tituloRemessa.getNossoNumero()));
	}

	private Label especieTitulo() {
		return new Label("especieTitulo", new Model<String>(tituloRemessa.getEspecieTitulo()));
	}

	private Label dataEmissaoTitulo() {
		return new Label("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(tituloRemessa.getDataEmissaoTitulo())));
	}

	private Label dataVencimentoTitulo() {
		return new Label("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(tituloRemessa.getDataVencimentoTitulo())));
	}

	private Label valorTitulo() {
		Label textField = new Label("valorTitulo", new Model<String>("R$ " + tituloRemessa.getValorTitulo().toString()));
		return textField;
	}

	private Label saldoTitulo() {
		Label textField = new Label("saldoTitulo", new Model<String>("R$ " + tituloRemessa.getSaldoTitulo().toString()));
		return textField;
	}

	private Label valorCustaCartorio() {
		BigDecimal valorCustaCartorio = BigDecimal.ZERO;
		if (tituloRemessa.getConfirmacao() != null) {
			valorCustaCartorio = tituloRemessa.getConfirmacao().getValorCustaCartorio();
		}
		if (tituloRemessa.getRetorno() != null) {
			valorCustaCartorio = tituloRemessa.getRetorno().getValorCustaCartorio();
		}
		return new LabelValorMonetario<BigDecimal>("valorCustaCartorio", valorCustaCartorio);
	}

	private Label valorDemaisDespesas() {
		BigDecimal valorDemaisDespesas = BigDecimal.ZERO;
		if (tituloRemessa.getConfirmacao() != null) {
			valorDemaisDespesas = tituloRemessa.getConfirmacao().getValorDemaisDespesas();
		}
		if (tituloRemessa.getRetorno() != null) {
			valorDemaisDespesas = tituloRemessa.getRetorno().getValorDemaisDespesas();
		}
		return new LabelValorMonetario<BigDecimal>("valorDemaisDespesas", valorDemaisDespesas);
	}

	private Label nomeSacadorVendedor() {
		return new Label("nomeSacadorVendedor", new Model<String>(tituloRemessa.getNomeSacadorVendedor()));
	}

	private Label documentoSacador() {
		return new Label("documentoSacador", new Model<String>(tituloRemessa.getDocumentoSacador()));
	}

	private Label ufSacadorVendedor() {
		return new Label("ufSacadorVendedor", new Model<String>(tituloRemessa.getUfSacadorVendedor()));
	}

	private Label cepSacadorVendedor() {
		return new Label("cepSacadorVendedor", new Model<String>(tituloRemessa.getCepSacadorVendedor()));
	}

	private Label cidadeSacadorVendedor() {
		return new Label("cidadeSacadorVendedor", new Model<String>(tituloRemessa.getCidadeSacadorVendedor()));
	}

	private Label enderecoSacadorVendedor() {
		return new Label("enderecoSacadorVendedor", new Model<String>(tituloRemessa.getEnderecoSacadorVendedor()));
	}

	private Label nomeDevedor() {
		return new Label("nomeDevedor", new Model<String>(tituloRemessa.getNomeDevedor()));
	}

	private Label documentoDevedor() {
		return new Label("documentoDevedor", new Model<String>(tituloRemessa.getNumeroIdentificacaoDevedor()));
	}

	private Label ufDevedor() {
		return new Label("ufDevedor", new Model<String>(tituloRemessa.getUfDevedor()));
	}

	private Label cepDevedor() {
		return new Label("cepDevedor", new Model<String>(tituloRemessa.getCepDevedor()));
	}

	private Label cidadeDevedor() {
		return new Label("cidadeDevedor", new Model<String>(tituloRemessa.getCidadeDevedor()));
	}

	private Label enderecoDevedor() {
		return new Label("enderecoDevedor", new Model<String>(tituloRemessa.getEnderecoDevedor()));
	}

	private Label numeroControleDevedor() {
		return new Label("numeroControleDevedor", new Model<String>(tituloRemessa.getNumeroControleDevedor().toString()));
	}
}