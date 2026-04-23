package vnp.thread;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class ReadXMLFile {

    public static void main(String args[]) {

        try {

            File fXmlFile = new File("D:/XML.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.
                                               newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" +
                               doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("Balances");

            System.out.println("----------------------------");
            System.out.println(nList.getLength());
//            System.out.println("Beginning....");

            Node nNodeBalanceS = nList.item(0);

            NodeList nBalace = nNodeBalanceS.getChildNodes();
            System.out.println("----------------------------");
            System.out.println(nBalace.getLength());

            for (int temp = 0; temp < nBalace.getLength(); temp++) {
                Node nNode = nBalace.item(temp);

                if (nNode.getNodeName().equals("Balance")) {
                    System.out.println("\nCurrent Element :" +
                                       nNode.getNodeName());
                    Element eElement = (Element) nNode;
                    System.out.println("Balance : " +
                                       eElement.
                                       getElementsByTagName("Balance").
                                       item(0).getTextContent());

                    System.out.println("AvailableBalance : " +
                                       eElement.
                                       getElementsByTagName("AvailableBalance").
                                       item(0).getTextContent());
                    System.out.println("AccountExpiration : " +
                                       eElement.
                                       getElementsByTagName("AccountExpiration").
                                       item(0).getTextContent());
                    System.out.println("BalanceName : " +
                                       eElement.
                                       getElementsByTagName("BalanceName").item(
                                               0).getTextContent());

                }
            }

//            for (int temp = 0; temp < nList.getLength(); temp++) {
//
//                Node nNode = nList.item(temp);
//
//                System.out.println("\nCurrent Element :" + nNode.getNodeName());
//
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//
//                    Element eElement = (Element) nNode;
//                    System.out.println("Test : " + eElement.getNodeName());
//
//                    System.out.println("AvailableBalance : " +
//                                       eElement.
//                                       getElementsByTagName("AvailableBalance").
//                                       item(0).getTextContent());
//                    System.out.println("AccountExpiration : " +
//                                       eElement.
//                                       getElementsByTagName("AccountExpiration").
//                                       item(0).getTextContent());
//                    System.out.println("BalanceName : " +
//                                       eElement.
//                                       getElementsByTagName("BalanceName").item(
//                                               0).getTextContent());
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
