package WorkingClasses;
/*
 * The final assignment of Software Engineering
 * @author Alexey Titov and Shir Bentabou
 * @date 12.2018
 * @version 3.0
 */
//library
import java.io.Serializable;
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class CourseGrade implements Serializable { //serializable in order to pass CourseGrade between intents
    //variables
    private String course;
    private int grade;
    //empty constructor
    public CourseGrade(){
        course = "";
        grade = 0;
    }
    //constructor with course
    public CourseGrade(String course){
        this.course = course;
        grade = 0;
    }
    //constructor with course and grade
    public CourseGrade(String course, int grade){
        this.course = course;
        this.grade = grade;
    }
    //getter and setter of course
    public String getCourse(){
        return this.course;
    }

    public void setCourse(String course){
        this.course = course;
    }

    //getter and setter of grade
    public int getGrade(){
        return this.grade;
    }

    public void setGrade(int grade){
        this.grade = grade;
    }

}

