
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

public class QueryAndReasonOnGO {

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
        
        // List the name of all ontology entities created by the user 'midori'
        String queryGO = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + " PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + " PREFIX  owl: <http://www.w3.org/2002/07/owl#>"
                + " PREFIX  go: <http://www.geneontology.org/dtds/go.dtd#>"
                + " PREFIX  obo: <http://purl.obolibrary.org/obo/>" + " SELECT  ?id ?label"
                + " WHERE  {?id <http://www.geneontology.org/formats/oboInOwl#created_by>  \"midori\" ."
                + "         ?id <http://www.w3.org/2000/01/rdf-schema#label> ?label ." + "         } " + " LIMIT 10";

        // measure the size of a dataset
        String howManyTriples = "select (count(*) as ?total_number_of_triples) where {?s ?p ?o}";


        /**
         *
         * Test rules
         *
         */
        StringBuilder rules_mcf = new StringBuilder();

//         define rules here
        //rules_mcf.append("[rule14: (?a go:negatively_regulates ?c), (?c go:part_of ?b)  -> (?a go:regulates ?b)]");

        /**
         *
         * Create a data model and load file
         *
         */
        Model model_mcf = ModelFactory.createDefaultModel();

        String pathToOntology = "/path/to/go_daily-termdb.rdf-xml";

        InputStream in_mcf = FileManager.get().open(pathToOntology);

        Long start = System.currentTimeMillis();

        model_mcf.read(in_mcf, null);

        System.out.println("Import time : " + (System.currentTimeMillis() - start));

        /**
         *
         * Starting a reasoner
         *
         */
        GenericRuleReasoner reasoner_mcf = (GenericRuleReasoner) GenericRuleReasonerFactory.theInstance().create(null);

        reasoner_mcf.setRules(Rule.parseRules(rules_mcf.toString()));

        // change the type of reasoner
        reasoner_mcf.setMode(GenericRuleReasoner.HYBRID);

        start = System.currentTimeMillis();

        InfModel inf_mcf = ModelFactory.createInfModel(reasoner_mcf, model_mcf);

        System.out.println("Rules pre-processing time : " + (System.currentTimeMillis() - start));

        /**
         *
         * Create a query object
         *
         */
        Query query = QueryFactory.create(howManyTriples);

        start = System.currentTimeMillis();

        QueryExecution qexec_mcf = QueryExecutionFactory.create(query, inf_mcf);
        //QueryExecution qexec_mcf = QueryExecutionFactory.create(query, model_mcf);

        System.out.println("Query pre-processing time : " + (System.currentTimeMillis() - start));

        /**
         *
         * Execute Query and print result
         *
         */
        start = System.currentTimeMillis();

        try {

            ResultSet rs_mcf = qexec_mcf.execSelect();

            ResultSetFormatter.out(System.out, rs_mcf, query);

        } finally {

            qexec_mcf.close();
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
