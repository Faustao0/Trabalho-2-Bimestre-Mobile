package com.example.trabalho2bimestre.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trabalho2bimestre.R;
import com.example.trabalho2bimestre.controller.ItemController;

public class DialogCadastroItemActivity extends AppCompatActivity {

    private EditText et_descricao;
    private EditText et_valor_unit;
    private EditText et_unidade_media;
    private Button btn_criar_item;
    private ItemController itemController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dialog_cadastro_item);

        et_descricao = findViewById(R.id.et_descricao);
        et_valor_unit = findViewById(R.id.et_valor_unit);
        btn_criar_item = findViewById(R.id.btn_criar_item);
        et_unidade_media = findViewById(R.id.et_unidade_media);

        itemController = new ItemController(this);
    }
}