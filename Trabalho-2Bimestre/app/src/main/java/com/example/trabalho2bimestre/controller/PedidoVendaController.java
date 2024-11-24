package com.example.trabalho2bimestre.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.trabalho2bimestre.dao.ClienteDAO;
import com.example.trabalho2bimestre.dao.ItemDAO;
import com.example.trabalho2bimestre.dao.PedidoVendaDAO;
import com.example.trabalho2bimestre.helper.SQLiteDataHelper;
import com.example.trabalho2bimestre.model.Cliente;
import com.example.trabalho2bimestre.model.Endereco;
import com.example.trabalho2bimestre.model.Item;
import com.example.trabalho2bimestre.model.PedidoVenda;

import java.util.ArrayList;
import java.util.List;

public class PedidoVendaController {

    private final Context context;
    private SQLiteDataHelper openHelper;
    private PedidoVendaDAO pedidoVendaDAO;
    private ClienteDAO clienteDAO;
    private ItemDAO itemDAO;

    public PedidoVendaController(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Contexto não pode ser nulo no PedidoVendaController!");
        }
        this.context = context.getApplicationContext();
        this.pedidoVendaDAO = PedidoVendaDAO.getInstancia(context);
        openHelper = SQLiteDataHelper.getInstance(context);
        pedidoVendaDAO = new PedidoVendaDAO(context);
        clienteDAO = new ClienteDAO(context);
        itemDAO = new ItemDAO(context);
    }

    public long salvarPedidoVenda(double valorFrete, String condicaoPagamento, int quantidadeParcelas,
                                  Cliente cliente, Endereco endereco, List<Item> itens) {
        long pedidoId = -1;
        SQLiteDatabase db = null;

        try {
            db = openHelper.getWritableDatabase();
            db.beginTransaction();

            double valorTotalItens = calcularValorTotalItens(itens);

            double valorFinal = calcularValorFinal(valorTotalItens, valorFrete, condicaoPagamento);

            PedidoVenda pedidoVenda = new PedidoVenda();
            pedidoVenda.setValorTotal(valorFinal);
            pedidoVenda.setCondicaoPagamento(condicaoPagamento);
            pedidoVenda.setQuantidadeParcelas(quantidadeParcelas);
            pedidoVenda.setCliente(cliente);
            pedidoVenda.setEndereco(endereco);
            pedidoVenda.setItens(itens);

            ContentValues pedidoValues = new ContentValues();
            pedidoValues.put("VALOR_TOTAL", pedidoVenda.getValorTotal());
            pedidoValues.put("CONDICAO_PAGAMENTO", pedidoVenda.getCondicaoPagamento());
            pedidoValues.put("QUANTIDADE_PARCELAS", pedidoVenda.getQuantidadeParcelas());
            pedidoValues.put("VALOR_FRETE", valorFrete);
            pedidoValues.put("CODIGO_CLIENTE", cliente.getCodigo());
            pedidoValues.put("CODIGO_ENDERECO", endereco != null ? endereco.getCodigo() : null);

            pedidoId = db.insert("PEDIDO_VENDA", null, pedidoValues);
            if (pedidoId == -1) {
                throw new Exception("Erro ao inserir o pedido na tabela PEDIDO_VENDA.");
            }

            for (Item item : itens) {
                ContentValues itemValues = new ContentValues();
                itemValues.put("CODIGO_PEDIDO", pedidoId); // Relaciona ao pedido criado
                itemValues.put("CODIGO_ITEM", item.getCodigo()); // Código do item
                itemValues.put("QUANTIDADE", item.getUnidadeMedia()); // Quantidade do item no pedido

                long itemInsertId = db.insert("PEDIDO_ITEM", null, itemValues);
                if (itemInsertId == -1) {
                    throw new Exception("Erro ao inserir um item do pedido na tabela PEDIDO_ITEM.");
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("PedidoVendaController", "Erro ao salvar pedido e itens", e);
            pedidoId = -1;
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }

        return pedidoId;
    }

    public ArrayList<PedidoVenda> retornarTodosPedidos() {
        return PedidoVendaDAO.getInstancia(context).getAll();
    }

    public PedidoVenda buscarPedidoPorCodigo(int codigo) {
        PedidoVenda pedidoVenda = pedidoVendaDAO.getById(codigo);
        if (pedidoVenda != null) {
            List<Item> itens = itemDAO.getItensPorPedidoCodigo(codigo);
            double valorTotalItens = calcularValorTotalItens(itens);
            double valorFrete = calcularFrete(pedidoVenda.getEndereco().getCidade(), pedidoVenda.getEndereco().getUf());
            double valorFinal = calcularValorFinal(valorTotalItens, valorFrete, pedidoVenda.getCondicaoPagamento());

            pedidoVenda.setItens(itens);
            pedidoVenda.setValorTotal(valorFinal);

            Log.d("DEBUG", "Pedido recuperado com Valor Total: " + valorFinal);
        }

        return pedidoVenda;
    }

    public double calcularValorTotalItens(List<Item> itens) {
        double total = 0;
        for (Item item : itens) {
            total += item.getValorUnit() * item.getUnidadeMedia();
        }
        return total;
    }

    public double calcularFrete(String cidade, String estado) {
        if (!cidade.equals("Toledo-PR")) {
            return estado.equals("Paraná") ? 20.0 : 50.0;
        }
        return 0;
    }

    public double calcularValorFinal(double valorTotalItens, double valorFrete, String condicaoPagamento) {
        double valorTotal = valorTotalItens + valorFrete;
        return condicaoPagamento.equals("À VISTA") ? valorTotal * 0.95 : valorTotal * 1.05;
    }
}
