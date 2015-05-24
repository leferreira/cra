package br.com.ieptbto.cra.page.relatorio;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.page.base.BasePage;

public class RelatorioArquivosTitulosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	private Arquivo arquivo;
	private Form<Arquivo> form;
	private Instituicao instituicao;

	public RelatorioArquivosTitulosPage() {
		this.arquivo = new Arquivo();
		this.instituicao = getUser().getInstituicao();
		
		form = new Form<Arquivo>("form", getModel());
		if (instituicao.getTipoInstituicao().getTipoInstituicao().equals("CRA")){
			form.add(new RelatorioArquivosTitulosCraPanel("buscarArquivoInputPanel", getModel(), instituicao));
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals("Cartório")){
			form.add(new RelatorioArquivosTitulosCartorioPanel("buscarArquivoInputPanel", getModel(), instituicao));
		} else {
			form.add(new RelatorioArquivosTitulosInstituicaoPanel("buscarArquivoInputPanel", getModel(), instituicao));
		}
		add(form);
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}