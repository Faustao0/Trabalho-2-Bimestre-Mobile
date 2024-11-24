package com.example.trabalho2bimestre.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.trabalho2bimestre.helper.SQLiteDataHelper;
import com.example.trabalho2bimestre.model.Cliente;
import com.example.trabalho2bimestre.model.Endereco;

import java.util.ArrayList;

public class ClienteDAO implements IGenericDao<Cliente> {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase baseDados;
    private String[] colunas = {"CODIGO", "NOME", "CPF", "DATA_NASC", "CODIGO_ENDERECO"};
    private String tabela = "CLIENTE";
    private static ClienteDAO instancia;
    private Context context;

    public static ClienteDAO getInstancia(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("O contexto não pode ser nulo!");
        }
        if (instancia == null) {
            instancia = new ClienteDAO(context.getApplicationContext());
        }
        return instancia;
    }

    public ClienteDAO(Context context) {
        this.context = context;
        this.openHelper = SQLiteDataHelper.getInstance(context);
        this.baseDados = openHelper.getReadableDatabase();
    }

    @Override
    public long insert(Cliente cliente) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            ContentValues valores = new ContentValues();
            valores.put("NOME", cliente.getNome());
            valores.put("CPF", cliente.getCpf());
            valores.put("DATA_NASC", cliente.getDataNasc());

            return db.insert(tabela, null, valores);
        } catch (SQLException ex) {
            Log.e("ClienteDAO", "Erro ao inserir cliente: " + ex.getMessage());
        } finally {
            db.close();
        }
        return -1;
    }

    @Override
    public long update(Cliente cliente) {
        try {
            SQLiteDatabase db = openHelper.getWritableDatabase(); // Get writable database
            ContentValues valores = new ContentValues();
            valores.put(colunas[1], cliente.getNome());
            valores.put(colunas[2], cliente.getCpf());
            valores.put(colunas[3], cliente.getDataNasc());
            String[] identificador = {String.valueOf(cliente.getCodigo())};
            return db.update(tabela, valores, colunas[0] + " = ?", identificador);
        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: ClienteDao.update(): " + ex.getMessage());
        }
        return 0;
    }

    @Override
    public long delete(Cliente cliente) {
        try {
            SQLiteDatabase db = openHelper.getWritableDatabase(); // Get writable database
            String[] identificador = {String.valueOf(cliente.getCodigo())};
            return db.delete(tabela, colunas[0] + " = ?", identificador);
        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: ClienteDao.delete(): " + ex.getMessage());
        }
        return 0;
    }

    @Override
    public Cliente getById(int id) {
        Cliente cliente = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = openHelper.getReadableDatabase();
            String[] identificador = {String.valueOf(id)};
            cursor = db.query(tabela,
                    new String[]{"CODIGO", "NOME", "CPF", "DATA_NASC", "CODIGO_ENDERECO"},
                    "CODIGO = ?",
                    identificador,
                    null, null, null);

            if (cursor.moveToFirst()) {
                cliente = new Cliente();
                cliente.setCodigo(cursor.getInt(0));
                cliente.setNome(cursor.getString(1));
                cliente.setCpf(cursor.getString(2));
                cliente.setDataNasc(cursor.getString(3));

                // Buscar o endereço vinculado ao cliente
                int codigoEndereco = cursor.getInt(4);
                if (codigoEndereco > 0) {
                    Endereco endereco = EnderecoDAO.getInstancia(this.context).getById(codigoEndereco);
                    cliente.setEndereco(endereco);
                }
            }
        } catch (SQLException ex) {
            Log.e("ClienteDAO", "Erro ao buscar cliente: " + ex.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return cliente;
    }

    @Override
    public ArrayList<Cliente> getAll() {
        ArrayList<Cliente> lista = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = openHelper.getReadableDatabase(); // Get readable database
            cursor = db.query(tabela, colunas, null, null, null, null, colunas[0]);
            if (cursor.moveToFirst()) {
                do {
                    Cliente cliente = new Cliente();
                    cliente.setCodigo(cursor.getInt(0));
                    cliente.setNome(cursor.getString(1));
                    cliente.setCpf(cursor.getString(2));
                    cliente.setDataNasc(cursor.getString(3));
                    lista.add(cliente);
                } while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: ClienteDao.getAll(): " + ex.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return lista;
    }

    @SuppressLint("Range")
    public Cliente getByCpf(String cpf) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cliente cliente = null;

        Cursor cursor = db.query("Cliente", null, "cpf = ?", new String[]{cpf}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cliente = new Cliente();
            cliente.setCodigo(cursor.getInt(cursor.getColumnIndex("codigo")));
            cliente.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            cliente.setCpf(cursor.getString(cursor.getColumnIndex("cpf")));

            cursor.close();
        }
        db.close();
        return cliente;
    }

    public void close() {
        if (baseDados != null && baseDados.isOpen()) {
            baseDados.close();
        }
    }
}
