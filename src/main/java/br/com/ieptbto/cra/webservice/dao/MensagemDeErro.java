package br.com.ieptbto.cra.webservice.dao;

import java.util.ArrayList;
import java.util.List;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemRetornoXml;

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

	public MensagemRetornoXml getMensagemErro() {
		MensagemRetornoXml msgSucesso = new MensagemRetornoXml();
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
			codigoCompensacao = getNomeArquivo().substring(1, 4);
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
