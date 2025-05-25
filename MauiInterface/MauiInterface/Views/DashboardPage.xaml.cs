

using System.Collections.ObjectModel;
using System.Net.Http.Headers;
using System.Text.Json;
using MauiInterface.Models;

namespace MauiInterface.Views
{
    public partial class DashboardPage : ContentPage
    {
        private readonly string apiBaseUrl = "http://192.168.1.11:5127/"; // Troque para seu IP de backend se necessário
        private readonly string jwtToken;
        private readonly int criadorId;
        private readonly string nomeUsuario;

        public ObservableCollection<ConteudoDTO> Conteudos { get; set; } = new();

        public DashboardPage(string nomeUsuario, int criadorId, string token)
        {
            InitializeComponent();

            this.nomeUsuario = nomeUsuario;
            this.criadorId = criadorId;
            this.jwtToken = token;

            lblBemVindo.Text = $"Olá, {nomeUsuario}!";

            conteudosCollection.ItemsSource = Conteudos;
        }

        protected override void OnAppearing()
        {
            base.OnAppearing();
            CarregarConteudos();
        }

        private async void CarregarConteudos()
        {
            try
            {
                using var client = new HttpClient();
                client.BaseAddress = new Uri(apiBaseUrl);
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", jwtToken);

                // Use seu endpoint para pegar os conteúdos desse criador.
                var response = await client.GetAsync($"api/Conteudo/porCriador/{criadorId}");
                if (response.IsSuccessStatusCode)
                {
                    var json = await response.Content.ReadAsStringAsync();
                    var conteudos = JsonSerializer.Deserialize<List<ConteudoDTO>>(json, new JsonSerializerOptions { PropertyNameCaseInsensitive = true });
                    Conteudos.Clear();
                    foreach (var c in conteudos)
                        Conteudos.Add(c);
                }
                else
                {
                    await DisplayAlert("Erro", "Erro ao carregar conteúdos.", "OK");
                }
            }
            catch
            {
                await DisplayAlert("Erro", "Não foi possível conectar ao servidor.", "OK");
            }
        }

        private async void BtnAdicionarConteudo_Clicked(object sender, EventArgs e)
        {
            // Navegar para a tela de cadastro de conteúdo (você vai criar depois)
            await Navigation.PushAsync(new CadastroPage(jwtToken, criadorId));
        }

        private async void BtnSair_Clicked(object sender, EventArgs e)
        {
            // Implementar lógica de logout (limpar prefs, voltar para login)
            Application.Current.MainPage = new NavigationPage(new LoginPage());
        }
    }

    // Modelo rápido só para exemplo
    public class ConteudoDTO
    {
        public int Id { get; set; }
        public string Nome { get; set; }
        public string Tipo { get; set; }
        public string Url { get; set; }
        public int CriadorId { get; set; }
    }
}
