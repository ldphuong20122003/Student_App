package com.example.phuongldph29233.student_app.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.List;

public abstract class AdapterHelper<T, VB extends ViewBinding> extends RecyclerView.Adapter<AdapterHelper<T, VB>.BaseViewHolder> {
    protected List<T> items;
    protected Context context;
    protected OnItemActionListener<T> actionListener;

    public interface OnItemActionListener<T> {
        void onItemClick(T item);
        void onItemAction(T item, int actionType);
    }

    public AdapterHelper(List<T> items, OnItemActionListener<T> listener) {
        this.items = items;
        this.actionListener = listener;
    }

    public AdapterHelper(List<T> items) {
        this.items = items;
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        protected VB binding;

        public BaseViewHolder(VB binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public abstract void bind(T item);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateDataSet(List<T> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}