import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.VCARD;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static java.lang.Boolean.TRUE;
import static org.apache.jena.mem.HashCommon.NotifyEmpty.ignore;
import static org.apache.jena.ontology.OntModelSpec.OWL_MEM;
import static org.apache.jena.ontology.OntModelSpec.OWL_MEM_MICRO_RULE_INF;

public class Test {

	public static void main(String[] args) throws IOException {
		OntModel otm = ModelFactory.createOntologyModel( );
		otm.read("news.rdf");
		String NS ="http://mynamespace/news/#";


		OntClass theme = otm.createClass(NS + "Theme");
		OntClass sport = otm.createClass(NS + "Sport");
		OntClass science = otm.createClass(NS + "Scienece");
		OntClass crypt = otm.createClass(NS + "sci.crypt");
		OntClass baseball = otm.createClass(NS + "rec.sport.baseball");
		OntClass hockey = otm.createClass(NS + "rec.sport.hockey");

		theme.addSubClass(sport);
		theme.addSubClass(science);
		sport.addSubClass(baseball);
		sport.addSubClass(hockey);

		for (Iterator<OntClass> i = theme.listSubClasses(); i.hasNext(); ) {
			OntClass c = i.next();
			System.out.println( c.getURI() );
		}
		FileWriter out = null;
		out = new FileWriter("test_news.owl");
		otm.write(out, "RDF/XML-ABBREV");


		/**
		 *
		 * Register Namespaces
		 *
		 */

		String owlURI = "http://purl.org/dc/elements/1.1/";
		PrintUtil.registerPrefix("owl", owlURI);

		String rdfURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		PrintUtil.registerPrefix("rdf", rdfURI);

		String dcURI = "http://purl.org/dc/elements/1.1/";
		PrintUtil.registerPrefix("dc", dcURI);

		String jURI = "http://www.w3.org/ns/dcat#";
		PrintUtil.registerPrefix("j.0", jURI);

		String rdfsURI = "http://www.w3.org/2000/01/rdf-schema#";
		PrintUtil.registerPrefix("rdfs", rdfsURI);

		String xsdURI = "http://www.w3.org/ns/dcat#";
		PrintUtil.registerPrefix("xsd", xsdURI);

		// List all anatomical entities involved in the flexion of knee joint
		String Q1 = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				" PREFIX j.0: <http://www.w3.org/ns/dcat#>" +
				" SELECT ?t where {?c rdfs:subClassOf ?o. ?o rdfs:subClassOf ?z. ?t j.0:theme ?c}";

		StringBuilder rules = new StringBuilder();

		// define rules here
		rules.append("[rule1:  (?x rdfs:subClassOf ?y), (?y rdfs:subClassOf ?z) -> (?x rdfs:subClassOf ?z)] ");

		Model model = ModelFactory.createDefaultModel();

		String pathToOntology = "test_news.owl";

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

		Query query = QueryFactory.create(Q1);

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


		}}
