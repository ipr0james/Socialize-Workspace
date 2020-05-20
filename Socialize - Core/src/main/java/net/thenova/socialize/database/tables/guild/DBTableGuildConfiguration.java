package net.thenova.socialize.database.tables.guild;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.titan.library.database.sql.table.DatabaseTable;
import net.thenova.titan.library.database.sql.table.column.TableColumn;
import net.thenova.titan.library.database.sql.table.column.data_type.BigInt;
import net.thenova.titan.library.database.sql.table.column.data_type.IntAutoIncrement;
import net.thenova.titan.library.database.sql.table.column.data_type.Text;
import net.thenova.titan.library.database.sql.table.column.data_type.VarChar;

public final class DBTableGuildConfiguration extends DatabaseTable {

    public DBTableGuildConfiguration() {
        super("guild_configuration", new DBSocialize());
    }

    @Override
    public void init() {
        registerColumn(
                new TableColumn("id", new IntAutoIncrement()).setPrimary(),
                new TableColumn("guild_id", new BigInt()).setForeign("guild_data", "guild_id"),
                new TableColumn("data_key", new VarChar(VarChar.LENGTH_DEFAULT)).setNullable(false),
                new TableColumn("data_value", new Text()).setNullable(false)
        );
    }
}
