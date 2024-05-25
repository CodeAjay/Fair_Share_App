package com.example.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SelectablePersonAdapter extends RecyclerView.Adapter<SelectablePersonAdapter.SelectablePersonViewHolder> {
    private List<Person> persons;
    private List<Person> selectedPersons = new ArrayList<>();

    public SelectablePersonAdapter(List<Person> persons) {
        this.persons = persons;
    }

    @NonNull
    @Override
    public SelectablePersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person_selectable, parent, false);
        return new SelectablePersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectablePersonViewHolder holder, int position) {
        Person person = persons.get(position);
        holder.nameTextView.setText(person.getPersonName());
        holder.checkBox.setChecked(selectedPersons.contains(person));

        holder.itemView.setOnClickListener(v -> {
            if (selectedPersons.contains(person)) {
                selectedPersons.remove(person);
                holder.checkBox.setChecked(false);
            } else {
                selectedPersons.add(person);
                holder.checkBox.setChecked(true);
            }
        });

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedPersons.contains(person)) {
                    selectedPersons.add(person);
                }
            } else {
                selectedPersons.remove(person);
            }
        });
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public List<Person> getSelectedPersons() {
        return new ArrayList<>(selectedPersons);
    }

    public static class SelectablePersonViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        CheckBox checkBox;

        public SelectablePersonViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.personName);
            checkBox = itemView.findViewById(R.id.personCheckBox);
        }
    }
}

