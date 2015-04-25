package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;

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
	
	public List<TituloRemessa> buscarTitulosPorArquivo(Remessa remessa){
		List<TituloRemessa> listaTitulos = new ArrayList<TituloRemessa>();
		if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			return tituloDAO.buscarTitulosRemessa(remessa.getArquivo());
		} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
				List<Confirmacao> listaConfirmacao = tituloDAO.buscarTitulosConfirmacao(remessa.getArquivo());
				/* Arrumar a busca*/
				for (Confirmacao c: listaConfirmacao){
					listaTitulos.add(c.getTitulo());
				}
				return listaTitulos;
			} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				List<Retorno> listaRetorno = tituloDAO.buscarTitulosRetorno(remessa.getArquivo());
				for (Retorno r: listaRetorno){
					listaTitulos.add(r.getTitulo());
				}
				return listaTitulos;
		}
		return null;
	}
	
	public List<Historico> getHistoricoTitulo(TituloRemessa titulo){
		return tituloDAO.buscarHistoricoDoTitulo(titulo);
	}
	
	public TituloRemessa buscarTituloPorChave(TituloRemessa titulo){
		return tituloDAO.buscarTituloPorChave(titulo);
	}
	
	
}
