package il.co.topq.testinv.web.rest;

import il.co.topq.testinv.domain.TestResult;
import il.co.topq.testinv.repository.TestResultRepository;
import il.co.topq.testinv.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link il.co.topq.testinv.domain.TestResult}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TestResultResource {

    private final Logger log = LoggerFactory.getLogger(TestResultResource.class);

    private static final String ENTITY_NAME = "testResult";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TestResultRepository testResultRepository;

    public TestResultResource(TestResultRepository testResultRepository) {
        this.testResultRepository = testResultRepository;
    }

    /**
     * {@code POST  /test-results} : Create a new testResult.
     *
     * @param testResult the testResult to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new testResult, or with status {@code 400 (Bad Request)} if the testResult has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/test-results")
    public ResponseEntity<TestResult> createTestResult(@Valid @RequestBody TestResult testResult) throws URISyntaxException {
        log.debug("REST request to save TestResult : {}", testResult);
        if (testResult.getId() != null) {
            throw new BadRequestAlertException("A new testResult cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TestResult result = testResultRepository.save(testResult);
        return ResponseEntity
            .created(new URI("/api/test-results/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /test-results/:id} : Updates an existing testResult.
     *
     * @param id the id of the testResult to save.
     * @param testResult the testResult to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated testResult,
     * or with status {@code 400 (Bad Request)} if the testResult is not valid,
     * or with status {@code 500 (Internal Server Error)} if the testResult couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/test-results/{id}")
    public ResponseEntity<TestResult> updateTestResult(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TestResult testResult
    ) throws URISyntaxException {
        log.debug("REST request to update TestResult : {}, {}", id, testResult);
        if (testResult.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, testResult.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!testResultRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TestResult result = testResultRepository.save(testResult);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, testResult.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /test-results/:id} : Partial updates given fields of an existing testResult, field will ignore if it is null
     *
     * @param id the id of the testResult to save.
     * @param testResult the testResult to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated testResult,
     * or with status {@code 400 (Bad Request)} if the testResult is not valid,
     * or with status {@code 404 (Not Found)} if the testResult is not found,
     * or with status {@code 500 (Internal Server Error)} if the testResult couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/test-results/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TestResult> partialUpdateTestResult(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TestResult testResult
    ) throws URISyntaxException {
        log.debug("REST request to partial update TestResult partially : {}, {}", id, testResult);
        if (testResult.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, testResult.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!testResultRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TestResult> result = testResultRepository
            .findById(testResult.getId())
            .map(existingTestResult -> {
                if (testResult.getCreationDate() != null) {
                    existingTestResult.setCreationDate(testResult.getCreationDate());
                }
                if (testResult.getTestName() != null) {
                    existingTestResult.setTestName(testResult.getTestName());
                }
                if (testResult.getMethod() != null) {
                    existingTestResult.setMethod(testResult.getMethod());
                }
                if (testResult.getTestProperties() != null) {
                    existingTestResult.setTestProperties(testResult.getTestProperties());
                }
                if (testResult.getTestStatus() != null) {
                    existingTestResult.setTestStatus(testResult.getTestStatus());
                }
                if (testResult.getFailureMessage() != null) {
                    existingTestResult.setFailureMessage(testResult.getFailureMessage());
                }
                if (testResult.getFailureClassification() != null) {
                    existingTestResult.setFailureClassification(testResult.getFailureClassification());
                }
                if (testResult.getFix() != null) {
                    existingTestResult.setFix(testResult.getFix());
                }
                if (testResult.getComments() != null) {
                    existingTestResult.setComments(testResult.getComments());
                }

                return existingTestResult;
            })
            .map(testResultRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, testResult.getId().toString())
        );
    }

    /**
     * {@code GET  /test-results} : get all the testResults.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of testResults in body.
     */
    @GetMapping("/test-results")
    public List<TestResult> getAllTestResults() {
        log.debug("REST request to get all TestResults");
        return testResultRepository.findAll();
    }

    /**
     * {@code GET  /test-results/:id} : get the "id" testResult.
     *
     * @param id the id of the testResult to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the testResult, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/test-results/{id}")
    public ResponseEntity<TestResult> getTestResult(@PathVariable Long id) {
        log.debug("REST request to get TestResult : {}", id);
        Optional<TestResult> testResult = testResultRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(testResult);
    }

    /**
     * {@code DELETE  /test-results/:id} : delete the "id" testResult.
     *
     * @param id the id of the testResult to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/test-results/{id}")
    public ResponseEntity<Void> deleteTestResult(@PathVariable Long id) {
        log.debug("REST request to delete TestResult : {}", id);
        testResultRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
