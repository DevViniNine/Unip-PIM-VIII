namespace StreamingAPI.Models
{
    public class ConteudoUploadDTO
    {
        public string Nome { get; set; }
        public string Tipo { get; set; } // Video, Musica, Podcast, Imagem
        public IFormFile Arquivo { get; set; }
    }

}
