package il.co.topq.testinv.domain.enumeration;

/**
 * The Status enumeration.
 */
public enum Status {
    SUCCESS("Success"),
    FAILURE("Failure"),
    ERROR("Error"),
    WARNING("Warning");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
