package com.example.trabalho2bimestre.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalho2bimestre.R;
import com.example.trabalho2bimestre.controller.ClienteController;
import com.example.trabalho2bimestre.model.Cliente;

import java.util.ArrayList;

public class SelecionarClienteActivity extends AppCompatActivity {

    private Spinner spinnerCliente;
    private Button btnConfirmarCliente;
    private ClienteController clienteController;
    private Cliente clienteSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecionar_cliente);

        spinnerCliente = findViewById(R.id.spinner_cliente);
        btnConfirmarCliente = findViewById(R.id.btn_confirmar_cliente);

        clienteController = new ClienteController(this);

        carregarClientes();

        btnConfirmarCliente.setOnClickListener(v -> {
            if (clienteSelecionado != null) {
                int clienteId = clienteSelecionado.getCodigo();
                Intent intent = new Intent(SelecionarClienteActivity.this, PedidoVendaActivity.class);
                intent.putExtra("CLIENTE_ID", clienteId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Nenhum cliente selecionado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarClientes() {
        ArrayList<Cliente> listaClientes = clienteController.retornarTodosClientes();

        if (listaClientes != null && !listaClientes.isEmpty()) {
            ArrayList<String> nomesClientes = new ArrayList<>();
            for (Cliente cliente : listaClientes) {
                nomesClientes.add(cliente.getNome());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomesClientes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCliente.setAdapter(adapter);

            spinnerCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    clienteSelecionado = listaClientes.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    clienteSelecionado = null;
                }
            });
        } else {
            Toast.makeText(this, "Nenhum cliente encontrado", Toast.LENGTH_SHORT).show();
        }
    }
}