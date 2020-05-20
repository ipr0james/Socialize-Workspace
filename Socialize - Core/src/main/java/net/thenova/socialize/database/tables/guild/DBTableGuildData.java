package net.thenova.socialize.database.tables.guild;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.titan.library.database.sql.table.DatabaseTable;
import net.thenova.titan.library.database.sql.table.column.TableColumn;
import net.thenova.titan.library.database.sql.table.column.data_type.BigInt;

public final class DBTableGuildData extends DatabaseTable {

    public DBTableGuildData() {
        super("guild_data", new DBSocialize());
    }

    @Override
    public void init() {
        registerColumn(
                new TableColumn("guild_id", new BigInt()).setPrimary()
        );
    }
}
