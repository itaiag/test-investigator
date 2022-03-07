package il.co.topq.testinv.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import il.co.topq.testinv.domain.enumeration.FailureClassification;
import il.co.topq.testinv.domain.enumeration.Status;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A TestResult.
 */
@Entity
@Table(name = "test_result")
public class TestResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @NotNull
    @Size(min = 5, max = 50)
    @Column(name = "test_name", length = 50, nullable = false)
    private String testName;

    @Column(name = "method")
    private String method;

    @Column(name = "test_properties")
    private String testProperties;

    @Enumerated(EnumType.STRING)
    @Column(name = "test_status")
    private Status testStatus;

    @Column(name = "failure_message")
    private String failureMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_classification")
    private FailureClassification failureClassification;

    @Column(name = "fix")
    private String fix;

    @Column(name = "comments")
    private String comments;

    @ManyToOne
    @JsonIgnoreProperties(value = { "testResults" }, allowSetters = true)
    private Execution execution;

    @ManyToOne
    @JsonIgnoreProperties(value = { "testResults" }, allowSetters = true)
    private Bug bug;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TestResult id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public TestResult creationDate(LocalDate creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getTestName() {
        return this.testName;
    }

    public TestResult testName(String testName) {
        this.setTestName(testName);
        return this;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getMethod() {
        return this.method;
    }

    public TestResult method(String method) {
        this.setMethod(method);
        return this;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTestProperties() {
        return this.testProperties;
    }

    public TestResult testProperties(String testProperties) {
        this.setTestProperties(testProperties);
        return this;
    }

    public void setTestProperties(String testProperties) {
        this.testProperties = testProperties;
    }

    public Status getTestStatus() {
        return this.testStatus;
    }

    public TestResult testStatus(Status testStatus) {
        this.setTestStatus(testStatus);
        return this;
    }

    public void setTestStatus(Status testStatus) {
        this.testStatus = testStatus;
    }

    public String getFailureMessage() {
        return this.failureMessage;
    }

    public TestResult failureMessage(String failureMessage) {
        this.setFailureMessage(failureMessage);
        return this;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public FailureClassification getFailureClassification() {
        return this.failureClassification;
    }

    public TestResult failureClassification(FailureClassification failureClassification) {
        this.setFailureClassification(failureClassification);
        return this;
    }

    public void setFailureClassification(FailureClassification failureClassification) {
        this.failureClassification = failureClassification;
    }

    public String getFix() {
        return this.fix;
    }

    public TestResult fix(String fix) {
        this.setFix(fix);
        return this;
    }

    public void setFix(String fix) {
        this.fix = fix;
    }

    public String getComments() {
        return this.comments;
    }

    public TestResult comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Execution getExecution() {
        return this.execution;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    public TestResult execution(Execution execution) {
        this.setExecution(execution);
        return this;
    }

    public Bug getBug() {
        return this.bug;
    }

    public void setBug(Bug bug) {
        this.bug = bug;
    }

    public TestResult bug(Bug bug) {
        this.setBug(bug);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestResult)) {
            return false;
        }
        return id != null && id.equals(((TestResult) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TestResult{" +
            "id=" + getId() +
            ", creationDate='" + getCreationDate() + "'" +
            ", testName='" + getTestName() + "'" +
            ", method='" + getMethod() + "'" +
            ", testProperties='" + getTestProperties() + "'" +
            ", testStatus='" + getTestStatus() + "'" +
            ", failureMessage='" + getFailureMessage() + "'" +
            ", failureClassification='" + getFailureClassification() + "'" +
            ", fix='" + getFix() + "'" +
            ", comments='" + getComments() + "'" +
            "}";
    }
}
