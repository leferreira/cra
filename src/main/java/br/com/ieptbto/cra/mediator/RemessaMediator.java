package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import br.com.ieptbto.cra.processador.ProcessadorArquivo;

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

	private List<Remessa> remessasFiltradas;
	private List<Remessa> remessas = new ArrayList<Remessa>();

	public List<Remessa> buscarRemessaAvancado(Arquivo arquivo, Municipio municipio, Instituicao portador, LocalDate dataInicio, LocalDate dataFim, ArrayList<String> tipos, Usuario usuario) {
		
		try {
			remessas = remessaDao.buscarRemessaAvancado(arquivo, municipio, portador, dataInicio, dataFim, usuario, tipos);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			new InfraException("Não foi possível realizar a busca, contate a CRA.");
		}
		return remessas;
	}

	public List<Remessa> buscarRemessaSimples(Instituicao instituicao, ArrayList<String> tipos, ArrayList<String> situacoes, LocalDate dataInicio, LocalDate dataFim) {
		
		try {
			remessas = remessaDao.buscarRemessaSimples(instituicao, tipos, situacoes, dataInicio, dataFim);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			new InfraException("Não foi possível realizar a busca, contate a CRA.");
		}
		return remessas;
	}

	public Arquivo buscarArquivoPorNome(String nomeArquivo) {
		return arquivoDAO.buscarArquivosPorNome(nomeArquivo);
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

	public String processarArquivoXML(List<RemessaVO> arquivoRecebido, Usuario usuario, String nomeArquivo) {
		Arquivo arquivo = new Arquivo();
		logger.info("Iniciar processador do arquivo " + nomeArquivo);
		processadorArquivo.processarArquivo(arquivoRecebido, usuario, nomeArquivo, arquivo);
		logger.info("Fim processador do arquivo " + nomeArquivo);

		arquivo = salvarArquivo(arquivo, usuario);

		return gerarResposta(arquivo, usuario);
	}

	private String gerarResposta(Arquivo arquivo, Usuario usuario) {
		// TODO Auto-generated method stub
		return null;
	}

	private Arquivo salvarArquivo(Arquivo arquivo, Usuario usuario) {
		return arquivoDAO.salvar(arquivo, usuario);

	}

}
