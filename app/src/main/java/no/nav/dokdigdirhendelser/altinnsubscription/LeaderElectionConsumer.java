package no.nav.dokdigdirhendelser.altinnsubscription;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import static java.net.InetAddress.getLocalHost;

@Slf4j
@Component
public class LeaderElectionConsumer {

	private final RestClient restClient;
	private final ObjectMapper objectMapper;

	public LeaderElectionConsumer(RestClient restClient,
								  ObjectMapper objectMapper,
								  @Value("${elector.path}") String electorPath) {
		this.restClient = restClient.mutate()
				.baseUrl(electorPath.startsWith("http") ? electorPath : "http://" + electorPath)
				.build();
		this.objectMapper = objectMapper;
	}

	public boolean isLeader() {
		return restClient.get()
				.exchange((clientRequest, response) -> {
					if (response.getStatusCode().isError()) {
						ProblemDetail problemDetail = objectMapper.convertValue(response.getBody(), ProblemDetail.class);
						log.error("Failed to retrieve leader information: {}", problemDetail);
						return false;
					}
					String responseBody = response.bodyTo(String.class);
					String leader = objectMapper.readTree(responseBody).get("name").asString();
					String hostname = getLocalHost().getHostName();
					return hostname.equals(leader);
				});
	}

}
