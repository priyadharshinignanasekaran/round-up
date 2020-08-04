package tech.test.domain

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class AmountTest extends Specification {
    def "Equals"() {
        expect:
        EqualsVerifier.forClass(Amount.class).verify()
    }
}
