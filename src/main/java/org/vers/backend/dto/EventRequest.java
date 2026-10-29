package org.vers.backend.dto;

/**
 * Body for registering or updating an event. Only the fields belonging to
 * {@link #type} are read; the others are ignored.
 */
public class EventRequest {

    public String type;
    public LocationDTO location;

    // BIRTH
    public String childName;
    public String dateOfBirth;
    public String fatherName;
    public String motherName;
    public Float birthWeight;
    public String phoneNumber;

    // BIRTH and DEATH
    public String gender;

    // DEATH
    public String deceasedName;
    public String dateOfDeath;
    public String causeOfDeath;
    public String placeOfDeath;

    // DEATH and MARRIAGE
    public String certifierName;

    // MARRIAGE and DIVORCE
    public String maleSpouseName;
    public String femaleSpouseName;

    // MARRIAGE
    public String witnessOneName;
    public String witnessTwoName;
    public String dateOfMarriage;

    // DIVORCE
    public String courtName;
    public String dateOfDivorce;
}
