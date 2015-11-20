package br.com.ieptbto.cra.webservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.exception.XmlCraException;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class DesistenciaProtestoService extends CraWebService {

	@Autowired
	private DesistenciaProtestoMediator desistenciaProtestoMediator;
	@Autowired
	private InstituicaoMediator instituicaoMediator;
	private List<Exception> erros;

	public String processar(String nomeArquivo, Usuario usuario, String dados) {
		Arquivo arquivo = new Arquivo();
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);

		if (getUsuario() == null) {
			return setResposta(usuario.getInstituicao().getLayoutPadraoXML() ,new ArquivoVO(), nomeArquivo, CONSTANTE_REMESSA_XML);
		}

		if (dados == null || StringUtils.isEmpty(dados) || StringUtils.isBlank(dados)) {
			return setRespostaArquivoEmBranco(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
		}

		arquivo = gerarArquivoDesistencia(arquivo, dados);

		return gerarMensagem(gerarResposta(arquivo, getUsuario()), "relatorio");
	}

	private MensagemXml gerarResposta(Arquivo arquivo, Usuario usuario) {
		List<Mensagem> mensagens = new ArrayList<Mensagem>();
		MensagemXml mensagemRetorno = new MensagemXml();
		Descricao desc = new Descricao();
		Detalhamento detal = new Detalhamento();
		detal.setMensagem(mensagens);

		mensagemRetorno.setDescricao(desc);
		mensagemRetorno.setDetalhamento(detal);
		mensagemRetorno.setCodigoFinal("0000");
		mensagemRetorno.setDescricaoFinal("Arquivo processado com sucesso");

		desc.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		desc.setTipoArquivo(Descricao.XML_UPLOAD_SUSTACAO);
		desc.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		desc.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		desc.setUsuario(usuario.getNome());
		desc.setNomeArquivo(getNomeArquivo());

		for (DesistenciaProtesto desistenciaProtesto : arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto()) {
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo("0000");
			mensagem.setMunicipio(getMunicipio(desistenciaProtesto));
			mensagem.setDescricao(formatarMensagemRetorno(desistenciaProtesto));
			mensagens.add(mensagem);
		}

		for (Exception ex : getErros()) {
			XmlCraException exception = XmlCraException.class.cast(ex);
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo(exception.getErro().getCodigo());
			mensagem.setMunicipio(exception.getCodigoIbge());
			mensagem.setDescricao("Município: " + exception.getCodigoIbge() + " - " + exception.getMunicipio() + " - "
			        + exception.getErro().getDescricao());
			mensagens.add(mensagem);
		}

		return mensagemRetorno;
	}

	private Arquivo gerarArquivoDesistencia(Arquivo arquivo, String dados) {

		logger.info("Iniciar processador do arquivo de desistencia " + getNomeArquivo());
		arquivo = desistenciaProtestoMediator.processar(arquivo, dados, getErros(), getUsuario());
		arquivo.setInstituicaoEnvio(getUsuario().getInstituicao());
		arquivo.setNomeArquivo(getNomeArquivo());
		arquivo.setUsuarioEnvio(getUsuario());
		logger.info("Fim processador do arquivo de desistencia " + getNomeArquivo());
		return arquivo;

	}

	private String getMunicipio(DesistenciaProtesto desistenciaProtesto) {
		return desistenciaProtesto.getCabecalhoCartorio().getCodigoMunicipio();
	}

	private String formatarMensagemRetorno(DesistenciaProtesto desistenciaProtesto) {
		Instituicao instituicao = instituicaoMediator
		        .getCartorioPorCodigoIBGE(desistenciaProtesto.getCabecalhoCartorio().getCodigoMunicipio());
		return instituicao.getNomeFantasia() + " (" + desistenciaProtesto.getDesistencias().size() + ") ";

	}

	public List<Exception> getErros() {
		return erros;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}
}
