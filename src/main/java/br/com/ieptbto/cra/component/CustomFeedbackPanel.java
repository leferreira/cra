package br.com.ieptbto.cra.component;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

@SuppressWarnings("serial")
public class CustomFeedbackPanel extends FeedbackPanel {

	public CustomFeedbackPanel(String id) {
		super(id);
	}

	public CustomFeedbackPanel(String id, IFeedbackMessageFilter filter) {
		super(id, filter);
	}

	@Override
	protected String getCSSClass(FeedbackMessage message) {
		String css;
		switch (message.getLevel()) {
			case FeedbackMessage.SUCCESS:
				css = "alert alert-success alert-dismissable";
				break;
			case FeedbackMessage.INFO:
				css = "alert alert-info alert-dismissable";
				break;
			case FeedbackMessage.ERROR:
				css = "alert alert-danger alert-dismissable";
				break;
			default:
				css = "alert alert-dismissable";
		}

		return css;
	}
}