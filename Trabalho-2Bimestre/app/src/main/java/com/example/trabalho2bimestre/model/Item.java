package com.example.trabalho2bimestre.model;

public class Item {

    private int codigo;
    private String descricao;
    private double valorUnit;
    private int unidadeMedia;
    private int Codigocliente;
    private int CodigoPedido;

    public Item() {
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValorUnit() {
        return valorUnit;
    }

    public void setValorUnit(double valorUnit) {
        this.valorUnit = valorUnit;
    }

    public int getUnidadeMedia() {
        return unidadeMedia;
    }

    public void setUnidadeMedia(int unidadeMedia) {
        this.unidadeMedia = unidadeMedia;
    }

    public int getCodigocliente() {
        return Codigocliente;
    }

    public void setCodigocliente(int codigocliente) {
        Codigocliente = codigocliente;
    }

    public int getCodigoPedido() {
        return CodigoPedido;
    }

    public void setCodigoPedido(int codigoPedido) {
        CodigoPedido = codigoPedido;
    }
}
