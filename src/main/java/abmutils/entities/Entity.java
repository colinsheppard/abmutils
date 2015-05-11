package abmutils.entities;

import java.util.concurrent.atomic.AtomicInteger;

public class Entity {
	public static AtomicInteger atomicID = new AtomicInteger();
	public int id;

	public Entity() {
		id = atomicID.addAndGet(1);
	}
}
