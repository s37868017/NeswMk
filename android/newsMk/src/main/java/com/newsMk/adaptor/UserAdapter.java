package com.newsMk.adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newsMk.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<Map<String, String>> userList;

    public UserAdapter(Context context, List<Map<String, String>> userList) {
        this.context = context;
        this.userList = userList != null ? userList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> user = userList.get(position);

        holder.userName.setText(user.get("login"));
        holder.role.setText(user.get("roles"));
        holder.articleCount.setText("Статей: " + user.get("count"));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent("com.newsMk.ShowUser")
                    .setClassName(context.getPackageName(), "com.newsMk.ShowUser");
            intent.putExtra("id", user.get("id"));
            intent.putExtra("login", user.get("login"));
            intent.putExtra("count", user.get("count"));
            intent.putExtra("roles", user.get("roles"));
            intent.putExtra("email", user.get("email"));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateData(List<Map<String, String>> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, role, articleCount;

        ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName_row);
            role = itemView.findViewById(R.id.roleUser_row);
            articleCount = itemView.findViewById(R.id.number_row);
        }
    }
}