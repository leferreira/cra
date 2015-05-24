package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.MunicipioDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;

@Service
public class MunicipioMediator {

	@Autowired
	MunicipioDAO municipioDao;
	@Autowired
	InstituicaoDAO instituicaoDao;

	public Municipio adicionarMunicipio(Municipio municipio) {
		return municipioDao.salvar(municipio);
	}

	public Municipio alterarMunicipio(Municipio municipio) {
		return municipioDao.alterar(municipio);
	}

	/**
	 * Verificar se o município já está cadastrado
	 * 
	 * @param Municipio
	 * @return
	 * */
	public boolean isMunicipioNaoExiste(Municipio municipio) {
		Municipio municipioNovo = municipioDao.buscarMunicipioPorNome(municipio.getNomeMunicipio());
		if (municipioNovo == null) {
			return true;
		}
		return false;
	}

	/**
	 * Verificar se o município tem um cartorio cadastrado
	 * 
	 * @param Municipio
	 * @return
	 * */
	public Instituicao isMunicipioTemCartorio(Municipio municipio) {
		Instituicao cartorio = instituicaoDao.buscarCartorioPorMunicipio(municipio.getNomeMunicipio());
		if (cartorio != null) {
			return cartorio;
		}
		return null;
	}

	/**
	 * Retorna um município
	 * 
	 * @param String
	 *            nomeMunicipio
	 * @return
	 * */
	public Municipio buscarMunicipio(String nomeMunicipio) {
		return municipioDao.buscarMunicipioPorNome(nomeMunicipio);
	}

	/**
	 * Retorna a lista de todos os municípios
	 * 
	 * @param String
	 *            nomeMunicipio
	 * @return
	 * */
	public List<Municipio> listarTodos() {
		return municipioDao.listarTodos();
	}

	public Municipio salvar(Municipio municipio) {
		return municipioDao.salvar(municipio);

	}

	public Municipio buscaMunicipioPorCodigoIBGE(Integer codigoMunicipio) {
		return municipioDao.buscaMunicipioPorCodigoIBGE(codigoMunicipio);
	}
	
	public Municipio buscarMunicipioDoCartorio(Instituicao cartorio) {
		return municipioDao.buscarMunicipioDoCartorio(cartorio);
	}
}
