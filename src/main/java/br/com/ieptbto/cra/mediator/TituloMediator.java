package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TituloMediator {

	@Autowired
	TituloDAO tituloDAO;
	
	public List<TituloRemessa> buscarListaTitulos(TituloRemessa titulo, Usuario user){
		return tituloDAO.buscarListaTitulos(titulo, user);
	}
	
	public List<TituloRemessa> buscarTitulosPorRemessa(Remessa remessa, Instituicao instituicaoCorrente){
		return tituloDAO.buscarTitulosPorRemessa(remessa, instituicaoCorrente);
	}
	
	public List<TituloRemessa> buscarTitulosPorArquivo(Arquivo arquivo, Instituicao instituicaoCorrente){
		return tituloDAO.buscarTitulosPorArquivo(arquivo, instituicaoCorrente);
	}
	
	public List<TituloRemessa> buscarTitulosConfirmacaoRetorno(Arquivo arquivo, Instituicao instituicaoCorrente){
		return tituloDAO.buscarTitulosConfirmacaoRetorno(arquivo, instituicaoCorrente);
	}
	
	public List<Historico> getHistoricoTitulo(TituloRemessa titulo){
		return tituloDAO.buscarHistoricoDoTitulo(titulo);
	}
	
	public TituloRemessa buscarTituloPorChave(TituloRemessa titulo){
		return tituloDAO.buscarTituloPorChave(titulo);
	}
	
	public TituloRemessa carregarTitulo(TituloRemessa titulo){
		return tituloDAO.carregarTitulo(titulo);
	}
}
