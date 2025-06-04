using AutoMapper;
using static StreamingAPI.DTOs;
using StreamingAPI.Models;

namespace StreamingAPI
{
    public class EntitiesToDTOMappingProfile : Profile
    {
        public EntitiesToDTOMappingProfile()
        {


            CreateMap<ItemPlaylistDTO, ItemPlaylist>()
                .ForMember(dest => dest.Playlist, opt => opt.Ignore())
                .ForMember(dest => dest.Conteudo, opt => opt.Ignore());

            CreateMap<ItemPlaylist, ItemPlaylistDTO>();




            CreateMap<Playlist, PlaylistDTO>().ReverseMap();

            CreateMap<Curtida, CurtidaDTO>().ReverseMap();


            CreateMap<Usuario, UsuarioDTO>()
                .ForMember(dest => dest.Nome, opt => opt.MapFrom(src => src.Nome))
                .ReverseMap()
                .ForPath(src => src.PasswordHash, opt => opt.Ignore())
                .ForPath(src => src.PasswordSalt, opt => opt.Ignore());
        }
    }
}
