package br.com.ieptbto.cra.entidade;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "TB_PERMISSAO_ENVIO")
@org.hibernate.annotations.Table(appliesTo = "TB_PERMISSAO_ENVIO")
public class PermissaoEnvio extends AbstractEntidade<PermissaoEnvio> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 568191150155657647L;
	public int id;
	public TipoInstituicao tipoInstituicao;
	public TipoArquivo tipoArquivo;

	@Id
	@Column(name = "ID_PERMISSAO_ENVIO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "TIPO_INSTITUICAO_ID")
	public TipoInstituicao getTipoInstituicao() {
		return tipoInstituicao;
	}

	@ManyToOne
	@JoinColumn(name = "TIPO_ARQUIVO_ID")
	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTipoInstituicao(TipoInstituicao tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	@Override
	public int compareTo(PermissaoEnvio entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
