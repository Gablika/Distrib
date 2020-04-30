package connections;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import io.atlassian.util.concurrent.Promise;
import persistantStorage.DistribSettingsConfigurableConfig;

import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

public class JiraConnection {

    private DistribSettingsConfigurableConfig distribConfig;
    JiraRestClient restClient;
    AuthenticationHandler auth = builder ->
            builder.setHeader(
                    "Authorization",
                    "Basic " + Base64.getEncoder().encodeToString((this.distribConfig.getJiraApiEmail() + ":" + this.distribConfig.getJiraApiToken()).getBytes())
            );

    public JiraConnection(DistribSettingsConfigurableConfig distribConfig) {
        this.distribConfig = distribConfig;
        this.restClient = initRestClient();
    }

    private JiraRestClient initRestClient() {
        return new AsynchronousJiraRestClientFactory().create(URI.create(this.distribConfig.getJiraUrl()), this.auth);
    }

    public List<String> getSummariesOfAllIssues() {
        final Promise<SearchResult> issue = restClient.getSearchClient().searchJql("assignee=currentUser()");
        Iterator<Issue> iterator = issue.claim().getIssues().iterator();
        List<String> titlesOfIssues = new ArrayList<String>();

        while(iterator.hasNext()) {
            Issue element = iterator.next();
            if(element.getIssueType().getName().equals("Task") && !element.getStatus().getName().equals("Done")) {
                titlesOfIssues.add(element.getSummary());
            }
        }

        return titlesOfIssues;
    }

}
