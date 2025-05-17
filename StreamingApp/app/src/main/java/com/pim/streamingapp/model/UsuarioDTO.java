package com.pim.streamingapp.model;

public class UsuarioDTO {
    public int id;        // pode mandar 0
    public String nome;
    public String email;
    public String password;  // TEM que se chamar igual ao backend!
    public int admin;     // 0 ou 1
}