using StreamingAPI.Models;
using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

[Table("ItemPlaylist")]
public class ItemPlaylist
{
    public int PlaylistID { get; set; }
    public int ConteudoID { get; set; }

   
    public Playlist Playlist { get; set; }
    public Conteudo Conteudo { get; set; }
}