package il.co.topq.testinv.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import il.co.topq.testinv.IntegrationTest;
import il.co.topq.testinv.domain.Bug;
import il.co.topq.testinv.domain.enumeration.BugStatus;
import il.co.topq.testinv.repository.BugRepository;
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
 * Integration tests for the {@link BugResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BugResourceIT {

    private static final String DEFAULT_BUG_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BUG_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BugStatus DEFAULT_STATUS = BugStatus.OPEN;
    private static final BugStatus UPDATED_STATUS = BugStatus.CLOSE;

    private static final String ENTITY_API_URL = "/api/bugs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBugMockMvc;

    private Bug bug;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bug createEntity(EntityManager em) {
        Bug bug = new Bug().bugName(DEFAULT_BUG_NAME).description(DEFAULT_DESCRIPTION).status(DEFAULT_STATUS);
        return bug;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bug createUpdatedEntity(EntityManager em) {
        Bug bug = new Bug().bugName(UPDATED_BUG_NAME).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);
        return bug;
    }

    @BeforeEach
    public void initTest() {
        bug = createEntity(em);
    }

    @Test
    @Transactional
    void createBug() throws Exception {
        int databaseSizeBeforeCreate = bugRepository.findAll().size();
        // Create the Bug
        restBugMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bug)))
            .andExpect(status().isCreated());

        // Validate the Bug in the database
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeCreate + 1);
        Bug testBug = bugList.get(bugList.size() - 1);
        assertThat(testBug.getBugName()).isEqualTo(DEFAULT_BUG_NAME);
        assertThat(testBug.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBug.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createBugWithExistingId() throws Exception {
        // Create the Bug with an existing ID
        bug.setId(1L);

        int databaseSizeBeforeCreate = bugRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBugMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bug)))
            .andExpect(status().isBadRequest());

        // Validate the Bug in the database
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBugs() throws Exception {
        // Initialize the database
        bugRepository.saveAndFlush(bug);

        // Get all the bugList
        restBugMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bug.getId().intValue())))
            .andExpect(jsonPath("$.[*].bugName").value(hasItem(DEFAULT_BUG_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getBug() throws Exception {
        // Initialize the database
        bugRepository.saveAndFlush(bug);

        // Get the bug
        restBugMockMvc
            .perform(get(ENTITY_API_URL_ID, bug.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bug.getId().intValue()))
            .andExpect(jsonPath("$.bugName").value(DEFAULT_BUG_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBug() throws Exception {
        // Get the bug
        restBugMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBug() throws Exception {
        // Initialize the database
        bugRepository.saveAndFlush(bug);

        int databaseSizeBeforeUpdate = bugRepository.findAll().size();

        // Update the bug
        Bug updatedBug = bugRepository.findById(bug.getId()).get();
        // Disconnect from session so that the updates on updatedBug are not directly saved in db
        em.detach(updatedBug);
        updatedBug.bugName(UPDATED_BUG_NAME).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);

        restBugMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBug.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBug))
            )
            .andExpect(status().isOk());

        // Validate the Bug in the database
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeUpdate);
        Bug testBug = bugList.get(bugList.size() - 1);
        assertThat(testBug.getBugName()).isEqualTo(UPDATED_BUG_NAME);
        assertThat(testBug.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBug.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingBug() throws Exception {
        int databaseSizeBeforeUpdate = bugRepository.findAll().size();
        bug.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBugMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bug.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bug))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bug in the database
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBug() throws Exception {
        int databaseSizeBeforeUpdate = bugRepository.findAll().size();
        bug.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBugMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bug))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bug in the database
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBug() throws Exception {
        int databaseSizeBeforeUpdate = bugRepository.findAll().size();
        bug.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBugMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bug)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bug in the database
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBugWithPatch() throws Exception {
        // Initialize the database
        bugRepository.saveAndFlush(bug);

        int databaseSizeBeforeUpdate = bugRepository.findAll().size();

        // Update the bug using partial update
        Bug partialUpdatedBug = new Bug();
        partialUpdatedBug.setId(bug.getId());

        partialUpdatedBug.bugName(UPDATED_BUG_NAME);

        restBugMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBug.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBug))
            )
            .andExpect(status().isOk());

        // Validate the Bug in the database
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeUpdate);
        Bug testBug = bugList.get(bugList.size() - 1);
        assertThat(testBug.getBugName()).isEqualTo(UPDATED_BUG_NAME);
        assertThat(testBug.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBug.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateBugWithPatch() throws Exception {
        // Initialize the database
        bugRepository.saveAndFlush(bug);

        int databaseSizeBeforeUpdate = bugRepository.findAll().size();

        // Update the bug using partial update
        Bug partialUpdatedBug = new Bug();
        partialUpdatedBug.setId(bug.getId());

        partialUpdatedBug.bugName(UPDATED_BUG_NAME).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);

        restBugMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBug.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBug))
            )
            .andExpect(status().isOk());

        // Validate the Bug in the database
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeUpdate);
        Bug testBug = bugList.get(bugList.size() - 1);
        assertThat(testBug.getBugName()).isEqualTo(UPDATED_BUG_NAME);
        assertThat(testBug.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBug.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingBug() throws Exception {
        int databaseSizeBeforeUpdate = bugRepository.findAll().size();
        bug.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBugMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bug.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bug))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bug in the database
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBug() throws Exception {
        int databaseSizeBeforeUpdate = bugRepository.findAll().size();
        bug.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBugMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bug))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bug in the database
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBug() throws Exception {
        int databaseSizeBeforeUpdate = bugRepository.findAll().size();
        bug.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBugMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bug)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bug in the database
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBug() throws Exception {
        // Initialize the database
        bugRepository.saveAndFlush(bug);

        int databaseSizeBeforeDelete = bugRepository.findAll().size();

        // Delete the bug
        restBugMockMvc.perform(delete(ENTITY_API_URL_ID, bug.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Bug> bugList = bugRepository.findAll();
        assertThat(bugList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
