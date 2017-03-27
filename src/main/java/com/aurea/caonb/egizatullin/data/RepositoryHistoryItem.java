package com.aurea.caonb.egizatullin.data;

import java.util.Date;

public class RepositoryHistoryItem {

    public final Date date;
    public final RepositoryState state;
    public final String message;

    public RepositoryHistoryItem(Date date, RepositoryState state, String message) {
        this.date = date;
        this.state = state;
        this.message = message;
    }
}
