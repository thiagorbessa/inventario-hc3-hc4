# 🖥️ Inventário Unificado de TI (HC3 & HC4)

Sistema de gerenciamento e auditoria de inventários de equipamentos de TI unificado para as unidades HC3 e HC4.

## 🚀 Tecnologias Utilizadas
- **Backend:** Java 17, Spring Boot, Spring Data JPA, Spring Security
- **Frontend:** Thymeleaf, Bootstrap 5, FontAwesome
- **Banco de Dados:** MySQL 8.0 (Containerizado via Docker Compose)
- **Documentação & Observabilidade:** Springdoc OpenAPI (Swagger UI)
- **Persistência & Logs:** Auditoria completa de ações (CRUD / Importações) via `LogSistema`

## ✨ Funcionalidades Principais
- 📊 **Dashboard Interativo:** Visualização de totalizadores com tooltip individual por unidade (HC3 x HC4), Health Check de servidor (Uptime e Memória RAM) e atalho dinâmico para filtragem de base.
- 🔍 **Filtragem Avançada:** Sistema de busca híbrido (Busca Geral + Painel de Filtro Detalhado multicritério por Unidade, Netname, Modelo, Local e Andar).
- ↕️ **Ordenação em Tempo Real:** Ordenação crescente/decrescente (A-Z/Z-A) direto nos cabeçalhos da tabela com suporte a *sticky header*.
- 📂 **Importação/Exportação:** Processamento de planilhas Excel (.xlsx) e colagem direta de texto do Excel com mesclagem inteligente de dados; exportação respeitando estritamente o estado filtrado na tela.
- 🔐 **Segurança e Auditoria:** Controle de acesso por perfil (`ADMIN`, `MASTER`), gestão de usuários e página de registros de logs exclusiva para perfil `MASTER`.
