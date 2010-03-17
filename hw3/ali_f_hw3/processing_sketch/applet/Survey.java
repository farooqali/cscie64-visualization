import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

public class Survey {
	List students;
	Hashtable groupedStudents;
	
	public Survey(String[] lines)
	{
		students = new ArrayList();
		groupedStudents = new Hashtable();
		for(int i = 1; i < lines.length; i++)
			students.add(new Student(lines[i]));
		groupStudentsByProgrammingLanguage();
	}
        
        public Student[] getStudents()
        {
          return (Student[]) students.toArray(new Student[students.size()]);
        }
	
	public String[] getLanguages()
	{
		ArrayList langs = new ArrayList(groupedStudents.keySet());
		Collections.sort(langs);
		return (String[]) langs.toArray(new String[langs.size()]);
	}
	
	public Student[] getStudentsUsingProgrammingLanguage(String language)
	{
		List s = (List) groupedStudents.get(language);
		return (Student[]) s.toArray(new Student[s.size()]);
	}
	
	public Hashtable getStudentsByProgrammingLanguage()
	{
		return groupedStudents;
	}
	
	private Hashtable groupStudentsByProgrammingLanguage()
	{
		groupedStudents = new Hashtable();
		for(int i = 0; i < students.size(); i++)
		{
			Student student = (Student) students.get(i);
			
			if(!groupedStudents.containsKey(student.getProgrammingLanguage()))
				groupedStudents.put(student.getProgrammingLanguage(), new ArrayList());
			
			List studentsForLanguage = (List) groupedStudents.get(student.getProgrammingLanguage());
			studentsForLanguage.add(student);
			Collections.sort(studentsForLanguage, new StudentComfortLevelComparator());
		}
		
		return groupedStudents;
	}
	
	private final class StudentComfortLevelComparator implements Comparator {
		public int compare(Object x, Object y) {
			Student studentX = (Student) x;
			Student studentY = (Student) y;
			if(studentX.getComfortLevel() == studentY.getComfortLevel())
				return studentX.getExperience().compareTo(studentY.getExperience());
			else
				return new Integer(studentY.getComfortLevel()).compareTo(new Integer(studentX.getComfortLevel()));
		}
	}
}
