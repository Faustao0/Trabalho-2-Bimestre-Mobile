package com.example.trabalho2bimestre.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.trabalho2bimestre.helper.SQLiteDataHelper;
import com.example.trabalho2bimestre.model.Cliente;
import com.example.trabalho2bimestre.model.Endereco;
import com.example.trabalho2bimestre.model.PedidoVenda;

import java.util.ArrayList;

public class PedidoVendaDAO implements IGenericDao<PedidoVenda> {

    private SQLiteDataHelper openHelper;
    private SQLiteDatabase baseDados;
    private static PedidoVendaDAO instancia;
    private Context context;
    private ClienteDAO clienteDAO;
    private EnderecoDAO enderecoDAO;

    private String[] colunas = {
            "CODIGO", "VALOR_TOTAL", "CONDICAO_PAGAMENTO",
            "QUANTIDADE_PARCELAS", "VALOR_FRETE", "CODIGO_CLIENTE", "CODIGO_ENDERECO"
    };
    private String tabela = "PEDIDO_VENDA";

    public static PedidoVendaDAO getInstancia(Context context) {
        if (instancia == null) {
            if (context == null) {
                throw new IllegalArgumentException("Contexto não pode ser nulo ao criar PedidoVendaDAO");
            }
            instancia = new PedidoVendaDAO(context.getApplicationContext());
        }
        return instancia;
    }

    public PedidoVendaDAO(Context context) {
        this.context = context; // Sempre o ApplicationContext
        this.openHelper = new SQLiteDataHelper(context);
        this.baseDados = openHelper.getWritableDatabase();

        // Inicialize os DAOs auxiliares aqui
        this.clienteDAO = ClienteDAO.getInstancia(context);
        this.enderecoDAO = EnderecoDAO.getInstancia(context);
    }

    public long insert(PedidoVenda pedido) {
        try {
            ContentValues valores = new ContentValues();
            valores.put(colunas[1], pedido.getValorTotal());
            valores.put(colunas[2], pedido.getCondicaoPagamento());
            valores.put(colunas[3], pedido.getQuantidadeParcelas());
            valores.put(colunas[4], pedido.getValorFrete());
            valores.put(colunas[5], pedido.getCliente().getCodigo());
            valores.put(colunas[6], pedido.getEndereco().getCodigo());

            return baseDados.insert(tabela, null, valores);
        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: PedidoVendaDAO.insert(): " + ex.getMessage());
        }
        return 0;
    }

    public long update(PedidoVenda pedido) {
        try {
            ContentValues valores = new ContentValues();
            valores.put(colunas[1], pedido.getValorTotal());
            valores.put(colunas[2], pedido.getCondicaoPagamento());
            valores.put(colunas[3], pedido.getQuantidadeParcelas());
            valores.put(colunas[4], pedido.getValorFrete());
            valores.put(colunas[5], pedido.getCliente().getCodigo());
            valores.put(colunas[6], pedido.getEndereco().getCodigo());

            String[] identificador = {String.valueOf(pedido.getCodigo())};

            return baseDados.update(tabela, valores, colunas[0] + " = ?", identificador);

        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: PedidoVendaDAO.update(): " + ex.getMessage());
        }
        return 0;
    }

    public long delete(PedidoVenda pedido) {
        try {
            String[] identificador = {String.valueOf(pedido.getCodigo())};

            return baseDados.delete(tabela, colunas[0] + " = ?", identificador);

        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: PedidoVendaDAO.delete(): " + ex.getMessage());
        }
        return 0;
    }

    public PedidoVenda getById(int id) {
        PedidoVenda pedido = null;
        Cursor cursor = null;

        try {
            String[] identificador = {String.valueOf(id)};
            cursor = baseDados.query(tabela, colunas, colunas[0] + " = ?", identificador, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                pedido = new PedidoVenda();
                pedido.setCodigo(cursor.getInt(0));
                pedido.setValorTotal(cursor.getDouble(1));
                pedido.setCondicaoPagamento(cursor.getString(2));
                pedido.setQuantidadeParcelas(cursor.getInt(3));
                pedido.setValorFrete(cursor.getDouble(4));

                // Use os DAOs auxiliares já inicializados
                int codigoCliente = cursor.getInt(5);
                int codigoEndereco = cursor.getInt(6);

                Cliente cliente = clienteDAO.getById(codigoCliente);
                if (cliente == null) {
                    Log.e("PedidoVendaDAO", "Cliente não encontrado com o código: " + codigoCliente);
                }
                pedido.setCliente(cliente);

                Endereco endereco = enderecoDAO.getById(codigoEndereco);
                if (endereco == null) {
                    Log.e("PedidoVendaDAO", "Endereço não encontrado com o código: " + codigoEndereco);
                }
                pedido.setEndereco(endereco);
            } else {
                Log.e("PedidoVendaDAO", "Nenhum pedido encontrado com o código: " + id);
            }
        } catch (SQLException ex) {
            Log.e("PedidoVendaDAO", "Erro ao buscar pedido por ID: " + id, ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return pedido;
    }

    public ArrayList<PedidoVenda> getAll() {
        ArrayList<PedidoVenda> lista = new ArrayList<>();
        try {
            Cursor cursor = baseDados.query(tabela, colunas, null, null, null, null, colunas[0]);

            if (cursor.moveToFirst()) {
                do {
                    PedidoVenda pedido = new PedidoVenda();
                    pedido.setCodigo(cursor.getInt(0));
                    pedido.setValorTotal(cursor.getDouble(1));
                    pedido.setCondicaoPagamento(cursor.getString(2));
                    pedido.setQuantidadeParcelas(cursor.getInt(3));
                    pedido.setValorFrete(cursor.getDouble(4));

                    // Obtenha Cliente e Endereco
                    int codigoCliente = cursor.getInt(5);
                    int codigoEndereco = cursor.getInt(6);

                    Cliente cliente = clienteDAO.getById(codigoCliente);
                    Endereco endereco = enderecoDAO.getById(codigoEndereco);

                    pedido.setCliente(cliente);
                    pedido.setEndereco(endereco);

                    lista.add(pedido);
                } while (cursor.moveToNext());
            }
            return lista;
        } catch (SQLException ex) {
            Log.e("VENDA", "ERRO: PedidoVendaDAO.getAll(): " + ex.getMessage());
        }
        return null;
    }

    public long insertPedidoItem(long pedidoId, long itemId, int quantidade) {
        ContentValues values = new ContentValues();
        values.put("CODIGO_PEDIDO", pedidoId);
        values.put("CODIGO_ITEM", itemId);
        values.put("QUANTIDADE", quantidade);

        return baseDados.insert("PEDIDO_ITEM", null, values);
    }
}