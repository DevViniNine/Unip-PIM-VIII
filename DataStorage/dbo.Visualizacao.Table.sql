USE [StreamingAPI]
GO
/****** Object:  Table [dbo].[Visualizacao]    Script Date: 16/05/2025 19:54:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Visualizacao](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[UsuarioId] [int] NOT NULL,
	[ConteudoId] [int] NOT NULL,
	[DataVisualizacao] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Visualizacao] ADD  DEFAULT (getdate()) FOR [DataVisualizacao]
GO
ALTER TABLE [dbo].[Visualizacao]  WITH CHECK ADD  CONSTRAINT [FK_Visualizacao_Conteudo] FOREIGN KEY([ConteudoId])
REFERENCES [dbo].[Conteudo] ([id])
GO
ALTER TABLE [dbo].[Visualizacao] CHECK CONSTRAINT [FK_Visualizacao_Conteudo]
GO
ALTER TABLE [dbo].[Visualizacao]  WITH CHECK ADD  CONSTRAINT [FK_Visualizacao_Usuario] FOREIGN KEY([UsuarioId])
REFERENCES [dbo].[Usuario] ([id])
GO
ALTER TABLE [dbo].[Visualizacao] CHECK CONSTRAINT [FK_Visualizacao_Usuario]
GO
