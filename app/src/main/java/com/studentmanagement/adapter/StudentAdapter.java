package com.studentmanagement.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studentmanagement.R;
import com.studentmanagement.model.StudentInfo;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<StudentInfo> studentInfoList;

    public StudentAdapter(ArrayList<StudentInfo> studentInfoList) {
        this.studentInfoList=studentInfoList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.details_view_format, viewGroup, false);
        return new StudentViewHolder(itemView);
    }

    //Custom Holder for holding Student Data
    public class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView name,id;

        public StudentViewHolder(View view) {
            super(view);
            name=view.findViewById(R.id.tv_name);
            id= view.findViewById(R.id.tv_roll);

        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        StudentInfo mStudent=studentInfoList.get(i);
        StudentViewHolder holder=(StudentViewHolder)viewHolder;


        holder.name.setText(mStudent.getName());
        holder.id.setText(mStudent.getID());
    }

    @Override
    public int getItemCount() {
        return studentInfoList.size();
    }
}
