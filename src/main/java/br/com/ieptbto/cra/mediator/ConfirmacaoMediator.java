package br.com.ieptbto.cra.mediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ConfirmacaoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;


/**
 * @author thasso
 *
 */
@Service
public class ConfirmacaoMediator {

	private static final int NUMERO_SEQUENCIAL_CONFIRMACAO = 1;
	
	@Autowired
	ConfirmacaoDAO confirmacaoDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	
	private Instituicao cra;
	private TipoArquivo tipoArquivo;
	private Arquivo arquivo;
	
	public List<Remessa> buscarConfirmacoesPendentesDeEnvio() {
		return confirmacaoDAO.buscarConfirmacoesPendentesDeEnvio();
	}

	public boolean gerarConfirmacoes(Usuario usuarioCorrente, List<Remessa> confirmacoesParaEnvio) {
		List<Arquivo> arquivosDeConfirmacao = new ArrayList<Arquivo>();
		
		cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		tipoArquivo = tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.CONFIRMACAO);
		
		Instituicao instituicaoDestino = new Instituicao();
		for (Remessa confirmacao: confirmacoesParaEnvio){
			
			if (arquivo == null || !instituicaoDestino.equals(confirmacao.getInstituicaoDestino())){
				instituicaoDestino = confirmacao.getInstituicaoDestino();
				criarNovoArquivoDeConfirmacao(instituicaoDestino, confirmacao);
				
				List<Remessa> confirmacoes = confirmacaoDAO.buscarConfirmacoesPendentesPorInstituicao(instituicaoDestino);
				arquivo.setRemessas(confirmacoes);
				
				if (!arquivosDeConfirmacao.contains(arquivo) && arquivo != null)
					arquivosDeConfirmacao.add(arquivo);
			} 
		}
		
		confirmacaoDAO.salvarArquivosDeConfirmacaoGerados(usuarioCorrente, arquivosDeConfirmacao);
		return true;
	} 

	private void criarNovoArquivoDeConfirmacao(Instituicao destino,Remessa confirmacao) {
		arquivo = new Arquivo();
		arquivo.setTipoArquivo(tipoArquivo);
		arquivo.setNomeArquivo(gerarNomeArquivoConfirmacao(confirmacao));
		arquivo.setInstituicaoRecebe(destino);
		arquivo.setInstituicaoEnvio(cra);
		arquivo.setDataEnvio(new LocalDate());
	}

	private String gerarNomeArquivoConfirmacao(Remessa confirmacao) {
		
		return TipoArquivoEnum.CONFIRMACAO.getConstante() 
			+ confirmacao.getCabecalho().getNumeroCodigoPortador()
			+ gerarDataArquivo()
			+ NUMERO_SEQUENCIAL_CONFIRMACAO;
	}

	private String gerarDataArquivo(){
		SimpleDateFormat dataPadraArquivo = new SimpleDateFormat("ddMM.yy");
		return dataPadraArquivo.format(new Date()).toString();
	}
}
