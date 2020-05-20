package net.thenova.socialize.database;

import net.thenova.titan.library.database.connection.IDatabase;

public final class DBSocialize implements IDatabase {
    @Override
    public String database() {
        return "default";
    }
}
