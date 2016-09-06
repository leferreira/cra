package br.com.ieptbto.cra.webservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CraServiceEnum;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
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
public class ArquivosPendentesCartorioService extends CraWebService {

	@Autowired
	private ArquivoMediator arquivoMediator;
	@Autowired
	private DesistenciaProtestoMediator desistenciaMediator;
	@Autowired
	private InstituicaoMediator instituicaoMediator;

	public String buscarArquivosPendentesCartorio(Usuario usuario) {
		Arquivo arquivo = null;
		try {
			if (usuario == null) {
				return setRespostaUsuarioInvalido();
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.ARQUIVOS_PENDENTES_CARTORIO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			Instituicao instituicaoUsuario = instituicaoMediator.carregarInstituicaoPorId(usuario.getInstituicao());
			arquivo = arquivoMediator.arquivosPendentes(instituicaoUsuario);
			if (arquivo.getRemessas().isEmpty() && arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto().isEmpty()
					&& arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto().isEmpty()
					&& arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento().isEmpty()) {
				return gerarMensagemNaoHaArquivosPendentes();
			}
		} catch (Exception e) {
			logger.info(e.getCause(), e);
			return setRespostaErroInternoNoProcessamento(usuario, "");
		}
		return gerarMensagem(converterArquivoParaRelatorioArquivosPendentes(arquivo), CONSTANTE_RELATORIO_XML);
	}

	private RelatorioArquivosPendentes converterArquivoParaRelatorioArquivosPendentes(Arquivo arquivo) {
		RelatorioArquivosPendentes relatorioArquivosPendentes = new RelatorioArquivosPendentes();

		RemessaPendente remessaPendentes = new RemessaPendente();
		if (!arquivo.getRemessas().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (Remessa remessa : arquivo.getRemessas()) {
				remessa.setArquivo(arquivoMediator.carregarArquivoPorId(remessa.getArquivo()));
				nomeArquivos.add(remessa.getArquivo().getNomeArquivo());
			}
			remessaPendentes.setArquivos(nomeArquivos);
		}

		CancelamentoPendente cancelamentoPendentes = new CancelamentoPendente();
		if (!arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (CancelamentoProtesto cancelamento : arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto()) {
				cancelamento
						.setRemessaCancelamentoProtesto(desistenciaMediator.carregarRemessaCancelamentoPorId(cancelamento.getRemessaCancelamentoProtesto()));
				nomeArquivos.add(cancelamento.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo());
			}
			cancelamentoPendentes.setArquivos(nomeArquivos);
		}

		DesistenciaPendente desistenciaPendentes = new DesistenciaPendente();
		if (!arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (DesistenciaProtesto desistencia : arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto()) {
				desistencia.setRemessaDesistenciaProtesto(desistenciaMediator.carregarRemessaDesistenciaPorId(desistencia.getRemessaDesistenciaProtesto()));
				nomeArquivos.add(desistencia.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo());
			}
			desistenciaPendentes.setArquivos(nomeArquivos);
		}

		AutorizaCancelamentoPendente autorizaCancelamentoPendentes = new AutorizaCancelamentoPendente();
		if (!arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (AutorizacaoCancelamento ac : arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento()) {
				ac.setRemessaAutorizacaoCancelamento(desistenciaMediator.carregarRemessaAutorizacaoPorId(ac.getRemessaAutorizacaoCancelamento()));
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

	public String confirmarEnvioConfirmacaoRetorno(String nomeArquivo, Usuario usuario) {
		try {
			if (usuario == null) {
				return setRespostaUsuarioInvalido();
			}
			Arquivo arquivoJaEnviado = arquivoMediator.buscarArquivoEnviado(usuario, nomeArquivo);
			if (arquivoJaEnviado != null) {
				return gerarMensagemEnvioSucesso(usuario, arquivoJaEnviado);
			}
		} catch (Exception e) {
			logger.info(e.getCause(), e);
			return setRespostaErroInternoNoProcessamento(usuario, "Erro interno ao verificar se o arquivo ja foi enviado!");
		}
		return gerarRespostaArquivoNaoEnviado(usuario, nomeArquivo);
	}

	private String gerarMensagemEnvioSucesso(Usuario usuario, Arquivo arquivo) {
		String constanteTipoAcao = "XML_UPLOAD_CONFIRMACAO";
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			constanteTipoAcao = "XML_UPLOAD_RETORNO";
		}
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("<?xml version=\'1.0\' encoding=\'UTF-8\'?>");
		mensagem.append("<relatorio>");
		mensagem.append("	<descricao>");
		mensagem.append("		<dataEnvio>" + DataUtil.localDateToString(arquivo.getDataEnvio()) + "</dataEnvio>");
		mensagem.append("		<tipoArquivo>" + constanteTipoAcao + "</tipoArquivo>");
		mensagem.append("		<usuario>" + usuario.getInstituicao().getNomeFantasia() + "</usuario>");
		mensagem.append("	</descricao>");
		mensagem.append("	<final>" + CodigoErro.CRA_SUCESSO.getCodigo() + "</final>");
		mensagem.append("	<descricao_final>" + CodigoErro.CRA_SUCESSO.getDescricao() + "</descricao_final>");
		mensagem.append("</relatorio>");
		return XmlFormatterUtil.format(mensagem.toString());
	}

	private String gerarRespostaArquivoNaoEnviado(Usuario usuario, String nomeArquivo) {
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("<?xml version=\'1.0\' encoding=\'UTF-8\'?>");
		mensagem.append("<relatorio>");
		mensagem.append("	<descricao>");
		mensagem.append("		<dataMovimento>" + DataUtil.localDateTimeToString(new LocalDateTime()) + "</dataMovimento>");
		mensagem.append("	</descricao>");
		mensagem.append("	<final>9999</final>");
		mensagem.append("	<descricao_final>Arquivo não processado.</descricao_final>");
		mensagem.append("</relatorio>");
		return XmlFormatterUtil.format(mensagem.toString());
	}

	private String setRespostaUsuarioInvalido() {
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("<?xml version=\'1.0\' encoding=\'UTF-8\'?>");
		mensagem.append("<relatorio>");
		mensagem.append("	<descricao>");
		mensagem.append("		<dataMovimento>" + DataUtil.localDateTimeToString(new LocalDateTime()) + "</dataMovimento>");
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
		mensagem.append("		<dataMovimento>" + DataUtil.localDateTimeToString(new LocalDateTime()) + "</dataMovimento>");
		mensagem.append("	</descricao>");
		mensagem.append("	<final>0000</final>");
		mensagem.append("	<descricao_final>Não há arquivos pendentes.</descricao_final>");
		mensagem.append("</relatorio>");
		return XmlFormatterUtil.format(mensagem.toString());
	}
}