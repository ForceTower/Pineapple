package com.forcetower.uefs.db_service.helper;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import com.forcetower.uefs.db_service.entity.CreditsMention;
import com.forcetower.uefs.db_service.entity.Mention;

import java.util.List;

/**
 * Created by João Paulo on 03/06/2018.
 */
public class CreditAndMentions extends CreditsMention {
    @Relation(parentColumn = "uid", entityColumn = "credit_id", entity = Mention.class)
    private List<Mention> participants;

    public CreditAndMentions(String category) {
        super(category);
    }

    public List<Mention> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Mention> participants) {
        this.participants = participants;
    }
}
