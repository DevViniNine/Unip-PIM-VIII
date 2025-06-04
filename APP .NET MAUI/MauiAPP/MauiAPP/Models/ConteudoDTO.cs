using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MauiAPP.Models;

public class ConteudoDTO
{
    public int Id { get; set; }
    public string Nome { get; set; }
    public string Tipo { get; set; }
    public string Url { get; set; }
    public string NomeCriador { get; set; }
}
