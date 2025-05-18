using Microsoft.OpenApi.Models;
using StreamingAPI.Models;
using static StreamingAPI.DTOs;

namespace StreamingAPI
{
    public interface Interfaces
    {
        //INTERFACE DE AUTHENTICATE
        public interface IAuthenticate
        {
            Task<bool> AuthenticateAsync(string email, string senha);
            Task<bool> UserExists(string email);

            public string GenerateToken(int id, string email);
            public Task<Usuario> GetUserByEmail(string email);
        }

        //INTERFACE DE USUARIO REPOSITORY
        public interface IUsuarioRepository
        {
            Task<Usuario> Incluir(Usuario usuario);
            Task<Usuario> Alterar(Usuario usuario);
            Task<Usuario> Excluir(int id);
            Task<Usuario> SelecionarAsync(int id);
            Task<Usuario> SelecionarPorEmailAsync(string email);
            Task<IEnumerable<Usuario>> SelecionarTodosAsync();
        }


        //INTERFACE DE USUARIO SERVICE
        public interface IUsuarioService
        {
            Task<UsuarioDTO> Incluir(UsuarioDTO usuarioDTO);
            Task<UsuarioDTO> Alterar(UsuarioDTO usuarioDTO);
            Task<UsuarioDTO> Excluir(int id);
            Task<UsuarioDTO> SelecionarAsync(int id);

            Task<UsuarioDTO> SelecionarPorEmailAsync(string email);
            Task<IEnumerable<UsuarioDTO>> SelecionarTodosAsync();
        }

    }

    public static class DepedenceInjectionSwagger
    {
        public static IServiceCollection AddInfrastructureSwegger(this IServiceCollection services)
        {
            services.AddSwaggerGen(c =>
            {
                c.AddSecurityDefinition("Bearer", new Microsoft.OpenApi.Models.OpenApiSecurityScheme()
                {
                    Name = "Authorization",
                    Type = Microsoft.OpenApi.Models.SecuritySchemeType.ApiKey,
                    BearerFormat = "JWT",
                    In = Microsoft.OpenApi.Models.ParameterLocation.Header,
                    Description = "JSON Web Tokens (JWT) são um padrão aberto, que é definido em JSON Web Token (JWT) Specification RFC 7519. Elas representam de forma segura solicitações entre duas partes As solicitações podem ser relacionadas a qualquer processo de negócios, mas geralmente são usadas para representar uma identidade e suas associações Por exemplo, um usuário cuja identidade o JWT representa pertence a uma função ou grupo de administrador..",
                });

                c.AddSecurityRequirement(new Microsoft.OpenApi.Models.OpenApiSecurityRequirement()
            {
                {
                    new OpenApiSecurityScheme()
                    {
                        Reference = new OpenApiReference()
                        {
                            Type = ReferenceType.SecurityScheme,
                            Id = "Bearer"
                        }
                    },
                    new string[]{}
                }

            });
            });

            return services;
        }

    }

    public interface IPlaylistService
    {
        Task<PlaylistDTO> CriarAsync(PlaylistDTO dto, int usuarioId);
        Task<IEnumerable<PlaylistDTO>> ListarDoUsuarioAsync(int usuarioId);
        Task<PlaylistDTO> AlterarAsync(PlaylistDTO dto, int usuarioId);
        Task DeletarAsync(int id, int usuarioId);
    }
    public interface IPlaylistRepository
    {
        Task<Playlist> IncluirAsync(Playlist playlist);
        Task<IEnumerable<Playlist>> ListarPorUsuarioAsync(int usuarioId);
    }


    public interface IItemPlaylistService
    {
        Task<IEnumerable<ItemPlaylistDTO>> ListarTodosAsync();
        Task<ItemPlaylistDTO> AdicionarAsync(ItemPlaylistDTO dto);
    }

    public interface IItemPlaylistRepository
    {
        Task AdicionarAsync(ItemPlaylist entidade);
        Task<IEnumerable<ItemPlaylist>> ListarTodosAsync();
        Task<bool> ExisteAsync(int playlistId, int conteudoId);
    }

    public interface ICurtidaRepository
    {
        Task AdicionarAsync(Curtida curtida);
        Task RemoverAsync(int usuarioId, int conteudoId);
        Task<bool> ExisteAsync(int usuarioId, int conteudoId);
        Task<IEnumerable<Curtida>> ListarPorConteudoAsync(int conteudoId);
        Task<bool> UsuarioCurtiuConteudoAsync(int usuarioId, int conteudoId);
    }

    public interface IComentarioRepository
    {
        Task AdicionarAsync(Comentario comentario);
        Task<IEnumerable<Comentario>> ListarPorConteudoAsync(int conteudoId);
    }


    public interface IVisualizacaoRepository
    {
        Task AdicionarAsync(Visualizacao visualizacao);
        Task<IEnumerable<Visualizacao>> ListarPorUsuarioAsync(int usuarioId);

        Task<int> ContarPorConteudoAsync(int conteudoId);
        Task<IEnumerable<Visualizacao>> ListarPorConteudoAsync(int conteudoId);
    }

}

