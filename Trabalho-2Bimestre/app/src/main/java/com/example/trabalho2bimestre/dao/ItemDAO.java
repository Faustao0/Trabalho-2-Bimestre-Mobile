package com.example.trabalho2bimestre.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.trabalho2bimestre.helper.SQLiteDataHelper;
import com.example.trabalho2bimestre.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemDAO implements IGenericDao<Item>{

    private SQLiteDataHelper openHelper;
    private SQLiteDatabase baseDados;
    private String[] colunas = {"CODIGO", "DESCRICAO", "VALOR_UNIT", "UNIDADE_MEDIA"};
    private String tabela = "ITEM";
    private static ItemDAO instancia;

    public static ItemDAO getInstancia(Context context) {
        if (instancia == null) {
            instancia = new ItemDAO(context);
        }
        return instancia;
    }

    public ItemDAO(Context context) {
        openHelper = new SQLiteDataHelper(context);
        baseDados = openHelper.getWritableDatabase();
    }

    public long insert(Item item) {
        try {
            ContentValues valores = new ContentValues();
            valores.put(colunas[1], item.getDescricao());
            valores.put(colunas[2], item.getValorUnit());
            valores.put(colunas[3], item.getUnidadeMedia());
            valores.put("CLIENTE_CODIGO", item.getCodigocliente());

            return baseDados.insert(tabela, null, valores);

        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: ItemDAO.insert(): " + ex.getMessage());
        }
        return 0;
    }

    public long update(Item item) {
        try {
            ContentValues valores = new ContentValues();
            valores.put(colunas[1], item.getDescricao());
            valores.put(colunas[2], item.getValorUnit());
            valores.put(colunas[3], item.getUnidadeMedia());

            String[] identificador = {String.valueOf(item.getCodigo())};

            return baseDados.update(tabela, valores, colunas[0] + " = ?", identificador);

        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: ItemDAO.update(): " + ex.getMessage());
        }
        return 0;
    }

    public long delete(Item item) {
        try {
            String[] identificador = {String.valueOf(item.getCodigo())};

            return baseDados.delete(tabela, colunas[0] + " = ?", identificador);

        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: ItemDAO.delete(): " + ex.getMessage());
        }
        return 0;
    }

    public Item getById(int id) {
        try {
            String[] identificador = {String.valueOf(id)};
            Cursor cursor = baseDados.query(tabela, colunas, colunas[0] + " = ?", identificador, null, null, null);

            if (cursor.moveToFirst()) {
                Item item = new Item();
                item.setCodigo(cursor.getInt(0));
                item.setDescricao(cursor.getString(1));
                item.setValorUnit(cursor.getDouble(2));
                item.setUnidadeMedia(cursor.getInt(3));

                return item;
            }
        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: ItemDAO.getById(): " + ex.getMessage());
        }
        return null;
    }

    public ArrayList<Item> getAll() {
        ArrayList<Item> lista = new ArrayList<>();
        try {
            Cursor cursor = baseDados.query(tabela, colunas, null, null, null, null, colunas[0]);

            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setCodigo(cursor.getInt(0));
                    item.setDescricao(cursor.getString(1));
                    item.setValorUnit(cursor.getDouble(2));
                    item.setUnidadeMedia(cursor.getInt(3));

                    lista.add(item);
                } while (cursor.moveToNext());
            }
            return lista;
        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: ItemDAO.getAll(): " + ex.getMessage());
        }
        return null;
    }

    public ArrayList<Item> retornarItensPorCliente(int codigoCliente) {
        ArrayList<Item> lista = new ArrayList<>();
        String query = "SELECT CODIGO, DESCRICAO, VALOR_UNIT, UNIDADE_MEDIA " +
                "FROM ITEM " +
                "WHERE CLIENTE_CODIGO = ?";

        Log.d("ItemDAO", "Executando query para cliente ID: " + codigoCliente);

        try (Cursor cursor = baseDados.rawQuery(query, new String[]{String.valueOf(codigoCliente)})) {
            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setCodigo(cursor.getInt(0));
                    item.setDescricao(cursor.getString(1));
                    item.setValorUnit(cursor.getDouble(2));
                    item.setUnidadeMedia(cursor.getInt(3));
                    lista.add(item);
                } while (cursor.moveToNext());

                Log.d("ItemDAO", "Itens encontrados para cliente ID " + codigoCliente + ": " + lista.size());
            } else {
                Log.d("ItemDAO", "Nenhum item encontrado para o cliente ID: " + codigoCliente);
            }
        } catch (SQLException ex) {
            Log.e("ItemDAO", "Erro ao buscar itens por cliente: " + ex.getMessage());
        }
        return lista;
    }

    public void removerItensPorCliente(int clienteId) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete("ITEM", "CLIENTE_CODIGO = ?", new String[]{String.valueOf(clienteId)});
        db.close();
    }

    @SuppressLint("Range")
    public List<Item> getItensPorPedidoCodigo(int pedidoCodigo) {
        List<Item> itens = new ArrayList<>();
        SQLiteDatabase db = openHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "ITEM",
                null,
                "CODIGO_PEDIDO = ?",
                new String[]{String.valueOf(pedidoCodigo)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setCodigo(cursor.getInt(cursor.getColumnIndex("CODIGO")));
                item.setDescricao(cursor.getString(cursor.getColumnIndex("DESCRICAO")));
                item.setValorUnit(cursor.getFloat(cursor.getColumnIndex("VALOR_UNIT")));
                item.setUnidadeMedia(Integer.parseInt(cursor.getString(cursor.getColumnIndex("UNIDADE_MEDIA"))));
                item.setCodigoPedido(cursor.getInt(cursor.getColumnIndex("CODIGO_PEDIDO")));
                itens.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return itens;
    }
}