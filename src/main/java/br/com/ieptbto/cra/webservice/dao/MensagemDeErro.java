package br.com.ieptbto.cra.webservice.dao;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.vo.*;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
public class MensagemDeErro {

	private String nomeArquivo;
	private String codigoCompensacao;
	private Usuario usuario;
	private CodigoErro erro;
	private String descricaoErro;

	public MensagemDeErro(String nomeArquivo, Usuario usuario, CodigoErro erro) {
		this.nomeArquivo = nomeArquivo;
		this.usuario = usuario;
		this.erro = erro;
	}

	public MensagemDeErro(String nomeArquivo, Usuario usuario, String descricaoErro) {
		this.nomeArquivo = nomeArquivo;
		this.usuario = usuario;
		this.erro = CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO;
		this.descricaoErro = descricaoErro;
	}

	public MensagemDeErro(String codigoApresentante, CodigoErro erro) {
		this.erro = erro;
		this.codigoCompensacao = codigoApresentante;
	}

	public MensagemXmlVO getMensagemErro() {
		MensagemXmlVO mensagemXML = new MensagemXmlVO();
		DescricaoVO descricao = getDescricao();

		mensagemXML.setDescricao(descricao);

		DetalhamentoVO detalhamento = new DetalhamentoVO();

		List<MensagemVO> listaMensagens = new ArrayList<MensagemVO>();
		MensagemVO mensagem = new MensagemVO();
		mensagem.setCodigo(getErro().getCodigo());
		mensagem.setDescricao(getErro().getDescricao());
		if (getDescricaoErro() != null) {
			if (StringUtils.isNotBlank(getDescricaoErro())) {
				mensagem.setDescricao(getDescricaoErro());
			}
		}
		listaMensagens.add(mensagem);

		detalhamento.setMensagem(listaMensagens);

		mensagemXML.setDetalhamento(detalhamento);
		mensagemXML.setCodigoFinal(getErro().getCodigo());
		mensagemXML.setDescricaoFinal(getErro().getDescricao());
		if (getDescricaoErro() != null) {
			if (StringUtils.isNotBlank(getDescricaoErro())) {
				mensagemXML.setDescricaoFinal(getDescricaoErro());
			}
		}

		return mensagemXML;
	}

	public MensagemXmlSerproVO getMensagemErroSerpro() {
		MensagemXmlSerproVO msgSucesso = new MensagemXmlSerproVO();
		msgSucesso.setNomeArquivo(getNomeArquivo());

		List<ComarcaDetalhamentoSerproVO> listaComarcas = new ArrayList<ComarcaDetalhamentoSerproVO>();
		ComarcaDetalhamentoSerproVO comarcaDetalhamento = new ComarcaDetalhamentoSerproVO();
		comarcaDetalhamento.setCodigoMunicipio(StringUtils.EMPTY);
		comarcaDetalhamento.setDataHora(DataUtil.localDateToStringddMMyyyy(new LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
		comarcaDetalhamento.setRegistro(StringUtils.EMPTY);
		comarcaDetalhamento.setCodigo(getErro().getCodigo());
		comarcaDetalhamento.setOcorrencia(getErro().getDescricao());
		comarcaDetalhamento.setTotalRegistros(0);
		listaComarcas.add(comarcaDetalhamento);

		msgSucesso.setComarca(listaComarcas);
		return msgSucesso;
	}

	private DescricaoVO getDescricao() {
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.getDataAtual() + " " + DataUtil.getHoraAtual());
		if (nomeArquivo.contains("B")) {
			descricao.setTipoArquivo("XML_UPLOAD_REMESSA");
		} else if (nomeArquivo.contains("C")) {
			descricao.setTipoArquivo("XML_DOWNLOAD_CONFIRMACAO");
		} else if (nomeArquivo.contains("R")) {
			descricao.setTipoArquivo("XML_DOWNLOAD_RETORNO");
		} else if (nomeArquivo.contains("DP")) {
			descricao.setTipoArquivo("XML_UPLOAD_SUSTACAO");
		} else if (nomeArquivo.contains("CP")) {
			descricao.setTipoArquivo("XML_UPLOAD_CANCELAMENTO");
		} else if (nomeArquivo.contains("AC")) {
			descricao.setTipoArquivo("XML_UPLOAD_AUTORIZACAO");
		}
		descricao.setNomeArquivo(nomeArquivo);
		descricao.setDataMovimento(DataUtil.getDataAtual());
		descricao.setPortador(getCodigoCompensacao());
		descricao.setUsuario(getUsuario().getNome());
		return descricao;
	}

	public String getNomeArquivo() {
		if (nomeArquivo == null) {
			nomeArquivo = StringUtils.EMPTY;
		}
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getCodigoCompensacao() {
		if (codigoCompensacao == null) {
			codigoCompensacao = StringUtils.EMPTY;
			if (getNomeArquivo() != null) {
				if (getNomeArquivo().length() == 12) {
					codigoCompensacao = getNomeArquivo().substring(1, 4);
				} else if (getNomeArquivo().length() == 13) {
					codigoCompensacao = getNomeArquivo().substring(2, 5);
				}
			}
		}
		return codigoCompensacao;
	}

	public void setCodigoCompensacao(String codigoCompensacao) {
		this.codigoCompensacao = codigoCompensacao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public CodigoErro getErro() {
		return erro;
	}

	public void setErro(CodigoErro erro) {
		this.erro = erro;
	}

	public String getDescricaoErro() {
		return descricaoErro;
	}

}
