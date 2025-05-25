using System.Net.Http.Headers;
using System.Text.Json;
using System.Text;
using MauiAPP.Models;

namespace MauiAPP.Services;

public class ApiService
{
    private readonly HttpClient _http = new();
    private const string BaseUrl = "http://192.168.1.11:5127";

    public async Task<string> LoginAsync(string email, string password)
    {
        var loginData = new LoginRequest { Email = email, Password = password };
        var content = new StringContent(JsonSerializer.Serialize(loginData), Encoding.UTF8, "application/json");

        var response = await _http.PostAsync($"{BaseUrl}/api/Usuario/login", content);
        if (response.IsSuccessStatusCode)
        {
            var result = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<Dictionary<string, string>>(result);
            return json?["token"];
        }
        return null;
    }

    public async Task<bool> RegistrarAsync(UsuarioDTO usuario)
    {
        var content = new StringContent(JsonSerializer.Serialize(usuario), Encoding.UTF8, "application/json");
        var response = await _http.PostAsync($"{BaseUrl}/api/Usuario/cadastrar", content);
        return response.IsSuccessStatusCode;
    }


    public async Task<List<ConteudoDTO>> ListarConteudosAsync()
    {
        var token = Preferences.Get("jwt_token", null);

        var client = new HttpClient();
        client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", token);

        var response = await client.GetAsync($"{BaseUrl}/api/Conteudo/listar");

        if (response.IsSuccessStatusCode)
        {
            var json = await response.Content.ReadAsStringAsync();
            return JsonSerializer.Deserialize<List<ConteudoDTO>>(json);
        }
        else
        {
            var erro = await response.Content.ReadAsStringAsync();
            Console.WriteLine($"Erro: {erro}");
        }

        return new List<ConteudoDTO>();
    }
}
