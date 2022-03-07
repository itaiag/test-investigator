package il.co.topq.testinv.domain.enumeration;

/**
 * The FailureClassification enumeration.
 */
public enum FailureClassification {
    TO_INVESTIGATE("To investigate"),
    PRODUCT_BUG("Product bug"),
    AUTO_BUG("Automation bug"),
    SYSTEM_ISSUE("System issue");

    private final String value;

    FailureClassification(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
