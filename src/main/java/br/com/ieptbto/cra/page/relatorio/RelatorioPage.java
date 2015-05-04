package br.com.ieptbto.cra.page.relatorio;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.page.base.BasePage;

public class RelatorioPage extends BasePage<Arquivo>{

	/***/
	private static final long serialVersionUID = 1L;
	private Arquivo arquivo;
	private Form<Arquivo> form;
	private Instituicao instituicao;
	
	public RelatorioPage() {
		this.arquivo  = new Arquivo();
		this.instituicao = getUser().getInstituicao();
		
		form = new Form<Arquivo>("form", getModel());
		if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals("CRA")){
			form.add(new RelatorioCraPanel("relatorioPanel", getModel(), getInstituicao()));
		} else if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals("Cartório")){
			form.add(new RelatorioCartorioPanel("relatorioPanel", getModel(), getInstituicao()));
		} else {
			form.add(new RelatorioBancoPanel("relatorioPanel", getModel(), getInstituicao()));
		}
		add(form);
	}

	
	public Arquivo getArquivo() {
		return arquivo;
	}


	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}


	public Instituicao getInstituicao() {
		return instituicao;
	}


	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}


	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
