package il.co.topq.testinv.domain;

import static org.assertj.core.api.Assertions.assertThat;

import il.co.topq.testinv.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExecutionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Execution.class);
        Execution execution1 = new Execution();
        execution1.setId(1L);
        Execution execution2 = new Execution();
        execution2.setId(execution1.getId());
        assertThat(execution1).isEqualTo(execution2);
        execution2.setId(2L);
        assertThat(execution1).isNotEqualTo(execution2);
        execution1.setId(null);
        assertThat(execution1).isNotEqualTo(execution2);
    }
}
