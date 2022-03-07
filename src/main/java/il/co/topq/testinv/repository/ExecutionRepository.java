package il.co.topq.testinv.repository;

import il.co.topq.testinv.domain.Execution;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Execution entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExecutionRepository extends JpaRepository<Execution, Long> {}
