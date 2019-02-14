import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.macrokeysserver.option.LRUQueque;

public class TestLRUQueque {

	@Test
	public void testLimit() {
		final int LIMIT = 10;
		LRUQueque<Integer> q = new LRUQueque<>(LIMIT);
		
		for(int i = 0; i < 25; i++) {
			q.add(i);
			assertTrue(q.getObjects().size() <= LIMIT);
			assertTrue(q.getLimit() == LIMIT);
		}
	}
	
	
	@Test
	public void testOrder() {
		LRUQueque<Integer> q = new LRUQueque<>(10);
		
		q.add(1);
		q.add(2);
		q.add(3);
		q.add(4);
		q.add(5);
		
		List<Integer> l = q.getObjects();
		assertEquals(5, (int) l.get(0));
		assertEquals(4, (int) l.get(1));
		assertEquals(3, (int) l.get(2));
		assertEquals(2, (int) l.get(3));
		assertEquals(1, (int) l.get(4));
		
		q.add(1);
		assertEquals(1, (int) l.get(0));
		assertEquals(5, (int) l.get(1));
		assertEquals(4, (int) l.get(2));
		assertEquals(3, (int) l.get(3));
		assertEquals(2, (int) l.get(4));
	}

}
