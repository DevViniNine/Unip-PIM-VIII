package com.pim.streamingapp;

import com.pim.streamingapp.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    Call<Void> curtirConteudo(@Path("id") int conteudoId);

    @DELETE("api/Curtida/descurtir/{id}")
    Call<Void> descurtirConteudo(@Path("id") int conteudoId);

    @GET("api/Curtida/listar")
    Call<List<CurtidaDTO>> listarCurtidas();

    @GET("api/Curtida/existe")
    Call<Boolean> verificarCurtida(
            @Query("usuarioId") int usuarioId,
            @Query("conteudoId") int conteudoId
    );

    @GET("api/Curtida/listar/{id}")
    Call<ResumoCurtidaResponse> listarCurtidas(@Path("id") int conteudoId);

    @GET("api/Comentario/listar/{conteudoId}")
    Call<RespostaComentarioDTO> listarComentarios(@Path("conteudoId") int conteudoId);

    @POST("api/Comentario/comentar/{conteudoId}")
    Call<Void> comentar(
            @Path("conteudoId") int conteudoId,
            @Body ComentarioDTO comentario
    );

    @POST("api/Visualizacao/registrar/{id}")
    Call<Void> registrarVisualizacao(@Path("id") int conteudoId);

    @GET("api/Visualizacao/total/{conteudoId}")
    Call<ResumoVisualizacaoResponse> getTotalVisualizacoes(@Path("conteudoId") int conteudoId);


    // Alterar playlist
    @PUT("api/Playlist/alterar/{id}")
    Call<Void> alterarPlaylist(@Path("id") int id, @Body Playlist playlist);

    // Deletar playlist
    @DELETE("api/Playlist/deletar/{id}")
    Call<Void> deletarPlaylist(@Path("id") int id);

    // Listar playlists (supondo que você tenha esse endpoint futuramente)
    @POST("api/Playlist/criar")
    Call<Void> criarPlaylist(@Body Playlist playlist);

    @GET("api/Playlist/MinhasPlaylist")
    Call<List<Playlist>> listarPlaylists();


    @POST("api/ItemPlaylist")
    Call<Void> adicionarConteudoNaPlaylist(@Body ItemPlaylistDTO dto);

    @GET("api/ItemPlaylist/{playlistId}/conteudos")
    Call<List<Conteudo>> listarConteudosDaPlaylist(@Path("playlistId") int playlistId);

    @GET("api/Visualizacao/ultimos")
    Call<RespostaVisualizacaoDTO> listarVisualizacoesRecentes();

    @GET("/api/Curtida/curtidos")
    Call<RespostaCurtidosDTO> listarCurtidos();

}

