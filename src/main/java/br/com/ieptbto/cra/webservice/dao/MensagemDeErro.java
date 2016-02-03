package br.com.ieptbto.cra.webservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;
import br.com.ieptbto.cra.webservice.VO.ComarcaDetalhamentoSerpro;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;
import br.com.ieptbto.cra.webservice.VO.MensagemXmlSerpro;

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

	public MensagemDeErro(String nomeArquivo, Usuario usuario, CodigoErro erro) {
		this.nomeArquivo = nomeArquivo;
		this.usuario = usuario;
		this.erro = erro;
	}
	
	public MensagemDeErro(String codigoApresentante, CodigoErro erro) {
		this.erro = erro;
		this.codigoCompensacao = codigoApresentante;
	}
	
	public MensagemXml getMensagemErro() {
		MensagemXml msgSucesso = new MensagemXml();
		Descricao descricao = getDescricao();

		msgSucesso.setDescricao(descricao);

		Detalhamento detalhamento = new Detalhamento();

		List<Mensagem> listaMensagens = new ArrayList<Mensagem>();
		Mensagem mensagem = new Mensagem();
		mensagem.setCodigo(getErro().getCodigo());
		mensagem.setDescricao(getErro().getDescricao());
		listaMensagens.add(mensagem);

		detalhamento.setMensagem(listaMensagens);

		msgSucesso.setDetalhamento(detalhamento);
		msgSucesso.setCodigoFinal(getErro().getCodigo());
		msgSucesso.setDescricaoFinal(getErro().getDescricao());

		return msgSucesso;
	}
	
	public MensagemXmlSerpro getMensagemErroSerpro() {
		MensagemXmlSerpro msgSucesso = new MensagemXmlSerpro();
		msgSucesso.setNomeArquivo(getNomeArquivo());
		
		List<ComarcaDetalhamentoSerpro> listaComarcas = new ArrayList<ComarcaDetalhamentoSerpro>();
		ComarcaDetalhamentoSerpro comarcaDetalhamento = new ComarcaDetalhamentoSerpro();
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

	private Descricao getDescricao() {
		Descricao descricao = new Descricao();
		descricao.setDataEnvio(DataUtil.getDataAtual() + " " + DataUtil.getHoraAtual());
		descricao.setTipoArquivo("XML_UPLOAD_REMESSA");
		descricao.setNomeArquivo(nomeArquivo);
		descricao.setDataMovimento(DataUtil.getDataAtual());
		descricao.setPortador(getCodigoCompensacao());
		descricao.setUsuario(getUsuario().getNome());
		return descricao;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getCodigoCompensacao() {
		if (codigoCompensacao == null) {
			codigoCompensacao = StringUtils.EMPTY;
			if (getNomeArquivo() != null) {
				if (getNomeArquivo().length() > 5) {
					codigoCompensacao = getNomeArquivo().substring(1, 4);				
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

}
