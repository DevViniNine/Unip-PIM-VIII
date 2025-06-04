namespace StreamingAPI.Models
{
    public class Curtida
    {
        public int Id { get; set; }

        public int UsuarioId { get; set; }
        public Usuario Usuario { get; set; }

        public int ConteudoId { get; set; }
        public Conteudo Conteudo { get; set; }

        public DateTime DataCurtida { get; set; } = DateTime.UtcNow;
    }
}
