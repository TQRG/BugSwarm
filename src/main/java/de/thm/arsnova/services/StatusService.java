package de.thm.arsnova.services;

import java.util.Map;

public interface StatusService {
	void putMaintenanceReason(Class<?> type, String reason);
	void removeMaintenanceReason(Class<?> type);
	Map<Class<?>, String> getMaintenanceReasons();
	boolean isMaintenanceActive();
}
