package ml2.load;

import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException {
String mm = """
package relational;
class Table {
}
""";

		String ocl = """
package relational

context Table inv people: 
	Table.allInstances()->size() > 3

endpackage		
		""";
		
		
		var lmm = ModelLoader.read(mm);
		var pivot = ModelLoader.readOCL(lmm, ocl);
		System.out.println(pivot);
		
	}
	
}
