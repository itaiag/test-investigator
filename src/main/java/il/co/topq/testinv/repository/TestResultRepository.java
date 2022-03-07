package il.co.topq.testinv.repository;

import il.co.topq.testinv.domain.TestResult;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TestResult entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {}
