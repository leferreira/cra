package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.PermissaoEnvioDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class PermissaoEnvioMediator {

	private static final Logger logger = Logger.getLogger(PermissaoEnvioMediator.class);
	
	@Autowired
	PermissaoEnvioDAO permissaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	
	private PermissaoEnvio permitido = new PermissaoEnvio();
	
	
	public PermissaoEnvio salvar(PermissaoEnvio permissaoEnvio){
		return permissaoDAO.salvar(permissaoEnvio);
	}
	
	public PermissaoEnvio alterar(PermissaoEnvio permissaoEnvio){
		return permissaoDAO.alterar(permissaoEnvio);
	}
	
	/**
	 * Retorna a lista de permissões para um tipo de instituicao
	 * @param
	 * 		 TipoInstituicao
	 * @return
	 * 		 List<PermissaoEnvio>
	 * */
	public List<PermissaoEnvio> permissoesPorTipoInstituicao(TipoInstituicao tipo){
		return permissaoDAO.permissoesPorTipoInstituicao(tipo);
	}
	
	/**
	 * Método que seta a permissão para o tipo instituicao
	 * */
	public void setPermissoes(TipoInstituicao tipoInstituicao, List<String> tiposArquivos){
		
		List<TipoArquivo> listaTipo = buscarListaTipoArquivos(tiposArquivos);
		List<PermissaoEnvio> permissoes = permissoesPorTipoInstituicao(tipoInstituicao);

		try {
			if (permissoes.isEmpty()){
				for (TipoArquivo tipoArquivo: listaTipo){
					permitido.setTipoArquivo(tipoArquivo);
					permitido.setTipoInstituicao(tipoInstituicao);
					salvar(permitido);
				}
			}
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
			new InfraException(ex.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Não foi possível realizar esta operação! \n Entre em contato com a CRA ");
		}
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
