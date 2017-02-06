package br.com.ieptbto.cra.component.dataTable;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author Thasso Araújo
 *
 */
public class CraButtonPanel<T> extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	private TypeCraButton typeButton;
	private ILinkListener linkListener;
	
	public CraButtonPanel(String id, IModel<T> model, TypeCraButton typeButton, ILinkListener linkListener) {
		super(id, model);
		this.typeButton = typeButton;
		this.linkListener = linkListener;
		addComponents();
	}

	private void addComponents() {
		Link<T> button = new Link<T>("button") {
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				linkListener.onLinkClicked();
			}
		};
		button.add(buttonSpanIcon());
		if (this.typeButton == TypeCraButton.DOWNLOAD_FILE || this.typeButton == TypeCraButton.ATTACHMENT) {
			button.setVisible(false);
		}
		add(button);

		Link<T> link = new Link<T>("link") {
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				linkListener.onLinkClicked();
			}
		};
		link.add(imageLink());
		if (this.typeButton == TypeCraButton.REMOVE || this.typeButton == TypeCraButton.REPORT
				|| this.typeButton == TypeCraButton.RETURN) {
			link.setVisible(false);
		}
		add(link);
	}

	private Label buttonSpanIcon() {
		Label label = new Label("label");
		label.add(new AttributeAppender("class", typeButton.getHtmlClass()));
		return label;
	}

	private ContextImage imageLink() {
		ContextImage image = new ContextImage("image");
		image.add(new AttributeAppender("class", "download-ico"));
		image.add(new AttributeAppender("src", typeButton.getSrcImage()));
		return image;
	}

}