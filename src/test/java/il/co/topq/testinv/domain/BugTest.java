package il.co.topq.testinv.domain;

import static org.assertj.core.api.Assertions.assertThat;

import il.co.topq.testinv.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BugTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bug.class);
        Bug bug1 = new Bug();
        bug1.setId(1L);
        Bug bug2 = new Bug();
        bug2.setId(bug1.getId());
        assertThat(bug1).isEqualTo(bug2);
        bug2.setId(2L);
        assertThat(bug1).isNotEqualTo(bug2);
        bug1.setId(null);
        assertThat(bug1).isNotEqualTo(bug2);
    }
}
