package com.example.trabalho2bimestre.model;

import java.util.List;

public class PedidoVenda {

    private int codigo;
    private double valorTotal;
    private String condicaoPagamento;
    private int quantidadeParcelas;
    private double valorFrete;
    private Cliente cliente;
    private Endereco endereco;
    private List<Item> itens;

    public PedidoVenda() {
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getCondicaoPagamento() {
        return condicaoPagamento;
    }

    public void setCondicaoPagamento(String condicaoPagamento) {
        this.condicaoPagamento = condicaoPagamento;
    }

    public int getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(int quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public double getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(double valorFrete) {
        this.valorFrete = valorFrete;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public List<Item> getItens() {
        return itens;
    }

    public void setItens(List<Item> itens) {
        this.itens = itens;
    }
}
