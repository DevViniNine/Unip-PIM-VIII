using MauiAPP.Models;
using MauiAPP.Services;
using System.Collections.ObjectModel;

namespace MauiAPP.Views;

public partial class DashboardPage : ContentPage
{
    public ObservableCollection<ConteudoDTO> Conteudos { get; set; } = new();

    public DashboardPage(string token)
    {
        InitializeComponent();
        BindingContext = this;

        Conteudos.Add(new ConteudoDTO { Nome = "Corrida de moto" });
        Conteudos.Add(new ConteudoDTO { Nome = "Viagem de moto" });
        Conteudos.Add(new ConteudoDTO { Nome = "teste de video 1" });
        Conteudos.Add(new ConteudoDTO { Nome = "teste de video 2" });
        Conteudos.Add(new ConteudoDTO { Nome = "teste de video 3" });

        videoList.ItemsSource = Conteudos;
    }
}
