using MauiInterface.Models;
using System.Net.Http.Json;

namespace MauiInterface.Services;

public class ApiService
{
    private readonly HttpClient _httpClient;

    public ApiService()
    {
        _httpClient = new HttpClient
        {
            BaseAddress = new Uri("http://192.168.1.11:5127/") // Coloque o IP do seu backend
        };
    }

    public async Task<string> Login(LoginRequest login)
    {
        var response = await _httpClient.PostAsJsonAsync("api/Usuario/login", login);
        if (response.IsSuccessStatusCode)
        {
            var content = await response.Content.ReadFromJsonAsync<LoginResponse>();
            return content.Token; // Ajuste para o seu modelo
        }
        return null;
    }

    public async Task<UsuarioDTO> CadastrarUsuario(string email)
    {
        var response = await _httpClient.GetAsync($"api/Usuario/email/{email}");
        if (response.IsSuccessStatusCode)
        {
            return await response.Content.ReadFromJsonAsync<UsuarioDTO>();
        }
        return null;
    }
}
