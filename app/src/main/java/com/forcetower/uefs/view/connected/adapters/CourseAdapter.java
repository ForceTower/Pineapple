package com.forcetower.uefs.view.connected.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemCourseBinding;
import com.forcetower.uefs.db_service.entity.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 20/06/2018.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseHolder> {
    private final List<Course> courses;
    private final OnCourseClickListener listener;

    public CourseAdapter(OnCourseClickListener listener) {
        this.listener = listener;
        this.courses = new ArrayList<>();
    }

    public void setCourses(List<Course> courses) {
        this.courses.clear();
        this.courses.addAll(courses);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCourseBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_course, parent, false);
        return new CourseHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHolder holder, int position) {
        holder.bind(courses.get(position));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class CourseHolder extends RecyclerView.ViewHolder {
        private final ItemCourseBinding binding;

        CourseHolder(ItemCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(v -> onClick());
        }

        void bind(Course course) {
            this.binding.setCourse(course);
        }

        void onClick() {
            int position = getAdapterPosition();
            listener.onCourseClicked(courses.get(position));
        }
    }

    public interface OnCourseClickListener {
        void onCourseClicked(Course course);
    }
}
