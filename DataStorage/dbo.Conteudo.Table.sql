USE [StreamingAPI]
GO
/****** Object:  Table [dbo].[Conteudo]    Script Date: 16/05/2025 19:54:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Conteudo](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[Nome] [varchar](50) NOT NULL,
	[Tipo] [varchar](50) NOT NULL,
	[CriadorID] [int] NOT NULL,
	[url] [varchar](300) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Conteudo]  WITH CHECK ADD  CONSTRAINT [FK_Conteudo_Criador] FOREIGN KEY([CriadorID])
REFERENCES [dbo].[Criador] ([id])
GO
ALTER TABLE [dbo].[Conteudo] CHECK CONSTRAINT [FK_Conteudo_Criador]
GO
