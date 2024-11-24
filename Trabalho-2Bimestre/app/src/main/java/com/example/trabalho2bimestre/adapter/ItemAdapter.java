package com.example.trabalho2bimestre.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trabalho2bimestre.R;
import com.example.trabalho2bimestre.model.Item;
import com.example.trabalho2bimestre.model.PedidoVenda;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> itemList;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList != null ? itemList : new ArrayList<>();
    }

    public void setItemList(List<Item> newItemList) {
        this.itemList = newItemList != null ? newItemList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addItem(Item newItem) {
        itemList.add(newItem);
        notifyItemInserted(itemList.size() - 1);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < itemList.size()) {
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());
        }
    }

    public void updateItem(int position, Item updatedItem) {
        if (position >= 0 && position < itemList.size()) {
            itemList.set(position, updatedItem);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        double valorTotal = item.getValorUnit() * item.getUnidadeMedia();
        holder.tvDescricao.setText("Descrição do Item: " + item.getDescricao());
        holder.tvUnidadeMedia.setText("Unidade de Média do Item: " + item.getUnidadeMedia());
        holder.tvValorUnit.setText(String.format("Valor do Item: R$ %.2f", item.getValorUnit()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescricao, tvUnidadeMedia, tvValorUnit;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescricao = itemView.findViewById(R.id.tvDescricao);
            tvUnidadeMedia = itemView.findViewById(R.id.tvUnidadeMedia);
            tvValorUnit = itemView.findViewById(R.id.tvValorUnit);
        }
    }
}
