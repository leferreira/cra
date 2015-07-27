package br.com.ieptbto.cra.webservice.dao;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.HistoricoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class HistoricoOcorrenciaService {

	@Autowired
	private TituloMediator tituloMediator;
	@Autowired
	private HistoricoMediator historicoMediator;
	
	public static final Logger logger = Logger.getLogger(HistoricoOcorrenciaServiceImpl.class);
	private String resposta;
	private TituloRemessa tituloRemessa;
	private TipoOcorrencia tipoOcorrencia;
	private LocalDate dataOcorrencia;
	private String codigoPortador;
	private String nossoNumero;
	private String numeroTitulo;
	
	public String processarOcorrencia(String codigoPortador, String nossoNumero, String numeroTitulo, 
			String ocorrencia, String dataOcorrencia) {
		this.codigoPortador = codigoPortador;
		this.nossoNumero = nossoNumero;
		this.numeroTitulo = numeroTitulo;
		this.dataOcorrencia = DataUtil.stringToLocalDate(dataOcorrencia);
		
		try {
			buscarTituloReferente();
			buscarTipoOcorrencia(ocorrencia);

			historicoMediator.salvarHistoricoOcorrencia(getTituloRemessa(), getTipoOcorrencia(), getDataOcorrencia());
		} catch (InfraException ex) {
			logger.info(ex.getMessage());
			setResposta(ex.getMessage());
		} catch (Exception ex) {
			logger.info(ex.getMessage());
			setResposta(MensagemHistoricoOcorrencia.ERRO_DE_PRECESSAMENTO_CRA.getMensagem());
		}
		return getResposta();
	}

	private void buscarTituloReferente(){
		TituloRemessa titulo = new TituloRemessa();
		titulo.setCodigoPortador(getCodigoPortador());
		titulo.setNumeroTitulo(getNumeroTitulo());
		titulo.setNossoNumero(getNossoNumero());
		
		setTituloRemessa(tituloMediator.buscarTituloPorChave(titulo));
	}

	private void buscarTipoOcorrencia(String ocorrencia) {
		TipoOcorrencia tipoOcorrencia = TipoOcorrencia.getTipoOcorrencia(ocorrencia);
		if (tipoOcorrencia == null) {
			throw new InfraException(MensagemHistoricoOcorrencia.OCORRENCIA_NAO_ENCONTRADA_OU_NAO_EXISTE.getMensagem());
		}
		setTipoOcorrencia(tipoOcorrencia);
	}
	
	public String getResposta() {
		if (resposta == null) {
			this.resposta = MensagemHistoricoOcorrencia.OCORRENCIA_PROCESSADA_COM_SUCESSO.getMensagem();
		}
		return resposta;
	}

	public String getCodigoPortador() {
		if (codigoPortador == null || codigoPortador.length() < 3) {
			throw new InfraException(MensagemHistoricoOcorrencia.CODIGO_PORTADOR_INVALIDO.getMensagem());
		}
		return codigoPortador;
	}
	
	public String getNossoNumero() {
		if (nossoNumero == null) {
			throw new InfraException(MensagemHistoricoOcorrencia.NOSSO_NUMERO_INVALIDO.getMensagem());
		}
		return nossoNumero;
	}
	
	public String getNumeroTitulo() {
		if (numeroTitulo == null) {
			throw new InfraException(MensagemHistoricoOcorrencia.NUMERO_TITULO_INVALIDO.getMensagem());
		}
		return numeroTitulo;
	}
	
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public TituloRemessa getTituloRemessa() {
		return tituloRemessa;
	}

	public void setTituloRemessa(TituloRemessa tituloRemessa) {
		this.tituloRemessa = tituloRemessa;
	}

	public TipoOcorrencia getTipoOcorrencia() {
		return tipoOcorrencia;
	}

	public void setTipoOcorrencia(TipoOcorrencia tipoOcorrencia) {
		this.tipoOcorrencia = tipoOcorrencia;
	}

	public LocalDate getDataOcorrencia() {
		return dataOcorrencia;
	}
}
