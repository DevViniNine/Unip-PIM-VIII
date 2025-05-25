using MauiInterface.Models;
using MauiInterface.Services;
using MauiInterface.Views;

namespace MauiInterface.Views;

public partial class LoginPage : ContentPage
{
    private ApiService api = new ApiService();

    public LoginPage()
    {
        InitializeComponent();
    }

    private async void OnLoginClicked(object sender, EventArgs e)
    {
        MsgLabel.Text = "";
        var login = new LoginRequest
        {
            Email = EmailEntry.Text,
            Password = PasswordEntry.Text
        };

        // Login retorna o token JWT
        var token = await api.Login(login);

        if (!string.IsNullOrEmpty(token))
        {
            Preferences.Set("jwt_token", token); // Salva token local
            await DisplayAlert("Sucesso", "Login realizado!", "OK");

            // Buscar os dados do usuário logado pelo email
            var usuario = await api.BuscarUsuarioPorEmail(login.Email);

            if (usuario != null)
            {
                string nomeUsuario = usuario.Nome;
                int usuarioId = usuario.Id;

                // Troca a página principal para a Dashboard passando dados
                Application.Current.MainPage = new DashboardPage(nomeUsuario, usuarioId, token);
            }
            else
            {
                await DisplayAlert("Erro", "Não foi possível obter os dados do usuário!", "OK");
            }
        }
        else
        {
            MsgLabel.Text = "Email ou senha inválidos!";
        }
    }

    private async void OnCadastrarClicked(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new CadastroPage());
    }
}
