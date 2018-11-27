package fi.minedu.oiva.backend.util;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Stuff to handle queries more easily
 */
public class DbUtils {

    public static Collection<Field<?>> combine(final Field<?>[] fields1, final Field<?>... fields2) {
        final Collection<Field<?>> searchFields = new ArrayList<>();
        Collections.addAll(searchFields, fields1);
        Collections.addAll(searchFields, fields2);
        return searchFields;
    }

    /**
     * Conditional condition builder
     */
    public static class Conditions {
        private Conditions() {}

        public static Conditions conditions() {
            return new Conditions();
        }

        private List<Supplier<Condition>> conditionSuppliers = new ArrayList<>();

        public Conditions add(final boolean isPresent, final Supplier<Condition> conditionSupplier) {
            if (isPresent) conditionSuppliers.add(conditionSupplier);
            return this;
        }

        public Conditions add(final Supplier<Condition> conditionSupplier) {
            conditionSuppliers.add(conditionSupplier);
            return this;
        }

        public Condition buildAnd() {
            return conditionSuppliers
                .stream()
                .map(Supplier::get)
                .reduce(DSL.trueCondition(), Condition::and);
        }

        public Condition buildOr() {
            return conditionSuppliers
                .stream()
                .map(Supplier::get)
                .reduce(DSL.falseCondition(), Condition::or);
        }
    }
}
