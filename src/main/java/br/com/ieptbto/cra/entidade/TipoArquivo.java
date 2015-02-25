package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TIPO_ARQUIVO")
@org.hibernate.annotations.Table(appliesTo = "TIPO_ARQUIVO")
public class TipoArquivo extends AbstractEntidade<TipoArquivo> {

	/****/
	private static final long serialVersionUID = 1L;
	private int id;
	private String constante; // Constante ex: B-Remessa
	private String tipoArquivo;
	private List<TipoInstituicao> listaTipoInstituicao;

	@Id
	@Column(name = "ID_TIPO_ARQUIVO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "CONSTANTE")
	public String getConstante() {
		return constante;
	}

	@Column(name = "TIPO_ARQUIVO")
	public String getTipoArquivo() {
		return tipoArquivo;
	}

	@ManyToMany(mappedBy="arquivosEnvioPermitido")
	public List<TipoInstituicao> getListaTipoInstituicao() {
		return listaTipoInstituicao;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setConstante(String constante) {
		this.constante = constante;
	}

	public void setTipoArquivo(String tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setListaTipoInstituicao(
			List<TipoInstituicao> listaTipoInstituicao) {
		this.listaTipoInstituicao = listaTipoInstituicao;
	}

	@Override
	public int compareTo(TipoArquivo entidade) {
		return 0;
	}

}
