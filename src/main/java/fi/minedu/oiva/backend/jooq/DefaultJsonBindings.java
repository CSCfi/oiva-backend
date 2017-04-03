package fi.minedu.oiva.backend.jooq;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Objects;

public interface DefaultJsonBindings<U> extends Binding<Object, U> {

    Converter<Object, U> converter();

    @Override
    default void sql(BindingSQLContext<U> ctx) throws SQLException {
        ctx.render().visit(DSL.val(ctx.convert(converter()).value())).sql("::jsonb");
    }

    @Override
    default void register(BindingRegisterContext<U> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    @Override
    default void set(BindingSetStatementContext<U> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
    }

    @Override
    @SuppressWarnings("unchecked")
    default void get(BindingGetResultSetContext<U> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
    }

    @Override
    @SuppressWarnings("unchecked")
    default void get(BindingGetStatementContext<U> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
    }

    @Override
    default void set(BindingSetSQLOutputContext<U> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void get(BindingGetSQLInputContext<U> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

}
