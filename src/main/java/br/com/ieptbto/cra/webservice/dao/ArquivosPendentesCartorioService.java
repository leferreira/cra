package br.com.ieptbto.cra.webservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.XmlFormatterUtil;
import br.com.ieptbto.cra.webservice.VO.AutorizaCancelamentoPendente;
import br.com.ieptbto.cra.webservice.VO.CancelamentoPendente;
import br.com.ieptbto.cra.webservice.VO.DesistenciaPendente;
import br.com.ieptbto.cra.webservice.VO.RelatorioArquivosPendentes;
import br.com.ieptbto.cra.webservice.VO.RemessaPendente;

/**
 * @author Thasso Aráujo
 *
 */
@Service
public class ArquivosPendentesCartorioService extends CraWebService{

	@Autowired
	private RemessaMediator remessaMediator;
	
	public String buscarArquivosPendentesCartorio(Usuario usuario) {
		this.usuario = usuario;
		this.nomeArquivo= StringUtils.EMPTY;
		
		Arquivo arquivo = null;
		try {
			if (usuario == null) {
				return setRespostaUsuarioInvalido();
			}
			arquivo = remessaMediator.arquivosPendentes(usuario.getInstituicao());
			if (arquivo.getRemessas().isEmpty() &&
					arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto().isEmpty() &&
					arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto().isEmpty() &&
					arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento().isEmpty()) {
				return gerarMensagemNaoHaArquivosPendentes();
			}
		} catch (Exception e) {
			return setRespostaErroInternoNoProcessamento(LayoutPadraoXML.CRA_NACIONAL, nomeArquivo);
		}
		return gerarMensagem(converterArquivoParaRelatorioArquivosPendentes(arquivo), CONSTANTE_RELATORIO_XML);
	}
	
	private RelatorioArquivosPendentes converterArquivoParaRelatorioArquivosPendentes(Arquivo arquivo) {
		RelatorioArquivosPendentes relatorioArquivosPendentes = new RelatorioArquivosPendentes();

		RemessaPendente remessaPendentes = new RemessaPendente();
		if (!arquivo.getRemessas().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (Remessa remessa : arquivo.getRemessas()){
				nomeArquivos.add(remessa.getArquivo().getNomeArquivo());
			}
			remessaPendentes.setArquivos(nomeArquivos);
		}
		
		CancelamentoPendente cancelamentoPendentes = new CancelamentoPendente();
		if (!arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (CancelamentoProtesto cancelamento : arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto()){
				nomeArquivos.add(cancelamento.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo());
			}
			cancelamentoPendentes.setArquivos(nomeArquivos);
		}
		
		DesistenciaPendente desistenciaPendentes = new DesistenciaPendente();
		if (!arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (DesistenciaProtesto desistencia : arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto()){
				nomeArquivos.add(desistencia.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo());
			}
			desistenciaPendentes.setArquivos(nomeArquivos);
		}
		
		AutorizaCancelamentoPendente autorizaCancelamentoPendentes = new AutorizaCancelamentoPendente();
		if (!arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (AutorizacaoCancelamento ac : arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento()){
				nomeArquivos.add(ac.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo());
			}
			autorizaCancelamentoPendentes.setArquivos(nomeArquivos);
		}
		
		relatorioArquivosPendentes.setRemessas(remessaPendentes);
		relatorioArquivosPendentes.setCancelamentos(cancelamentoPendentes);
		relatorioArquivosPendentes.setDesistencias(desistenciaPendentes);
		relatorioArquivosPendentes.setAutorizaCancelamentos(autorizaCancelamentoPendentes);
		return relatorioArquivosPendentes; 
	} 

	private String setRespostaUsuarioInvalido() {
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("<?xml version=\'1.0\' encoding=\'UTF-8\'?>");
		mensagem.append("<relatorio>");
		mensagem.append("	<descricao>");
		mensagem.append("		<dataMovimento>"+ DataUtil.localDateTimeToString(new LocalDateTime()) +"</dataMovimento>");
		mensagem.append("	</descricao>");
		mensagem.append("	<final>0001</final>");
		mensagem.append("	<descricao_final>Falha na autenticação.</descricao_final>");
		mensagem.append("</relatorio>");
		return XmlFormatterUtil.format(mensagem.toString());
	}

	private String gerarMensagemNaoHaArquivosPendentes() {
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("<?xml version=\'1.0\' encoding=\'UTF-8\'?>");
		mensagem.append("<relatorio>");
		mensagem.append("	<descricao>");
		mensagem.append("		<dataMovimento>"+ DataUtil.localDateTimeToString(new LocalDateTime()) +"</dataMovimento>");
		mensagem.append("	</descricao>");
		mensagem.append("	<final>0000</final>");
		mensagem.append("	<descricao_final>Não há arquivos pendentes.</descricao_final>");
		mensagem.append("</relatorio>");
		return XmlFormatterUtil.format(mensagem.toString());
	}
}
