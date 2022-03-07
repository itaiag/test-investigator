package il.co.topq.testinv.repository;

import il.co.topq.testinv.domain.Bug;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Bug entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {}
