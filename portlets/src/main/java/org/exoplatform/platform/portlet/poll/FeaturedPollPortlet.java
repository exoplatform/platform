package org.exoplatform.platform.portlet.poll;

import javax.portlet.*;
import java.io.IOException;

public class FeaturedPollPortlet extends GenericPortlet {

    public static final String CHOSEN_POLL = "POLL_ID";

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        PortletRequestDispatcher prDispatcher = getPortletContext().getRequestDispatcher("/featured-poll/index.jsp");

        PortletPreferences preferences = request.getPreferences();
        String chosenPoll = preferences.getValue(CHOSEN_POLL, "");
        request.setAttribute("pollId", chosenPoll);

        prDispatcher.include(request, response);
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String pollId = request.getParameter("pollid");

        PortletPreferences preferences = request.getPreferences();
        preferences.setValue(CHOSEN_POLL, pollId);
        preferences.store();

        response.setPortletMode(PortletMode.VIEW);
    }
}
