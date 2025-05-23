package com.pim.streamingapp.model;

public class ComentarioDTO {

    public int usuarioId;
    public int conteudoId;
    public String texto;
    public String usuarioNome;
    public transient String dataComentario;
}