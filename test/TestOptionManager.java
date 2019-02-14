import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.macrokeysserver.option.OptionManager;



public class TestOptionManager {

	// @Test
	// public void testLoadSavePrimitives() throws IOException {
	// 	final List<String> l1 = new ArrayList<>();
	// 	l1.add("S1");
	// 	l1.add("S2");
	// 	l1.add("S3");
	// 	l1.add("S4");
		
	// 	final List<String> l2 = new ArrayList<>();
	// 	l2.add("S5");
	// 	l2.add("S6");
	// 	l2.add("S7");
	// 	l2.add("S8");
		
	// 	final List<String> lvoid = new ArrayList<>();
		
	// 	File f = File.createTempFile("TEST", null);
		
	// 	OptionManager o = new OptionManager(f.getAbsolutePath());
		
	// 	o.load();
		
	// 	o.put("1", 1);
	// 	o.put("2", 2);
	// 	o.put("list1", l1);
	// 	o.put("3", 3);
	// 	o.put("list2", l2);
	// 	o.put("list_void", lvoid);
		
	// 	o.save();
		
	// 	OptionManager oCheck = new OptionManager(f.getAbsolutePath());
		
	// 	oCheck.load();
		
	// 	assertEquals(1, o.get("1", 0));
	// 	assertEquals(2, o.get("2", 0));
	// 	assertEquals(3, o.get("3", 0));
		
	// 	List<String> l1_check = o.get("list1", null);
	// 	assertTrue(l1_check.equals(l1));
		
	// 	List<String> l2_check = o.get("list2", null);
	// 	assertTrue(l2_check.equals(l2));
		
	// 	List<String> lvoid_check = o.get("list_void", null);
	// 	assertTrue(lvoid_check.equals(lvoid));
		
	// 	f.delete();
	// }

}
