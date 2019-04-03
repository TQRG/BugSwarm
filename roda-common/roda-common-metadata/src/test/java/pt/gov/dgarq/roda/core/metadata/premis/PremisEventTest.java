package pt.gov.dgarq.roda.core.metadata.premis;

import java.util.Date;

import pt.gov.dgarq.roda.core.data.EventPreservationObject;
import pt.gov.dgarq.roda.core.data.RODAObject;

public class PremisEventTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		EventPreservationObject eventPO = new EventPreservationObject(null,
				"roda:x", "roda:p:event", new Date(), new Date(),
				RODAObject.STATE_ACTIVE);

		eventPO.setID("roda:ev");
		eventPO
				.setEventType(EventPreservationObject.PRESERVATION_EVENT_TYPE_INGESTION);
		eventPO.setAgentID("roda:p:agent:007");
		eventPO
				.setAgentRole(EventPreservationObject.PRESERVATION_EVENT_AGENT_ROLE_INGEST_TASK);
		eventPO.setDatetime(new Date());
		eventPO.setEventDetail("details");
		eventPO.setOutcome("OK");
		eventPO.setOutcomeDetailNote("detail notes");
		eventPO.setOutcomeDetailExtension("<pá putinha qu& pariu>");

		try {

			new PremisEventHelper(eventPO).saveToByteArray();

		} catch (PremisMetadataException e) {
			e.printStackTrace();
		}

	}

}
