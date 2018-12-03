import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.VCARD;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        // some definitions
        String personURI = "http://mynamespace/news/#";
        // create an empty Model
        Model model = ModelFactory.createDefaultModel();
        File csv = new File("C:\\Users\\William\\AI_km\\text_news.csv");  // CSV文件路径
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            br.readLine();//第一行信息，为标题信息，不用,如果需要，注释掉
            String line = null;
            while ((line = br.readLine()) != null) {
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                // create the resource
                Resource document = model.createResource(personURI + item[1].replace(" ","_").replace("'","").replace("\"",""))
                        .addProperty(DCAT.theme, model.createResource(personURI +item[3].replace(" ","%20")))
                        .addProperty(DC.title,item[2]);
                String last = item[1];//这就是你要的数据了
                //int value = Integer.parseInt(last);//如果是数值，可以转化为数值
                //System.out.println(last);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();         // get next statement
            Resource subject = stmt.getSubject();   // get the subject
            Property predicate = stmt.getPredicate(); // get the predicate
            RDFNode object = stmt.getObject();    // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }
            System.out.println(" .");

            FileOutputStream news;
            news = new FileOutputStream(".\\news.rdf");
            model.write(news,"RDF/XML");




        }
    }
}
