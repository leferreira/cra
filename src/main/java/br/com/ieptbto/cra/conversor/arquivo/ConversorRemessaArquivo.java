package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ConversorRemessaArquivo {

	public ArquivoVO converter(List<TituloRemessa> titulos) {
		ArquivoVO arquivo = new ArquivoVO();
		arquivo.setCabecalhos(converterCabecalho(titulos.get(0).getRemessa().getCabecalho()));
		arquivo.setRodapes(converterRodape(titulos.get(0).getRemessa().getRodape()));
		arquivo.setTitulos(converterTitulos(titulos));
		arquivo.setIdentificacaoRegistro("0");
		arquivo.setTipoArquivo(titulos.get(0).getRemessa().getArquivo().getTipoArquivo());

		return arquivo;
	}

	private List<TituloVO> converterTitulos(List<TituloRemessa> titulos) {
		List<TituloVO> titulosVO = new ArrayList<TituloVO>();
		for (TituloRemessa titulo : titulos) {
			TituloVO tituloVO = TituloVO.parseTitulo(titulo);
			titulosVO.add(tituloVO);
		}
		return titulosVO;
	}

	private List<RodapeVO> converterRodape(Rodape rodape) {
		List<RodapeVO> rodapes = new ArrayList<RodapeVO>();
		RodapeVO rodapeVO = RodapeVO.parseRodape(rodape);
		rodapes.add(rodapeVO);
		return rodapes;
	}

	private List<CabecalhoVO> converterCabecalho(CabecalhoRemessa cabecalho) {
		List<CabecalhoVO> cabecalhos = new ArrayList<CabecalhoVO>();
		CabecalhoVO cabecalhoVO = CabecalhoVO.parseCabecalho(cabecalho);
		cabecalhos.add(cabecalhoVO);
		return cabecalhos;
	}
}
