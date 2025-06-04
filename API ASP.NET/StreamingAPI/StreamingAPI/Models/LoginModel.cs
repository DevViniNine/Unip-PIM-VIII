using System.ComponentModel.DataAnnotations;

namespace StreamingAPI.Models
{
    public class LoginModel
    {
        [Required(ErrorMessage = "O E-mail é obrigatorio")]
        [DataType(DataType.EmailAddress)]
        public string Email { get; set; }

        [Required(ErrorMessage = "A senha é obrigatorio")]
        [DataType(DataType.Password)]
        public string Password { get; set; }
    }
}
