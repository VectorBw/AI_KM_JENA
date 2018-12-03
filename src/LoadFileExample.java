

import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class LoadFileExample {

	public static void main(String[] args) {
		
		String inputfile = "SW-FAQ-feed.rdf";
		// create an empty model
		Model model = ModelFactory.createDefaultModel();
		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(inputfile);
		 
		if (in == null) {
			    throw new IllegalArgumentException(
			                                 "File: " + inputfile + " not found");
			}

			// read the RDF/XML file
			model.read(in, null);

			// write it to standard out
			model.write(System.out);

	}

}
