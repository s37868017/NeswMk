package com.newsMk.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newsMk.R;

import java.util.List;
import java.util.Map;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.ViewHolder> {

    private Context context;
    private List<Map<String, String>> data;

    public PublicationAdapter(Context context, List<Map<String, String>> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_list_publication, parent, false);
        return new ViewHolder(view);
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> item = data.get(position);
        holder.title.setText(item.get("title"));
        holder.intro.setText(item.get("intro"));

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, intro;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_row);
            intro = itemView.findViewById(R.id.intro_row);
        }
    }
}