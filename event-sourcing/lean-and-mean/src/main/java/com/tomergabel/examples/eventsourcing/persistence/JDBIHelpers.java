package com.tomergabel.examples.eventsourcing.persistence;

import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import java.sql.BatchUpdateException;
import java.sql.SQLIntegrityConstraintViolationException;

public class JDBIHelpers {
    public static boolean isPKViolation(Throwable e) {
        if (e == null)
            return false;
        else if (e instanceof UnableToExecuteStatementException)
            return isPKViolation(e.getCause());
        else if (e instanceof BatchUpdateException)
            return isPKViolation(e.getCause());
        else
            return e instanceof SQLIntegrityConstraintViolationException;
    }
}
