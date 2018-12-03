import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;



public class QueryAndReasonOnLocalRDF {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

		/**
		 * 
		 * Register Namespaces
		 * 
		 */

		String mcfURI = "http://www.mycorporisfabrica.org/ontology/mcf.owl#";
		PrintUtil.registerPrefix("mcf", mcfURI);

		String rdfURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		PrintUtil.registerPrefix("rdf", rdfURI);

		String goURI = "http://www.geneontology.org/dtds/go.dtd#";
		PrintUtil.registerPrefix("go", goURI);

		String oboURI = "http://purl.obolibrary.org/obo";
		PrintUtil.registerPrefix("obo", oboURI);

		/**
		 * 
		 * Test queries
		 * 
		 * 
		 */

		// List all anatomical entities involved in the flexion of knee joint
		String queryMyCF = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ " PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ " PREFIX  owl: <http://www.w3.org/2002/07/owl#>"
				+ " PREFIX  mcf: <http://www.mycorporisfabrica.org/ontology/mcf.owl#>" + " SELECT distinct ?y"
				+ " WHERE { ?s mcf:ContributesTo mcf:Flexion_of_knee_joint.  }";

		// List the name of all ontology entities created by the user 'midori'
		String queryGO = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ " PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ " PREFIX  owl: <http://www.w3.org/2002/07/owl#>"
				+ " PREFIX  go: <http://www.geneontology.org/formats/oboInOwl#>"
				+ " PREFIX  obo: <http://purl.obolibrary.org/obo/>" + " SELECT  ?id ?label"
				+ " WHERE  {?id <http://www.geneontology.org/formats/oboInOwl#created_by>  \"midori\" ."
				+ "         ?id <http://www.w3.org/2000/01/rdf-schema#label> ?label ." + "         } " + " LIMIT 10";

		// measure the size of a dataset
		String howManyTriples = "select (count(*) as ?total_number_of_triples) where {?s ?p ?o}";

		// measure the size of a dataset after
		String howManyTriplesAfter = "select (count(*) as ?total_number_of_triples) where {?s ?p ?o. ?o ?p ?z}";

		
		// sample some classes
		String someClasses = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "SELECT (count(*) as ?total_number_of_triples) where {?c rdfs:subClassOf ?o}";

		// sample some classes
		String PartOf = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+" PREFIX  mcf: <http://www.mycorporisfabrica.org/ontology/mcf.owl#> "
				+ "SELECT (count(*) as ?total_number_of_triples) where {?c mcf:PartOf ?o}";

		//mcf:RightSubClassOf
		String RightSubClassOf = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+" PREFIX  mcf: <http://www.mycorporisfabrica.org/ontology/mcf.owl#> "
				+ "SELECT (count(*) as ?total_number_of_triples) where {?c mcf:RightSubClassOf ?o}";

		//mcf:LeftSubClassOf
		String LeftSubClassOf = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+" PREFIX  mcf: <http://www.mycorporisfabrica.org/ontology/mcf.owl#> "
				+ "SELECT (count(*) as ?total_number_of_triples) where {?c mcf:LeftSubClassOf ?o}";

		//owl:sameAs
		String sameAs = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+" PREFIX  mcf: <http://www.mycorporisfabrica.org/ontology/mcf.owl#> "
				+ " PREFIX  owl: <http://www.w3.org/2002/07/owl#>"
				+ "SELECT (count(*) as ?total_number_of_triples) where {?c owl:sameAs ?o}";

		/**
		 * 
		 * Test rules
		 * 
		 */

		StringBuilder rules = new StringBuilder();

		// define rules here
		rules.append("[rule1:  (?x mcf:PartOf ?y), (?y mcf:PartOf ?z) -> (?x mcf:PartOf ?z)] ");
		rules.append("[rule2:  (?x rdfs:subClassOf ?y), (?y rdfs:subClassOf ?z) -> (?x rdfs:subClassOf ?z)] ");

		/**
		 * 
		 * Create a data model and load file
		 * 
		 */

		Model model = ModelFactory.createDefaultModel();

		String pathToOntology = "F:\\respository\\AI_KM_JENA\\MyCF2Sat.rdf";

		InputStream in = FileManager.get().open(pathToOntology);

		Long start = System.currentTimeMillis();

		model.read(in, null);

		System.out.println("Import time : " + (System.currentTimeMillis() - start));

		/**
		 * 
		 * Starting a reasoner
		 * 
		 */

		GenericRuleReasoner reasoner = (GenericRuleReasoner) GenericRuleReasonerFactory.theInstance().create(null);

		reasoner.setRules(Rule.parseRules(rules.toString()));

		// change the type of reasoner
		reasoner.setMode(GenericRuleReasoner.HYBRID);

		start = System.currentTimeMillis();

		InfModel inf = ModelFactory.createInfModel(reasoner, model);

		System.out.println("Rules pre-processing time : " + (System.currentTimeMillis() - start));

		/**
		 * 
		 * Create a query object
		 * 
		 */

		Query query = QueryFactory.create(sameAs);

		start = System.currentTimeMillis();

		QueryExecution qexec = QueryExecutionFactory.create(query, inf);

		System.out.println("Query pre-processing time : " + (System.currentTimeMillis() - start));

		/**
		 * 
		 * Execute Query and print result
		 * 
		 */
		start = System.currentTimeMillis();

		try {

			ResultSet rs = qexec.execSelect();

			ResultSetFormatter.out(System.out, rs, query);

		} finally {

			qexec.close();
		}

		System.out.println("Query + Display time : " + (System.currentTimeMillis() - start));

		// /**
		// *
		// * Export saturated ontology
		// *
		// */
		//
		// PrintWriter resultWriter;
		// try {
		// resultWriter = new PrintWriter("testmycfsat.rdf");
		//
		// inf.write(resultWriter);
		//
		// resultWriter.close();
		//
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }

	}
}
