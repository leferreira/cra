package br.com.ieptbto.cra.mediator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.conversor.arquivo.ConversorRemessaArquivo;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.XmlCraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemRetornoXml;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("unused")
@Service
public class RemessaMediator {

	protected static final Logger logger = Logger.getLogger(RemessaMediator.class);

	@Autowired
	RemessaDAO remessaDao;
	@Autowired
	ArquivoDAO arquivoDAO;

	@Autowired
	private ConversorRemessaArquivo conversorRemessaArquivo;
	@Autowired
	private ProcessadorArquivo processadorArquivo;
	private List<Exception> erros;

	private List<Remessa> remessasFiltradas;
	private List<Remessa> remessas = new ArrayList<Remessa>();

	public List<Remessa> buscarRemessaAvancado(Arquivo arquivo, Municipio municipio, Instituicao portador, LocalDate dataInicio,
	        LocalDate dataFim, ArrayList<String> tipos, Usuario usuario) {

		try {
			remessas = remessaDao.buscarRemessaAvancado(arquivo, municipio, portador, dataInicio, dataFim, usuario, tipos);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			new InfraException("Não foi possível realizar a busca, contate a CRA.");
		}
		return remessas;
	}

	public List<Remessa> buscarRemessaSimples(Instituicao instituicao, ArrayList<String> tipos, ArrayList<String> situacoes,
	        LocalDate dataInicio, LocalDate dataFim) {

		try {
			remessas = remessaDao.buscarRemessaSimples(instituicao, tipos, situacoes, dataInicio, dataFim);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			new InfraException("Não foi possível realizar a busca, contate a CRA.");
		}
		return remessas;
	}

	public List<Remessa> buscarRemessasDoArquivo(Arquivo arquivo, Instituicao instituicao) {
		return remessaDao.buscarRemessasDoArquivo(instituicao, arquivo);
	}

	public Arquivo buscarArquivoPorNome(Instituicao instituicao, String nomeArquivo) {
		return arquivoDAO.buscarArquivosPorNome(instituicao, nomeArquivo);
	}

	public int buscarTotalRemessas() {
		return 0;
	}

	public List<Remessa> buscarRemessa(Remessa qp) {
		// TODO Auto-generated method stub
		return null;
	}

	public Remessa buscarRemessaPorId(long id) {
		Remessa remessa = new Remessa();
		remessa.setId((int) id);
		return remessaDao.buscarPorPK(remessa);
	}

	public ArquivoVO buscarArquivos(String nome) {
		Remessa remessa = remessaDao.buscarArquivosPorNome(nome);
		if (remessa == null) {
			return null;
		}

		ArquivoVO arquivo = conversorRemessaArquivo.converter(remessa);

		return arquivo;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MensagemRetornoXml processarArquivoXML(List<RemessaVO> arquivoRecebido, Usuario usuario, String nomeArquivo) {
		Arquivo arquivo = processarArquivoXMLManual(arquivoRecebido, usuario, nomeArquivo);

		return gerarResposta(arquivo, usuario);
	}

	/**
	 * 
	 * @param arquivoRecebido
	 * @param usuario
	 * @param nomeArquivo
	 * @return
	 */
	public Arquivo processarArquivoXMLManual(List<RemessaVO> arquivoRecebido, Usuario usuario, String nomeArquivo) {
		Arquivo arquivo = new Arquivo();
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setRemessas(new ArrayList<Remessa>());
		logger.info("Iniciar processador do arquivo " + nomeArquivo);
		processadorArquivo.processarArquivo(arquivoRecebido, usuario, nomeArquivo, arquivo, getErros());
		logger.info("Fim processador do arquivo " + nomeArquivo);

		arquivo = salvarArquivo(arquivo, usuario);
		return arquivo;
	}

	private MensagemRetornoXml gerarResposta(Arquivo arquivo, Usuario usuario) {
		List<Mensagem> mensagens = new ArrayList<Mensagem>();
		MensagemRetornoXml mensagemRetorno = new MensagemRetornoXml();
		Descricao desc = new Descricao();
		Detalhamento detal = new Detalhamento();
		detal.setMensagem(mensagens);

		mensagemRetorno.setDescricao(desc);
		mensagemRetorno.setDetalhamento(detal);
		mensagemRetorno.setCodigoFinal("0000");
		mensagemRetorno.setDescricaoFinal("Arquivo processado com sucesso");

		desc.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		desc.setTipoArquivo(Descricao.XML_UPLOAD_REMESSA);
		desc.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		desc.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		desc.setUsuario(usuario.getNome());

		for (Remessa remessa : arquivo.getRemessas()) {
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo("0000");
			mensagem.setMunicipio(remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE().toString());
			mensagem.setDescricao(formatarMensagemRetorno(remessa));
			mensagens.add(mensagem);
		}

		for (Exception ex : getErros()) {
			XmlCraException exception = XmlCraException.class.cast(ex);
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo(exception.getErro().getCodigo());
			mensagem.setMunicipio(exception.getCodigoIbge());
			mensagem.setDescricao("Município: " + exception.getCodigoIbge() + " - " + exception.getMunicipio() + " - "
			        + exception.getErro().getDescricao());
			mensagens.add(mensagem);
		}

		return mensagemRetorno;
	}

	private String formatarMensagemRetorno(Remessa remessa) {
		return "Município: " + remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE().toString() + " - "
		        + remessa.getInstituicaoDestino().getMunicipio().getNomeMunicipio() + " - " + remessa.getCabecalho().getQtdTitulosRemessa()
		        + " Títulos.";
	}

	private Arquivo salvarArquivo(Arquivo arquivo, Usuario usuario) {
		return arquivoDAO.salvar(arquivo, usuario);
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}

	public File baixarRemessaTXT(Remessa remessa) {
		remessa = remessaDao.buscarPorPK(remessa);
		return processadorArquivo.processarArquivoTXT(remessa);
	}
}
