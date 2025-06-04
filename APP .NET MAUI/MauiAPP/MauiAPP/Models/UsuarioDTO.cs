using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MauiAPP.Models;

public class UsuarioDTO
{
    public int Id { get; set; } = 0;
    public string Nome { get; set; }
    public string Email { get; set; }
    public string Password { get; set; }
    public int Admin => 0;
}
