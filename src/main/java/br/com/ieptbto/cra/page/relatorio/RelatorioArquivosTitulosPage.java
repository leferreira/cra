package br.com.ieptbto.cra.page.relatorio;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class RelatorioArquivosTitulosPage extends BasePage<Arquivo> {

	private Arquivo arquivo;
	private Instituicao instituicao;

	public RelatorioArquivosTitulosPage() {
		this.arquivo = new Arquivo();
		this.instituicao = getUser().getInstituicao();
		
		if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)){
			add(new RelatorioArquivosTitulosCraPanel("buscarArquivoInputPanel", getModel(), getInstituicao()));
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)){
			add(new RelatorioArquivosTitulosCartorioPanel("buscarArquivoInputPanel", getModel(), getInstituicao(), getUser()));
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			add(new RelatorioArquivosTitulosInstituicaoPanel("buscarArquivoInputPanel", getModel(), getInstituicao(), getUser()));
		}
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