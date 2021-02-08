package org.dockbox.selene.database.exceptions;

import org.dockbox.selene.core.exceptions.global.UncheckedSeleneException;
import org.jooq.exception.DataAccessException;

public class NoSuchTableException extends UncheckedSeleneException
{
    public NoSuchTableException(String tableName, DataAccessException e)
    {
        super("Table '" + tableName + "' does not exist", e);
    }
}
