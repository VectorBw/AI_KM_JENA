

import java.io.InputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class QueryModelExample {

	public static void main(String[] args) {
		
		String inputfile = "C:\\Users\\William\\Downloads\\news.rdf";
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
		model.write(System.out);

		String queryString = "SELECT ?s " +  
				"WHERE { ?s ?p ?o }";
		Query query = QueryFactory.create(queryString) ;
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
