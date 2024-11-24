package com.example.trabalho2bimestre.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalho2bimestre.R;


public class MainActivity extends AppCompatActivity {

    private Button btCadastroClientes, btPedidosVendas, btPesquisarPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btCadastroClientes = findViewById(R.id.btCadastroClientes);
        btPedidosVendas = findViewById(R.id.btPedidosVendas);
        btPesquisarPedidos = findViewById(R.id.btPesquisarPedidos);

        btCadastroClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =
                        new Intent(MainActivity.this,
                                CadastroClienteActivity.class);

                startActivity(intent);
            }
        });

        btPedidosVendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =
                        new Intent(MainActivity.this,
                                SelecionarClienteActivity.class);

                startActivity(intent);
            }
        });

        btPesquisarPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =
                        new Intent(MainActivity.this,
                                BuscarPedidosActivity.class);

                startActivity(intent);
            }
        });

    }
}