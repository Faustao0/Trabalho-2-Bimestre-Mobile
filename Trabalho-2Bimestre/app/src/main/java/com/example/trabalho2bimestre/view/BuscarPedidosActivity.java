package com.example.trabalho2bimestre.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalho2bimestre.R;
import com.example.trabalho2bimestre.controller.PedidoVendaController;
import com.example.trabalho2bimestre.model.Item;
import com.example.trabalho2bimestre.model.PedidoVenda;

import java.util.List;

public class BuscarPedidosActivity extends AppCompatActivity {

    private EditText editTextCodigoPedido;
    private Button buttonBuscarPedido;
    private TextView textViewTituloInfoPedido;
    private TextView textViewCodigoPedido;
    private TextView textViewClientePedido;
    private TextView textViewCpfCliente;
    private TextView textViewValorTotal, textTipoVenda, textViewLocalEntrega;
    private PedidoVendaController pedidoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar_pedidos);

        editTextCodigoPedido = findViewById(R.id.editTextCodigoPedido);
        buttonBuscarPedido = findViewById(R.id.buttonBuscarPedido);
        textViewTituloInfoPedido = findViewById(R.id.textViewTituloInfoPedido);
        textViewCodigoPedido = findViewById(R.id.textViewCodigoPedido);
        textViewClientePedido = findViewById(R.id.textViewClientePedido);
        textViewCpfCliente = findViewById(R.id.textViewCpfCliente);
        textViewValorTotal = findViewById(R.id.textViewValorTotal);
        textViewLocalEntrega = findViewById(R.id.textViewLocalEntrega);
        textTipoVenda = findViewById(R.id.textTipoVenda);

        pedidoController = new PedidoVendaController(this);

        buttonBuscarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = editTextCodigoPedido.getText().toString().trim();
                if (codigo.isEmpty()) {
                    Toast.makeText(BuscarPedidosActivity.this, "Digite o código do pedido", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int codigoPedido = Integer.parseInt(codigo);
                    PedidoVenda pedido = pedidoController.buscarPedidoPorCodigo(codigoPedido);

                    Log.d("BuscarPedidosActivity", "Pedido buscado: " + (pedido != null ? pedido.toString() : "null"));

                    if (pedido != null) {
                        exibirInformacoesPedido(pedido);
                    } else {
                        esconderInformacoesPedido();
                        Toast.makeText(BuscarPedidosActivity.this, "Pedido não encontrado", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(BuscarPedidosActivity.this, "Código inválido", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(BuscarPedidosActivity.this, "Erro ao buscar pedido: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void exibirInformacoesPedido(PedidoVenda pedido) {
        textViewTituloInfoPedido.setVisibility(View.VISIBLE);
        textViewCodigoPedido.setVisibility(View.VISIBLE);
        textViewClientePedido.setVisibility(View.VISIBLE);
        textViewCpfCliente.setVisibility(View.VISIBLE);
        textViewValorTotal.setVisibility(View.VISIBLE);
        textViewLocalEntrega.setVisibility(View.VISIBLE);
        textTipoVenda.setVisibility(View.VISIBLE);

        textViewCodigoPedido.setText("Código do Pedido: " + pedido.getCodigo());
        textViewClientePedido.setText("Cliente: " + pedido.getCliente().getNome());
        textViewCpfCliente.setText("CPF do Cliente: " + pedido.getCliente().getCpf());

        textViewValorTotal.setText("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));

        textViewLocalEntrega.setText("Endereço da entrega: " + pedido.getEndereco().getCidade() + " - " + pedido.getEndereco().getUf());
        textTipoVenda.setText("Condição de Pagamento: " + pedido.getCondicaoPagamento());

        List<Item> itens = pedido.getItens();
        if (itens != null && !itens.isEmpty()) {
            for (Item item : itens) {
                Log.d("BuscarPedidosActivity", "Item do Pedido: " + item.getDescricao() +
                        ", Quantidade: " + item.getUnidadeMedia() +
                        ", Valor Unitário: R$ " + item.getValorUnit());
            }
        } else {
            Log.d("BuscarPedidosActivity", "Nenhum item encontrado para este pedido.");
        }
    }

    private void esconderInformacoesPedido() {
        textViewTituloInfoPedido.setVisibility(View.GONE);
        textViewCodigoPedido.setVisibility(View.GONE);
        textViewClientePedido.setVisibility(View.GONE);
        textViewCpfCliente.setVisibility(View.GONE);
        textViewValorTotal.setVisibility(View.GONE);
        textViewLocalEntrega.setVisibility(View.GONE);
        textTipoVenda.setVisibility(View.GONE);
    }
}