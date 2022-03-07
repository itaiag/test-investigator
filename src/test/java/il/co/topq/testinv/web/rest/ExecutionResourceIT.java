package il.co.topq.testinv.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import il.co.topq.testinv.IntegrationTest;
import il.co.topq.testinv.domain.Execution;
import il.co.topq.testinv.repository.ExecutionRepository;
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
 * Integration tests for the {@link ExecutionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExecutionResourceIT {

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_EXECUTION_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_EXECUTION_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_EXECUTION_PROPERTIES = "AAAAAAAAAA";
    private static final String UPDATED_EXECUTION_PROPERTIES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/executions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExecutionMockMvc;

    private Execution execution;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Execution createEntity(EntityManager em) {
        Execution execution = new Execution()
            .creationDate(DEFAULT_CREATION_DATE)
            .executionDescription(DEFAULT_EXECUTION_DESCRIPTION)
            .executionProperties(DEFAULT_EXECUTION_PROPERTIES);
        return execution;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Execution createUpdatedEntity(EntityManager em) {
        Execution execution = new Execution()
            .creationDate(UPDATED_CREATION_DATE)
            .executionDescription(UPDATED_EXECUTION_DESCRIPTION)
            .executionProperties(UPDATED_EXECUTION_PROPERTIES);
        return execution;
    }

    @BeforeEach
    public void initTest() {
        execution = createEntity(em);
    }

    @Test
    @Transactional
    void createExecution() throws Exception {
        int databaseSizeBeforeCreate = executionRepository.findAll().size();
        // Create the Execution
        restExecutionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(execution)))
            .andExpect(status().isCreated());

        // Validate the Execution in the database
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeCreate + 1);
        Execution testExecution = executionList.get(executionList.size() - 1);
        assertThat(testExecution.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testExecution.getExecutionDescription()).isEqualTo(DEFAULT_EXECUTION_DESCRIPTION);
        assertThat(testExecution.getExecutionProperties()).isEqualTo(DEFAULT_EXECUTION_PROPERTIES);
    }

    @Test
    @Transactional
    void createExecutionWithExistingId() throws Exception {
        // Create the Execution with an existing ID
        execution.setId(1L);

        int databaseSizeBeforeCreate = executionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExecutionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(execution)))
            .andExpect(status().isBadRequest());

        // Validate the Execution in the database
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllExecutions() throws Exception {
        // Initialize the database
        executionRepository.saveAndFlush(execution);

        // Get all the executionList
        restExecutionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(execution.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].executionDescription").value(hasItem(DEFAULT_EXECUTION_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].executionProperties").value(hasItem(DEFAULT_EXECUTION_PROPERTIES)));
    }

    @Test
    @Transactional
    void getExecution() throws Exception {
        // Initialize the database
        executionRepository.saveAndFlush(execution);

        // Get the execution
        restExecutionMockMvc
            .perform(get(ENTITY_API_URL_ID, execution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(execution.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.executionDescription").value(DEFAULT_EXECUTION_DESCRIPTION))
            .andExpect(jsonPath("$.executionProperties").value(DEFAULT_EXECUTION_PROPERTIES));
    }

    @Test
    @Transactional
    void getNonExistingExecution() throws Exception {
        // Get the execution
        restExecutionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewExecution() throws Exception {
        // Initialize the database
        executionRepository.saveAndFlush(execution);

        int databaseSizeBeforeUpdate = executionRepository.findAll().size();

        // Update the execution
        Execution updatedExecution = executionRepository.findById(execution.getId()).get();
        // Disconnect from session so that the updates on updatedExecution are not directly saved in db
        em.detach(updatedExecution);
        updatedExecution
            .creationDate(UPDATED_CREATION_DATE)
            .executionDescription(UPDATED_EXECUTION_DESCRIPTION)
            .executionProperties(UPDATED_EXECUTION_PROPERTIES);

        restExecutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExecution.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedExecution))
            )
            .andExpect(status().isOk());

        // Validate the Execution in the database
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeUpdate);
        Execution testExecution = executionList.get(executionList.size() - 1);
        assertThat(testExecution.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testExecution.getExecutionDescription()).isEqualTo(UPDATED_EXECUTION_DESCRIPTION);
        assertThat(testExecution.getExecutionProperties()).isEqualTo(UPDATED_EXECUTION_PROPERTIES);
    }

    @Test
    @Transactional
    void putNonExistingExecution() throws Exception {
        int databaseSizeBeforeUpdate = executionRepository.findAll().size();
        execution.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExecutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, execution.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(execution))
            )
            .andExpect(status().isBadRequest());

        // Validate the Execution in the database
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExecution() throws Exception {
        int databaseSizeBeforeUpdate = executionRepository.findAll().size();
        execution.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExecutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(execution))
            )
            .andExpect(status().isBadRequest());

        // Validate the Execution in the database
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExecution() throws Exception {
        int databaseSizeBeforeUpdate = executionRepository.findAll().size();
        execution.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExecutionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(execution)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Execution in the database
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExecutionWithPatch() throws Exception {
        // Initialize the database
        executionRepository.saveAndFlush(execution);

        int databaseSizeBeforeUpdate = executionRepository.findAll().size();

        // Update the execution using partial update
        Execution partialUpdatedExecution = new Execution();
        partialUpdatedExecution.setId(execution.getId());

        partialUpdatedExecution.creationDate(UPDATED_CREATION_DATE).executionProperties(UPDATED_EXECUTION_PROPERTIES);

        restExecutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExecution.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExecution))
            )
            .andExpect(status().isOk());

        // Validate the Execution in the database
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeUpdate);
        Execution testExecution = executionList.get(executionList.size() - 1);
        assertThat(testExecution.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testExecution.getExecutionDescription()).isEqualTo(DEFAULT_EXECUTION_DESCRIPTION);
        assertThat(testExecution.getExecutionProperties()).isEqualTo(UPDATED_EXECUTION_PROPERTIES);
    }

    @Test
    @Transactional
    void fullUpdateExecutionWithPatch() throws Exception {
        // Initialize the database
        executionRepository.saveAndFlush(execution);

        int databaseSizeBeforeUpdate = executionRepository.findAll().size();

        // Update the execution using partial update
        Execution partialUpdatedExecution = new Execution();
        partialUpdatedExecution.setId(execution.getId());

        partialUpdatedExecution
            .creationDate(UPDATED_CREATION_DATE)
            .executionDescription(UPDATED_EXECUTION_DESCRIPTION)
            .executionProperties(UPDATED_EXECUTION_PROPERTIES);

        restExecutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExecution.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExecution))
            )
            .andExpect(status().isOk());

        // Validate the Execution in the database
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeUpdate);
        Execution testExecution = executionList.get(executionList.size() - 1);
        assertThat(testExecution.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testExecution.getExecutionDescription()).isEqualTo(UPDATED_EXECUTION_DESCRIPTION);
        assertThat(testExecution.getExecutionProperties()).isEqualTo(UPDATED_EXECUTION_PROPERTIES);
    }

    @Test
    @Transactional
    void patchNonExistingExecution() throws Exception {
        int databaseSizeBeforeUpdate = executionRepository.findAll().size();
        execution.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExecutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, execution.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(execution))
            )
            .andExpect(status().isBadRequest());

        // Validate the Execution in the database
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExecution() throws Exception {
        int databaseSizeBeforeUpdate = executionRepository.findAll().size();
        execution.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExecutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(execution))
            )
            .andExpect(status().isBadRequest());

        // Validate the Execution in the database
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExecution() throws Exception {
        int databaseSizeBeforeUpdate = executionRepository.findAll().size();
        execution.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExecutionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(execution))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Execution in the database
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExecution() throws Exception {
        // Initialize the database
        executionRepository.saveAndFlush(execution);

        int databaseSizeBeforeDelete = executionRepository.findAll().size();

        // Delete the execution
        restExecutionMockMvc
            .perform(delete(ENTITY_API_URL_ID, execution.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Execution> executionList = executionRepository.findAll();
        assertThat(executionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
