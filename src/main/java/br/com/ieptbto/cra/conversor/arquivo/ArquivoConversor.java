package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Cabecalho;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;

/**
 * 
 * @author Lefer
 *
 */
public class ArquivoConversor {

	public Arquivo converterArquivo(ArquivoVO arquivoVO) {
		Arquivo arquivo = new Arquivo();
		List<Remessa> remessas = new ArrayList<Remessa>();
		arquivo.setRemessas(remessas);
		Remessa remessa = new Remessa();

		for (CabecalhoVO cabecalhoVO : arquivoVO.getCabecalhos()) {
			Cabecalho cabecalho = new CabecalhoConversor().converter(Cabecalho.class, cabecalhoVO);
			cabecalho.setRemessa(remessa);
			remessa.setCabecalho(cabecalho);
		}
		// for (TituloVO tituloVO : arquivoVO.getTitulos()) {
		// Titulo titulo = new TituloConversor().converter(Titulo.class,
		// tituloVO);
		// titulo.setArquivo(arquivo);
		// arquivo.getTitulos().add(titulo);
		// }
		// for (RodapeVO rodapeVO : arquivoVO.getRodapes()) {
		// arquivo.getRodapes().add(new
		// RodapeConversor().converter(Rodape.class, rodapeVO));
		// }

		return arquivo;
	}

}
