using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;
using Microsoft.Maui.Controls;
//using static Android.Renderscripts.FileA3D;

namespace MauiInterface.Views
{
    public partial class CadastroConteudoPage : ContentPage
    {
        private readonly string apiBaseUrl = "http://192.168.1.11:5127/";
        private readonly string jwtToken;
        private readonly int criadorId;

        public CadastroConteudoPage(string jwtToken, int criadorId)
        {
            InitializeComponent();
            this.jwtToken = jwtToken;
            this.criadorId = criadorId;
        }

        private async void BtnCadastrar_Clicked(object sender, EventArgs e)
        {
            var nome = entryNome.Text?.Trim();
            var tipo = entryTipo.Text?.Trim();
            var url = entryUrl.Text?.Trim();

            if (string.IsNullOrWhiteSpace(nome) || string.IsNullOrWhiteSpace(tipo) || string.IsNullOrWhiteSpace(url))
            {
                await DisplayAlert("Aviso", "Preencha todos os campos!", "OK");
                return;
            }

            var conteudo = new
            {
                Nome = nome,
                Tipo = tipo,
                Url = url,
                CriadorId = criadorId
            };

            try
            {
                using var client = new HttpClient();
                client.BaseAddress = new Uri(apiBaseUrl);
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", jwtToken);

                var json = JsonSerializer.Serialize(conteudo);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var response = await client.PostAsync("api/Conteudo/cadastrar", content);

                if (response.IsSuccessStatusCode)
                {
                    await DisplayAlert("Sucesso", "Conteúdo cadastrado!", "OK");
                    await Navigation.PopAsync();
                }
                else
                {
                    await DisplayAlert("Erro", "Falha ao cadastrar conteúdo.", "OK");
                }
            }
            catch
            {
                await DisplayAlert("Erro", "Falha na conexão.", "OK");
            }
        }

        private async void BtnCancelar_Clicked(object sender, EventArgs e)
        {
            await Navigation.PopAsync();
        }
    }
}
