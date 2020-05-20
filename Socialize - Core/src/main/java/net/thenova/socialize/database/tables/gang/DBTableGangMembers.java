package net.thenova.socialize.database.tables.gang;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.titan.library.database.sql.table.DatabaseTable;
import net.thenova.titan.library.database.sql.table.column.TableColumn;
import net.thenova.titan.library.database.sql.table.column.data_type.BigInt;
import net.thenova.titan.library.database.sql.table.column.data_type.IntAutoIncrement;
import net.thenova.titan.library.database.sql.table.column.data_type.VarChar;

/**
 * Copyright 2019 ipr0james
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class DBTableGangMembers extends DatabaseTable {

    public DBTableGangMembers() {
        super("gang_members", new DBSocialize());
    }

    @Override
    public void init() {
        registerColumn(
                new TableColumn("id", new IntAutoIncrement()).setPrimary(),
                new TableColumn("gang_id", new BigInt()).setForeign("gang_gangs", "gang_id"),
                new TableColumn("user_id", new BigInt()),
                new TableColumn("user_role", new VarChar(VarChar.LENGTH_NAME))
        );
    }
}
