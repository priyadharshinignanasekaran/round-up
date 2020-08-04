package tech.test.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.util.Currency;

public class Amount {
    private final Currency currency;
    private final BigDecimal minorUnits;

    public Amount(final Currency currency, final BigDecimal minorUnits) {

        this.currency = currency;
        this.minorUnits = minorUnits;
    }

    public String getCurrency() {
        return currency.getCurrencyCode();
    }

    public BigDecimal getMinorUnits() {
        return minorUnits;
    }

    @Override
    public final boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public final int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}