package com.pim.streamingapp;

import com.pim.streamingapp.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/Conteudo/listar")
    Call<List<Conteudo>> getConteudos();

    @POST("api/Usuario/cadastrar")
    Call<Void> cadastrarUsuario(@Body UsuarioDTO dto);

    @POST("api/Usuario/login")
    Call<LoginResponse> fazerLogin(@Body LoginRequest login);

    @GET("api/Usuario/email/{email}")
    Call<UsuarioDTO> getUsuarioPorEmail(@Path("email") String email);
}

