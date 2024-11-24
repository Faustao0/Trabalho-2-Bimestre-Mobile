package com.example.trabalho2bimestre.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.trabalho2bimestre.helper.SQLiteDataHelper;
import com.example.trabalho2bimestre.model.Endereco;

import java.util.ArrayList;

public class EnderecoDAO implements IGenericDao<Endereco> {

    private SQLiteDataHelper openHelper;
    private SQLiteDatabase baseDados;
    private String[] colunas = {"CODIGO", "RUA", "NUMERO", "BAIRRO", "CIDADE", "ESTADO"};
    private String tabela = "ENDERECO";
    private static EnderecoDAO instancia;

    public static EnderecoDAO getInstancia(Context context) {
        if (instancia == null) {
            if (context == null) {
                throw new IllegalArgumentException("Context não pode ser nulo ao criar EnderecoDAO");
            }
            instancia = new EnderecoDAO(context.getApplicationContext());
        }
        return instancia;
    }

    private EnderecoDAO(Context context) {
        openHelper = new SQLiteDataHelper(context);
        baseDados = openHelper.getWritableDatabase();
    }

    public long insert(Endereco endereco) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            ContentValues valores = new ContentValues();
            valores.put("RUA", endereco.getLogradouro());
            valores.put("NUMERO", endereco.getNumero());
            valores.put("BAIRRO", endereco.getBairro());
            valores.put("CIDADE", endereco.getCidade());
            valores.put("UF", endereco.getUf());
            valores.put("CLIENTE_CODIGO", endereco.getClienteCodigo());

            return db.insert(tabela, null, valores);
        } catch (SQLException ex) {
            Log.e("EnderecoDAO", "Erro ao inserir endereço: " + ex.getMessage());
        } finally {
            db.close();
        }
        return -1;
    }

    public long update(Endereco endereco) {
        try {
            ContentValues valores = new ContentValues();
            valores.put(colunas[1], endereco.getLogradouro());
            valores.put(colunas[2], endereco.getNumero());
            valores.put(colunas[3], endereco.getBairro());
            valores.put(colunas[4], endereco.getCidade());
            valores.put(colunas[5], endereco.getUf());

            String[] identificador = {String.valueOf(endereco.getCodigo())};

            return baseDados.update(tabela, valores, colunas[0] + " = ?", identificador);

        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: EnderecoDAO.update(): " + ex.getMessage());
        }
        return 0;
    }

    public long delete(Endereco endereco) {
        try {
            String[] identificador = {String.valueOf(endereco.getCodigo())};

            return baseDados.delete(tabela, colunas[0] + " = ?", identificador);

        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: EnderecoDAO.delete(): " + ex.getMessage());
        }
        return 0;
    }

    public Endereco getById(int id) {
        try {
            String[] identificador = {String.valueOf(id)};
            Cursor cursor = baseDados.query(tabela,
                    new String[]{"CODIGO", "RUA", "NUMERO", "BAIRRO", "CIDADE", "UF"},
                    "CODIGO = ?",
                    identificador,
                    null, null, null);

            if (cursor.moveToFirst()) {
                Endereco endereco = new Endereco();
                endereco.setCodigo(cursor.getInt(0));
                endereco.setLogradouro(cursor.getString(1));
                endereco.setNumero(cursor.getString(2));
                endereco.setBairro(cursor.getString(3));
                endereco.setCidade(cursor.getString(4));
                endereco.setUf(cursor.getString(5));

                cursor.close();
                return endereco;
            }
            cursor.close();
        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: EnderecoDAO.getById(): " + ex.getMessage());
        }
        return null;
    }

    public ArrayList<Endereco> getAll() {
        ArrayList<Endereco> lista = new ArrayList<>();
        try {
            Cursor cursor = baseDados.query(tabela, colunas, null, null, null, null, colunas[0]);

            if (cursor.moveToFirst()) {
                do {
                    Endereco endereco = new Endereco();
                    endereco.setCodigo(cursor.getInt(0));
                    endereco.setLogradouro(cursor.getString(1));
                    endereco.setNumero(cursor.getString(2));
                    endereco.setBairro(cursor.getString(3));
                    endereco.setCidade(cursor.getString(4));
                    endereco.setUf(cursor.getString(5));

                    lista.add(endereco);
                } while (cursor.moveToNext());
            }
            return lista;
        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: EnderecoDAO.getAll(): " + ex.getMessage());
        }
        return null;
    }

    @SuppressLint("Range")
    public Endereco getByClienteCodigo(int clienteCodigo) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "ENDERECO",
                null,
                "CLIENTE_CODIGO = ?", // Nome correto da coluna
                new String[]{String.valueOf(clienteCodigo)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            Endereco endereco = new Endereco();
            endereco.setCodigo(cursor.getInt(cursor.getColumnIndex("CODIGO")));
            endereco.setLogradouro(cursor.getString(cursor.getColumnIndex("RUA")));
            endereco.setNumero(cursor.getString(cursor.getColumnIndex("NUMERO")));
            endereco.setBairro(cursor.getString(cursor.getColumnIndex("BAIRRO")));
            endereco.setCidade(cursor.getString(cursor.getColumnIndex("CIDADE")));
            endereco.setUf(cursor.getString(cursor.getColumnIndex("UF")));
            endereco.setClienteCodigo(cursor.getInt(cursor.getColumnIndex("CLIENTE_CODIGO")));
            cursor.close();
            return endereco;
        }

        return null;
    }

    public void close() {
        if (openHelper != null) {
            openHelper.close();
        }
    }
}
