package edu.gcc.future_millionaires;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailBuilderTest {

    @Test
    void generatesEmailWithFirstAndMiddleInitial() {
        String input = "Hutchins, Jonathan O.";
        String result = EmailAddressBuilder.generateEmail(input);

        assertEquals("hutchinsjo@gcc.edu", result);
    }

    @Test
    void generatesEmailWithOnlyFirstInitial() {
        String input = "Smith, John";
        String result = EmailAddressBuilder.generateEmail(input);

        assertEquals("smithj@gcc.edu", result);
    }

    @Test
    void handlesLowerAndUpperCaseProperly() {
        String input = "McDonald, ALICE B.";
        String result = EmailAddressBuilder.generateEmail(input);

        assertEquals("mcdonaldab@gcc.edu", result);
    }

    @Test
    void handlesMissingMiddleInitial() {
        String input = "Brown, Charlie";
        String result = EmailAddressBuilder.generateEmail(input);

        assertEquals("brownc@gcc.edu", result);
    }

    @Test
    void throwsExceptionWhenNoCommaPresent() {
        String input = "InvalidFormatName";

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            EmailAddressBuilder.generateEmail(input);
        });
    }

    @Test
    void throwsExceptionWhenFirstNameMissing() {
        String input = "Smith,";

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            EmailAddressBuilder.generateEmail(input);
        });
    }

    @Test
    void throwsExceptionWhenEmptyString() {
        String input = "";

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            EmailAddressBuilder.generateEmail(input);
        });
    }
}