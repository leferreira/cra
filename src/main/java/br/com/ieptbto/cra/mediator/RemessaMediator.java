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
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
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
	private ArquivoDAO arquivoDAO;
	@Autowired
	ConversorRemessaArquivo conversorRemessaArquivo;
	private List<Remessa> remessasFiltradas;
	private List<Remessa> remessas = new ArrayList<Remessa>();
	@Autowired
	private ProcessadorArquivo processadorArquivo;

	public List<Remessa> buscarArquivos(Instituicao instituicao, ArrayList<String> tipos, ArrayList<String> situacoes, LocalDate data) {
		return remessaDao.buscarArquivos(instituicao, tipos, situacoes, data);
	}

	public List<Remessa> buscarArquivos(Arquivo arquivo, Municipio municipio, Instituicao portador, LocalDate dataInicio,
	        LocalDate dataFim, ArrayList<String> tipos, Usuario usuario) {

		try {
			remessasFiltradas = new ArrayList<Remessa>();
			remessas = remessaDao.buscarRemessas(arquivo, municipio, portador, dataInicio, dataFim, usuario, tipos);
			for (Remessa remessa : remessas) {
				if (!tipos.isEmpty())
					filtroTipoArquivo(tipos, remessa);
				else
					return remessas;
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			new InfraException("Não foi possível realizar a busca, contate a CRA.");
		}
		return remessasFiltradas;
	}

	private void filtroTipoArquivo(ArrayList<String> tipos, Remessa remessa) {
		TipoArquivo tipoArquivo = new TipoArquivo();

		for (String constante : tipos) {
			if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().getConstante().equals(constante)) {
				if (!remessasFiltradas.contains(remessa))
					remessasFiltradas.add(remessa);
			}
		}
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

	public String processarArquivoXML(ArquivoVO arquivoRecebido, Usuario usuario, String nomeArquivo) {
		Arquivo arquivo = new Arquivo();
		processadorArquivo.processarArquivo(arquivoRecebido, usuario, nomeArquivo, arquivo);
		salvarArquivo(arquivo, usuario);
		return null;
	}

	private Arquivo salvarArquivo(Arquivo arquivo, Usuario usuario) {
		return arquivoDAO.salvar(arquivo, usuario);

	} 
	
	
}
