package edu.gcc.future_millionaires;

public class Professor {
    private final int legacyId;           // old tid
    private final String id;              // new GraphQL ID (base64)
    private final String firstName;
    private final String lastName;
    private final String name;
    private final int numOfRatings;
    private final double overallRating;
    private final double avgDifficulty;
    private final String department;

    public Professor(String id, int legacyId, String firstName, String lastName,
                     int numOfRatings, double overallRating, double avgDifficulty, String department) {
        this.id = id;
        this.legacyId = legacyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = firstName + " " + lastName;
        this.numOfRatings = numOfRatings;
        this.overallRating = (numOfRatings < 1) ? 0.0 : overallRating;
        this.avgDifficulty = avgDifficulty;
        this.department = department;
    }

    public String getId() { return id; }
    public int getLegacyId() { return legacyId; }
    public String getName() { return name; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getNumOfRatings() { return numOfRatings; }
    public double getOverallRating() { return overallRating; }
    public double getAvgDifficulty() { return avgDifficulty; }
    public String getDepartment() { return department; }
}