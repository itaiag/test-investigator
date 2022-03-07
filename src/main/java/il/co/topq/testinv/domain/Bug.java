package il.co.topq.testinv.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import il.co.topq.testinv.domain.enumeration.BugStatus;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Bug.
 */
@Entity
@Table(name = "bug")
public class Bug implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "bug_name")
    private String bugName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BugStatus status;

    @OneToMany(mappedBy = "bug")
    @JsonIgnoreProperties(value = { "execution", "bug" }, allowSetters = true)
    private Set<TestResult> testResults = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Bug id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBugName() {
        return this.bugName;
    }

    public Bug bugName(String bugName) {
        this.setBugName(bugName);
        return this;
    }

    public void setBugName(String bugName) {
        this.bugName = bugName;
    }

    public String getDescription() {
        return this.description;
    }

    public Bug description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BugStatus getStatus() {
        return this.status;
    }

    public Bug status(BugStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BugStatus status) {
        this.status = status;
    }

    public Set<TestResult> getTestResults() {
        return this.testResults;
    }

    public void setTestResults(Set<TestResult> testResults) {
        if (this.testResults != null) {
            this.testResults.forEach(i -> i.setBug(null));
        }
        if (testResults != null) {
            testResults.forEach(i -> i.setBug(this));
        }
        this.testResults = testResults;
    }

    public Bug testResults(Set<TestResult> testResults) {
        this.setTestResults(testResults);
        return this;
    }

    public Bug addTestResult(TestResult testResult) {
        this.testResults.add(testResult);
        testResult.setBug(this);
        return this;
    }

    public Bug removeTestResult(TestResult testResult) {
        this.testResults.remove(testResult);
        testResult.setBug(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bug)) {
            return false;
        }
        return id != null && id.equals(((Bug) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bug{" +
            "id=" + getId() +
            ", bugName='" + getBugName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
