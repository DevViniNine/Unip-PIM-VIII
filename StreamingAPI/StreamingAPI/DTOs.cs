using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace StreamingAPI
{
    public class DTOs
    {
        public class UsuarioDTO
        {
            public int Id { get; set; }

            [Required(ErrorMessage = "O Nome é obrigatorio.")]
            [MaxLength(100, ErrorMessage = "O Nome dever ter, no máximo 100 caracteres.")]
            public string Nome { get; set; }

            [Required(ErrorMessage = "O Email é obrigatorio.")]
            [MaxLength(100, ErrorMessage = "O Email dever ter, no máximo 100 caracteres.")]
            public string Email { get; set; }

            // Remover o [Required] para permitir vir nulo na atualização!
           // [MaxLength(100, ErrorMessage = "A Senha dever ter, no máximo 100 caracteres.")]
            //[MinLength(8, ErrorMessage = "A Senha dever ter, no mínimo, 8 caracteres.")]
            [NotMapped]
            
            public string Password { get; set; }

            [NotMapped]
            public int Admin { get; set; }
        }

        public class ConteudoDTO
        {
            public int Id { get; set; }
            public string Nome { get; set; }
            public string Tipo { get; set; }
            public string Url { get; set; }
            public string NomeCriador { get; set; }
        }
        public class ConteudoUpdateDTO
        {
            public int Id { get; set; }
            public string Nome { get; set; }
            public string Tipo { get; set; }
            public string Url { get; set; } 
            public string NomeCriador { get; set; }
        }

        public class CriadorDTO
        {
            public string Nome { get; set; }
        }

        public class PlaylistDTO
        {
            
            public int Id { get; set; }
            public string Nome { get; set; }

            public int UsuarioId { get; set; }

        }

        public class ItemPlaylistDTO
        {
            public int PlaylistID { get; set; }
            public int ConteudoID { get; set; }
        }

        public class CurtidaDTO
        {
            public int UsuarioId { get; set; }
            public int ConteudoId { get; set; }
        }

        public class ComentarioDTO
        {
            public int UsuarioId { get; set; }
            public int ConteudoId { get; set; }

            public string UsuarioNome { get; set; }

            public string Texto { get; set; }

            public DateTime DataComentario { get; set; }
        }

        public class VisualizacaoDTO
        {
            public int UsuarioId { get; set; }
            public int ConteudoId { get; set; }
            public string NomeConteudo { get; set; }
            public string TipoConteudo { get; set; }
            public DateTime DataVisualizacao { get; set; }
        }
    }
}
