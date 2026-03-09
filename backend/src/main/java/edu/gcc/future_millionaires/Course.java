package edu.gcc.future_millionaires;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Course {

    private int credits;

    private List<String> faculty;

    @JsonProperty("is_lab")
    private boolean isLab;

    @JsonProperty("is_open")
    private boolean isOpen;

    private String location;
    private String name;
    private int number;

    @JsonProperty("open_seats")
    private int openSeats;

    private String section;
    private String semester;
    private String subject;
    private String department;

    private List<TimeSlot> times;

    @JsonProperty("total_seats")
    private int totalSeats;

    // Required empty constructor
    public Course() {}

    // Getters and Setters

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public List<String> getFaculty() {
        return faculty;
    }

    public void setFaculty(List<String> faculty) {
        this.faculty = faculty;
    }

    public boolean isLab() {
        return isLab;
    }

    public void setLab(boolean lab) {
        isLab = lab;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getOpenSeats() {
        return openSeats;
    }

    public void setOpenSeats(int openSeats) {
        this.openSeats = openSeats;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<TimeSlot> getTimes() {
        return times;
    }

    public void setTimes(List<TimeSlot> times) {
        this.times = times;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getDepartment() { return department; }


    // old file:
//    public class Course {
//
//        // private class variables
//        private int courseID;
//        private String department;
//        private int courseCode;
//        private String professor;
//        private String[] days;
//        private String semester;
//        private int credits;
//        private List<LocalTime[]> meetingTime;
//        private int maxCapacity;
//        private ArrayList<Integer> enrolledStudents;
//
//
//        // class methods
//
//        // Constructor
//        public Course() {
//            enrolledStudents = new ArrayList<>();
//        }
//        // getters
//        public int getCourseID() {
//            return courseID;
//        }
//        public String getDepartment() {
//            return department;
//        }
//        public int getCourseCode() {
//            return courseCode;
//        }
//        public String getProfessor() {
//            return professor;
//        }
//        public String[] getDays() {
//            return days;
//        }
//        public String getSemester() {
//            return semester;
//        }
//        public int getCredits() {
//            return credits;
//        }
//        public List<LocalTime[]>  getMeetingTime() {
//            return meetingTime;
//        }
//        public int getMaxCapacity() {
//            return maxCapacity;
//        }
//        public ArrayList<Integer> getEnrolledStudents() {
//            return enrolledStudents;
//        }
//
//        // setters
//        public void setCourseID(int courseID) {
//            this.courseID = courseID;
//        }
//        public void setDepartment(String department) {
//            this.department = department;
//        }
//        public void setCourseCode(int courseCode) {
//            this.courseCode = courseCode;
//        }
//        public void setProfessor(String professor) {
//            this.professor = professor;
//        }
//        public void setDays(String[] days) {
//            this.days = days;
//        }
//        public void setSemester(String semester) {
//            this.semester = semester;
//        }
//        public void setCredits(int credits) {
//            this.credits = credits;
//        }
//        public void setMeetingTime(List<LocalTime[]> meetingTime) {
//            this.meetingTime = meetingTime;
//        }
//        public void setMaxCapacity(int maxCapacity) {
//            this.maxCapacity = maxCapacity;
//        }
//        public void setEnrolledStudents(ArrayList<Integer> enrolledStudents) {
//            this.enrolledStudents = enrolledStudents;
//        }
//
//        // other methods
//        public void deleteCourse() {
//            enrolledStudents.clear(); // clear the list of students
//        }
//
//        public void archiveCourse() {
//
//        }
//
//        public void editCourse(String professor, String[] days, List<LocalTime[]> meetingTime, int maxCapacity) {
//            setProfessor(professor);
//            setDays(days);
//            setMeetingTime(meetingTime);
//            setMaxCapacity(maxCapacity);
//        }
//
//        public void addStudent(int studentID) {
//            if (!enrolledStudents.contains(studentID) && enrolledStudents.size() < maxCapacity) {
//                enrolledStudents.add(studentID);
//            }
//        }
//
//        public void removeStudent(int studentID) {
//            enrolledStudents.remove(studentID);
//        }
//    }
}