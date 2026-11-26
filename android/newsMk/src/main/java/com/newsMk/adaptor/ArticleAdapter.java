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

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<Map<String, String>> data;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ArticleAdapter(Context context, List<Map<String, String>> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> item = data.get(position);
        holder.title.setText(item.get("1"));
        holder.intro.setText(item.get("2"));
        holder.author.setText(item.get("8"));
        holder.date.setText(item.get("5"));

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, intro, author, date;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_row);
            intro = itemView.findViewById(R.id.intro_row);
            author = itemView.findViewById(R.id.autor_row);
            date = itemView.findViewById(R.id.cD_row);
        }
    }
}
