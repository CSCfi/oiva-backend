package fi.minedu.oiva.backend.model.jooq;

import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.JSONB;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Objects;

public interface DefaultJsonBindings<U> extends Binding<JSONB, U> {

    Converter<JSONB, U> converter();

    @Override
    default void sql(final BindingSQLContext<U> ctx) throws SQLException {
        ctx.render().visit(DSL.val(ctx.convert(converter()).value())).sql("::jsonb");
    }

    @Override
    default void register(final BindingRegisterContext<U> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    @Override
    default void set(final BindingSetStatementContext<U> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
    }

    @Override
    @SuppressWarnings("unchecked")
    default void get(final BindingGetResultSetContext<U> ctx) throws SQLException {
        ctx.convert(converter()).value(JSONB.valueOf(ctx.resultSet().getString(ctx.index())));
    }

    @Override
    @SuppressWarnings("unchecked")
    default void get(final BindingGetStatementContext<U> ctx) throws SQLException {
        ctx.convert(converter()).value(JSONB.valueOf(ctx.statement().getString(ctx.index())));
    }

    @Override
    default void set(final BindingSetSQLOutputContext<U> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void get(final BindingGetSQLInputContext<U> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
