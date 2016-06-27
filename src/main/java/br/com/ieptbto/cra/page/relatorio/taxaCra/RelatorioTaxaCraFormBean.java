package br.com.ieptbto.cra.page.relatorio.taxaCra;

import java.io.Serializable;
import java.util.Date;

import br.com.ieptbto.cra.entidade.Instituicao;

public class RelatorioTaxaCraFormBean implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	private Date dataInicio;
	private Date dataFim;
	private Instituicao convenio;
	private Instituicao cartorio;
	private String situacaoTituloRelatorio;

	public Date getDataInicio() {
		return dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public Instituicao getConvenio() {
		return convenio;
	}

	public Instituicao getCartorio() {
		return cartorio;
	}

	public String getSituacaoTituloRelatorio() {
		return situacaoTituloRelatorio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public void setConvenio(Instituicao convenio) {
		this.convenio = convenio;
	}

	public void setCartorio(Instituicao cartorio) {
		this.cartorio = cartorio;
	}

	public void setSituacaoTituloRelatorio(String situacaoTituloRelatorio) {
		this.situacaoTituloRelatorio = situacaoTituloRelatorio;
	}
}