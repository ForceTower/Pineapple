package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.db.dao.MessageDao;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.rep.sgrs.RefreshRepository;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.util.AbsentLiveData;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class MessagesViewModel extends ViewModel {
    private final RefreshRepository refreshRepository;
    private final MessageDao messageDao;
    private LiveData<List<Message>> messages;
    private boolean refreshing;

    private LiveData<Resource<Integer>> refresh;

    @Inject
    MessagesViewModel(RefreshRepository refreshRepository, MessageDao messageDao) {
        this.refreshRepository = refreshRepository;
        this.messageDao = messageDao;
    }

    public LiveData<List<Message>> getMessages() {
        if (messages == null) {
            messages = messageDao.getAllMessages();
        }
        return messages;
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
