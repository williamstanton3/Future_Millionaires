package edu.gcc.future_millionaires;

public class EmailAddressBuilder {

    public static String generateEmail(String facultyString) {

        // Example input: "Hutchins, Jonathan O."
        String[] parts = facultyString.split(",");

        String lastName = parts[0].trim().toLowerCase();

        String[] firstParts = parts[1].trim().split(" ");

        String firstInitial = firstParts[0].substring(0, 1).toLowerCase();

        String middleInitial = "";
        if (firstParts.length > 1) {
            middleInitial = firstParts[1].substring(0, 1).toLowerCase();
        }

        return lastName + firstInitial + middleInitial + "@gcc.edu";
    }
}
