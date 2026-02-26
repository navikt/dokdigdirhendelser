# dokdigdirhendelser
Dokdigdirhendelser tar imot hendelser publisert av Altinn Event, og videreformidle disse internt via kafka-topic teamdokumenthandtering.privat-altinn-melding-hendelse. Interne tjenester som skal behandle hendelsene lytter på kafka topicen.

Dokdigdirhendelser eksponerer et webhook-endepunkt som er registrert i Altinn, og mottar hendelser som leveres til dette endepunktet. Se [dokumentasjonen fra Altinn](https://docs.altinn.studio/nb/events/subscribe-to-events/) for mer informasjon om hvordan dette gjøres.

Hendelsene blir publiserte til følgende Kafka-topic:
- teamdokumenthandtering.privat-altinn-melding-hendelse i prod-gcp 
- teamdokumenthandtering.privat-altinn-melding-hendelse i dev-gcp

For mer informasjon om appen kan du se på [funksjonell beskrivelse på confluence](https://confluence.adeo.no/spaces/BOA/pages/752101359/altinnMeldingHendelse).

### Henvendelser
Spørsmål om koden eller prosjektet kan rettes til [Slack-kanalen for \#Team Dokumentløsninger](https://nav-it.slack.com/archives/C6W9E5GPJ).
