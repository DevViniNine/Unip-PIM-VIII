using Microsoft.OpenApi.Models;
using Swashbuckle.AspNetCore.SwaggerGen;

public class SwaggerFileOperationFilter : IOperationFilter
{
    public void Apply(OpenApiOperation operation, OperationFilterContext context)
    {
        if (operation.RequestBody == null ||
            !context.ApiDescription.HttpMethod.Equals("POST", StringComparison.OrdinalIgnoreCase))
            return;

        var isUpload = context.ApiDescription.ParameterDescriptions
            .Any(p => p.Type == typeof(IFormFile));
        if (!isUpload) return;

        operation.RequestBody.Content["multipart/form-data"] = new OpenApiMediaType
        {
            Schema = new OpenApiSchema
            {
                Type = "object",
                Properties = new Dictionary<string, OpenApiSchema>
                {
                    ["arquivo"] = new OpenApiSchema { Type = "string", Format = "binary" },
                    ["nome"] = new OpenApiSchema { Type = "string" },
                    ["tipo"] = new OpenApiSchema { Type = "string" }
                },
                Required = new HashSet<string> { "arquivo", "nome", "tipo" }
            }
        };
    }
}
