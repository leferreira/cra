package br.com.ieptbto.cra.page.batimento;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.ViewBatimento;

/**
 * @author Thasso Araújo
 *
 */
public class SelectDepositosBatimentoPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	public SelectDepositosBatimentoPanel(String id, final IModel<ViewBatimento> model, List<Deposito> depositosExtrato) {
		super(id, model);
		ArrayList<Deposito> depositosArquivo = new ArrayList<Deposito>();
		this.add(new ListMultipleChoice<Deposito>("depositos", new Model<ArrayList<Deposito>>(depositosArquivo), depositosExtrato));
		model.getObject().setListaDepositos(depositosArquivo);
	}
}
