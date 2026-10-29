package org.vers.backend.dto;

import java.util.List;

/** Values the report filters can take, for populating dropdowns. */
public class ReportFilterOptions {

    public List<String> types;
    public List<String> statuses;
    public List<String> genders;
    public List<String> regions;
    public List<Integer> registrationYears;
}
