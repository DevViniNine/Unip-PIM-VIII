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

        // Exemplo mockado
        Conteudos.Add(new ConteudoDTO { Nome = "Corrida de moto" });
        Conteudos.Add(new ConteudoDTO { Nome = "Corrida de moto" });
        Conteudos.Add(new ConteudoDTO { Nome = "Corrida de moto" });
        Conteudos.Add(new ConteudoDTO { Nome = "Corrida de moto" });
        Conteudos.Add(new ConteudoDTO { Nome = "Corrida de moto" });

        videoList.ItemsSource = Conteudos;
    }
}
