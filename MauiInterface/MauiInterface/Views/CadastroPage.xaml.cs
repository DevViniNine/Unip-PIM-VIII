using MauiInterface.Models;
using MauiInterface.Services;

namespace MauiInterface.Views;


public partial class CadastroPage : ContentPage
{
    private ApiService api = new ApiService();
    private string jwtToken;
    private int criadorId;

    public CadastroPage()
    {
        InitializeComponent();
    }

    public CadastroPage(string jwtToken, int criadorId)
    {
        this.jwtToken = jwtToken;
        this.criadorId = criadorId;
    }

    private async void OnCadastrarClicked(object sender, EventArgs e)
    {
        MsgLabel.Text = "";
        var user = new UsuarioDTO
        {
            Nome = NomeEntry.Text,
            Email = EmailEntry.Text,
            Password = PasswordEntry.Text,
            Admin = 0 // Sempre comum para cadastro aberto
        };

        bool ok = await api.CadastrarUsuario(user);
        if (ok)
        {
            await DisplayAlert("Sucesso", "Cadastro realizado!", "OK");
            await Navigation.PopAsync(); // Volta para tela de login
        }
        else
        {
            MsgLabel.Text = "Erro ao cadastrar. Tente outro email!";
        }
    }
}
