package br.com.ieptbto.cra.relatorio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class SinteticoJRDataSource implements Serializable{

	/***/
	private static final long serialVersionUID = 1L;
	private String nomeMunicipio;
	private BigInteger apontados;
	private BigInteger totalTitulos;
	private BigInteger irregulares;
	private BigInteger sustados;
	private BigInteger retirados;
	private BigInteger protestados;
	private BigInteger pagos;
	private BigDecimal valorTitulo;
	private BigDecimal valorSaldo;
	private BigDecimal valorRepasse;
	private BigDecimal valorCusta;
	private BigDecimal valorDemaisDespesas;
	
	public SinteticoJRDataSource() {
		// TODO Auto-generated constructor stub
	}

	public String getNomeMunicipio() {
		return nomeMunicipio;
	}

	public BigInteger getTotalTitulos() {
		return totalTitulos;
	}

	public BigInteger getIrregulares() {
		return irregulares;
	}

	public BigInteger getSustados() {
		return sustados;
	}

	public BigInteger getRetirados() {
		return retirados;
	}

	public BigInteger getProtestados() {
		return protestados;
	}

	public BigInteger getPagos() {
		return pagos;
	}

	public BigDecimal getValorSaldo() {
		return valorSaldo;
	}

	public BigDecimal getValorRepasse() {
		return valorRepasse;
	}

	public BigDecimal getValorCusta() {
		return valorCusta;
	}

	public BigDecimal getValorDemaisDespesas() {
		return valorDemaisDespesas;
	}

	public void setNomeMunicipio(String nomeMunicipio) {
		this.nomeMunicipio = nomeMunicipio;
	}

	public void setTotalTitulos(BigInteger totalTitulos) {
		this.totalTitulos = totalTitulos;
	}

	public void setIrregulares(BigInteger irregulares) {
		this.irregulares = irregulares;
	}

	public void setSustados(BigInteger sustados) {
		this.sustados = sustados;
	}

	public void setRetirados(BigInteger retirados) {
		this.retirados = retirados;
	}

	public void setProtestados(BigInteger protestados) {
		this.protestados = protestados;
	}

	public void setPagos(BigInteger pagos) {
		this.pagos = pagos;
	}

	public void setValorSaldo(BigDecimal valorSaldo) {
		this.valorSaldo = valorSaldo;
	}

	public void setValorRepasse(BigDecimal valorRepasse) {
		this.valorRepasse = valorRepasse;
	}

	public void setValorCusta(BigDecimal valorCusta) {
		this.valorCusta = valorCusta;
	}

	public void setValorDemaisDespesas(BigDecimal valorDemaisDespesas) {
		this.valorDemaisDespesas = valorDemaisDespesas;
	}

	public BigDecimal getValorTitulo() {
		return valorTitulo;
	}

	public void setValorTitulo(BigDecimal valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public BigInteger getApontados() {
		return apontados;
	}

	public void setApontados(BigInteger apontados) {
		this.apontados = apontados;
	}
	
	
}
