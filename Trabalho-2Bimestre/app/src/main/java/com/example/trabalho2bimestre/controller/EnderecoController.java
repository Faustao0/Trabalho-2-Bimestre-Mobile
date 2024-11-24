package com.example.trabalho2bimestre.controller;

import android.content.Context;
import android.text.TextUtils;

import com.example.trabalho2bimestre.dao.EnderecoDAO;
import com.example.trabalho2bimestre.model.Cliente;
import com.example.trabalho2bimestre.model.Endereco;

import java.util.ArrayList;

public class EnderecoController {

    private Context context;

    public EnderecoController(Context context) {
        this.context = context;
    }

    public String salvarEndereco(String logradouro, String numero, String bairro, String cidade, String uf, Cliente cliente) {
        try {
            if (TextUtils.isEmpty(logradouro)) {
                return "Informe o logradouro.";
            }
            if (TextUtils.isEmpty(numero)) {
                return "Informe o número.";
            }
            if (TextUtils.isEmpty(bairro)) {
                return "Informe o bairro.";
            }
            if (TextUtils.isEmpty(cidade)) {
                return "Informe a cidade.";
            }
            if (TextUtils.isEmpty(uf)) {
                return "Informe a UF.";
            }
            if (cliente == null || cliente.getCodigo() <= 0) {
                return "ID do cliente inválido.";
            }

            Endereco endereco = new Endereco();
            endereco.setLogradouro(logradouro);
            endereco.setNumero(numero);
            endereco.setBairro(bairro);
            endereco.setCidade(cidade);
            endereco.setUf(uf);
            endereco.setClienteCodigo(cliente.getCodigo());

            long id = EnderecoDAO.getInstancia(context).insert(endereco);
            if (id > 0) {
                return "Endereço gravado com sucesso.";
            } else {
                return "Erro ao gravar endereço no BD.";
            }
        } catch (Exception ex) {
            return "Erro ao salvar dados do endereço.";
        }
    }

    public ArrayList<Endereco> retornarTodosEnderecos() {
        return EnderecoDAO.getInstancia(context).getAll();
    }
}
