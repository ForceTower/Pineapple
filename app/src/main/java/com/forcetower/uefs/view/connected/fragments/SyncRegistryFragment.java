package com.forcetower.uefs.view.connected.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Rect;
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

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentSyncRegistryBinding;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.SyncRegistry;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.adapters.SyncRegistryAdapter;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 23/06/2018.
 */
public class SyncRegistryFragment extends Fragment implements Injectable {
    @Inject
    AppDatabase database;

    private FragmentSyncRegistryBinding binding;
    private SyncRegistryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sync_registry, container, false);
        prepareRecyclerView();
        return binding.getRoot();
    }

    private void prepareRecyclerView() {
        adapter = new SyncRegistryAdapter();
        binding.recyclerRegistry.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position == parent.getAdapter().getItemCount() - 1) {
                    outRect.setEmpty();
                } else {
                    super.getItemOffsets(outRect, view, parent, state);
                }
            }
        });
        binding.recyclerRegistry.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerRegistry.setNestedScrollingEnabled(false);
        binding.recyclerRegistry.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        database.syncRegistryDao().getAllRegistry().observe(this, this::onRegistryUpdate);
    }

    private void onRegistryUpdate(List<SyncRegistry> syncRegistries) {
        adapter.setRegistries(syncRegistries);
    }
}
