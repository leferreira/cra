package br.com.ieptbto.cra.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.view.ViewBatimentoRetorno;

/**
 * @author Thasso Araújo
 *
 */
public class MultipleChoiceDepositosBatimentoPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	
	public MultipleChoiceDepositosBatimentoPanel(String id, final IModel<ViewBatimentoRetorno> model, List<Deposito> depositosExtrato) {
		super(id, model);
		ArrayList<Deposito> depositosArquivo = new ArrayList<Deposito>();
		ListMultipleChoice<Deposito> multipleChoice = new ListMultipleChoice<Deposito>("depositos", new Model<ArrayList<Deposito>>(depositosArquivo), depositosExtrato);
		multipleChoice.add(new AttributeAppender("class", "chosen-select text-center"));
		model.getObject().setListaDepositos(depositosArquivo);
		add(multipleChoice);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
	}
}