package il.co.topq.testinv.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import il.co.topq.testinv.IntegrationTest;
import il.co.topq.testinv.domain.TestResult;
import il.co.topq.testinv.domain.enumeration.FailureClassification;
import il.co.topq.testinv.domain.enumeration.Status;
import il.co.topq.testinv.repository.TestResultRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TestResultResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TestResultResourceIT {

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_TEST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TEST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_METHOD = "AAAAAAAAAA";
    private static final String UPDATED_METHOD = "BBBBBBBBBB";

    private static final String DEFAULT_TEST_PROPERTIES = "AAAAAAAAAA";
    private static final String UPDATED_TEST_PROPERTIES = "BBBBBBBBBB";

    private static final Status DEFAULT_TEST_STATUS = Status.SUCCESS;
    private static final Status UPDATED_TEST_STATUS = Status.FAILURE;

    private static final String DEFAULT_FAILURE_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_FAILURE_MESSAGE = "BBBBBBBBBB";

    private static final FailureClassification DEFAULT_FAILURE_CLASSIFICATION = FailureClassification.TO_INVESTIGATE;
    private static final FailureClassification UPDATED_FAILURE_CLASSIFICATION = FailureClassification.PRODUCT_BUG;

    private static final String DEFAULT_FIX = "AAAAAAAAAA";
    private static final String UPDATED_FIX = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/test-results";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTestResultMockMvc;

    private TestResult testResult;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestResult createEntity(EntityManager em) {
        TestResult testResult = new TestResult()
            .creationDate(DEFAULT_CREATION_DATE)
            .testName(DEFAULT_TEST_NAME)
            .method(DEFAULT_METHOD)
            .testProperties(DEFAULT_TEST_PROPERTIES)
            .testStatus(DEFAULT_TEST_STATUS)
            .failureMessage(DEFAULT_FAILURE_MESSAGE)
            .failureClassification(DEFAULT_FAILURE_CLASSIFICATION)
            .fix(DEFAULT_FIX)
            .comments(DEFAULT_COMMENTS);
        return testResult;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestResult createUpdatedEntity(EntityManager em) {
        TestResult testResult = new TestResult()
            .creationDate(UPDATED_CREATION_DATE)
            .testName(UPDATED_TEST_NAME)
            .method(UPDATED_METHOD)
            .testProperties(UPDATED_TEST_PROPERTIES)
            .testStatus(UPDATED_TEST_STATUS)
            .failureMessage(UPDATED_FAILURE_MESSAGE)
            .failureClassification(UPDATED_FAILURE_CLASSIFICATION)
            .fix(UPDATED_FIX)
            .comments(UPDATED_COMMENTS);
        return testResult;
    }

    @BeforeEach
    public void initTest() {
        testResult = createEntity(em);
    }

    @Test
    @Transactional
    void createTestResult() throws Exception {
        int databaseSizeBeforeCreate = testResultRepository.findAll().size();
        // Create the TestResult
        restTestResultMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(testResult)))
            .andExpect(status().isCreated());

        // Validate the TestResult in the database
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeCreate + 1);
        TestResult testTestResult = testResultList.get(testResultList.size() - 1);
        assertThat(testTestResult.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testTestResult.getTestName()).isEqualTo(DEFAULT_TEST_NAME);
        assertThat(testTestResult.getMethod()).isEqualTo(DEFAULT_METHOD);
        assertThat(testTestResult.getTestProperties()).isEqualTo(DEFAULT_TEST_PROPERTIES);
        assertThat(testTestResult.getTestStatus()).isEqualTo(DEFAULT_TEST_STATUS);
        assertThat(testTestResult.getFailureMessage()).isEqualTo(DEFAULT_FAILURE_MESSAGE);
        assertThat(testTestResult.getFailureClassification()).isEqualTo(DEFAULT_FAILURE_CLASSIFICATION);
        assertThat(testTestResult.getFix()).isEqualTo(DEFAULT_FIX);
        assertThat(testTestResult.getComments()).isEqualTo(DEFAULT_COMMENTS);
    }

    @Test
    @Transactional
    void createTestResultWithExistingId() throws Exception {
        // Create the TestResult with an existing ID
        testResult.setId(1L);

        int databaseSizeBeforeCreate = testResultRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTestResultMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(testResult)))
            .andExpect(status().isBadRequest());

        // Validate the TestResult in the database
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTestNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = testResultRepository.findAll().size();
        // set the field null
        testResult.setTestName(null);

        // Create the TestResult, which fails.

        restTestResultMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(testResult)))
            .andExpect(status().isBadRequest());

        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTestResults() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList
        restTestResultMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testResult.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].testName").value(hasItem(DEFAULT_TEST_NAME)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD)))
            .andExpect(jsonPath("$.[*].testProperties").value(hasItem(DEFAULT_TEST_PROPERTIES)))
            .andExpect(jsonPath("$.[*].testStatus").value(hasItem(DEFAULT_TEST_STATUS.toString())))
            .andExpect(jsonPath("$.[*].failureMessage").value(hasItem(DEFAULT_FAILURE_MESSAGE)))
            .andExpect(jsonPath("$.[*].failureClassification").value(hasItem(DEFAULT_FAILURE_CLASSIFICATION.toString())))
            .andExpect(jsonPath("$.[*].fix").value(hasItem(DEFAULT_FIX)))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)));
    }

    @Test
    @Transactional
    void getTestResult() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get the testResult
        restTestResultMockMvc
            .perform(get(ENTITY_API_URL_ID, testResult.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(testResult.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.testName").value(DEFAULT_TEST_NAME))
            .andExpect(jsonPath("$.method").value(DEFAULT_METHOD))
            .andExpect(jsonPath("$.testProperties").value(DEFAULT_TEST_PROPERTIES))
            .andExpect(jsonPath("$.testStatus").value(DEFAULT_TEST_STATUS.toString()))
            .andExpect(jsonPath("$.failureMessage").value(DEFAULT_FAILURE_MESSAGE))
            .andExpect(jsonPath("$.failureClassification").value(DEFAULT_FAILURE_CLASSIFICATION.toString()))
            .andExpect(jsonPath("$.fix").value(DEFAULT_FIX))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS));
    }

    @Test
    @Transactional
    void getNonExistingTestResult() throws Exception {
        // Get the testResult
        restTestResultMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTestResult() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        int databaseSizeBeforeUpdate = testResultRepository.findAll().size();

        // Update the testResult
        TestResult updatedTestResult = testResultRepository.findById(testResult.getId()).get();
        // Disconnect from session so that the updates on updatedTestResult are not directly saved in db
        em.detach(updatedTestResult);
        updatedTestResult
            .creationDate(UPDATED_CREATION_DATE)
            .testName(UPDATED_TEST_NAME)
            .method(UPDATED_METHOD)
            .testProperties(UPDATED_TEST_PROPERTIES)
            .testStatus(UPDATED_TEST_STATUS)
            .failureMessage(UPDATED_FAILURE_MESSAGE)
            .failureClassification(UPDATED_FAILURE_CLASSIFICATION)
            .fix(UPDATED_FIX)
            .comments(UPDATED_COMMENTS);

        restTestResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTestResult.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTestResult))
            )
            .andExpect(status().isOk());

        // Validate the TestResult in the database
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeUpdate);
        TestResult testTestResult = testResultList.get(testResultList.size() - 1);
        assertThat(testTestResult.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testTestResult.getTestName()).isEqualTo(UPDATED_TEST_NAME);
        assertThat(testTestResult.getMethod()).isEqualTo(UPDATED_METHOD);
        assertThat(testTestResult.getTestProperties()).isEqualTo(UPDATED_TEST_PROPERTIES);
        assertThat(testTestResult.getTestStatus()).isEqualTo(UPDATED_TEST_STATUS);
        assertThat(testTestResult.getFailureMessage()).isEqualTo(UPDATED_FAILURE_MESSAGE);
        assertThat(testTestResult.getFailureClassification()).isEqualTo(UPDATED_FAILURE_CLASSIFICATION);
        assertThat(testTestResult.getFix()).isEqualTo(UPDATED_FIX);
        assertThat(testTestResult.getComments()).isEqualTo(UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void putNonExistingTestResult() throws Exception {
        int databaseSizeBeforeUpdate = testResultRepository.findAll().size();
        testResult.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, testResult.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(testResult))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestResult in the database
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTestResult() throws Exception {
        int databaseSizeBeforeUpdate = testResultRepository.findAll().size();
        testResult.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(testResult))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestResult in the database
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTestResult() throws Exception {
        int databaseSizeBeforeUpdate = testResultRepository.findAll().size();
        testResult.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(testResult)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TestResult in the database
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTestResultWithPatch() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        int databaseSizeBeforeUpdate = testResultRepository.findAll().size();

        // Update the testResult using partial update
        TestResult partialUpdatedTestResult = new TestResult();
        partialUpdatedTestResult.setId(testResult.getId());

        partialUpdatedTestResult
            .testName(UPDATED_TEST_NAME)
            .testProperties(UPDATED_TEST_PROPERTIES)
            .failureMessage(UPDATED_FAILURE_MESSAGE)
            .failureClassification(UPDATED_FAILURE_CLASSIFICATION)
            .fix(UPDATED_FIX)
            .comments(UPDATED_COMMENTS);

        restTestResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTestResult.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTestResult))
            )
            .andExpect(status().isOk());

        // Validate the TestResult in the database
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeUpdate);
        TestResult testTestResult = testResultList.get(testResultList.size() - 1);
        assertThat(testTestResult.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testTestResult.getTestName()).isEqualTo(UPDATED_TEST_NAME);
        assertThat(testTestResult.getMethod()).isEqualTo(DEFAULT_METHOD);
        assertThat(testTestResult.getTestProperties()).isEqualTo(UPDATED_TEST_PROPERTIES);
        assertThat(testTestResult.getTestStatus()).isEqualTo(DEFAULT_TEST_STATUS);
        assertThat(testTestResult.getFailureMessage()).isEqualTo(UPDATED_FAILURE_MESSAGE);
        assertThat(testTestResult.getFailureClassification()).isEqualTo(UPDATED_FAILURE_CLASSIFICATION);
        assertThat(testTestResult.getFix()).isEqualTo(UPDATED_FIX);
        assertThat(testTestResult.getComments()).isEqualTo(UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void fullUpdateTestResultWithPatch() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        int databaseSizeBeforeUpdate = testResultRepository.findAll().size();

        // Update the testResult using partial update
        TestResult partialUpdatedTestResult = new TestResult();
        partialUpdatedTestResult.setId(testResult.getId());

        partialUpdatedTestResult
            .creationDate(UPDATED_CREATION_DATE)
            .testName(UPDATED_TEST_NAME)
            .method(UPDATED_METHOD)
            .testProperties(UPDATED_TEST_PROPERTIES)
            .testStatus(UPDATED_TEST_STATUS)
            .failureMessage(UPDATED_FAILURE_MESSAGE)
            .failureClassification(UPDATED_FAILURE_CLASSIFICATION)
            .fix(UPDATED_FIX)
            .comments(UPDATED_COMMENTS);

        restTestResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTestResult.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTestResult))
            )
            .andExpect(status().isOk());

        // Validate the TestResult in the database
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeUpdate);
        TestResult testTestResult = testResultList.get(testResultList.size() - 1);
        assertThat(testTestResult.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testTestResult.getTestName()).isEqualTo(UPDATED_TEST_NAME);
        assertThat(testTestResult.getMethod()).isEqualTo(UPDATED_METHOD);
        assertThat(testTestResult.getTestProperties()).isEqualTo(UPDATED_TEST_PROPERTIES);
        assertThat(testTestResult.getTestStatus()).isEqualTo(UPDATED_TEST_STATUS);
        assertThat(testTestResult.getFailureMessage()).isEqualTo(UPDATED_FAILURE_MESSAGE);
        assertThat(testTestResult.getFailureClassification()).isEqualTo(UPDATED_FAILURE_CLASSIFICATION);
        assertThat(testTestResult.getFix()).isEqualTo(UPDATED_FIX);
        assertThat(testTestResult.getComments()).isEqualTo(UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void patchNonExistingTestResult() throws Exception {
        int databaseSizeBeforeUpdate = testResultRepository.findAll().size();
        testResult.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, testResult.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(testResult))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestResult in the database
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTestResult() throws Exception {
        int databaseSizeBeforeUpdate = testResultRepository.findAll().size();
        testResult.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(testResult))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestResult in the database
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTestResult() throws Exception {
        int databaseSizeBeforeUpdate = testResultRepository.findAll().size();
        testResult.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(testResult))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TestResult in the database
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTestResult() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        int databaseSizeBeforeDelete = testResultRepository.findAll().size();

        // Delete the testResult
        restTestResultMockMvc
            .perform(delete(ENTITY_API_URL_ID, testResult.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TestResult> testResultList = testResultRepository.findAll();
        assertThat(testResultList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
