package xw.graphqlserver.configs;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.regex.Pattern;

public class CamelCasePhysicalNamingStrategy implements PhysicalNamingStrategy {
    @Override
    public Identifier toPhysicalCatalogName(
        Identifier name, JdbcEnvironment jdbcEnvironment
    ) {
        return name;
    }

    @Override
    public Identifier toPhysicalSchemaName(
        Identifier name, JdbcEnvironment jdbcEnvironment
    ) {
        return name;
    }

    @Override
    public Identifier toPhysicalTableName(
        Identifier name, JdbcEnvironment jdbcEnvironment
    ) {
        return name;
    }

    @Override
    public Identifier toPhysicalSequenceName(
        Identifier name, JdbcEnvironment jdbcEnvironment
    ) {
        return name;
    }

    @Override
    public Identifier toPhysicalColumnName(
        Identifier name, JdbcEnvironment jdbcEnvironment
    ) {
        // This has been such a pain to figure out 😖. Postgres didn't like
        // createdAt without wrapping it around double quotes. case sensitivity
        if (Pattern.compile("[A-Z]+").matcher(name.getText()).find()) {
            return Identifier.toIdentifier("\"" + name.getText() + "\"");
        } else {
            return name;
        }
    }
}
