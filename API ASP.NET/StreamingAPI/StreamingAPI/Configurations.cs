using Microsoft.EntityFrameworkCore.Metadata.Builders;
using Microsoft.EntityFrameworkCore;
using StreamingAPI.Models;

namespace StreamingAPI
{
    public interface Configurations
    {

        public class UsuarioConfiguration : IEntityTypeConfiguration<Usuario>
        {
            public void Configure(EntityTypeBuilder<Usuario> builder)
            {
                builder.HasKey(x => x.Id);
                builder.Property(x => x.Email).HasMaxLength(100).IsRequired();
                builder.Property(x => x.Nome).HasMaxLength(100).IsRequired();
            }
        }
    }
}
