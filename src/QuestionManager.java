import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by wdyczko on 9/28/2015.
 */
public class QuestionManager {
    private ArrayList<Question> questions;
    private ArrayList<Integer> randedQuestions;
    private String description;

    public QuestionManager()
    {
        this.randedQuestions = new ArrayList<>();
        this.loadQuestions(getModules()[0]);
    }

    public void loadQuestions(String moduleName)
    {
        randedQuestions.clear();
        description = "";
        questions = null;
        try {
            questions = new ArrayList<>();
            File xmlFile = new File(System.getProperty("user.dir") + "\\data\\modules\\" + moduleName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(xmlFile);
            document.getDocumentElement().normalize();
            description = document.getElementsByTagName("Description").item(0).getTextContent();
            NodeList nList = document.getElementsByTagName("Question");
            for (int i = 0; i < nList.getLength(); i++ )
            {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element) nNode;
                    Question question = new Question();
                    question.setHangul(element.getElementsByTagName("Hangul").item(0).getTextContent());
                    question.setPronunciation(element.getElementsByTagName("Pronunciation").item(0).getTextContent());
                    question.setVowel( ( (element.getElementsByTagName("Vowel").item(0).getTextContent().equals("Yes")) ? true : false ) );
                    question.setConsonant(((element.getElementsByTagName("Consonant").item(0).getTextContent().equals("Yes")) ? true : false));
                    questions.add(question);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getModules()
    {
        ArrayList<String> list = new ArrayList<>();
        File dataFolder = new File(System.getProperty("user.dir") + "\\data\\modules\\");
        for( final File fileEntry : dataFolder.listFiles())
        {
            if(fileEntry.isFile())
            {
                list.add(fileEntry.getName());
            }
        }
        return list.toArray(new String[]{});
    }

    public String getModuleDescription()
    {
        return description;
    }

    public Question randQuestion()
    {
        Random random = new Random();
        int rand = random.nextInt(questions.size());
        while(randedQuestions.contains(rand))
        {
            if(randedQuestions.size() == questions.size())
            {
                randedQuestions.clear();
            }
            rand = random.nextInt(questions.size());
        }
        Question quest = questions.get(rand);
        randedQuestions.add(rand);
        return quest;
    }
}
