package br.com.ieptbto.cra.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.arquivo.ConstrutorDeArquivoTXT;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;

/**
 * @author thasso
 *
 */
@Service
public class DownloadMediator {

	@Autowired
	ArquivoDAO arquivoDAO;
	
	public void gerarArquivoTXT(Instituicao instituicao, Arquivo file) {
		
		Arquivo arquivo = arquivoDAO.buscarArquivosPorNome(instituicao, file.getNomeArquivo());
		ArquivoVO arquivoVO = new ConstrutorDeArquivoTXT().converter(arquivo, ArquivoVO.class);
		System.out.println(arquivoVO);
	}


}
