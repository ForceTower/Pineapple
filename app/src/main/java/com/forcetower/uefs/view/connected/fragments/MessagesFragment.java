package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.adapters.MessagesAdapter;
import com.forcetower.uefs.vm.MessagesViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.forcetower.uefs.Constants.URL_PATTERN;
import static com.forcetower.uefs.util.WordUtils.getLinksOnText;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class MessagesFragment extends Fragment implements Injectable {
    @BindView(R.id.recycler_view)
    RecyclerView rvMessages;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MessagesViewModel messagesViewModel;
    private MessagesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messagesViewModel = ViewModelProviders.of(this, viewModelFactory).get(MessagesViewModel.class);
        messagesViewModel.getMessages().observe(this, this::onMessagesReceived);
    }

    private void onMessagesReceived(List<Message> messages) {
        if (messages != null) {
            Timber.d("Messages Received");
            Collections.sort(messages);
            adapter.setMessages(messages);
        }
    }

    private void setupRecyclerView() {
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessagesAdapter(new ArrayList<>());
        adapter.setOnClickListener(this::onMessageClicked);
        rvMessages.setAdapter(adapter);
    }

    private void onMessageClicked(Message message) {
        List<String> links = getLinksOnText(message.getMessage());
        Timber.d("Links: %s", links);
        if (links.size() == 1)
            openLink(links.get(0));
        else if (links.size() > 1) {
            AlertDialog.Builder selectDialog = new AlertDialog.Builder(getContext());
            selectDialog.setIcon(R.drawable.ic_link_black_24dp);
            selectDialog.setTitle(R.string.select_a_link);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item);
            arrayAdapter.addAll(links);

            selectDialog.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

            selectDialog.setAdapter(arrayAdapter, (dialog, which) -> {
                String url = arrayAdapter.getItem(which);
                dialog.dismiss();
                openLink(url);
            });

            selectDialog.show();
        }
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
