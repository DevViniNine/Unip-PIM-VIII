package com.pim.streamingapp;

import com.pim.streamingapp.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/Conteudo/listar")
    Call<List<Conteudo>> getConteudos();

    @POST("api/Usuario/cadastrar")
    Call<Void> cadastrarUsuario(@Body UsuarioDTO dto);

    @POST("api/Usuario/login")
    Call<LoginResponse> fazerLogin(@Body LoginRequest login);

    @GET("api/Usuario/email/{email}")
    Call<UsuarioDTO> getUsuarioPorEmail(@Path("email") String email);

    @POST("api/Curtida/curtir/{id}")
    Call<Void> curtirConteudo(@Path("id") int conteudoId, @Header("Authorization") String token);

    @DELETE("api/Curtida/descurtir/{id}")
    Call<Void> descurtirConteudo(@Path("id") int conteudoId, @Header("Authorization") String token);

    @GET("api/Curtida/listar")
    Call<List<CurtidaDTO>> listarCurtidas(@Header("Authorization") String token);

    @GET("api/Curtida/existe")
    Call<Boolean> verificarCurtida(
            @Query("usuarioId") int usuarioId,
            @Query("conteudoId") int conteudoId,
            @Header("Authorization") String token
    );

    @GET("api/Curtida/listar/{id}")
    Call<ResumoCurtidaResponse> listarCurtidas(@Path("id") int conteudoId);

    @GET("api/Comentario/listar/{conteudoId}")
    Call<RespostaComentarioDTO> listarComentarios(@Path("conteudoId") int conteudoId);

    @POST("api/Comentario/comentar/{conteudoId}")
    Call<Void> comentar(
            @Path("conteudoId") int conteudoId,
            @Body ComentarioDTO comentario,
            @Header("Authorization") String token
    );

    @POST("api/Visualizacao/registrar/{id}")
    Call<Void> registrarVisualizacao(@Path("id") int conteudoId, @Header("Authorization") String token);

    @GET("api/Visualizacao/total/{conteudoId}")
    Call<ResumoVisualizacaoResponse> getTotalVisualizacoes(@Path("conteudoId") int conteudoId);


}

