package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.db.dao.MessageDao;
import com.forcetower.uefs.db.entity.Message;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class MessagesViewModel extends ViewModel {
    private final MessageDao messageDao;
    private LiveData<List<Message>> messages;

    @Inject
    MessagesViewModel(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public LiveData<List<Message>> getMessages() {
        if (messages == null) {
            messages = messageDao.getAllMessages();
        }
        return messages;
    }
}
