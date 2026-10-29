package org.vers.backend.dto;

import java.util.Map;

/** Event statistics for the filters in {@link #filters}. */
public class ReportResponse {

    public Map<String, Object> filters;
    public long totalEvents;
    public Map<String, Long> byType;
    public Map<String, Long> byStatus;
    /** Births and deaths only; marriages and divorces have no single gender. */
    public Map<String, Long> byGender;
    public Map<String, Long> byRegion;
    /** Keyed by registration month, YYYY-MM. */
    public Map<String, Long> byMonth;
    public Double averageBirthWeight;
}
