using StreamingAPI.Models;

public class Visualizacao
{
    public int Id { get; set; }

    public int UsuarioId { get; set; }
    public Usuario Usuario { get; set; }

    public int ConteudoId { get; set; }
    public Conteudo Conteudo { get; set; }

    public DateTime DataVisualizacao { get; set; } = DateTime.UtcNow;
}