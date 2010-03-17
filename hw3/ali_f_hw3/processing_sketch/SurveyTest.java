import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class SurveyTest extends TestCase {
	
	public void testReadsProgrammingLanguages() throws IOException
	{
		Survey survey = new Survey(readLines("test/survey.csv"));
		String[] expectedLanguages = new String[]{"BASIC", "C", "C#", "C++", "HTML / CSS", "Java", "JavaScript", "PHP", "Python", "Ruby", "SQL", "VB / VBScript"};
		String[] actualLanguages = survey.getLanguages();
		assertEquals(expectedLanguages.length, actualLanguages.length);
		for(int i = 0; i < expectedLanguages.length; i++)
			assertEquals("Language # " + i, expectedLanguages[i], actualLanguages[i]);
	}
	
	public void testGroupsStudentsByProgrammingLanguage() throws IOException
	{
		Survey survey = new Survey(readLines("test/survey.csv"));
		assertEquals(5, survey.getStudentsUsingProgrammingLanguage("Ruby").length);
	}
	
	private String[] readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List lines = new ArrayList();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        lines.remove(0);
        bufferedReader.close();
        return (String[]) lines.toArray(new String[lines.size()]);
    }
}
