package br.com.ieptbto.cra.entidade;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
	private Date horaEnvioInicio;
	private Date horaEnvioFim;
	private List<TipoInstituicao> listaTipoInstituicao;

	@Id
	@Column(name = "ID_TIPO_ARQUIVO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "CONSTANTE", length = 2, unique = true)
	public String getConstante() {
		return constante.trim().toUpperCase();
	}

	@Column(name = "TIPO_ARQUIVO", length = 30)
	public String getTipoArquivo() {
		return tipoArquivo.trim();
	}

	@ManyToMany(mappedBy = "arquivosEnvioPermitido")
	public List<TipoInstituicao> getListaTipoInstituicao() {
		return listaTipoInstituicao;
	}

	@Column(name = "HORA_ENVIO_FIM")
	@Temporal(TemporalType.TIME)
	public Date getHoraEnvioFim() {
		return horaEnvioFim;
	}

	@Column(name = "HORA_ENVIO_INICIO")
	@Temporal(TemporalType.TIME)
	public Date getHoraEnvioInicio() {
		return horaEnvioInicio;
	}

	public void setHoraEnvioFim(Date horaEnvioFim) {
		this.horaEnvioFim = horaEnvioFim;
	}

	public void setHoraEnvioInicio(Date horaEnvioInicio) {
		this.horaEnvioInicio = horaEnvioInicio;
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

	public void setListaTipoInstituicao(List<TipoInstituicao> listaTipoInstituicao) {
		this.listaTipoInstituicao = listaTipoInstituicao;
	}

	@Override
	public int compareTo(TipoArquivo entidade) {
		return 0;
	}

}
