package Frontend_UI.Helper;

import java.util.Map;
import java.util.List;
/*
 * This method is to save filter criteria for internship opportunity search
 * 
 */

public class Filter {
    public String filterType; // the attribute sequence to sort by
    public boolean ascending; // true for ascending order, false for descending order
    public Map<String, List<String>> filterIn; // the filtering criteria
    public Filter(String filterType, boolean ascending, Map<String, List<String>> filterIn) {
        this.filterType = filterType;
        this.ascending = ascending;
        this.filterIn = filterIn;
    }
    public String getFilterType() {
        return filterType;
    }
    public boolean isAscending() {
        return ascending;
    }
    public Map<String, List<String>> getFilterIn() {
        return filterIn;
    }
}
