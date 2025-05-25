using MauiAPP.Services;
using System.Text.Json;
using System.Text;
using System.Net.Http.Headers;

namespace MauiAPP.Views;

public partial class LoginPage : ContentPage
{
    private readonly ApiService _api = new();

    public LoginPage()
    {
        InitializeComponent();
    }

    private async void OnLoginClicked(object sender, EventArgs e)
    {
        msgLabel.Text = "";
        var email = emailEntry.Text;
        var password = passwordEntry.Text;

        if (string.IsNullOrWhiteSpace(email) || string.IsNullOrWhiteSpace(password))
        {
            msgLabel.Text = "Preencha todos os campos.";
            return;
        }

        var token = await _api.LoginAsync(email, password);

        if (!string.IsNullOrEmpty(token))
        {
            Preferences.Set("jwt_token", token); // Salva localmente
            await DisplayAlert("Sucesso", "Login realizado!", "OK");
            await Navigation.PushAsync(new DashboardPage(token));
        }
        else
        {
            msgLabel.Text = "Email ou senha inválidos.";
        }
    }

    // RECUPERAR O TOKEN EM QUALQUER LUGAR COM  :  var token = Preferences.Get("jwt_token", null);

    // QUANDO USUARIO DESLOGAR REMOVAR O TOKEM COM : Preferences.Remove("jwt_token");

    private async void OnRegisterClicked(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new RegisterPage());
    }

    private void OnExitClicked(object sender, EventArgs e)
    {
        System.Diagnostics.Process.GetCurrentProcess().CloseMainWindow();
    }
}
