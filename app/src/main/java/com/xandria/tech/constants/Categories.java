package com.xandria.tech.constants;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xandria.tech.model.CategoryModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Categories {
    public static final String[] categories = {
            "Religion", "History", "Literature", "Politics", "Economics", "Business",
            "Self Help", "Sciences", "Computer", "Mathematics", "Psychology", "Philosophy"
    };
    public static final Map<String, String[]> categoriesMap = new HashMap<>();

    // this is not an exhaustive list, it just creates a place holder other subcategories should be added dynamically
    static void createCategoriesMap(){
        categoriesMap.put("Religion", new String[]{"Hindu", "Christianity", "Islam"});
        categoriesMap.put("History", new String[]{"Biographies", "Monarchs", "Wars"});
        categoriesMap.put("Literature", new String[]{"Drama", "Fantasy", "Fiction"});
        categoriesMap.put("Politics", new String[]{"Laws", "Wars"});
        categoriesMap.put("Economics", new String[]{"Microeconomics", "Macroeconomics", "Trade"});
        categoriesMap.put("Business", new String[]{"Startups", "Entrepreneur"});
        categoriesMap.put("Self Help", new String[]{"Diet", "Motivation", "Inspiration", "Mindfulness"});
        categoriesMap.put("Sciences", new String[]{"Biology", "History", "Physics"});
        categoriesMap.put("Computer", new String[]{"Technology", "Programming", "Software", "Hardware"});
        categoriesMap.put("Mathematics", new String[]{"Algebra", "Number", "Arithmetic", "Calculus", "Numerical Analysis"});
        categoriesMap.put("Psychology", new String[]{"Personality", "Cognitive", "Social"});
        categoriesMap.put("Philosophy", new String[]{"Metaphysics", "Epistemology", "Axiology", "Logic"});
    }

    static final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.CATEGORIES);

    // this should run if the categories database is empty
    public static void initCategories(){
        createCategoriesMap();
        for (String category: categories) {
            CategoryModel categoryModel = new CategoryModel(
                    category,
                    Arrays.asList(Objects.requireNonNull(categoriesMap.get(category)))
            );
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) databaseReference.child(category).setValue(categoryModel); // only initialise if database is empty
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // in case categories are modified previous ones should be cleared
    public static void clearCategories(){
        databaseReference.removeValue();
    }

    // in case you modified the main categories above you can update
    public static void updateCategories(){
        clearCategories();
        initCategories();
    }
}
