package com.example.trabalho2bimestre.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trabalho2bimestre.R;
import com.example.trabalho2bimestre.adapter.ItemAdapter;
import com.example.trabalho2bimestre.controller.ClienteController;
import com.example.trabalho2bimestre.controller.ItemController;
import com.example.trabalho2bimestre.controller.PedidoVendaController;
import com.example.trabalho2bimestre.dao.ClienteDAO;
import com.example.trabalho2bimestre.model.Cliente;
import com.example.trabalho2bimestre.model.Endereco;
import com.example.trabalho2bimestre.model.Item;
import com.example.trabalho2bimestre.model.PedidoVenda;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PedidoVendaActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPedidoVenda;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private FloatingActionButton btcadastrarItens;
    private ItemController itemController;
    private int clienteId;
    private RadioGroup rgCondicaoPagamento;
    private RadioButton rbAVista, rbAPrazo;
    private EditText etQuantidadeParcelas;
    private TextView tvEnderecoEntrega;
    private Button btnConcluirPedido;
    private PedidoVenda pedidoVenda;
    private Cliente cliente;
    private Endereco endereco;
    private TextView tvValorTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_venda);

        Intent intent = getIntent();
        this.clienteId = intent.getIntExtra("CLIENTE_ID", -1);

        if (clienteId != -1) {
            ClienteController clienteController = new ClienteController(this);
            Cliente cliente = clienteController.retornarClientePorId(clienteId);

            if (cliente != null) {
                this.cliente = cliente;
                this.endereco = cliente.getEndereco();

                String enderecoInfo = (this.endereco != null) ? this.endereco.toString() : "Endereço não disponível";
                String clienteInfo = "Cliente: " + cliente.getNome() + "\n"
                        + "CPF: " + cliente.getCpf() + "\n"
                        + "Data de Nascimento: " + cliente.getDataNasc() + "\n"
                        + "Endereço: " + enderecoInfo;

                Log.d("PedidoVendaActivity", clienteInfo);
            } else {
                Toast.makeText(this, "Cliente não encontrado!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "ID do cliente inválido!", Toast.LENGTH_SHORT).show();
        }

        itemController = new ItemController(this);

        initUI();

        setupListeners();

        atualizarListaItens();

        carregarEndereco();
    }

    private void initUI() {
        recyclerViewPedidoVenda = findViewById(R.id.recyclerViewPedidoVenda);
        recyclerViewPedidoVenda.setLayoutManager(new LinearLayoutManager(this));

        rgCondicaoPagamento = findViewById(R.id.rgCondicaoPagamento);
        rbAVista = findViewById(R.id.rbAVista);
        rbAPrazo = findViewById(R.id.rbAPrazo);
        etQuantidadeParcelas = findViewById(R.id.etQuantidadeParcelas);
        tvEnderecoEntrega = findViewById(R.id.tvEnderecoEntrega);
        btnConcluirPedido = findViewById(R.id.btnConcluirPedido);
        btcadastrarItens = findViewById(R.id.btcadastrarItens);
        tvValorTotal = findViewById(R.id.tvValorTotal);
        tvValorTotal.setVisibility(View.GONE);

        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList);
        recyclerViewPedidoVenda.setAdapter(itemAdapter);
    }

    private void setupListeners() {
        btcadastrarItens.setOnClickListener(v -> mostrarDialogCadastroItem());
        btnConcluirPedido.setOnClickListener(v -> concluirPedido());

        rgCondicaoPagamento.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAPrazo) {
                etQuantidadeParcelas.setVisibility(View.VISIBLE);
            } else {
                etQuantidadeParcelas.setVisibility(View.GONE);
            }
        });
    }

    private void carregarEndereco() {
        try {
            Log.d("carregarEndereco", "Cliente ID recebido: " + clienteId);

            if (clienteId == -1) {
                Toast.makeText(this, "ID do cliente inválido!", Toast.LENGTH_SHORT).show();
                return;
            }


            ClienteController clienteController = new ClienteController(this);
            Cliente cliente = clienteController.retornarClientePorId(clienteId);

            if (cliente == null) {
                Toast.makeText(this, "Cliente não encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }

            Endereco endereco = cliente.getEndereco();
            if (endereco == null) {
                tvEnderecoEntrega.setText("Nenhum endereço vinculado.");
                Toast.makeText(this, "Nenhum endereço vinculado ao cliente.", Toast.LENGTH_SHORT).show();
                return;
            }

            String enderecoInfo = "Enderço de Entrega: "+ endereco.getCidade() + " - " + endereco.getUf();
            tvEnderecoEntrega.setText(enderecoInfo);

            String clienteInfo = "Cliente: " + cliente.getNome() + "\n"
                    + "CPF: " + cliente.getCpf() + "\n"
                    + "Data de Nascimento: " + cliente.getDataNasc() + "\n"
                    + "Endereço: " + enderecoInfo;

            Log.d("PedidoVendaActivity", clienteInfo);

        } catch (Exception e) {
            Log.e("PedidoVendaActivity", "Erro ao carregar endereço.", e);
            Toast.makeText(this, "Erro ao carregar o endereço.", Toast.LENGTH_SHORT).show();
        }
    }

    private void concluirPedido() {
        try {
            if (itemList.isEmpty()) {
                Toast.makeText(this, "Adicione pelo menos um item ao pedido antes de concluir.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rgCondicaoPagamento.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Por favor, selecione uma condição de pagamento (À Vista ou A Prazo).", Toast.LENGTH_SHORT).show();
                return;
            }

            PedidoVendaController controller = new PedidoVendaController(this);

            String condicaoPagamento = rbAVista.isChecked() ? "À VISTA" : "A PRAZO";

            int quantidadeParcelas = 1;
            if (rbAPrazo.isChecked()) {
                String parcelasTexto = etQuantidadeParcelas.getText().toString().trim();

                if (parcelasTexto.isEmpty()) {
                    Toast.makeText(this, "Informe a quantidade de parcelas para pagamento a prazo.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    quantidadeParcelas = Integer.parseInt(parcelasTexto);

                    if (quantidadeParcelas < 1) {
                        Toast.makeText(this, "A quantidade de parcelas deve ser maior que zero.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Quantidade de parcelas inválida.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            double valorFrete = controller.calcularFrete(cliente.getEndereco().getCidade(), cliente.getEndereco().getUf());

            Cliente cliente = obterCliente(clienteId);
            Endereco endereco = obterEnderecoSelecionado();

            if (cliente == null || endereco == null) {
                Toast.makeText(this, "Cliente ou endereço inválido.", Toast.LENGTH_SHORT).show();
                return;
            }

            long pedidoId = controller.salvarPedidoVenda(
                    valorFrete, condicaoPagamento, quantidadeParcelas, cliente, endereco, itemList
            );

            if (pedidoId > 0) {
                Toast.makeText(this, "Pedido concluído com sucesso! Número do pedido: " + pedidoId, Toast.LENGTH_LONG).show();
                limparCampos();
            } else {
                Toast.makeText(this, "Erro ao salvar o pedido.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao concluir pedido: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Cliente obterCliente(int clienteId) {
        return this.cliente;
    }

    private Endereco obterEnderecoSelecionado() {
        return this.endereco;
    }


    private void mostrarDialogCadastroItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_dialog_cadastro_item, null);
        builder.setView(dialogView);

        EditText etDescricao = dialogView.findViewById(R.id.et_descricao);
        EditText etValorUnit = dialogView.findViewById(R.id.et_valor_unit);
        EditText etUnidadeMedia = dialogView.findViewById(R.id.et_unidade_media);
        Button btnCriarItem = dialogView.findViewById(R.id.btn_criar_item);

        AlertDialog dialog = builder.create();

        btnCriarItem.setOnClickListener(v -> {
            String descricao = etDescricao.getText().toString().trim();
            String valorUnitStr = etValorUnit.getText().toString().trim();
            String unidadeMediaStr = etUnidadeMedia.getText().toString().trim();

            if (descricao.isEmpty()) {
                etDescricao.setError("Preencha o campo descrição.");
                etDescricao.requestFocus();
                return;
            }

            if (valorUnitStr.isEmpty()) {
                etValorUnit.setError("Preencha o campo valor unitário.");
                etValorUnit.requestFocus();
                return;
            }

            if (unidadeMediaStr.isEmpty()) {
                etUnidadeMedia.setError("Preencha o campo unidade média.");
                etUnidadeMedia.requestFocus();
                return;
            }

            double valorUnit;
            int unidadeMedia;

            try {
                valorUnit = Double.parseDouble(valorUnitStr);

                if (valorUnit <= 0) {
                    etValorUnit.setError("O valor unitário deve ser maior que zero.");
                    etValorUnit.requestFocus();
                    return;
                }

            } catch (NumberFormatException e) {
                etValorUnit.setError("Valor unitário deve ser um número válido.");
                etValorUnit.requestFocus();
                return;
            }

            try {
                unidadeMedia = Integer.parseInt(unidadeMediaStr);

                if (unidadeMedia <= 0) {
                    etUnidadeMedia.setError("A quantidade deve ser maior que zero.");
                    etUnidadeMedia.requestFocus();
                    return;
                }

            } catch (NumberFormatException e) {
                etUnidadeMedia.setError("Quantidade deve ser um número válido.");
                etUnidadeMedia.requestFocus();
                return;
            }

            String resultado = itemController.salvarItem(descricao, String.valueOf(valorUnit), unidadeMedia, clienteId);
            Toast.makeText(PedidoVendaActivity.this, resultado, Toast.LENGTH_SHORT).show();

            if (resultado.equals("Item gravado com sucesso.")) {
                dialog.dismiss();
                atualizarListaItens();
            }
        });

        dialog.show();
    }

    private void atualizarListaItens() {
        List<Item> itensCliente = itemController.retornarItensPorCliente(clienteId);

        itemList.clear();
        if (itensCliente != null) {
            itemList.addAll(itensCliente);
        }

        itemAdapter.setItemList(itemList);

        if (itemList.isEmpty()) {
            tvValorTotal.setVisibility(View.GONE);
        } else {
            tvValorTotal.setVisibility(View.VISIBLE);

            PedidoVendaController pedidoVendaController = new PedidoVendaController(this);
            double valorTotal = pedidoVendaController.calcularValorTotalItens(itemList);

            tvValorTotal.setText(String.format("Valor Total do Pedido: R$ %.2f", valorTotal));
        }
    }

    private void limparCampos() {
        itemList.clear();
        itemAdapter.setItemList(new ArrayList<>());

        rgCondicaoPagamento.clearCheck();
        etQuantidadeParcelas.setText("");
        tvValorTotal.setVisibility(View.GONE);

        itemController.limparItensCliente(clienteId);
    }
}