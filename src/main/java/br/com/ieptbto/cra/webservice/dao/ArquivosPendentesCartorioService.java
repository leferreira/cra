package br.com.ieptbto.cra.webservice.dao;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.webservice.VO.RelatorioArquivosPendentes;

/**
 * @author Thasso Aráujo
 *
 */
@Service
public class ArquivosPendentesCartorioService extends CraWebService{

	@Autowired
	private RemessaMediator remessaMediator;
	
	public String buscarArquivosPendentesCartorio(Usuario usuario) {
		this.nomeArquivo= StringUtils.EMPTY;
		
		if (usuario == null) {
			return setResposta(LayoutPadraoXML.CRA_NACIONAL, new ArquivoVO(), nomeArquivo, CONSTANTE_RELATORIO_XML);
		}
		Arquivo arquivo = remessaMediator.arquivosPendentes(usuario.getInstituicao());
		if (arquivo.getRemessas().isEmpty() &&
				arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto().isEmpty() &&
				arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto().isEmpty() &&
				arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento().isEmpty()) {
//			return 
		}
		return gerarMensagem(converterArquivoParaRelatorioArquivosPendentes(arquivo), CONSTANTE_RELATORIO_XML);
	}

	private RelatorioArquivosPendentes converterArquivoParaRelatorioArquivosPendentes(Arquivo arquivo) {
		RelatorioArquivosPendentes relatorioArquivosPendentes = new RelatorioArquivosPendentes();
		
		
		return relatorioArquivosPendentes; 
	} 
}
