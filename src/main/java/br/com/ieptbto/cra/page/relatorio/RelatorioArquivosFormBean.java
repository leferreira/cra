package br.com.ieptbto.cra.page.relatorio;

import java.io.Serializable;
import java.util.Date;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoRelatorio;

public class RelatorioArquivosFormBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date dataInicio;
	private Date dataFim;
	private TipoRelatorio tipoRelatorio;
	private TipoArquivoEnum tipoArquivo;
	private Instituicao instituicao;

	public Date getDataInicio() {
		return dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public TipoRelatorio getTipoRelatorio() {
		return tipoRelatorio;
	}

	public TipoArquivoEnum getTipoArquivo() {
		return tipoArquivo;
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public void setTipoArquivo(TipoArquivoEnum tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}

}