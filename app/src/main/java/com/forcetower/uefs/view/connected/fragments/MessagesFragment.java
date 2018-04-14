package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.adapters.MessagesAdapter;
import com.forcetower.uefs.vm.MessagesViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.forcetower.uefs.util.NetworkUtils.openLink;
import static com.forcetower.uefs.util.WordUtils.getLinksOnText;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class MessagesFragment extends Fragment implements Injectable {
    @BindView(R.id.recycler_view)
    RecyclerView rvMessages;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MessagesViewModel messagesViewModel;
    private MessagesAdapter adapter;

    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        ButterKnife.bind(this, view);
        controller.getTabLayout().setVisibility(View.GONE);
        setupRecyclerView();
        setupRefreshLayout();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messagesViewModel = ViewModelProviders.of(this, viewModelFactory).get(MessagesViewModel.class);
        messagesViewModel.getMessages().observe(this, this::onMessagesReceived);
        messagesViewModel.refresh(false).observe(this, this::onUpdateReceived);
        refreshLayout.setRefreshing(messagesViewModel.isRefreshing());
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

    private void setupRefreshLayout() {
        refreshLayout.setOnRefreshListener(() -> {
            if (messagesViewModel.isRefreshing()) return;

            messagesViewModel.refresh(true).observe(this, this::onUpdateReceived);
            refreshLayout.setRefreshing(true);
            messagesViewModel.setRefreshing(true);
        });
    }

    private void onUpdateReceived(Resource<Integer> resource) {
        if (resource == null) return;

        if (resource.status == Status.SUCCESS) {
            refreshLayout.setRefreshing(false);
            messagesViewModel.setRefreshing(false);
        } else if (resource.status == Status.ERROR) {
            refreshLayout.setRefreshing(false);
            messagesViewModel.setRefreshing(false);
            if (resource.data != null)
                Toast.makeText(getContext(), resource.data, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
        } else {
            //noinspection ConstantConditions
            Timber.d("Updating.. Received Status: %s", getString(resource.data));
        }
    }

    private void onMessageClicked(Message message) {
        List<String> links = getLinksOnText(message.getMessage());
        Timber.d("Links: %s", links);
        if (links.size() == 1)
            openLink(requireContext(), links.get(0));
        else if (links.size() > 1) {
            AlertDialog.Builder selectDialog = new AlertDialog.Builder(requireContext());
            selectDialog.setIcon(R.drawable.ic_link_black_24dp);
            selectDialog.setTitle(R.string.select_a_link);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.select_dialog_item);
            arrayAdapter.addAll(links);

            selectDialog.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

            selectDialog.setAdapter(arrayAdapter, (dialog, which) -> {
                String url = arrayAdapter.getItem(which);
                dialog.dismiss();
                openLink(requireContext(), url);
            });

            selectDialog.show();
        }
    }
}
