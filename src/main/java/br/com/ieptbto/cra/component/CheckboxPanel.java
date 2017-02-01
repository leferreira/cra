package br.com.ieptbto.cra.component;

import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author Thasso Araújo
 *
 */
public class CheckboxPanel<T> extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	public CheckboxPanel(String id, IModel<T> model, CheckGroup<T> group) {
		super(id, model);
		this.add(new Check<T>("checkBox", model, group));
	}
}