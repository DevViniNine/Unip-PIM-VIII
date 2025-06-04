using MauiAPP.Services;
using MauiAPP.Models;

namespace MauiAPP.Views;

public partial class RegisterPage : ContentPage
{
    private readonly ApiService _api = new();

    public RegisterPage()
    {
        InitializeComponent();
    }

    private async void OnRegisterClicked(object sender, EventArgs e)
    {
        msgLabel.Text = "";

        var usuario = new UsuarioDTO
        {
            Nome = nomeEntry.Text,
            Email = emailEntry.Text,
            Password = passwordEntry.Text
        };

        if (string.IsNullOrWhiteSpace(usuario.Nome) ||
            string.IsNullOrWhiteSpace(usuario.Email) ||
            string.IsNullOrWhiteSpace(usuario.Password))
        {
            msgLabel.Text = "Todos os campos são obrigatórios.";
            return;
        }

        var sucesso = await _api.RegistrarAsync(usuario);
        if (sucesso)
        {
            await DisplayAlert("Sucesso", "Cadastro realizado!", "OK");
            await Navigation.PopAsync();
        }
        else
        {
            msgLabel.Text = "Erro ao registrar.";
        }
    }
}
