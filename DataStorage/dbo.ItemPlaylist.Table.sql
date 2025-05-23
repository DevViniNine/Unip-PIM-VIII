USE [StreamingAPI]
GO
/****** Object:  Table [dbo].[ItemPlaylist]    Script Date: 16/05/2025 19:54:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ItemPlaylist](
	[PlaylistID] [int] NOT NULL,
	[ConteudoID] [int] NOT NULL
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[ItemPlaylist]  WITH CHECK ADD  CONSTRAINT [FK_ItemPlaylist_Conteudo] FOREIGN KEY([ConteudoID])
REFERENCES [dbo].[Conteudo] ([id])
GO
ALTER TABLE [dbo].[ItemPlaylist] CHECK CONSTRAINT [FK_ItemPlaylist_Conteudo]
GO
ALTER TABLE [dbo].[ItemPlaylist]  WITH CHECK ADD  CONSTRAINT [FK_ItemPlaylist_Playlist] FOREIGN KEY([PlaylistID])
REFERENCES [dbo].[Playlist] ([id])
GO
ALTER TABLE [dbo].[ItemPlaylist] CHECK CONSTRAINT [FK_ItemPlaylist_Playlist]
GO
