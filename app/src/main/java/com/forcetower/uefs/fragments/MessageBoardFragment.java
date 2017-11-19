package com.forcetower.uefs.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.forcetower.uefs.R;
import com.forcetower.uefs.adapters.MessageBoardAdapter;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

import java.util.List;

/**
 * Created by João Paulo on 18/11/2017.
 */

public class MessageBoardFragment extends Fragment {
    private Context context;
    private View rootView;
    private RecyclerView recyclerView;
    private RelativeLayout relativeLayout;

    public static MessageBoardFragment newInstance() {
        return new MessageBoardFragment();
    }

    public MessageBoardFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message_board, container, false);
        context = getActivity();

        relativeLayout = rootView.findViewById(R.id.rl_root);
        recyclerView = rootView.findViewById(R.id.rv_messages);

        if (Utils.supportsMaterialDesign()) {
            rootView.setNestedScrollingEnabled(false);
            relativeLayout.setNestedScrollingEnabled(false);
        }

        fillWithMessages();

        return rootView;
    }

    private void fillWithMessages() {
        List<SagresMessage> messages = SagresProfile.getCurrentProfile().getMessages();

        if (Utils.supportsMaterialDesign()) relativeLayout.setElevation(2);
        relativeLayout.setBackgroundResource(android.R.color.white);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new MessageBoardAdapter(context, messages));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);

    }
}
