package br.com.ieptbto.cra.webservice.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.ConversorArquivoVo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.HistoricoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CartorioProtestoService extends CraWebService {

	public static final Logger logger = Logger.getLogger(CartorioProtestoServiceImpl.class);
	@Autowired
	private TituloMediator tituloMediator;
	@Autowired
	private HistoricoMediator historicoMediator;
	@Autowired
	private RemessaMediator remessaMediator;
	private ArquivoVO arquivoVO;
	private List<RemessaVO> remessas;
	private String retorno;
	private TituloRemessa tituloRemessa;
	private TipoOcorrencia tipoOcorrencia;
	private LocalDate dataOcorrencia;
	private String codigoPortador;
	private String nossoNumero;
	private String numeroTitulo;
	
	public String processarRemessa(String nomeArquivo, Usuario usuario) {
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		
		ArquivoVO arquivoVO = remessaMediator.buscarRemessaParaCartorio(getUsuario().getInstituicao(), getNomeArquivo());
		if (getNomeArquivo() == null || getUsuario() == null) {
			return setResposta(arquivoVO, getNomeArquivo());
		}

		return setResposta(arquivoVO, getNomeArquivo());
	}
	
	public String processarConfirmacao(String nomeArquivo, Usuario usuario, String dados) {
		return processar(nomeArquivo, usuario, converterStringArquivoVO(dados));
	}
	
	public String processarRetorno(String nomeArquivo, Usuario usuario, String dados) {
		// TODO Auto-generated method stub
		return null;
	}
	
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
			setRetorno(ex.getMessage());
		} catch (Exception ex) {
			logger.info(ex.getMessage());
			setRetorno(MensagemCartorioProtesto.ERRO_DE_PRECESSAMENTO_CRA.getMensagem());
		}
		return getRetorno();
	}

	/**
	 * 
	 * @param dados
	 * @return
	 */
	private ArquivoVO converterStringArquivoVO(String dados) {
		JAXBContext context;
		ArquivoVO arquivo = null;

		try {
			context = JAXBContext.newInstance(ArquivoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlDeTeste = "";

			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(dados).getBytes()));
			while (scanner.hasNext()) {
				xmlDeTeste = xmlDeTeste + scanner.nextLine().replaceAll("& ", "&amp;");
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlDeTeste.getBytes());
			arquivo = (ArquivoVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage());
			new InfraException(e.getMessage(), e.getCause());
		}
		return arquivo;
	}
	
	public String processar(String nomeArquivo, Usuario usuario, ArquivoVO arquivoRecebido) {
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		setArquivoVO(arquivoRecebido);

		if (getArquivoVO() == null || getUsuario() == null) {
			ArquivoVO arquivo = new ArquivoVO();
			return setResposta(arquivo, nomeArquivo);
		}

		setRemessas(ConversorArquivoVo.converterParaRemessaVO(getArquivoVO()));
		return gerarMensagem(remessaMediator.processarArquivoXML(getRemessas(), getUsuario(), nomeArquivo), "relatorio");
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
			throw new InfraException(MensagemCartorioProtesto.OCORRENCIA_NAO_ENCONTRADA_OU_NAO_EXISTE.getMensagem());
		}
		setTipoOcorrencia(tipoOcorrencia);
	}
	
	public String getRetorno() {
		if (retorno == null) {
			this.retorno = MensagemCartorioProtesto.OCORRENCIA_PROCESSADA_COM_SUCESSO.getMensagem();
		}
		return retorno;
	}

	public String getCodigoPortador() {
		if (codigoPortador == null || codigoPortador.length() < 3) {
			throw new InfraException(MensagemCartorioProtesto.CODIGO_PORTADOR_INVALIDO.getMensagem());
		}
		return codigoPortador;
	}
	
	public String getNossoNumero() {
		if (nossoNumero == null) {
			throw new InfraException(MensagemCartorioProtesto.NOSSO_NUMERO_INVALIDO.getMensagem());
		}
		return nossoNumero;
	}
	
	public String getNumeroTitulo() {
		if (numeroTitulo == null) {
			throw new InfraException(MensagemCartorioProtesto.NUMERO_TITULO_INVALIDO.getMensagem());
		}
		return numeroTitulo;
	}
	
	public void setRetorno(String resposta) {
		this.retorno = resposta;
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

	public ArquivoVO getArquivoVO() {
		return arquivoVO;
	}

	public void setArquivoVO(ArquivoVO arquivoVO) {
		this.arquivoVO = arquivoVO;
	}

	public List<RemessaVO> getRemessas() {
		return remessas;
	}

	public void setRemessas(List<RemessaVO> remessas) {
		this.remessas = remessas;
	}
}
