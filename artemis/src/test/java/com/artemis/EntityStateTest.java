package com.artemis;

import com.artemis.annotations.Wire;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@Wire @SuppressWarnings("deprecation")
public class EntityStateTest {
	private World world;
	private Entity e;
	private Management management;

	@Before
	public void init() {
		world = new World(new WorldConfiguration()
				.setManager(Management.class));
		world.inject(this);
		world.initialize();
		
		e = world.createEntity();
	}
	
	@Test
	public void disable_enable_test() {
		e.disable();
		e.enable();
		world.process();
		
		Assert.assertEquals(1, management.state);
	}
	
	@Test
	public void enable_disable_test() {
		e.enable();
		e.disable();
		world.process();
		
		Assert.assertEquals(-1, management.state);
	}
	
	public static class Management extends Manager {
		int state = 0;
		
		@Override
		public void enabled(Entity e) {
			state++;
		}
		
		@Override
		public void disabled(Entity e) {
			state--;
		}
	}
}
