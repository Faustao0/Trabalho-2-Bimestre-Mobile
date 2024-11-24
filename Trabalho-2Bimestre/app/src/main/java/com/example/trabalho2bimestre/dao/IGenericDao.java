package com.example.trabalho2bimestre.dao;

import java.util.ArrayList;

public interface IGenericDao<Objeto>{

    long insert(Objeto obj);
    long update(Objeto obj);
    long delete(Objeto obj);
    Objeto getById(int id);
    ArrayList<Objeto> getAll();
}
