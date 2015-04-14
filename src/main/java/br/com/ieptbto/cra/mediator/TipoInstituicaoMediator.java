package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.dao.TipoInstituicaoDAO;
import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;

@Service
public class TipoInstituicaoMediator {
	
	@Autowired
	TipoInstituicaoDAO tipoInstituicaoDao;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	
	public TipoInstituicao salvar(TipoInstituicao tipo, List<String> tiposArquivos) {
		List<TipoArquivo> tiposPermitidos = buscarListaTipoArquivos(tiposArquivos); 
		return  tipoInstituicaoDao.salvar(tipo, tiposPermitidos);
	}
	
	public TipoInstituicao alterar(TipoInstituicao tipo, List<String> tiposArquivos) {
		List<TipoArquivo> tiposPermitidos = buscarListaTipoArquivos(tiposArquivos); 
		return tipoInstituicaoDao.alterar(tipo, tiposPermitidos);
	}

	
	public TipoInstituicao buscarTipoInstituicao(String tipoInstituicao) {
		return tipoInstituicaoDao.buscarTipoInstituicao(tipoInstituicao);
	}
	
	public List<TipoInstituicao> listaTipoInstituicao() {
		return tipoInstituicaoDao.buscarListaTipoInstituicao();
	}
	
	public List<TipoInstituicao> listarTipos() {
		return tipoInstituicaoDao.listarTodos();
	}
	
	public List<PermissaoEnvio> permissoesPorTipoInstituicao(TipoInstituicao tipo){
		return tipoInstituicaoDao.permissoesPorTipoInstituicao(tipo);
	}
	
	/***
	 * Método que transforma o array de strings em TipoArquivo 
	 * */
	private List<TipoArquivo> buscarListaTipoArquivos(List<String> tiposArquivo){
		List<TipoArquivo> listaTiposArquivos = new ArrayList<TipoArquivo>();
		TipoArquivo tipoArquivo = new TipoArquivo();
		for (String constante: tiposArquivo){
			tipoArquivo= tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.getTipoArquivoEnum(constante));
			listaTiposArquivos.add(tipoArquivo);
		}
		return listaTiposArquivos;
	}
}
