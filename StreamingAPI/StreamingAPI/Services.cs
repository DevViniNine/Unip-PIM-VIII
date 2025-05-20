using AutoMapper;
using static StreamingAPI.DTOs;
using static StreamingAPI.Interfaces;
using StreamingAPI.Models;
using System.Security.Cryptography;
using System.Text;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;

namespace StreamingAPI
{

    //SERVICES DO USUARIO
        public class UsuarioService : IUsuarioService
        {
            public readonly IUsuarioRepository _repository;
            public readonly IMapper _mapper;

            public UsuarioService(IUsuarioRepository repository, IMapper mapper)
            {
                _mapper = mapper;
                _repository = repository;
            }
        

        public async Task<UsuarioDTO> Alterar(UsuarioDTO dto)
        {
            var usuarioExistente = await _repository.SelecionarAsync(dto.Id);
            if (usuarioExistente == null)
                throw new Exception("Usuário não encontrado");

            
           usuarioExistente.AlterarNome(dto.Nome);
           usuarioExistente.AlterarEmail(dto.Email);

            
            if (!string.IsNullOrEmpty(dto.Password))
            {
                using var hmac = new HMACSHA512();
                usuarioExistente.PasswordSalt = hmac.Key;
                usuarioExistente.PasswordHash = hmac.ComputeHash(Encoding.UTF8.GetBytes(dto.Password));
            }

            var usuarioAlterado = await _repository.Alterar(usuarioExistente);
            return _mapper.Map<UsuarioDTO>(usuarioAlterado);
        }



        public async Task<UsuarioDTO> Excluir(int id)
            {
                var usuario = await _repository.Excluir(id);
                return _mapper.Map<UsuarioDTO>(usuario);
            }

        public async Task<UsuarioDTO> SelecionarPorEmailAsync(string email)
        {
            var usuario = await _repository.SelecionarPorEmailAsync(email);
            return _mapper.Map<UsuarioDTO>(usuario);
        }

        public async Task<UsuarioDTO> Incluir(UsuarioDTO usuarioDTO)
        {
            if (string.IsNullOrWhiteSpace(usuarioDTO.Password))
            {
                throw new ArgumentException("A senha não pode estar vazia.");
            }

            var usuario = _mapper.Map<Usuario>(usuarioDTO);

            using var hmac = new HMACSHA512();
            usuario.PasswordHash = hmac.ComputeHash(Encoding.UTF8.GetBytes(usuarioDTO.Password));
            usuario.PasswordSalt = hmac.Key;

            var usuarioIncluido = await _repository.Incluir(usuario);
            return _mapper.Map<UsuarioDTO>(usuarioIncluido);
        }
        public async Task<UsuarioDTO> SelecionarAsync(int id)
            {
                var usuario = await _repository.SelecionarAsync(id);
                return _mapper.Map<UsuarioDTO>(usuario);
            }

            public async Task<IEnumerable<UsuarioDTO>> SelecionarTodosAsync()
            {
                var usuarios = await _repository.SelecionarTodosAsync();
                return _mapper.Map<IEnumerable<UsuarioDTO>>(usuarios);
            }
        }

    public class AuthenticateService : IAuthenticate
    {
        private readonly StreamingAPIContext _context;
        private readonly IConfiguration _configuration;

        public AuthenticateService(StreamingAPIContext context, IConfiguration configuration)
        {
            _context = context;
            _configuration = configuration;
        }

        public async Task<bool> AuthenticateAsync(string email, string senha)
        {
            var usuario = await _context.Usuario.Where(x => x.Email.ToLower() == email.ToLower()).FirstOrDefaultAsync();
            if (usuario == null)
            {
                return false;
            }

            using var hmac = new HMACSHA512(usuario.PasswordSalt);
            var computeHash = hmac.ComputeHash(Encoding.UTF8.GetBytes(senha));
            for (int x = 0; x < computeHash.Length; x++)
            {
                if (computeHash[x] != usuario.PasswordHash[x]) return false;
            }

            return true;
        }

        //SERVIÇO DE AUTENTICAÇÃO
        public string GenerateToken(int id, string email)
        {
            var claims = new[]
            {
            new Claim("id", id.ToString()),
            new Claim("email", email),
            new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString())
        };
            var privateKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes
                (_configuration["jwt:secretKey"]));
            var credentials = new SigningCredentials(privateKey, SecurityAlgorithms.HmacSha256);

            var expiration = DateTime.UtcNow.AddHours(1);

