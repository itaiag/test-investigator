package il.co.topq.testinv.domain.enumeration;

/**
 * The BugStatus enumeration.
 */
public enum BugStatus {
    OPEN("Open"),
    CLOSE("Close");

    private final String value;

    BugStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
