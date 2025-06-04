using StreamingAPI.Models;

public class Comentario
{
    public int Id { get; set; }

    public int UsuarioId { get; set; }
    public Usuario Usuario { get; set; }

    public int ConteudoId { get; set; }
    public Conteudo Conteudo { get; set; }

    public string Texto { get; set; }

    public DateTime DataComentario { get; set; } = DateTime.UtcNow;
}