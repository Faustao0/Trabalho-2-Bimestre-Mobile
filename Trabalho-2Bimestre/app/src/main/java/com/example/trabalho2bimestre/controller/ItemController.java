package com.example.trabalho2bimestre.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.trabalho2bimestre.dao.ItemDAO;
import com.example.trabalho2bimestre.helper.SQLiteDataHelper;
import com.example.trabalho2bimestre.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemController {

    private Context context;
    private SQLiteDataHelper openHelper;
    private SQLiteDatabase baseDados;

    public ItemController(Context context) {
        this.context = context;
        openHelper = new SQLiteDataHelper(context);
        baseDados = openHelper.getWritableDatabase();
    }

    public String salvarItem(String descricao, String valorUnit, int unidadeMedia, int codigocliente) {
        try {
            if (TextUtils.isEmpty(descricao)) {
                return "Informe a descrição do item.";
            }
            if (TextUtils.isEmpty(valorUnit)) {
                return "Informe o valor unitário.";
            }

            Item item = new Item();
            item.setDescricao(descricao);
            item.setValorUnit(Double.parseDouble(valorUnit));
            item.setUnidadeMedia(unidadeMedia);
            item.setCodigocliente(codigocliente);

            long id = ItemDAO.getInstancia(context).insert(item);
            if (id > 0) {
                return "Item gravado com sucesso.";
            } else {
                return "Erro ao gravar item no BD.";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Erro ao salvar dados do item.";
        }
    }
    public ArrayList<Item> retornarTodosItens() {
        return ItemDAO.getInstancia(context).getAll();
    }

    public List<Item> retornarItensPorCliente(int codigoCliente) {
        List<Item> itens = new ArrayList<>();
        Cursor cursor = baseDados.query("Item",
                new String[]{"CODIGO", "DESCRICAO", "VALOR_UNIT", "UNIDADE_MEDIA", "CLIENTE_CODIGO"},
                "CLIENTE_CODIGO = ?",
                new String[]{String.valueOf(codigoCliente)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setCodigo(cursor.getInt(cursor.getColumnIndexOrThrow("CODIGO")));
                item.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow("DESCRICAO")));
                item.setValorUnit(cursor.getDouble(cursor.getColumnIndexOrThrow("VALOR_UNIT")));
                item.setUnidadeMedia(cursor.getInt(cursor.getColumnIndexOrThrow("UNIDADE_MEDIA")));
                item.setCodigocliente(cursor.getInt(cursor.getColumnIndexOrThrow("CLIENTE_CODIGO")));
                itens.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itens;
    }

    public void limparItensCliente(int clienteId) {
        ItemDAO itemDAO = new ItemDAO(context);
        itemDAO.removerItensPorCliente(clienteId);
    }
}
