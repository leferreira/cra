package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;

@Service
public class TipoArquivoMediator {

	@Autowired
	TipoArquivoDAO tipoArquivoDao;
	
	public List<TipoArquivo> getTiposArquivos(){
		return tipoArquivoDao.buscarTiposArquivo();
	}
	
	public TipoArquivo buscarTipoPorNome(TipoArquivoEnum tipoArquivo){
		return tipoArquivoDao.buscarPorTipoArquivo(tipoArquivo);
	}
}
