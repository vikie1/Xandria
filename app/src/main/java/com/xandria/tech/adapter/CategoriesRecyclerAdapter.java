package com.xandria.tech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xandria.tech.R;

import java.util.List;

public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.ViewHolder>{
    private final List<String> categories;
    private final Context context;
    public final CategoryClicked categoryClicked;

    public CategoriesRecyclerAdapter(Context context, List<String> categories, CategoryClicked categoryClicked) {
        this.context = context;
        this.categories = categories;
        this.categoryClicked = categoryClicked;
    }

    @NonNull
    @Override
    public CategoriesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_list, parent, false);
        return new CategoriesRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryView.setText(category);
        holder.romanNumView.setText(getRomanFromInt(position+1));
        holder.categoryView.setOnClickListener(v -> categoryClicked.onCategoryClick(category));

        // add colors
//        if (position == 0){
//            holder.categoryView.setBackgroundColor(Color.WHITE);
//            holder.categoryView.setTextColor(Color.BLACK);
//        }
//        else if (position == categories.size() -1){
//            holder.categoryView.setBackgroundColor(Color.BLACK);
//            holder.categoryView.setTextColor(Color.WHITE);
//        }
//        else {
//            Random r = new Random();
//            int red = r.nextInt(255);
//            int green = r.nextInt(255);
//            int blue = r.nextInt(255);
//            holder.categoryView.setBackgroundColor(Color.rgb(red, green, blue));
//        }
//        System.out.println();
    }

    private String getRomanFromInt(int decimal){
        switch (decimal){
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            case 11: return "XI";
            case 12: return "XII";
            default: return "N/A";
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView categoryView;
        private final TextView romanNumView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryView = itemView.findViewById(R.id.simple_list_text);
            romanNumView = itemView.findViewById(R.id.simple_list_text_label);
        }
    }

    public interface CategoryClicked{
        void onCategoryClick(String category);
    }
}
