import java.util.List;

public class Student {
	private String concentration, 
				   hometown, 
				   operatingSystem, 
				   codingFrequency, 
				   programmingLanguage, 
				   interestingFactoid, 
				   gender, 
				   age;
	private int comfortLevel;
	private Experience experience;
	
	public Student(String string) {
		List fields = new CSVParser().parse(string);
		concentration = (String) fields.get(0);
		hometown = (String) fields.get(1);
		operatingSystem = (String) fields.get(2);
		experience = Experience.parse((String) fields.get(3));
		codingFrequency = (String) fields.get(4);
		programmingLanguage = (String) fields.get(5);
		comfortLevel = Integer.parseInt((String) fields.get(6));
		interestingFactoid = (String) fields.get(7);
		gender = (String) fields.get(8);
		age = (String) fields.get(9);
	}

	public String getConcentration() {
		return concentration;
	}
	public void setName(String name) {
		this.concentration = name;
	}
	public String getHometown() {
		return hometown;
	}
	public void setHometown(String hometown) {
		this.hometown = hometown;
	}
	public String getOperatingSystem() {
		return operatingSystem;
	}
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	public String getCodingFrequency() {
		return codingFrequency;
	}
	public void setCodingFrequency(String codingFrequency) {
		this.codingFrequency = codingFrequency;
	}
	public String getProgrammingLanguage() {
		return programmingLanguage;
	}
	public void setProgrammingLanguage(String programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}
	public String getInterestingFactoid() {
		return interestingFactoid;
	}
	public void setnterestingFactoid(String interestingFactoid) {
		this.interestingFactoid = interestingFactoid;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public int getComfortLevel() {
		return comfortLevel;
	}
	public void setComfortLevel(int comfortLevel) {
		this.comfortLevel = comfortLevel;
	}
	public Experience getExperience() {
		return experience;
	}
	public void setExperience(Experience experience) {
		this.experience = experience;
	}
}
