package Frontend_UI.Helper;

import java.util.Map;
import java.util.List;

/**
 * Helper class to encapsulate filter criteria for internship opportunity search.
 * <p>
 * This class stores sorting and filtering parameters that can be applied to 
 * internship opportunity lists, including the sort attribute, sort order, and 
 * multi-field filtering criteria.
 * </p>
 * 
 * @author Allen
 * @version 1.0
 */
public class Filter {
    /** The attribute to sort by (e.g., "company", "internshipLevel") */
    private String filterType;
    
    /** True for ascending order, false for descending order */
    private boolean ascending;
    
    /** The filtering criteria mapping field names to lists of acceptable values */
    private Map<String, List<String>> filterIn;
    
    /**
     * Constructs a Filter with specified sorting and filtering criteria.
     * 
     * @param filterType The attribute to sort by
     * @param ascending True for ascending order, false for descending
     * @param filterIn Map of field names to lists of acceptable values for filtering
     */
    public Filter(String filterType, boolean ascending, Map<String, List<String>> filterIn) {
        this.filterType = filterType;
        this.ascending = ascending;
        this.filterIn = filterIn;
    }
    
    /**
     * Gets the attribute type to sort by.
     * 
     * @return The filter type
     */
    public String getFilterType() {
        return filterType;
    }
    
    /**
     * Checks if sorting is in ascending order.
     * 
     * @return True if ascending, false if descending
     */
    public boolean isAscending() {
        return ascending;
    }
    
    /**
     * Gets the filtering criteria map.
     * 
     * @return Map of field names to lists of acceptable values
     */
    public Map<String, List<String>> getFilterIn() {
        return filterIn;
    }
}
