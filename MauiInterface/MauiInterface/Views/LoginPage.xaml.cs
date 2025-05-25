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

            // Buscar os dados do usu�rio logado pelo email
            var usuario = await api.BuscarUsuarioPorEmail(login.Email);

            if (usuario != null)
            {
                string nomeUsuario = usuario.Nome;
                int usuarioId = usuario.Id;

                // Troca a p�gina principal para a Dashboard passando dados
                Application.Current.MainPage = new DashboardPage(nomeUsuario, usuarioId, token);
            }
            else
            {
                await DisplayAlert("Erro", "N�o foi poss�vel obter os dados do usu�rio!", "OK");
            }
        }
        else
        {
            MsgLabel.Text = "Email ou senha inv�lidos!";
        }
    }

    private async void OnCadastrarClicked(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new CadastroPage());
    }
}
