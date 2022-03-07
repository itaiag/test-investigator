package il.co.topq.testinv.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Execution.
 */
@Entity
@Table(name = "execution")
public class Execution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "execution_description")
    private String executionDescription;

    @Column(name = "execution_properties")
    private String executionProperties;

    @OneToMany(mappedBy = "execution")
    @JsonIgnoreProperties(value = { "execution", "bug" }, allowSetters = true)
    private Set<TestResult> testResults = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Execution id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public Execution creationDate(LocalDate creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getExecutionDescription() {
        return this.executionDescription;
    }

    public Execution executionDescription(String executionDescription) {
        this.setExecutionDescription(executionDescription);
        return this;
    }

    public void setExecutionDescription(String executionDescription) {
        this.executionDescription = executionDescription;
    }

    public String getExecutionProperties() {
        return this.executionProperties;
    }

    public Execution executionProperties(String executionProperties) {
        this.setExecutionProperties(executionProperties);
        return this;
    }

    public void setExecutionProperties(String executionProperties) {
        this.executionProperties = executionProperties;
    }

    public Set<TestResult> getTestResults() {
        return this.testResults;
    }

    public void setTestResults(Set<TestResult> testResults) {
        if (this.testResults != null) {
            this.testResults.forEach(i -> i.setExecution(null));
        }
        if (testResults != null) {
            testResults.forEach(i -> i.setExecution(this));
        }
        this.testResults = testResults;
    }

    public Execution testResults(Set<TestResult> testResults) {
        this.setTestResults(testResults);
        return this;
    }

    public Execution addTestResult(TestResult testResult) {
        this.testResults.add(testResult);
        testResult.setExecution(this);
        return this;
    }

    public Execution removeTestResult(TestResult testResult) {
        this.testResults.remove(testResult);
        testResult.setExecution(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Execution)) {
            return false;
        }
        return id != null && id.equals(((Execution) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Execution{" +
            "id=" + getId() +
            ", creationDate='" + getCreationDate() + "'" +
            ", executionDescription='" + getExecutionDescription() + "'" +
            ", executionProperties='" + getExecutionProperties() + "'" +
            "}";
    }
}
