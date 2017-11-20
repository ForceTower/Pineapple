package com.forcetower.uefs.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.MessageActivity;
import com.forcetower.uefs.adapters.ui.MessageBoardAdapter;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

import java.util.List;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 18/11/2017.
 */

public class MessageBoardFragment extends Fragment {
    private Context context;
    private RecyclerView recyclerView;
    private RelativeLayout relativeLayout;

    public static MessageBoardFragment newInstance() {
        return new MessageBoardFragment();
    }

    public MessageBoardFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message_board, container, false);

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

        MessageBoardAdapter messageAdapter = new MessageBoardAdapter(context, messages);
        messageAdapter.setOnMessageClickListener(onMessageClickListener);

        if (Utils.supportsMaterialDesign()) relativeLayout.setElevation(2);
        relativeLayout.setBackgroundResource(android.R.color.white);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(messageAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);

    }

    private MessageBoardAdapter.OnMessageClickListener onMessageClickListener = new MessageBoardAdapter.OnMessageClickListener() {
        @Override
        public void onMessageClicked(View view, int position, SagresMessage message) {
            Log.d(APP_TAG, "Clicked Message: " + message);
            MessageActivity.startActivity(context, message);
        }
    };
}
