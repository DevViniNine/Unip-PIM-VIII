using AutoMapper;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using StreamingAPI.Models;
using System;
using System.Security.Claims;
using static StreamingAPI.DTOs;
using static StreamingAPI.Interfaces;
using static StreamingAPI.ItemPlaylistService;
using static StreamingAPI.Models.Usuario;
using static StreamingAPI.Repository;

namespace StreamingAPI
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsuarioController : ControllerBase
    {
        private readonly IAuthenticate _authenticateService;
        private readonly IUsuarioService _usuarioService;
        // private readonly IMapper _mapper;
        public UsuarioController(IAuthenticate authenticateService, IUsuarioService usuarioService/*IMapper mapper*/)
        {
            // _mapper = mapper;
            _authenticateService = authenticateService;
            _usuarioService = usuarioService;
        }
        /*
             
  "name": "admin",
  "email": "admin@admin.com",
  "password": "admin@2025",
  "admin": 1
         */

        [HttpPost("cadastrar")]
        //  [Authorize]
        public async Task<ActionResult<UserToken>> Incluir(UsuarioDTO usuarioDTO)
        {
            if (usuarioDTO == null)
            {
                return BadRequest("Dados Inválidos");
            }
            var emailExiste = await _authenticateService.UserExists(usuarioDTO.Email);

            if (emailExiste)
            {
                return BadRequest("Este E-mail já possui um cadastro.");
            }
            var usuario = await _usuarioService.Incluir(usuarioDTO);
            if (usuario == null)
            {
                return BadRequest("Ocorreu um erro ao cadastrar");
            }

            var token = _authenticateService.GenerateToken(usuario.Id, usuario.Email);

            return new UserToken
            {
                Token = token
            };
        }

        [HttpGet("listar")]
        // [Authorize]
        public async Task<ActionResult<IEnumerable<UsuarioDTO>>> GetUsuarios()
        {
            var usuarios = await _usuarioService.SelecionarTodosAsync();
            return Ok(usuarios);
        }

        [HttpPut("alterar/{id}")]
        // [Authorize]
        public async Task<ActionResult<UsuarioDTO>> Put(int id, [FromBody] UsuarioDTO usuarioDTO)
        {
            if (id != usuarioDTO.Id)
                return BadRequest("O ID do corpo e da URL devem ser iguais.");

            try
            {
                var usuarioAtualizado = await _usuarioService.Alterar(usuarioDTO);
                return Ok(usuarioAtualizado);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erro ao atualizar o usuário: {ex.Message}");
            }
        }

        [HttpPost("login")]

        public async Task<ActionResult<UserToken>> Selecionar(LoginModel loginModel)
        {
            var existe = await _authenticateService.UserExists(loginModel.Email);
            if (!existe)
            {
                return Unauthorized("Usuario não existe.");
            }

            var result = await _authenticateService.AuthenticateAsync(loginModel.Email, loginModel.Password);
            if (!result)
            {
                return Unauthorized("Usuário ou senha inválido.");
            }

            var usuario = await _authenticateService.GetUserByEmail(loginModel.Email);

            var token = _authenticateService.GenerateToken(usuario.Id, usuario.Email);

            return new UserToken
            {
                Token = token
            };
        }

        [HttpGet("email/{email}")]
        // [Authorize] // opcional, se quiser proteger
        public async Task<ActionResult<UsuarioDTO>> GetUsuarioPorEmail(string email)
        {
            var usuario = await _usuarioService.SelecionarPorEmailAsync(email);
            if (usuario == null)
                return NotFound("Usuário não encontrado.");

            return Ok(usuario);
        }

        [HttpDelete("deletar/{id}")]
        // [Authorize]
        public async Task<ActionResult<UsuarioDTO>> Delete(int id)
        {
            try
            {
                var usuarioExcluido = await _usuarioService.Excluir(id);

                if (usuarioExcluido == null)
                    return NotFound("Usuário não encontrado.");

                return Ok(usuarioExcluido);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erro ao excluir o usuário: {ex.Message}");
            }
        }
    }

    [ApiController]
    [Route("api/[controller]")]
    public class ConteudoController : ControllerBase
    {
        private readonly StreamingAPIContext _context;

        public ConteudoController(StreamingAPIContext context)
        {
            _context = context;
        }
        [HttpPost("cadastrar")]
        public async Task<IActionResult> CriarConteudo([FromBody] ConteudoDTO dto)
        {
            if (string.IsNullOrWhiteSpace(dto.NomeCriador))
                return BadRequest("O nome do criador é obrigatório.");

            var criador = await _context.Criadores
                .FirstOrDefaultAsync(c => c.Nome == dto.NomeCriador);

            if (criador == null)
                return NotFound("Criador não encontrado.");

            var conteudo = new Conteudo
            {
                Nome = dto.Nome,
                Tipo = dto.Tipo,
                Url = dto.Url, // NOVO
                CriadorID = criador.Id
            };

            _context.Conteudos.Add(conteudo);
            await _context.SaveChangesAsync();

            return Ok(new
            {
                mensagem = "Conteúdo criado com sucesso!",
                conteudo
            });
        }

        [HttpDelete("deletar/{id}")]
        public async Task<IActionResult> DeletarConteudo(int id)
        {
            var conteudo = await _context.Conteudos.FindAsync(id);
            if (conteudo == null)
            {
                return NotFound(new { mensagem = "Conteúdo não encontrado." });
            }

            _context.Conteudos.Remove(conteudo);
            await _context.SaveChangesAsync();

            return Ok(new { mensagem = "Conteúdo deletado com sucesso!" });
        }


        [HttpGet("listar")]
        public async Task<IActionResult> ListarConteudos()
        {
            var conteudos = await _context.Conteudos
                .Include(c => c.Criador)
                .Select(c => new ConteudoDTO
                {
                    Id = c.Id,
                    Nome = c.Nome,
                    Tipo = c.Tipo,
                    Url = c.Url,
                    NomeCriador = c.Criador != null ? c.Criador.Nome : null
                })
                .ToListAsync();

            return Ok(conteudos);
        }

        [HttpPut("alterar/{id}")]
        [Authorize]
        public async Task<IActionResult> AlterarConteudo(int id, [FromBody] ConteudoUpdateDTO dto)
        {
            var conteudo = await _context.Conteudos.FindAsync(id);

            if (conteudo == null)
                return NotFound(new { mensagem = "Conteúdo não encontrado." });

            var criador = await _context.Criadores.FirstOrDefaultAsync(c => c.Nome == dto.NomeCriador);
            if (criador == null)
                return NotFound(new { mensagem = "Criador informado não existe." });

            conteudo.Nome = dto.Nome;
            conteudo.Tipo = dto.Tipo;
            conteudo.Url = dto.Url; // NOVO
            conteudo.CriadorID = criador.Id;

            await _context.SaveChangesAsync();

            return Ok(new
            {
                mensagem = "Conteúdo atualizado com sucesso!",
                conteudo
            });
        }

    }

    [ApiController]
    [Route("api/[controller]")]
    public class CriadorController : ControllerBase
    {
        private readonly StreamingAPIContext _context;

        public CriadorController(StreamingAPIContext context)
        {
            _context = context;
        }

        [HttpPost("cadastrar")]
        public async Task<IActionResult> CadastrarCriador([FromBody] CriadorDTO dto)
        {
            if (string.IsNullOrWhiteSpace(dto.Nome))
                return BadRequest("O nome do criador é obrigatório.");

            var criador = new Criador
            {
                Nome = dto.Nome
            };

            _context.Criadores.Add(criador);
            await _context.SaveChangesAsync();

            return Ok(new
            {
                mensagem = "Criador cadastrado com sucesso.",
                criador
            });
        }
        [HttpGet("listar")]
        public async Task<IActionResult> ListarCriadores()
        {
            var criadores = await _context.Criadores.ToListAsync();
            return Ok(criadores);
        }


        [HttpPut("alterar/{id}")]
        public async Task<IActionResult> AlterarCriador(int id, [FromBody] CriadorDTO dto)
        {
            var criador = await _context.Criadores.FindAsync(id);
            if (criador == null)
                return NotFound("Criador não encontrado.");

            criador.Nome = dto.Nome;
            _context.Entry(criador).State = EntityState.Modified;

            await _context.SaveChangesAsync();
            return Ok(new
            {
                mensagem = "Criador alterado com sucesso.",
                criador
            });
        }


        [HttpDelete("deletar/{id}")]
        public async Task<IActionResult> DeletarCriador(int id)
        {
            var criador = await _context.Criadores.FindAsync(id);
            if (criador == null)
                return NotFound("Criador não encontrado.");

            _context.Criadores.Remove(criador);
            await _context.SaveChangesAsync();

            return Ok(new { mensagem = "Criador deletado com sucesso." });
        }
    }

    [ApiController]
    [Route("api/[controller]")]
    public class PlaylistController : ControllerBase
    {

        private readonly IPlaylistService _playlistService;

        public PlaylistController(IPlaylistService playlistService)
        {
            _playlistService = playlistService;
        }

        [HttpPost("criar")]
        [Authorize]
        public async Task<IActionResult> Criar(PlaylistDTO dto)
        {
            var userIdClaim = User.Claims.FirstOrDefault(c => c.Type == "id");
            if (userIdClaim == null) return Unauthorized("Usuário não autenticado.");

            int userId = int.Parse(userIdClaim.Value);
            var result = await _playlistService.CriarAsync(dto, userId);
            return Ok(result);
        }


        [HttpGet("MinhasPlaylist")]
        [Authorize]
        public async Task<IActionResult> MinhasPlaylists()
        {
            var usuarioIdStr = User.Claims.FirstOrDefault(x => x.Type == "id")?.Value;
            if (string.IsNullOrEmpty(usuarioIdStr)) return Unauthorized();

            int usuarioId = int.Parse(usuarioIdStr);

            var playlists = await _playlistService.ListarDoUsuarioAsync(usuarioId);
            return Ok(playlists);
        }

        [HttpPut("alterar/{id}")]
        [Authorize]
        public async Task<IActionResult> Alterar(int id, [FromBody] PlaylistDTO dto)
        {
            var idUsuario = int.Parse(User.FindFirst("id").Value);
            dto.Id = id;

            try
            {
                var resultado = await _playlistService.AlterarAsync(dto, idUsuario);
                return Ok(resultado);
            }
            catch (UnauthorizedAccessException)
            {
                return Forbid("Você não tem permissão para alterar esta playlist.");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpDelete("deletar/{id}")]
        [Authorize]
        public async Task<IActionResult> Deletar(int id)
        {
            var idUsuario = int.Parse(User.FindFirst("id").Value);

            try
            {
                await _playlistService.DeletarAsync(id, idUsuario);
                return Ok("Playlist deletada com sucesso.");
            }
            catch (UnauthorizedAccessException)
            {
                return Forbid("Você não tem permissão para deletar esta playlist.");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }


    }


    [ApiController]
    [Route("api/[controller]")]
    public class ItemPlaylistController : ControllerBase
    {
        private readonly IItemPlaylistService _service;
        private readonly StreamingAPIContext _context;

        public ItemPlaylistController(IItemPlaylistService service, StreamingAPIContext context)
        {
            _service = service;
            _context = context;
        }

        [HttpPost]
        public async Task<IActionResult> Adicionar(ItemPlaylistDTO dto)
        {
            try
            {
                await _service.AdicionarAsync(dto);
                return Ok(new { message = "Conteúdo adicionado à playlist com sucesso!" });
            }
            catch (Exception ex)
            {
                return BadRequest(new
                {
                    error = ex.Message,
                    inner = ex.InnerException?.Message,
                    stack = ex.StackTrace
                });
            }
        }

        [HttpGet("{playlistId}/conteudos")]
        public async Task<IActionResult> ListarConteudosDaPlaylist(int playlistId)
        {
            var conteudos = await _context.ItemPlaylist
                .Where(ip => ip.PlaylistID == playlistId)
                .Join(_context.Conteudos,
                      ip => ip.ConteudoID,
                      c => c.Id,
                      (ip, c) => c)
                .Join(_context.Criadores,
                      c => c.CriadorID,
                      criador => criador.Id,
                      (c, criador) => new ConteudoDTO
                      {
                          Nome = c.Nome,
                          Tipo = c.Tipo,
                          Url = c.Url,
                          NomeCriador = criador.Nome // Preenchendo corretamente!
                      })
                .ToListAsync();

            return Ok(conteudos);
        }



        [HttpGet]
        public async Task<IActionResult> ListarTodos()
        {
            var lista = await _service.ListarTodosAsync();

            return Ok(lista);
        }
    }

    [ApiController]
    [Route("api/[controller]")]
    public class CurtidaController : ControllerBase
    {
        private readonly ICurtidaService _service;
        private readonly StreamingAPIContext _context;


        public CurtidaController(ICurtidaService service, StreamingAPIContext context)
        {
            _service = service;
            _context = context;
        }

        [HttpPost("curtir/{conteudoId}")]
        [Authorize]
        public async Task<IActionResult> Curtir(int conteudoId)
        {
            var userId = int.Parse(User.FindFirst("id").Value);

            try
            {
                await _service.CurtirAsync(userId, conteudoId);
                return Ok(new
                {
                    sucesso = true,
                    mensagem = "Conteúdo curtido com sucesso."
                });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new
                {
                    sucesso = false,
                    mensagem = ex.Message
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new
                {
                    sucesso = false,
                    mensagem = "Erro ao curtir o conteúdo.",
                    erro = ex.Message
                });
            }
        }
        [HttpGet("existe")]
        public async Task<ActionResult<bool>> ExisteCurtida([FromQuery] int usuarioId, [FromQuery] int conteudoId)
        {
            try
            {
                var existe = await _service.UsuarioCurtiuConteudoAsync(usuarioId, conteudoId);
                return Ok(existe);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erro interno: {ex.Message}");
            }
        }



        [HttpDelete("descurtir/{conteudoId}")]
        [Authorize]
        public async Task<IActionResult> Descurtir(int conteudoId)
        {
            var userId = int.Parse(User.FindFirst("id").Value);

            try
            {
                await _service.DescurtirAsync(userId, conteudoId);
                return Ok(new
                {
                    sucesso = true,
                    mensagem = "Curtida removida com sucesso."
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new
                {
                    sucesso = false,
                    mensagem = "Erro ao remover curtida.",
                    erro = ex.Message
                });
            }
        }

        [HttpGet("listar/{conteudoId}")]
        public async Task<IActionResult> ListarCurtidas(int conteudoId)
        {
            var lista = await _service.ListarCurtidasDoConteudoAsync(conteudoId);
            return Ok(new
            {
                totalCurtidas = lista.Count(),
                usuarios = lista
            });
        }
    

    [HttpGet("curtidos")]
        [Authorize]
        public async Task<IActionResult> ListarCurtidosDoUsuario()
        {
            var userId = int.Parse(User.FindFirst("id").Value);
            // Buscar as curtidas do usuário, incluindo o conteúdo
            var curtidos = await _context.Curtidas
                .Where(c => c.UsuarioId == userId)
                .Include(c => c.Conteudo)
                .OrderByDescending(c => c.Id) // Mais recente primeiro (opcional)
                .ToListAsync();

            var lista = curtidos.Select(c => new {
                conteudoId = c.ConteudoId,
                nome = c.Conteudo.Nome,
                tipo = c.Conteudo.Tipo,
                url = c.Conteudo.Url,
                Criador = c.Conteudo.Criador != null ? c.Conteudo.Criador.Nome : "Desconhecido"
            }).ToList();

            return Ok(new
            {
                total = lista.Count,
                conteudos = lista
            });
        }

    } 
}



    [ApiController]
    [Route("api/[controller]")]
    public class ComentarioController : ControllerBase
    {
        private readonly IComentarioService _service;

        public ComentarioController(IComentarioService service)
        {
            _service = service;
        }

        [HttpPost("comentar/{conteudoId}")]
        [Authorize]
        public async Task<IActionResult> Comentar(int conteudoId, [FromBody] ComentarioDTO dto)
        {
            var userId = int.Parse(User.FindFirst("id").Value);

            try
            {
                await _service.ComentarAsync(userId, conteudoId, dto.Texto);
                return Ok(new
                {
                    sucesso = true,
                    mensagem = "Comentado com sucesso!"
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new
                {
                    sucesso = false,
                    mensagem = "Erro ao comentar.",
                    erro = ex.Message,
                    inner = ex.InnerException?.Message,
                    stack = ex.StackTrace
                });
            }
        }

        [HttpGet("listar/{conteudoId}")]
        public async Task<IActionResult> Listar(int conteudoId)
        {
            var lista = await _service.ListarAsync(conteudoId);
            return Ok(new
            {
                totalComentarios = lista.Count(),
                comentarios = lista
            });
        }
    }


    [ApiController]
    [Route("api/[controller]")]
    public class VisualizacaoController : ControllerBase
    {
        private readonly IVisualizacaoService _service;
        private readonly StreamingAPIContext _context;

        public VisualizacaoController(IVisualizacaoService service, StreamingAPIContext context)
        {
            _service = service;
            _context = context;
        }

        [HttpPost("registrar/{conteudoId}")]
        [Authorize]
        public async Task<IActionResult> Registrar(int conteudoId)
        {
            var userId = int.Parse(User.FindFirst("id").Value);

            try
            {
                await _service.RegistrarAsync(userId, conteudoId);
                return Ok(new
                {
                    sucesso = true,
                    mensagem = "Visualização registrada com sucesso."
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new
                {
                    sucesso = false,
                    mensagem = "Erro ao registrar visualização.",
                    erro = ex.Message,
                    inner = ex.InnerException?.Message,
                    stack = ex.StackTrace
                });
            }
        }

        [HttpGet("ultimos")]
        [Authorize]
        public async Task<IActionResult> ListarDoUsuario()
        {
            var userId = int.Parse(User.FindFirst("id").Value);
            var lista = await _service.ListarPorUsuarioAsync(userId);

            return Ok(new
            {
                total = lista.Count(),
                visualizacoes = lista
            });
        }

        [HttpGet("total/{conteudoId}")]
        public async Task<IActionResult> GetTotal(int conteudoId)
        {
            var total = await _service.TotalPorConteudoAsync(conteudoId);
            return Ok(new { totalVisualizacoes = total });
        }

        [HttpGet("populares")]
        public async Task<IActionResult> ListarMaisVisualizados()
        {
            var agrupado = await _context.Visualizacoes
                .Include(v => v.Conteudo)
                    .ThenInclude(c => c.Criador)
                .GroupBy(v => v.ConteudoId)
                .ToListAsync(); // força execução no banco até aqui

            var conteudos = agrupado
                .Select(g => new
                {
                    ConteudoId = g.Key,
                    TotalVisualizacoes = g.Count(),
                    Dados = g.First().Conteudo
                })
                .OrderByDescending(g => g.TotalVisualizacoes)
                .Select(result => new
                {
                    Id = result.ConteudoId,
                    Nome = result.Dados.Nome,
                    Tipo = result.Dados.Tipo,
                    Url = result.Dados.Url,
                    Criador = result.Dados.Criador?.Nome,
                    TotalVisualizacoes = result.TotalVisualizacoes
                })
                .ToList();

            return Ok(conteudos);
        }
    }
    






