package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TipoInstituicaoDAO;
import br.com.ieptbto.cra.entidade.TipoInstituicao;

@Service
public class TipoInstituicaoMediator {
	
	@Autowired
	TipoInstituicaoDAO tipoInstituicaoDao;
	
	public TipoInstituicao salvar(TipoInstituicao tipo) {
		return tipoInstituicaoDao.salvar(tipo);
	}
	
	public TipoInstituicao alterar(TipoInstituicao tipo) {
		return tipoInstituicaoDao.alterar(tipo);
	}

	
	public TipoInstituicao buscarTipoInstituicao(String tipoInstituicao) {
		return tipoInstituicaoDao.buscarTipoInstituicao(tipoInstituicao);
	}
	
	public List<TipoInstituicao> listaTipoInstituicao() {
		return tipoInstituicaoDao.buscarListaTipoInstituicao();
	}
}
