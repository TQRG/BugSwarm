package de.thm.arsnova.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatusServiceImpl implements StatusService {
	private final Map<Class<?>, String> maintenanceReasons = new HashMap<>();

	@Override
	public void putMaintenanceReason(final Class<?> type, final String reason) {
		maintenanceReasons.put(type, reason);
	}

	@Override
	public void removeMaintenanceReason(final Class<?> type) {
		maintenanceReasons.remove(type);
	}

	@Override
	public Map<Class<?>, String> getMaintenanceReasons() {
		return maintenanceReasons;
	}

	@Override
	public boolean isMaintenanceActive() {
		return !maintenanceReasons.isEmpty();
	}
}
