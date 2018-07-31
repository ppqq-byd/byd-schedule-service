package com.ora.blockchain.exception;

import org.quartz.SchedulerException;

public class ScheduleException extends RuntimeException {
    public ScheduleException(SchedulerException e) {
        super(e);
    }
}
