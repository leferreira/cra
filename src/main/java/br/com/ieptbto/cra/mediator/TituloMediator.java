package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Remessa;
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
		
		if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			return tituloDAO.buscarTitulosPorArquivo(remessa.getArquivo());
		} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
				List<TituloRemessa> listaTitulos = new ArrayList<TituloRemessa>();;
				List<Confirmacao> listaConfirmacao = tituloDAO.buscarTitulosConfirmacao(remessa.getArquivo());
				for (Confirmacao c: listaConfirmacao){
					listaTitulos.add(c.getTitulo());
				}
				return listaTitulos;
			} else {
				return null;
		}
	}
	
	public List<Historico> getHistoricoTitulo(TituloRemessa titulo){
		return tituloDAO.getHistoricoTitulo(titulo);
	}
}
