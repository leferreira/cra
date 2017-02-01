package br.com.ieptbto.cra.webservice.dao;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.CampoArquivo;
import br.com.ieptbto.cra.conversor.arquivo.FabricaConversor;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.InstituicaoVO;
import br.com.ieptbto.cra.enumeration.CraServiceEnum;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoCampo51;
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
public class CartorioService extends CraWebService {

	@Autowired
	InstituicaoMediator instituicaoMediator;
	@Autowired
	ArquivoMediator arquivoMediator;
	@Autowired
	DesistenciaProtestoMediator desistenciaMediator;

	public String buscarArquivosPendentesCartorio(Usuario usuario) {
		Arquivo arquivo = null;
		try {
			if (usuario == null) {
				return setRespostaUsuarioInvalido();
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.ARQUIVOS_PENDENTES_CARTORIO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			arquivo = arquivoMediator.arquivosPendentes(usuario.getInstituicao());
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
		if (arquivo.getRemessas() != null && !arquivo.getRemessas().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (Remessa remessa : arquivo.getRemessas()) {
				nomeArquivos.add(arquivoMediator.carregarArquivoPorId(remessa.getArquivo()).getNomeArquivo());
			}
			remessaPendentes.setNomeArquivos(nomeArquivos);
		}
		CancelamentoPendente cancelamentoPendentes = new CancelamentoPendente();
		if (arquivo.getRemessaCancelamentoProtesto() != null
				&& !arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (CancelamentoProtesto cancelamento : arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto()) {
				cancelamento.setRemessaCancelamentoProtesto(
						desistenciaMediator.carregarRemessaCancelamentoPorId(cancelamento.getRemessaCancelamentoProtesto()));
				nomeArquivos.add(cancelamento.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo());
			}
			cancelamentoPendentes.setArquivos(nomeArquivos);
		}
		DesistenciaPendente desistenciaPendentes = new DesistenciaPendente();
		if (arquivo.getRemessaDesistenciaProtesto() != null
				&& !arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (DesistenciaProtesto desistencia : arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto()) {
				desistencia.setRemessaDesistenciaProtesto(
						desistenciaMediator.carregarRemessaDesistenciaPorId(desistencia.getRemessaDesistenciaProtesto()));
				nomeArquivos.add(desistencia.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo());
			}
			desistenciaPendentes.setArquivos(nomeArquivos);
		}
		AutorizaCancelamentoPendente autorizaCancelamentoPendentes = new AutorizaCancelamentoPendente();
		if (arquivo.getRemessaAutorizacao() != null && !arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento().isEmpty()) {
			List<String> nomeArquivos = new ArrayList<String>();
			for (AutorizacaoCancelamento ac : arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento()) {
				ac.setRemessaAutorizacaoCancelamento(
						desistenciaMediator.carregarRemessaAutorizacaoPorId(ac.getRemessaAutorizacaoCancelamento()));
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

	/**
	 * @param usuario
	 * @return
	 */
	public String buscarDesistenciaPendentesCartorio(Usuario usuario) {
		Arquivo arquivo = null;

		try {
			if (usuario == null) {
				return setRespostaUsuarioInvalido();
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.ARQUIVOS_PENDENTES_CARTORIO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			arquivo = arquivoMediator.desistenciaPendentes(usuario.getInstituicao());
			if (arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto().isEmpty()) {
				return gerarMensagemNaoHaArquivosPendentes();
			}
		} catch (Exception e) {
			logger.info(e.getCause(), e);
			return setRespostaErroInternoNoProcessamento(usuario, "Erro interno no processamento.");
		}
		return gerarMensagem(converterArquivoParaRelatorioArquivosPendentes(arquivo), CONSTANTE_RELATORIO_XML);
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
			return setRespostaErroInternoNoProcessamento(usuario,
					"Erro interno ao verificar se o arquivo de  Retorno/Confirmação ja foi enviado!");
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

	/**
	 * Consulta para atualização de apresentantes para o sistema de protesto
	 * 
	 * @param usuario
	 * @param codigoAprensentante
	 * @return
	 */
	public String consultaDadosApresentante(Usuario usuario, String codigoAprensentante) {
		Instituicao instituicao = null;

		try {
			if (usuario == null) {
				return setRespostaUsuarioInvalido();
			}
			instituicao = instituicaoMediator.buscarApresentantePorCodigoPortador(codigoAprensentante);
			if (instituicao == null) {
				return mensagemApresentanteNaoEncontrado(codigoAprensentante);
			}
		} catch (Exception e) {
			logger.info(e.getCause(), e);
			return setRespostaErroInternoNoProcessamento(usuario, "Erro interno ao verificar dados do apresentante.");
		}
		return gerarRespostaDadosApresentante(instituicao);
	}

	private String mensagemApresentanteNaoEncontrado(String codigoAprensentante) {
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("<?xml version=\'1.0\' encoding=\'UTF-8\'?>");
		mensagem.append("<relatorio>");
		mensagem.append("	<descricao>");
		mensagem.append("		<dataMovimento>" + DataUtil.localDateTimeToString(new LocalDateTime()) + "</dataMovimento>");
		mensagem.append("	</descricao>");
		mensagem.append("	<final>9999</final>");
		mensagem.append("	<descricao_final>Apresentante com o código [" + codigoAprensentante + "] não encontrado.</descricao_final>");
		mensagem.append("</relatorio>");
		return XmlFormatterUtil.format(mensagem.toString());
	}

	private String gerarRespostaDadosApresentante(Instituicao instituicao) {
		InstituicaoVO instituicaoVO = new InstituicaoVO();
		BeanWrapper propertyAccessEntidadeVO = PropertyAccessorFactory.forBeanPropertyAccess(instituicaoVO);
		BeanWrapper propertyAccessEntidade = PropertyAccessorFactory.forBeanPropertyAccess(instituicao);

		PropertyDescriptor[] propertyDescriptorsVO = propertyAccessEntidadeVO.getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptorVO : propertyDescriptorsVO) {
			String propertyName = propertyDescriptorVO.getName();
			if (propertyAccessEntidadeVO.isReadableProperty(propertyName) && propertyAccessEntidade.isWritableProperty(propertyName)) {
				Object valor = propertyAccessEntidade.getPropertyValue(propertyName);
				if (Boolean.class.isInstance(valor)) {
					valor = Boolean.class.cast(valor).toString();
				}
				if (Integer.class.isInstance(valor)) {
					valor = Integer.class.cast(valor);
				}
				if (TipoInstituicao.class.isInstance(valor)) {
					valor = TipoInstituicao.class.cast(valor).getTipoInstituicao().toString();
				}
				if (TipoCampo51.class.isInstance(valor)) {
					valor = TipoCampo51.class.cast(valor).toString();
				}
				propertyAccessEntidadeVO.setPropertyValue(propertyName, valor);
			}
		}
		return gerarMensagem(instituicaoVO, CONSTANTE_APRESENTANTE_XML);
	}

	protected String converterValor(Object propriedade, CampoArquivo campo) {
		if (propriedade == null) {
			return "";
		}
		return FabricaConversor.getValorConvertidoParaString(campo, propriedade.getClass(), propriedade).trim();
	}
}