package org.vers.backend.dto;

import org.vers.backend.entity.Location;

public class LocationDTO {

    public String region;
    public String zone;
    public String woreda;

    public LocationDTO() {}

    public LocationDTO(String region, String zone, String woreda) {
        this.region = region;
        this.zone = zone;
        this.woreda = woreda;
    }

    public static LocationDTO from(Location location) {
        return new LocationDTO(location.region, location.zone, location.woreda);
    }
}
