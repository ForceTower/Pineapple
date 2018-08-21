package com.forcetower.uefs.vm.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.forcetower.uefs.db.dao.MessageDao;
import com.forcetower.uefs.db.dao.MessageUNESDao;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.db.entity.MessageUNES;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.sgrs.RefreshRepository;
import com.forcetower.uefs.util.AbsentLiveData;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class MessagesViewModel extends ViewModel {
    private final RefreshRepository refreshRepository;
    private final MessageDao messageDao;
    private final MessageUNESDao messageUNESDao;
    private LiveData<List<Message>> messages;
    private LiveData<List<MessageUNES>> serviceMessages;
    private boolean refreshing;

    private LiveData<Resource<Integer>> refresh;

    @Inject
    MessagesViewModel(RefreshRepository refreshRepository, MessageDao messageDao, MessageUNESDao messageUNESDao) {
        this.refreshRepository = refreshRepository;
        this.messageDao = messageDao;
        this.messageUNESDao = messageUNESDao;
    }

    public LiveData<List<Message>> getMessages() {
        if (messages == null) {
            messages = messageDao.getAllMessages();
        }
        return messages;
    }

    public LiveData<List<MessageUNES>> getServiceMessages() {
        if (serviceMessages == null) {
            serviceMessages = messageUNESDao.getAllMessages();
        }
        return serviceMessages;
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public LiveData<Resource<Integer>> refresh(boolean start) {
        if (start) {
            if (refresh == null) {
                refresh = refreshRepository.refreshData();
            }
            return refresh;
        } else {
            if (refresh == null)
                return AbsentLiveData.create();
            return refresh;
        }
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        if (!refreshing) refresh = null;
    }
}
