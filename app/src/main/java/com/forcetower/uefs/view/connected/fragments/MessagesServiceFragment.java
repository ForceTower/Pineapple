package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentServerMessagesBinding;
import com.forcetower.uefs.db.entity.MessageUNES;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.adapters.MessagesServerAdapter;
import com.forcetower.uefs.vm.base.MessagesViewModel;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.forcetower.uefs.util.NetworkUtils.openLink;
import static com.forcetower.uefs.util.WordUtils.getLinksOnText;

public class MessagesServiceFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    AppExecutors executors;

    private MessagesServerAdapter adapter;
    private FragmentServerMessagesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_server_messages, container, false);
        setupRecycler();
        return binding.getRoot();
    }

    private void setupRecycler() {
        adapter = new MessagesServerAdapter();
        adapter.setOnClickListener(this::onMessageClicked);
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(adapter);
        binding.recycler.setItemAnimator(new DefaultItemAnimator());
        binding.recycler.setNestedScrollingEnabled(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MessagesViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(MessagesViewModel.class);
        viewModel.getServiceMessages().observe(this, this::onMessagesReceived);
    }

    private void onMessagesReceived(List<MessageUNES> messages) {
        if (messages != null) {
            Timber.d("Service Messages Received");

            if (messages.isEmpty()) {
                binding.noMessagesLayout.setVisibility(View.VISIBLE);
                binding.recycler.setVisibility(View.GONE);
            } else {
                binding.noMessagesLayout.setVisibility(View.GONE);
                binding.recycler.setVisibility(View.VISIBLE);

                executors.others().execute(() -> {
                    for (MessageUNES message : messages) {
                        SpannableString spannable = new SpannableString(message.getMessage());
                        Linkify.addLinks(spannable, Linkify.WEB_URLS);
                        message.setSpannable(spannable);
                    }
                    executors.mainThread().execute(() -> {
                        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED))
                            adapter.setMessages(messages);
                    });
                });
            }
        }
    }

    private void onMessageClicked(MessageUNES message) {
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
