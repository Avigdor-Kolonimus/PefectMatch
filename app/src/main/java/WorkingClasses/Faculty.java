package WorkingClasses;
/*
 * The final assignment of Software Engineering
 * @author Alexey Titov and Shir Bentabou
 * @date 12.2018
 * @version 3.0
 */
//libraries
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Faculty {
    //variables
    protected String facultyName;			//faculty name
    protected int minAverage;				//minimum average of faculty
    protected String projectNature;		    //Research, faculty requires nature project from applicant
    protected int projectGrade;				//minimum project grade
    protected int numOfApplicants;			//number of applicant that faculty wants study
    protected Map<String, Integer> minGrades=new HashMap<String, Integer>();				//list minimum grades
    protected Map<String, Double> formulaWeights=new HashMap<String, Double>();;			//list with grade weight
    protected List<Applicant> applicants=new ArrayList<Applicant>();						//list of applicants
    /**
     * empty constructor of faculty
     */
    public Faculty() {
    }
    /**
     * getter facultyName
     * @return faculty name
     */
    public String getFacultyName() {
        return facultyName;
    }
    /**
     * setter facultyName
     * @param facultyName - faculty name
     */
    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
    /**
     * getter minAverage
     * @return minimum average
     */
    public int getMinAverage() {
        return minAverage;
    }
    /**
     * update minAverage
     * @param minAverage - minimum average for faculty
     * @return true, if it was able to update; false, otherwise
     */
    public boolean updateMinAverage(int minAverage) {
        this.minAverage = minAverage;
        return true;
    }
    /**
     * getter project nature
     * @return true, if faculty requires nature project from applicant
     */
    public boolean isProjectNatureBoolean() {
        if (projectNature.equals("Research"))
            return true;
        return false;
    }
    /**
     * update projectNature
     * @param projectNature - new value projectNature
     * @return  faculty requires nature project from applicant
     */
    public boolean updateProjectNature(String projectNature) {
        this.projectNature = projectNature;
        return true;
    }
    /**
     * getter projectGrade
     * @return minimum project grade that faculty requires
     */
    public int getProjectGrade() {
        return projectGrade;
    }
    /**
     * update projectGrade
     * @param projectGrade - new minimum project grade of faculty
     * @return true, if it was able to update; false, otherwise
     */
    public boolean updateProjectGrade(int projectGrade) {
        this.projectGrade = projectGrade;
        return true;
    }
    /**
     * function getter numOfApplicant
     * @return numOfApplicant
     */
    public int getNumOfApplicants() {
        return numOfApplicants;
    }
    /**
     * update numOfApplicant
     * @param numOfApplicants - new numberr of applicant for faculty
     * @return true, if it was able to update; false, otherwise
     */
    public boolean updateNumOfApplicants(int numOfApplicants) {
        this.numOfApplicants = numOfApplicants;
        return true;
    }
    /**
     * getter minGrades
     * @return list of minimum grades of course
     */
    public Map<String, Integer> getMinGrades() {
        return minGrades;
    }
    /**
     * update minGrades
     * @param minGrades - new minimum grades for cource
     * @return true, if it was able to update; false, otherwise
     */
    public boolean updateMinGrades(HashMap<String, Integer> minGrades) {
        this.minGrades.clear();
        this.minGrades = (HashMap)minGrades.clone();
        return true;
    }
    /**
     * getter formulaWeights
     * @return list of weights of grades
     */
    public Map<String, Double> getFormulaWeights() {
        return formulaWeights;
    }
    /**
     * update formulaWeights
     * @param formulaWeights - new weights of grades
     */
    public void updateFormulaWeights(HashMap<String, Double> formulaWeights) {
        this.formulaWeights.clear();
        this.formulaWeights = (HashMap)formulaWeights.clone();
    }
    /**
     * getter applicants
     * @return list of applicants in faculty
     */
    public List<Applicant> getApplicants() {
        return applicants;
    }
    /**
     * setter applicants
     * @param applicants new list of applicants for faculty
     */
    public void setApplicants(ArrayList<Applicant> applicants) {
        this.applicants.clear();
        this.applicants = (ArrayList) applicants.clone();
    }
    /**
     * function adds new applicant to list
     * @param _newApplicant - new applicant
     * @return true, if it was able to update; false, otherwise
     */
    public boolean addApplicant(Applicant _newApplicant) {
        this.applicants.add(_newApplicant);
        return true;
    }
    /**
     * function removes applicant to list
     * @param _oldApplicant - applicant that we need remove
     * @return true, if it was able to update; false, otherwise
     */
    public boolean removeApplicant(Applicant _oldApplicant) {
        this.applicants.remove(_oldApplicant);
        return true;
    }
    /**
     * update minGrades
     * @param _course - name of course
     * @param _grade - grade of course
     * @return true, if it was able to update; false, otherwise
     */
    public boolean updateMinGrades(String _course, int _grade) {
        this.minGrades.put(_course, _grade);
        return true;
    }
    /**
     * update formulaWeights
     * @param _course - name of course
     * @param _weight - weight of course
     * @return true, if it was able to update; false, otherwise
     */
    public boolean updateFormulaWeights(String _course, double _weight) {
        this.formulaWeights.put(_course, _weight);
        return true;
    }
    /**
     * toString for Faculty
     */
    @Override
    public String toString() {
        return "Faculty [facultyName=" + facultyName + ", numOfApplicants=" + numOfApplicants + ", applicants="
                + applicants + "]";
    }
}
