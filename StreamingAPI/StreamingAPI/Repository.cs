using static StreamingAPI.Repository;
using StreamingAPI.Models;
using static StreamingAPI.Interfaces;
using Microsoft.EntityFrameworkCore;
using static StreamingAPI.DTOs;
using System;

namespace StreamingAPI
{
    public class Repository
    {
        public class UsuarioRepository : IUsuarioRepository
        {
            private readonly StreamingAPIContext _context;
            public UsuarioRepository(StreamingAPIContext context)
            {
                _context = context;
            }
            public async Task<Usuario> Alterar(Usuario usuario)
            {
                _context.Usuario.Update(usuario);
                await _context.SaveChangesAsync();
                return usuario;
            }
            public async Task<Usuario> Excluir(int id)
            {
                var usuario = await _context.Usuario.FindAsync(id);
                if (usuario != null)
                {
                    _context.Usuario.Remove(usuario);
                    await _context.SaveChangesAsync();
                    return usuario;
                }

                return null;
            }
            public async Task<Usuario> Incluir(Usuario usuario)
            {
                _context.Usuario.Add(usuario);
                await _context.SaveChangesAsync();
                return usuario;
            }

            public async Task<Usuario> SelecionarAsync(int id)
            {
                return await _context.Usuario.FindAsync(id);

            }
            public async Task<IEnumerable<Usuario>> SelecionarTodosAsync()
            {
                return await _context.Usuario.ToListAsync();
            }
            public async Task<Usuario> SelecionarPorEmailAsync(string email)

            {
                return await _context.Usuario.FirstOrDefaultAsync(u => u.Email == email);

            }
            public async Task<Usuario> ObterPorIdAsync(int id)
            {
                return await _context.Usuario.FirstOrDefaultAsync(u => u.Id == id);
            }

        }
        public class PlaylistRepository : IPlaylistRepository
        {
            private readonly StreamingAPIContext _context;

            public PlaylistRepository(StreamingAPIContext context)
            {
                _context = context;
            }
            public async Task<Playlist> IncluirAsync(Playlist playlist)
            {
                _context.Playlists.Add(playlist);
                await _context.SaveChangesAsync();
                return playlist;
            }
            public async Task<IEnumerable<Playlist>> ListarPorUsuarioAsync(int usuarioId)
            {
                return await _context.Playlists
                    .Where(p => p.UsuarioId == usuarioId)
                    .ToListAsync();
            }
        }
        public class ItemPlaylistRepository : IItemPlaylistRepository
        {
            private readonly StreamingAPIContext _context;

            public ItemPlaylistRepository(StreamingAPIContext context)
            {
                _context = context;
            }
            public async Task AdicionarAsync(ItemPlaylist entidade)
            {
                _context.ItemPlaylist.Add(entidade);
                await _context.SaveChangesAsync();
            }
            public async Task<IEnumerable<ItemPlaylist>> ListarTodosAsync()
            {
                return await _context.ItemPlaylist.ToListAsync();
            }
            public async Task<bool> ExisteAsync(int playlistId, int conteudoId)
            {
                return await _context.ItemPlaylist
                    .AnyAsync(ip => ip.PlaylistID == playlistId && ip.ConteudoID == conteudoId);
            }
        }
        public class CurtidaRepository : ICurtidaRepository
        {
            private readonly StreamingAPIContext _context;

            public CurtidaRepository(StreamingAPIContext context)
            {
                _context = context;
            }

            public async Task AdicionarAsync(Curtida curtida)
            {
                _context.Curtidas.Add(curtida);
                await _context.SaveChangesAsync();
            }
            public async Task<bool> UsuarioCurtiuConteudoAsync(int usuarioId, int conteudoId)
            {
                return await _context.Curtidas
                    .AnyAsync(c => c.UsuarioId == usuarioId && c.ConteudoId == conteudoId);
            }
            public async Task RemoverAsync(int usuarioId, int conteudoId)
            {
                var curtida = await _context.Curtidas
                    .FirstOrDefaultAsync(c => c.UsuarioId == usuarioId && c.ConteudoId == conteudoId);

                if (curtida != null)
                {
                    _context.Curtidas.Remove(curtida);
                    await _context.SaveChangesAsync();
                }
            }

            public async Task<bool> ExisteAsync(int usuarioId, int conteudoId)
            {
                return await _context.Curtidas
                    .AnyAsync(c => c.UsuarioId == usuarioId && c.ConteudoId == conteudoId);
            }

            public async Task<IEnumerable<Curtida>> ListarPorConteudoAsync(int conteudoId)
            {
                return await _context.Curtidas
                    .Where(c => c.ConteudoId == conteudoId)
                    .ToListAsync();
            }
        }


        public class ComentarioRepository : IComentarioRepository
        {
            private readonly StreamingAPIContext _context;

            public ComentarioRepository(StreamingAPIContext context)
            {
                _context = context;
            }

            public async Task AdicionarAsync(Comentario comentario)
            {
                _context.Comentarios.Add(comentario);
                await _context.SaveChangesAsync();
            }

            public async Task<IEnumerable<Comentario>> ListarPorConteudoAsync(int conteudoId)
            {
                return await _context.Comentarios
                    .Where(c => c.ConteudoId == conteudoId)
                    .Include(c => c.Usuario)
                    .OrderByDescending(c => c.DataComentario)
                    .ToListAsync();
            }
        }


        public class VisualizacaoRepository : IVisualizacaoRepository
        {
            private readonly StreamingAPIContext _context;

            public VisualizacaoRepository(StreamingAPIContext context)
            {
                _context = context;
            }
            public async Task<int> ContarPorConteudoAsync(int conteudoId)
            {
                return await _context.Visualizacoes
                    .CountAsync(v => v.ConteudoId == conteudoId);
            }

            public async Task AdicionarAsync(Visualizacao visualizacao)
            {
                _context.Visualizacoes.Add(visualizacao);
                await _context.SaveChangesAsync();
            }

            public async Task<IEnumerable<Visualizacao>> ListarPorUsuarioAsync(int usuarioId)
            {
                return await _context.Visualizacoes
                    .Where(v => v.UsuarioId == usuarioId)
                    .Include(v => v.Conteudo) 
                    .OrderByDescending(v => v.DataVisualizacao)
                    .ToListAsync();
            }

            public async Task<IEnumerable<Visualizacao>> ListarPorConteudoAsync(int conteudoId)
            {
                return await _context.Visualizacoes
                    .Where(v => v.ConteudoId == conteudoId)
                    .Include(v => v.Usuario)
                    .ToListAsync();
            }
        }
    }
}