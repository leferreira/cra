package br.com.ieptbto.cra.page.relatorio.arquivo;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioInstituicoesCartoriosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	private Arquivo arquivo;

	public RelatorioInstituicoesCartoriosPage() {
		this.arquivo = new Arquivo();
		
		carregarPanelInstituicaoCartorio();
	}

	private void carregarPanelInstituicaoCartorio() {
		if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)){
			add(new RelatorioCartorioPanel("relatorioInstituicoesCartorioPanel", getModel(), getUser()));
		} else if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			add(new RelatorioInstituicaoPanel("relatorioInstituicoesCartorioPanel", getModel(), getUser()));
		}
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}