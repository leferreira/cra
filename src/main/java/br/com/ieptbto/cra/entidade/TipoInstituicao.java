package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "TIPO_INSTITUICAO")
@org.hibernate.annotations.Table(appliesTo = "TIPO_INSTITUICAO")
public class TipoInstituicao extends AbstractEntidade<TipoInstituicao> {

	private static final long serialVersionUID = 1L;
	private int id;
	private String tipoInstituicao;
	private List<Instituicao> listaInstituicoes;
	private List<TipoArquivo> arquivosEnvioPermitido;

	@Id
	@Column(name = "ID_TIPO_INSTITUICAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "TIPO_INSTITUICAO", nullable = false)
	public String getTipoInstituicao() {
		return tipoInstituicao;
	}
	
	@OneToMany(mappedBy = "tipoInstituicao")
	public List<Instituicao> getListaInstituicoes() {
		return listaInstituicoes;
	}
	
	@ManyToMany
	@JoinTable(name="PERMISSAO_ENVIO_ARQUIVO", 
			joinColumns= @JoinColumn(name="TIPO_INSTITUICAO_ID"),
			inverseJoinColumns = @JoinColumn(name="TIPO_ARQUIVO_ID"))
	public List<TipoArquivo> getArquivosEnvioPermitido() {
		return arquivosEnvioPermitido;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setTipoInstituicao(String tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
	}
	
	public void setListaInstituicoes(List<Instituicao> listaInstituicoes) {
		this.listaInstituicoes = listaInstituicoes;
	}

	public void setarquivosEnvioPermitido(List<TipoArquivo> arquivosEnvioPermitido) {
		this.arquivosEnvioPermitido = arquivosEnvioPermitido;
	}
	
	@Override
	public int compareTo(TipoInstituicao entidade) {
		return 0;
	}

}
