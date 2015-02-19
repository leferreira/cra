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
