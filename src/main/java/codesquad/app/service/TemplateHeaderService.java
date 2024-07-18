package codesquad.app.service;

import codesquad.app.model.User;
import codesquad.container.Component;
import codesquad.http.HttpRequest;
import codesquad.http.security.Session;
import codesquad.template.DynamicHtml;

@Component
public class TemplateHeaderService {
    private final SessionService sessionService;

    public TemplateHeaderService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void addHeaderProperties(HttpRequest httpRequest, DynamicHtml dynamicHtml) {
        Session session = sessionService.getSession(httpRequest);

        boolean authenticated = sessionService.isAuthenticated(session);
        dynamicHtml.setArg("authenticated", authenticated);
        if (authenticated) {
            User user = sessionService.getUser(session);
            dynamicHtml.setArg("user", user);
        }
    }
}
