package no.nav.dokdigdirhendelser.altinnsubscription;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AltinnAbonnementScheduler {

	private final LeaderElectionConsumer leaderElectionConsumer;
	private final AltinnSubscriptionService altinnSubscriptionService;

	public AltinnAbonnementScheduler(LeaderElectionConsumer leaderElectionConsumer,
									 AltinnSubscriptionService altinnSubscriptionService) {
		this.leaderElectionConsumer = leaderElectionConsumer;
		this.altinnSubscriptionService = altinnSubscriptionService;
	}


	@Scheduled(cron = "${dokdigdirhendelser.subscription.scheduler:0 20 12 16 2 ?}")
	public void subscribe() {
		if (leaderElectionConsumer.isLeader()) {
			AltinnAbonnementResponse altinnAbonnementResponse = altinnSubscriptionService.abonnerAltinnEvent();
			log.info("Abonnement på Altinn events registrert med id= {}", altinnAbonnementResponse.id());

			AltinnAbonnementResponse hentAbonnement = altinnSubscriptionService.hentAbonnement(altinnAbonnementResponse.id());
			log.info("Hentet abonnert Altinn event response: {}", hentAbonnement);
		}
	}
}

