import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

public class QueryModel {

	public static void main(String[] args) {
		
		String inputfile = ".\\news.rdf";
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

		String Q1 = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
				"PREFIX j.0:<http://www.w3.org/ns/dcat#>" +
				"PREFIX dc:<http://purl.org/dc/elements/1.1/>"+
				"SELECT ?s\n" +
				"WHERE {?s  dc:title ?y.}" ;

		String Q2 = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
				"PREFIX j.0:<http://www.w3.org/ns/dcat#>" +
				"PREFIX dc:<http://purl.org/dc/elements/1.1/>"+
				"SELECT ?y\n" +
				"WHERE {?s  j.0:theme 'sci.med'." +
				"?s dc:title ?y}" ;

		String Q3 = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
				"PREFIX j.0:<http://www.w3.org/ns/dcat#>" +
				"PREFIX dc:<http://purl.org/dc/elements/1.1/>"+
				"SELECT ?y\n" +
				"WHERE {?s  dc:title 'China to ease tax burdens for private enterprises'." +
				"?s j.0:theme ?y}" ;

		Query query = QueryFactory.create(Q1) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		  try {
		    ResultSet results = qexec.execSelect() ;
		    for ( ; results.hasNext() ; )
		    {
		      QuerySolution soln = results.nextSolution() ;
		      System.out.println(soln.toString());
		      
		    }
		  } finally { qexec.close() ; }

	}

}
