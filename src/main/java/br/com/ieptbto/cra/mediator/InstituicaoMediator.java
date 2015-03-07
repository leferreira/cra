package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.entidade.Instituicao;

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
	
	public Instituicao buscarInstituicao(String instituicao) {
		return instituicaoDAO.buscarInstituicao(instituicao);
	}

	public Instituicao buscarInstituicaoIncial(String instituicao) {
		return instituicaoDAO.buscarInstituicaoInicial(instituicao);
	}

	public List<Instituicao> listaDeInstituicao() {
		List<Instituicao> lista = instituicaoDAO.buscarListaInstituicao();
		return lista;
	}
}
