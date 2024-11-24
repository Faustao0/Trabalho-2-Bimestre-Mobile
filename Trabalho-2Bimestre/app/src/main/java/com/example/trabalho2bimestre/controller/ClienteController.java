package com.example.trabalho2bimestre.controller;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.trabalho2bimestre.dao.ClienteDAO;
import com.example.trabalho2bimestre.dao.EnderecoDAO;
import com.example.trabalho2bimestre.model.Cliente;
import com.example.trabalho2bimestre.model.Endereco;

import java.util.ArrayList;

public class ClienteController {

    private Context context;
    private ClienteDAO clienteDAO;
    private EnderecoDAO enderecoDAO;

    public ClienteController(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("O contexto não pode ser nulo ao inicializar o ClienteController!");
        }
        this.context = context.getApplicationContext();
        this.clienteDAO = ClienteDAO.getInstancia(context);
        this.enderecoDAO = EnderecoDAO.getInstancia(context);
    }

    // Método para salvar cliente com endereço
    public long salvarClienteComEndereco(String nome, String cpf, String dataNasc, String logradouro, String numero, String bairro, String cidade, String uf) {
        if (nome.isEmpty()) return -1;
        if (cpf.isEmpty()) return -2;
        if (dataNasc.isEmpty()) return -3;

        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setDataNasc(dataNasc);

        Cliente clienteExistente = clienteDAO.getByCpf(cpf);
        if (clienteExistente != null) {
            return -4;
        }

        long clienteId = clienteDAO.insert(cliente);

        if (clienteId > 0) {
            Endereco endereco = new Endereco();
            endereco.setLogradouro(logradouro);
            endereco.setNumero(numero);
            endereco.setBairro(bairro);
            endereco.setCidade(cidade);
            endereco.setUf(uf);
            endereco.setClienteCodigo((int) clienteId);
            long enderecoId = enderecoDAO.insert(endereco);

            if (enderecoId > 0) {
                return clienteId;
            } else {
                clienteDAO.delete(cliente);
                return -5;
            }
        }
        return -6;
    }

    public ArrayList<Cliente> retornarTodosClientes() {
        return clienteDAO.getAll();
    }

    public Cliente retornarClientePorId(int clienteId) {
        Cliente cliente = clienteDAO.getById(clienteId);
        if (cliente != null) {
            Endereco endereco = enderecoDAO.getByClienteCodigo(clienteId);
            cliente.setEndereco(endereco);
        }
        return cliente;
    }
}