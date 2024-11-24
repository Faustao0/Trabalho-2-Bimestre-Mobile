package com.example.trabalho2bimestre.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteDataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BD_VENDAS";
    private static final int DATABASE_VERSION = 10;

    private static SQLiteDataHelper instance;

    public SQLiteDataHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized SQLiteDataHelper getInstance(Context context) {
        if (instance == null) {
            if (context == null) {
                throw new IllegalArgumentException("Context não pode ser nulo ao inicializar SQLiteDataHelper");
            }
            instance = new SQLiteDataHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public synchronized void close() {
        if (instance != null) {
            super.close();
            instance = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ENDERECO (" +
                "CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "RUA VARCHAR(100), " +
                "NUMERO VARCHAR(10), " +
                "BAIRRO VARCHAR(50), " +
                "CIDADE VARCHAR(50), " +
                "UF VARCHAR(2), " +
                "ESTADO VARCHAR(2), " +
                "CLIENTE_CODIGO INTEGER, " +
                "FOREIGN KEY(CLIENTE_CODIGO) REFERENCES CLIENTE(CODIGO)" +
                ");");

        db.execSQL("CREATE TABLE CLIENTE (" +
                "CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NOME VARCHAR(100) NOT NULL, " +
                "CPF VARCHAR(11) NOT NULL UNIQUE, " +
                "DATA_NASC DATE, " +
                "CODIGO_ENDERECO INTEGER, " +
                "FOREIGN KEY(CODIGO_ENDERECO) REFERENCES ENDERECO(CODIGO)" +
                ");");

        db.execSQL("CREATE TABLE ITEM (" +
                "CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DESCRICAO TEXT NOT NULL, " +
                "VALOR_UNIT DECIMAL(10, 2) NOT NULL, " +
                "UNIDADE_MEDIA VARCHAR(20), " +
                "CLIENTE_CODIGO INTEGER, " +
                "CODIGO_PEDIDO INTEGER, " +
                "FOREIGN KEY(CODIGO_PEDIDO) REFERENCES PEDIDO_VENDA(CODIGO)" +
                ");");

        db.execSQL("CREATE TABLE PEDIDO_VENDA (" +
                "CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "VALOR_TOTAL DECIMAL(10, 2) NOT NULL, " +
                "CONDICAO_PAGAMENTO VARCHAR(50), " +
                "QUANTIDADE_PARCELAS INTEGER, " +
                "VALOR_FRETE DECIMAL(10, 2), " +
                "CODIGO_CLIENTE INTEGER NOT NULL, " +
                "CODIGO_ENDERECO INTEGER, " +
                "FOREIGN KEY(CODIGO_CLIENTE) REFERENCES CLIENTE(CODIGO), " +
                "FOREIGN KEY(CODIGO_ENDERECO) REFERENCES ENDERECO(CODIGO)" +
                ");");

        db.execSQL("CREATE TABLE PEDIDO_ITEM (" +
                "CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CODIGO_PEDIDO INTEGER NOT NULL, " +
                "CODIGO_ITEM INTEGER NOT NULL, " +
                "QUANTIDADE INTEGER NOT NULL, " +
                "FOREIGN KEY(CODIGO_PEDIDO) REFERENCES PEDIDO_VENDA(CODIGO) ON DELETE CASCADE, " +
                "FOREIGN KEY(CODIGO_ITEM) REFERENCES ITEM(CODIGO)" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE ITEM ADD COLUMN UNIDADE_MEDIA VARCHAR(20);");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE ENDERECO ADD COLUMN CLIENTE_CODIGO INTEGER;");
            db.execSQL("ALTER TABLE ENDERECO ADD COLUMN ESTADO VARCHAR(2);");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE ENDERECO ADD COLUMN ESTADO VARCHAR(2);");
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE ITEM ADD COLUMN CLIENTE_CODIGO INTEGER;");
        }
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE ITEM ADD COLUMN CODIGO_PEDIDO INTEGER;");
        }
        if (oldVersion < 9) {
            db.execSQL("PRAGMA foreign_keys=off;");
            db.execSQL("CREATE TABLE ITEM_TEMP AS SELECT * FROM ITEM;");
            db.execSQL("DROP TABLE ITEM;");
            db.execSQL("CREATE TABLE ITEM (" +
                    "CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "DESCRICAO TEXT NOT NULL, " +
                    "VALOR_UNIT DECIMAL(10, 2) NOT NULL, " +
                    "UNIDADE_MEDIA VARCHAR(20), " +
                    "CLIENTE_CODIGO INTEGER, " +
                    "CODIGO_PEDIDO INTEGER, " +
                    "FOREIGN KEY(CODIGO_PEDIDO) REFERENCES PEDIDO_VENDA(CODIGO)" +
                    ");");
            db.execSQL("INSERT INTO ITEM (CODIGO, DESCRICAO, VALOR_UNIT, UNIDADE_MEDIA, CLIENTE_CODIGO, CODIGO_PEDIDO) " +
                    "SELECT CODIGO, DESCRICAO, VALOR_UNIT, UNIDADE_MEDIA, CLIENTE_CODIGO, CODIGO_PEDIDO FROM ITEM_TEMP;");
            db.execSQL("DROP TABLE ITEM_TEMP;");
            db.execSQL("PRAGMA foreign_keys=on;");
        }

        if (oldVersion < 10) {
            db.execSQL("CREATE TABLE PEDIDO_ITEM (" +
                    "CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "CODIGO_PEDIDO INTEGER NOT NULL, " +
                    "CODIGO_ITEM INTEGER NOT NULL, " +
                    "QUANTIDADE INTEGER NOT NULL, " +
                    "FOREIGN KEY(CODIGO_PEDIDO) REFERENCES PEDIDO_VENDA(CODIGO) ON DELETE CASCADE, " +
                    "FOREIGN KEY(CODIGO_ITEM) REFERENCES ITEM(CODIGO)" +
                    ");");
        }
    }
}