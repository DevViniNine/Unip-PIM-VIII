﻿// <auto-generated />
using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Metadata;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using StreamingAPI.Models;

#nullable disable

namespace StreamingAPI.Migrations
{
    [DbContext(typeof(StreamingAPIContext))]
    partial class StreamingAPIContextModelSnapshot : ModelSnapshot
    {
        protected override void BuildModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("ProductVersion", "9.0.3")
                .HasAnnotation("Relational:MaxIdentifierLength", 128);

            SqlServerModelBuilderExtensions.UseIdentityColumns(modelBuilder);

            modelBuilder.Entity("ItemPlaylist", b =>
                {
                    b.Property<int>("PlaylistID")
                        .HasColumnType("int");

                    b.Property<int>("ConteudoID")
                        .HasColumnType("int");

                    b.HasKey("PlaylistID", "ConteudoID");

                    b.HasIndex("ConteudoID");

                    b.ToTable("ItemPlaylist");
                });

            modelBuilder.Entity("StreamingAPI.Models.Conteudo", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("Id"));

                    b.Property<int>("CriadorID")
                        .HasColumnType("int");

                    b.Property<string>("Nome")
                        .HasColumnType("nvarchar(max)");

                    b.Property<string>("Tipo")
                        .HasColumnType("nvarchar(max)");

                    b.HasKey("Id");

                    b.HasIndex("CriadorID");

                    b.ToTable("Conteudo");
                });

            modelBuilder.Entity("StreamingAPI.Models.Criador", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("Id"));

                    b.Property<string>("Nome")
                        .HasColumnType("nvarchar(max)");

                    b.HasKey("Id")
                        .HasName("PK__Criador__3213E83FE6A9A3AE");

                    b.ToTable("Criador");
                });

            modelBuilder.Entity("StreamingAPI.Models.Curtida", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("Id"));

                    b.Property<int>("ConteudoId")
                        .HasColumnType("int");

                    b.Property<DateTime>("DataCurtida")
                        .HasColumnType("datetime2");

                    b.Property<int>("UsuarioId")
                        .HasColumnType("int");

                    b.HasKey("Id");

                    b.HasIndex("ConteudoId");

                    b.HasIndex("UsuarioId");

                    b.ToTable("Curtidas");
                });

            modelBuilder.Entity("StreamingAPI.Models.Playlist", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("Id"));

                    b.Property<string>("Nome")
                        .HasColumnType("nvarchar(max)");

                    b.Property<int>("UsuarioId")
                        .HasColumnType("int");

                    b.HasKey("Id");

                    b.HasIndex("UsuarioId");

                    b.ToTable("Playlist");
                });

            modelBuilder.Entity("StreamingAPI.Models.Usuario", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("Id"));

                    b.Property<int>("Admin")
                        .HasColumnType("int");

                    b.Property<string>("Email")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("nvarchar(100)");

                    b.Property<string>("Nome")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("nvarchar(100)");

                    b.Property<byte[]>("PasswordHash")
                        .IsRequired()
                        .HasColumnType("varbinary(max)");

                    b.Property<byte[]>("PasswordSalt")
                        .IsRequired()
                        .HasColumnType("varbinary(max)");

                    b.HasKey("Id")
                        .HasName("PK__Usuario__3213E83F45767D9D");

                    b.ToTable("Usuario");
                });

            modelBuilder.Entity("ItemPlaylist", b =>
                {
                    b.HasOne("StreamingAPI.Models.Conteudo", "Conteudo")
                        .WithMany("ItensPlaylist")
                        .HasForeignKey("ConteudoID")
                        .OnDelete(DeleteBehavior.NoAction)
                        .IsRequired();

                    b.HasOne("StreamingAPI.Models.Playlist", "Playlist")
                        .WithMany("ItensPlaylist")
                        .HasForeignKey("PlaylistID")
                        .OnDelete(DeleteBehavior.NoAction)
                        .IsRequired();

                    b.Navigation("Conteudo");

                    b.Navigation("Playlist");
                });

            modelBuilder.Entity("StreamingAPI.Models.Conteudo", b =>
                {
                    b.HasOne("StreamingAPI.Models.Criador", "Criador")
                        .WithMany("Conteudos")
                        .HasForeignKey("CriadorID")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Criador");
                });

            modelBuilder.Entity("StreamingAPI.Models.Curtida", b =>
                {
                    b.HasOne("StreamingAPI.Models.Conteudo", "Conteudo")
                        .WithMany()
                        .HasForeignKey("ConteudoId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("StreamingAPI.Models.Usuario", "Usuario")
                        .WithMany()
                        .HasForeignKey("UsuarioId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Conteudo");

                    b.Navigation("Usuario");
                });

            modelBuilder.Entity("StreamingAPI.Models.Playlist", b =>
                {
                    b.HasOne("StreamingAPI.Models.Usuario", "Usuario")
                        .WithMany("Playlists")
                        .HasForeignKey("UsuarioId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Usuario");
                });

            modelBuilder.Entity("StreamingAPI.Models.Conteudo", b =>
                {
                    b.Navigation("ItensPlaylist");
                });

            modelBuilder.Entity("StreamingAPI.Models.Criador", b =>
                {
                    b.Navigation("Conteudos");
                });

            modelBuilder.Entity("StreamingAPI.Models.Playlist", b =>
                {
                    b.Navigation("ItensPlaylist");
                });

            modelBuilder.Entity("StreamingAPI.Models.Usuario", b =>
                {
                    b.Navigation("Playlists");
                });
#pragma warning restore 612, 618
        }
    }
}
