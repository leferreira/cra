package br.com.ieptbto.cra.conversor.arquivo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.processador.FabricaDeRegistroTXT;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class GeradorDeArquivosTXT extends Gerador {

	public void gerar(RemessaVO remessaVO, File arquivoTXT) {
		try {
			Map<Integer, String> titulos = new HashMap<Integer, String>();
			BufferedWriter bWrite = new BufferedWriter(new FileWriter(arquivoTXT));

			bWrite.write(gerarLinha(remessaVO.getCabecalho()));
			bWrite.newLine();
			for (TituloVO tituloVO : remessaVO.getTitulos()) {
				titulos.put(Integer.parseInt(tituloVO.getNumeroSequencialArquivo()), gerarLinhaTitulo(tituloVO));
			}

			for (Integer i : titulos.keySet()) {
				bWrite.write(titulos.get(i));
				bWrite.newLine();
			}

			bWrite.write(gerarLinhaRodape(remessaVO.getRodape()));
			bWrite.newLine();

			bWrite.flush();
			bWrite.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String gerarLinhaRodape(RodapeVO rodape) {
		return FabricaDeRegistroTXT.getLinha(rodape);
	}

	private String gerarLinhaTitulo(TituloVO tituloVO) {
		return FabricaDeRegistroTXT.getLinha(tituloVO);
	}

	private String gerarLinha(CabecalhoVO cabecalhoVO) {
		return FabricaDeRegistroTXT.getLinha(cabecalhoVO);
	}

}
