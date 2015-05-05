package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.exception.InfraException;

@Service
public class InstituicaoMediator {

	@Autowired
	InstituicaoDAO instituicaoDAO;

	public Instituicao salvar(Instituicao instituicao) {
		return instituicaoDAO.salvar(instituicao);
	}

	public Instituicao alterar(Instituicao instituicao) {
		return instituicaoDAO.alterar(instituicao);
	}

	/**
	 * Verifica se a instituicao não está cadastrada
	 * 
	 * @param instituicao
	 * @return
	 */
	public boolean isInstituicaoNaoExiste(Instituicao instituicao) {
		Instituicao instituicaoNova = instituicaoDAO.buscarInstituicao(instituicao.getNomeFantasia());
		if (instituicaoNova == null) {
			return true;
		}
		return false;
	}

	/**
	 * Busca uma instituicao cadastrada
	 * 
	 * @param instituicao
	 * @return
	 */
	public Instituicao buscarInstituicao(Instituicao instituicao) {
		return instituicaoDAO.buscarInstituicao(instituicao);
	}

	/**
	 * Busca uma instituicao cadastrada
	 * 
	 * @param nomeFantasia
	 * @return
	 */
	public Instituicao buscarInstituicaoIncial(String instituicao) {
		return instituicaoDAO.buscarInstituicaoInicial(instituicao);
	}

	/**
	 * Busca as Instituicões ativas
	 */
	public List<Instituicao> listaDeInstituicoesAtivas() {
		List<Instituicao> lista = instituicaoDAO.buscarListaInstituicaoAtivas();
		return lista;
	}

	/**
	 * Buscar todos os cartórios e instituicao cadastrados
	 */
	public List<Instituicao> buscarCartoriosInstituicoes() {
		return instituicaoDAO.buscarCartoriosInstituicoes();
	}

	/**
	 * Busca todas as Instituicões ativas ou não, menos cartórios.
	 * 
	 */
	public List<Instituicao> listarTodasInstituicoes() {
		List<Instituicao> lista = instituicaoDAO.buscarListaInstituicao();
		return lista;
	}

	/**
	 * Busca todos os cartórios, ativos ou não
	 */
	public List<Instituicao> listaDeCartorio() {
		List<Instituicao> lista = instituicaoDAO.buscarListaCartorio();
		return lista;
	}

	public Instituicao getInstituicaoPorCodigoIBGE(Integer codigoMunicipio) {
		Instituicao instituicao = instituicaoDAO.getInstituicao(codigoMunicipio);
		if (instituicao == null) {
			throw new InfraException("Instituição não cadastrada com o código IBGE [" + codigoMunicipio + "]");
		}

		return instituicao;
	}

	/**
	 * Busca portador por Código do portador
	 */
	public Instituicao getInstituicaoPorCodigoPortador(String codigoPortador) {
		Instituicao instituicao = instituicaoDAO.getInstituicaoPorCodigo(codigoPortador);
		if (instituicao == null) {
			throw new InfraException("Instituição não cadastrada com o código de compesação [" + codigoPortador + "]");
		}

		return instituicao;
	}

	/**
	 * Busca portador por Código do portador
	 */
	public List<Instituicao> getInstituicoesFinanceiras() {
		return instituicaoDAO.getInstituicoesFinanceiras();
	}

	public boolean isInstituicaoAtiva(Instituicao instituicao) {
		return instituicaoDAO.isInstituicaoAtiva(instituicao);
	}

}
