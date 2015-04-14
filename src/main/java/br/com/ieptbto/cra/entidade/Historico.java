package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDateTime;

import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_HISTORICO")
@org.hibernate.annotations.Table(appliesTo = "TB_HISTORICO")
public class Historico extends AbstractEntidade<Historico> {

	/***/
	private static final long serialVersionUID = 1L;

	private int id;
	private TituloRemessa titulo;
	private Remessa remessa;
	private LocalDateTime dataOcorrencia;
	private Usuario usuarioAcao;

	@Id
	@Column(name = "ID_HISTORICO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "TITULO_ID")
	public TituloRemessa getTitulo() {
		return titulo;
	}

	@ManyToOne
	@JoinColumn(name = "REMESSA_ID")
	public Remessa getRemessa() {
		return remessa;
	}

	@ManyToOne
	@JoinColumn(name = "USUARIO_ID")
	public Usuario getUsuarioAcao() {
		return usuarioAcao;
	}

	@Column(name = "DATA_OCORRENCIA")
	public LocalDateTime getDataOcorrencia() {
		return dataOcorrencia;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitulo(TituloRemessa titulo) {
		this.titulo = titulo;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public void setDataOcorrencia(LocalDateTime dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public void setUsuarioAcao(Usuario usuarioAcao) {
		this.usuarioAcao = usuarioAcao;
	}

	@Override
	public String toString() {
		return "Arquivo " + remessa.getArquivo().getNomeArquivo() + " importado em " + DataUtil.localDateToString(dataOcorrencia.toLocalDate()) 
				+ " às " +   dataOcorrencia.toDateTime().toString()  + " por " + usuarioAcao.getNome() + ".";
	}

	@Override
	public int compareTo(Historico entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
