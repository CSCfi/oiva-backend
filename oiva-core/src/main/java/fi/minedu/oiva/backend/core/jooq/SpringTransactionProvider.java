package fi.minedu.oiva.backend.core.jooq;

import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_NESTED;

public class SpringTransactionProvider implements TransactionProvider {

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Override
    public void begin(final TransactionContext ctx) {
        final TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition(PROPAGATION_NESTED));
        ctx.transaction(new SpringTransaction(tx));
    }

    @Override
    public void commit(final TransactionContext ctx) {
        transactionManager.commit(((SpringTransaction) ctx.transaction()).tx);
    }

    @Override
    public void rollback(final TransactionContext ctx) {
        transactionManager.rollback(((SpringTransaction) ctx.transaction()).tx);
    }
}
