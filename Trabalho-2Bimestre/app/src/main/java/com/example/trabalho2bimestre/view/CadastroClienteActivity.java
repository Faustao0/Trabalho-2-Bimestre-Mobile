package com.example.trabalho2bimestre.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalho2bimestre.R;
import com.example.trabalho2bimestre.controller.ClienteController;

public class CadastroClienteActivity extends AppCompatActivity {

    private static final String TAG = "CadastroClienteActivity";
    private EditText etNome, etCpf, etDataNasc, etLogradouro, etNumero, etBairro, etCidade, etUf;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cliente);

        etNome = findViewById(R.id.et_nome);
        etCpf = findViewById(R.id.et_cpf);
        etDataNasc = findViewById(R.id.et_data_nasc);
        etLogradouro = findViewById(R.id.et_logradouro);
        etNumero = findViewById(R.id.et_numero);
        etBairro = findViewById(R.id.et_bairro);
        etCidade = findViewById(R.id.et_cidade);
        etUf = findViewById(R.id.et_uf);
        btnSalvar = findViewById(R.id.btn_salvar_cliente);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = etNome.getText().toString();
                String cpf = etCpf.getText().toString();
                String dataNasc = etDataNasc.getText().toString();

                String logradouro = etLogradouro.getText().toString();
                String numero = etNumero.getText().toString();
                String bairro = etBairro.getText().toString();
                String cidade = etCidade.getText().toString();
                String uf = etUf.getText().toString();

                if (nome.isEmpty() || cpf.isEmpty() || dataNasc.isEmpty() ||
                        logradouro.isEmpty() || numero.isEmpty() || bairro.isEmpty() || cidade.isEmpty() || uf.isEmpty()) {
                    Toast.makeText(CadastroClienteActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ClienteController clienteController = new ClienteController(CadastroClienteActivity.this);

                long clienteId = clienteController.salvarClienteComEndereco(nome, cpf, dataNasc, logradouro, numero, bairro, cidade, uf);

                if (clienteId > 0) {
                    Toast.makeText(CadastroClienteActivity.this, "Cliente cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CadastroClienteActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    String errorMessage;
                    switch ((int) clienteId) {
                        case -1:
                            errorMessage = "Informe o nome do cliente.";
                            break;
                        case -2:
                            errorMessage = "Informe o CPF do cliente.";
                            break;
                        case -3:
                            errorMessage = "Informe a data de nascimento do cliente.";
                            break;
                        case -4:
                            errorMessage = "O CPF (" + cpf + ") já está cadastrado.";
                            break;
                        case -5:
                            errorMessage = "Erro ao salvar endereço.";
                            break;
                        default:
                            errorMessage = "Erro ao salvar cliente.";
                            break;
                    }
                    Toast.makeText(CadastroClienteActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}