            JwtSecurityToken token = new JwtSecurityToken(
                issuer: _configuration["jwt:issuer"],
                audience: _configuration["jwt:audience"],
                claims: claims,
                expires: expiration,
                signingCredentials: credentials
                );
            return new JwtSecurityTokenHandler().WriteToken(token);
        }

        public async Task<Usuario> GetUserByEmail(string email)
        {
            return await _context.Usuario.Where(x => x.Email.ToLower() == email.ToLower()).FirstOrDefaultAsync();
        }

        public async Task<bool> UserExists(string email)
        {
            var usuario = await _context.Usuario.Where(x => x.Email.ToLower() == email.ToLower()).FirstOrDefaultAsync();
            if (usuario == null)
            {
                return false;
            }

            return true;
        }
        
    }
    //SERVIÇO DE AUTENTICAÇÃO
    public class AuthService
    {
        public void CreatePasswordHash(string password, out byte[] hash, out byte[] salt)
        {
            using (var hmac = new HMACSHA512())
            {
                salt = hmac.Key;
                hash = hmac.ComputeHash(Encoding.UTF8.GetBytes(password));
            }
        }

        public bool VerifyPasswordHash(string password, byte[] storedHash, byte[] storedSalt)
        {
            using (var hmac = new HMACSHA512(storedSalt))
            {
                var computedHash = hmac.ComputeHash(Encoding.UTF8.GetBytes(password));
                return computedHash.SequenceEqual(storedHash);
            }
        }
    }

    public class PlaylistService : IPlaylistService

    {
        
        private readonly IPlaylistRepository _repository;
        private readonly StreamingAPIContext _context;
        private readonly IMapper _mapper;
        public PlaylistService(IPlaylistRepository repository, IMapper mapper, StreamingAPIContext context)
        {
            _repository = repository;
            _context = context;
            _mapper = mapper;
        }

        public async Task<IEnumerable<PlaylistDTO>> ListarDoUsuarioAsync(int usuarioId)
        {
            var playlists = await _repository.ListarPorUsuarioAsync(usuarioId);
            return _mapper.Map<IEnumerable<PlaylistDTO>>(playlists);
        }

        public async Task<PlaylistDTO> CriarAsync(PlaylistDTO dto, int usuarioId)
        {
            var playlist = _mapper.Map<Playlist>(dto);
            playlist.UsuarioId = usuarioId;

            var criada = await _repository.IncluirAsync(playlist);
            return _mapper.Map<PlaylistDTO>(criada);
        }




        public async Task<PlaylistDTO> AlterarAsync(PlaylistDTO dto, int usuarioId)
        {
            var playlist = await _context.Playlists.FirstOrDefaultAsync(p => p.Id == dto.Id);

            if (playlist == null)
                throw new Exception("Playlist não encontrada.");

            if (playlist.UsuarioId != usuarioId)
                throw new UnauthorizedAccessException();

            playlist.Nome = dto.Nome;
            await _context.SaveChangesAsync();

            return _mapper.Map<PlaylistDTO>(playlist);
        }

        public async Task DeletarAsync(int id, int usuarioId)
        {
            var playlist = await _context.Playlists.FirstOrDefaultAsync(p => p.Id == id);


            if (playlist == null)
                throw new Exception("Playlist não encontrada.");

            if (playlist.UsuarioId != usuarioId)
                throw new UnauthorizedAccessException();

            _context.Playlists.Remove(playlist);
            await _context.SaveChangesAsync();
        }
    }
    public class ItemPlaylistService : IItemPlaylistService
    {
        private readonly IItemPlaylistRepository _repo;
        private readonly IMapper _mapper;

        public ItemPlaylistService(IItemPlaylistRepository repo, IMapper mapper)
        {
            _repo = repo;
            _mapper = mapper;
        }

        public async Task<ItemPlaylistDTO> AdicionarAsync(ItemPlaylistDTO dto)
        {
            bool existe = await _repo.ExisteAsync(dto.PlaylistID, dto.ConteudoID);

            if (existe)
                throw new InvalidOperationException("Este conteúdo já está na playlist.");

            var entidade = _mapper.Map<ItemPlaylist>(dto);
            await _repo.AdicionarAsync(entidade);
            return dto;
        }

        public async Task<IEnumerable<ItemPlaylistDTO>> ListarTodosAsync()
        {
            var entidades = await _repo.ListarTodosAsync();
            return entidades.Select(ip => _mapper.Map<ItemPlaylistDTO>(ip));
        }

        public interface ICurtidaService
        {
            Task CurtirAsync(int usuarioId, int conteudoId);
            Task<bool> UsuarioCurtiuConteudoAsync(int usuarioId, int conteudoId);

            Task DescurtirAsync(int usuarioId, int conteudoId);
            Task<IEnumerable<CurtidaDTO>> ListarCurtidasDoConteudoAsync(int conteudoId);



        }

        public class CurtidaService : ICurtidaService
        {
            //private readonly StreamingAPIContext _context;
            private readonly ICurtidaRepository _repository;
            private readonly IMapper _mapper;
            

            public CurtidaService(ICurtidaRepository repository, IMapper mapper)
            {
                _repository = repository;
                _mapper = mapper;
            }

            public async Task CurtirAsync(int usuarioId, int conteudoId)
            {
                if (await _repository.ExisteAsync(usuarioId, conteudoId))
                    throw new InvalidOperationException("Usuário já curtiu este conteúdo.");

                var curtida = new Curtida
                {
                    UsuarioId = usuarioId,
                    ConteudoId = conteudoId
                };

                await _repository.AdicionarAsync(curtida);
            }


            public async Task DescurtirAsync(int usuarioId, int conteudoId)
            {
                await _repository.RemoverAsync(usuarioId, conteudoId);
            }



            public async Task<bool> UsuarioCurtiuConteudoAsync(int usuarioId, int conteudoId)
            {
                return await _repository.UsuarioCurtiuConteudoAsync(usuarioId, conteudoId);
            }

            public async Task<IEnumerable<CurtidaDTO>> ListarCurtidasDoConteudoAsync(int conteudoId)
            {
                var curtidas = await _repository.ListarPorConteudoAsync(conteudoId);
                return curtidas.Select(c => new CurtidaDTO
                {
                    UsuarioId = c.UsuarioId,
                    ConteudoId = c.ConteudoId
                });
            }
        }



        public interface IComentarioService
        {
            Task ComentarAsync(int usuarioId, int conteudoId, string texto);
            Task<IEnumerable<ComentarioDTO>> ListarAsync(int conteudoId);
        }

        public class ComentarioService : IComentarioService
        {
            private readonly IComentarioRepository _repo;

            public ComentarioService(IComentarioRepository repo)
            {
                _repo = repo;
            }

            public async Task ComentarAsync(int usuarioId, int conteudoId, string texto)
            {
                var comentario = new Comentario
                {
                    UsuarioId = usuarioId,
                    ConteudoId = conteudoId,
                    Texto = texto,
                    DataComentario = DateTime.Now
                };

                await _repo.AdicionarAsync(comentario);
            }
            public async Task<IEnumerable<ComentarioDTO>> ListarAsync(int conteudoId)
            {
                var comentarios = await _repo.ListarPorConteudoAsync(conteudoId);

                return comentarios.Select(c => new ComentarioDTO
                {
                    UsuarioId = c.UsuarioId,
                    ConteudoId = c.ConteudoId,
                    Texto = c.Texto,
                    UsuarioNome = c.Usuario?.Nome,
                    DataComentario = c.DataComentario
                });
            }

        }


        public interface IVisualizacaoService
        {
            Task RegistrarAsync(int usuarioId, int conteudoId);
            Task<IEnumerable<VisualizacaoDTO>> ListarPorUsuarioAsync(int usuarioId);
            Task<int> TotalPorConteudoAsync(int conteudoId);
        }

        public class VisualizacaoService : IVisualizacaoService
        {

            private readonly IVisualizacaoRepository _repo;


            public VisualizacaoService(IVisualizacaoRepository repo)
            {
                _repo = repo;
            }

            public async Task RegistrarAsync(int usuarioId, int conteudoId)
            {
                var visualizacao = new Visualizacao
                {
                    UsuarioId = usuarioId,
                    ConteudoId = conteudoId
                };

                await _repo.AdicionarAsync(visualizacao);
            }
            public async Task<int> TotalPorConteudoAsync(int conteudoId)
            {
                return await _repo.ContarPorConteudoAsync(conteudoId);
            }

            public async Task<IEnumerable<VisualizacaoDTO>> ListarPorUsuarioAsync(int usuarioId)
            {
                // Certifique-se de que seu repo faz Include(v => v.Conteudo)
                var lista = await _repo.ListarPorUsuarioAsync(usuarioId);

                return lista.Select(v => new VisualizacaoDTO
                {
                    UsuarioId = v.UsuarioId,
                    ConteudoId = v.ConteudoId,
                    NomeConteudo = v.Conteudo?.Nome,    // <-- Aqui pega nome
                    TipoConteudo = v.Conteudo?.Tipo,    // <-- Aqui pega tipo
                    DataVisualizacao = v.DataVisualizacao
                });
            }
        }
    }

}

