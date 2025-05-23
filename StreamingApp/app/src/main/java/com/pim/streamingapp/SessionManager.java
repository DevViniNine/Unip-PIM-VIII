package com.pim.streamingapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String KEY_USER_ADMIN = "usuario_admin";
    private static final String PREF_NAME = "user_session";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "usuario_id";
    private static final String KEY_USER_NAME = "usuario_nome";
    private static final String KEY_USER_EMAIL = "usuario_email"; // NOVO

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void salvarToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void salvarUsuarioId(int id) {
        editor.putInt(KEY_USER_ID, id);
        editor.apply();
    }

    public void salvarUsuarioAdmin(int admin) {
        editor.putInt(KEY_USER_ADMIN, admin);
        editor.apply();
    }

    public int getUsuarioAdmin() {
        return prefs.getInt(KEY_USER_ADMIN, 0); // 0: padrão (usuário comum)
    }

    public int getUsuarioId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public void salvarUsuarioNome(String nome) {
        editor.putString(KEY_USER_NAME, nome);
        editor.apply();
    }

    public String getUsuarioNome() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    // NOVO: salvar e obter email
    public void salvarUsuarioEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }
    public String getUsuarioEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public void limparSessao() {
        editor.clear();
        editor.apply();
    }
}
