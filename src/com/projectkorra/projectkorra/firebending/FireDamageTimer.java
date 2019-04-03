package com.projectkorra.projectkorra.firebending;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.EntityBendingDeathEvent;

import java.util.concurrent.ConcurrentHashMap;

public class FireDamageTimer {

	private static final int MAX_TICKS = 90;
	private static final int DAMAGE = 1;
	private static final long BUFFER = 30;
	private static final ConcurrentHashMap<Entity, Player> INSTANCES = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<Entity, Long> TIMES = new ConcurrentHashMap<>();

	public FireDamageTimer(Entity entity, Player source) {
		if (entity.getEntityId() == source.getEntityId()) {
			return;
		}
		if (INSTANCES.containsKey(entity)) {
			INSTANCES.replace(entity, source);
		} else {
			INSTANCES.put(entity, source);
		}
	}

	public static boolean isEnflamed(Entity entity) {
		if (INSTANCES.containsKey(entity)) {
			if (TIMES.containsKey(entity)) {
				long time = TIMES.get(entity);
				if (System.currentTimeMillis() < time + BUFFER) {
					return false;
				}
			}
			TIMES.put(entity, System.currentTimeMillis());
			return true;
		} else {
			return false;
		}
	}

	public static void dealFlameDamage(Entity entity) {
		if (INSTANCES.containsKey(entity) && entity instanceof LivingEntity) {
			if (entity instanceof Player) {
				if (!HeatControlExtinguish.canBurn((Player) entity)) {
					return;
				}
			}
			LivingEntity Lentity = (LivingEntity) entity;
			Player source = INSTANCES.get(entity);
			if (Lentity.getHealth() - DAMAGE <= 0 && !entity.isDead()) {
				EntityBendingDeathEvent event = new EntityBendingDeathEvent(entity, DAMAGE, CoreAbility.getAbilitiesByElement(Element.FIRE).get(0));
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
			
			Lentity.damage(DAMAGE, source);
			if (entity.getFireTicks() > MAX_TICKS) {
				entity.setFireTicks(MAX_TICKS);
			}
		}
	}

	public static void handleFlames() {
		for (Entity entity : INSTANCES.keySet()) {
			if (entity.getFireTicks() <= 0) {
				INSTANCES.remove(entity);
			}
		}
	}

}
