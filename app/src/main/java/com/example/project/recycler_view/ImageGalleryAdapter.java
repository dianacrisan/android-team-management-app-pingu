package com.example.project.recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;

import java.util.Collections;
import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ProjectsViewHolder> {

    public abstract static class ClickListener {
        public abstract void click(int index);
    }

    List<ProjectsData> list = Collections.emptyList();

    Context context;
    ClickListener listener;

    public ImageGalleryAdapter(List<ProjectsData> list, Context context, ClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View photoView = inflater.inflate(R.layout.project_card, parent, false);
        ProjectsViewHolder viewHolder = new ProjectsViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();
        viewHolder.projectName.setText(list.get(position).projectName);
        viewHolder.projectDeadline.setText(list.get(position).deadline);
        viewHolder.activities.setText(list.get(position).activities);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                listener.click(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
