package org.vers.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.vers.backend.entity.BirthEvent;
import org.vers.backend.entity.DeathEvent;
import org.vers.backend.entity.DivorceEvent;
import org.vers.backend.entity.Event;
import org.vers.backend.entity.MarriageEvent;
import org.vers.backend.enums.EventType;

/** An event as returned by the API: the {@link EventRequest} fields plus metadata. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventResponse extends EventRequest {

    public Long id;
    /** Main person or couple the event is about, for lists and search. */
    public String name;
    public String status;
    public String dateOfRegistration;
    public String registrar;
    public String reviewedBy;
    public String reviewedAt;
    public String rejectionReason;

    public static EventResponse from(Event event) {
        EventResponse r = new EventResponse();
        r.id = event.id;
        r.type = typeOf(event).toString();
        r.status = event.status.toString();
        r.dateOfRegistration = event.dateOfRegistration.toString();
        r.registrar = event.registrar.username;
        r.location = LocationDTO.from(event.location);
        if (event.reviewedBy != null) r.reviewedBy = event.reviewedBy.username;
        if (event.reviewedAt != null) r.reviewedAt = event.reviewedAt.toString();
        r.rejectionReason = event.rejectionReason;

        if (event instanceof BirthEvent birth) {
            r.name = birth.childName;
            r.childName = birth.childName;
            r.gender = birth.gender.toString();
            r.fatherName = birth.fatherName;
            r.motherName = birth.motherName;
            r.birthWeight = birth.birthWeight;
            r.dateOfBirth = birth.dateOfBirth.toString();
            if (birth.child != null) r.phoneNumber = birth.child.phoneNumber;
        } else if (event instanceof DeathEvent death) {
            r.name = death.deceasedName;
            r.deceasedName = death.deceasedName;
            r.gender = death.gender.toString();
            r.causeOfDeath = death.causeOfDeath;
            r.certifierName = death.certifierName;
            r.dateOfDeath = death.dateOfDeath.toString();
            r.placeOfDeath = death.placeOfDeath;
        } else if (event instanceof MarriageEvent marriage) {
            r.name = marriage.maleSpouseName + " & " + marriage.femaleSpouseName;
            r.maleSpouseName = marriage.maleSpouseName;
            r.femaleSpouseName = marriage.femaleSpouseName;
            r.witnessOneName = marriage.witnessOneName;
            r.witnessTwoName = marriage.witnessTwoName;
            r.certifierName = marriage.certifierName;
            r.dateOfMarriage = marriage.dateOfMarriage.toString();
        } else if (event instanceof DivorceEvent divorce) {
            r.name = divorce.maleSpouseName + " & " + divorce.femaleSpouseName;
            r.maleSpouseName = divorce.maleSpouseName;
            r.femaleSpouseName = divorce.femaleSpouseName;
            r.courtName = divorce.courtName;
            r.dateOfDivorce = divorce.dateOfDivorce.toString();
        }
        return r;
    }

    // The discriminator-backed Event.type field is only populated once the
    // entity is reloaded, so derive the type from the subclass instead.
    public static EventType typeOf(Event event) {
        if (event instanceof BirthEvent) return EventType.BIRTH;
        if (event instanceof DeathEvent) return EventType.DEATH;
        if (event instanceof MarriageEvent) return EventType.MARRIAGE;
        return EventType.DIVORCE;
    }
}
