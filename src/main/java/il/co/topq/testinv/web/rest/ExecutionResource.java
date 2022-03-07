package il.co.topq.testinv.web.rest;

import il.co.topq.testinv.domain.Execution;
import il.co.topq.testinv.repository.ExecutionRepository;
import il.co.topq.testinv.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link il.co.topq.testinv.domain.Execution}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ExecutionResource {

    private final Logger log = LoggerFactory.getLogger(ExecutionResource.class);

    private static final String ENTITY_NAME = "execution";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExecutionRepository executionRepository;

    public ExecutionResource(ExecutionRepository executionRepository) {
        this.executionRepository = executionRepository;
    }

    /**
     * {@code POST  /executions} : Create a new execution.
     *
     * @param execution the execution to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new execution, or with status {@code 400 (Bad Request)} if the execution has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/executions")
    public ResponseEntity<Execution> createExecution(@RequestBody Execution execution) throws URISyntaxException {
        log.debug("REST request to save Execution : {}", execution);
        if (execution.getId() != null) {
            throw new BadRequestAlertException("A new execution cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Execution result = executionRepository.save(execution);
        return ResponseEntity
            .created(new URI("/api/executions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /executions/:id} : Updates an existing execution.
     *
     * @param id the id of the execution to save.
     * @param execution the execution to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated execution,
     * or with status {@code 400 (Bad Request)} if the execution is not valid,
     * or with status {@code 500 (Internal Server Error)} if the execution couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/executions/{id}")
    public ResponseEntity<Execution> updateExecution(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Execution execution
    ) throws URISyntaxException {
        log.debug("REST request to update Execution : {}, {}", id, execution);
        if (execution.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, execution.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!executionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Execution result = executionRepository.save(execution);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, execution.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /executions/:id} : Partial updates given fields of an existing execution, field will ignore if it is null
     *
     * @param id the id of the execution to save.
     * @param execution the execution to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated execution,
     * or with status {@code 400 (Bad Request)} if the execution is not valid,
     * or with status {@code 404 (Not Found)} if the execution is not found,
     * or with status {@code 500 (Internal Server Error)} if the execution couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/executions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Execution> partialUpdateExecution(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Execution execution
    ) throws URISyntaxException {
        log.debug("REST request to partial update Execution partially : {}, {}", id, execution);
        if (execution.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, execution.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!executionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Execution> result = executionRepository
            .findById(execution.getId())
            .map(existingExecution -> {
                if (execution.getCreationDate() != null) {
                    existingExecution.setCreationDate(execution.getCreationDate());
                }
                if (execution.getExecutionDescription() != null) {
                    existingExecution.setExecutionDescription(execution.getExecutionDescription());
                }
                if (execution.getExecutionProperties() != null) {
                    existingExecution.setExecutionProperties(execution.getExecutionProperties());
                }

                return existingExecution;
            })
            .map(executionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, execution.getId().toString())
        );
    }

    /**
     * {@code GET  /executions} : get all the executions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of executions in body.
     */
    @GetMapping("/executions")
    public List<Execution> getAllExecutions() {
        log.debug("REST request to get all Executions");
        return executionRepository.findAll();
    }

    /**
     * {@code GET  /executions/:id} : get the "id" execution.
     *
     * @param id the id of the execution to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the execution, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/executions/{id}")
    public ResponseEntity<Execution> getExecution(@PathVariable Long id) {
        log.debug("REST request to get Execution : {}", id);
        Optional<Execution> execution = executionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(execution);
    }

    /**
     * {@code DELETE  /executions/:id} : delete the "id" execution.
     *
     * @param id the id of the execution to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/executions/{id}")
    public ResponseEntity<Void> deleteExecution(@PathVariable Long id) {
        log.debug("REST request to delete Execution : {}", id);
        executionRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
