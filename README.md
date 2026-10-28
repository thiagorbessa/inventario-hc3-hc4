# 🖥️ Inventário Unificado de TI (HC3 & HC4)

Sistema de gerenciamento e auditoria de inventários de equipamentos de TI unificado para as unidades **HC3** e **HC4**.

---

## 🚀 Tecnologias Utilizadas

* **Backend:** Java 17, Spring Boot, Spring Data JPA, Spring Security
* **Frontend:** Thymeleaf, Bootstrap 5, FontAwesome
* **Banco de Dados:** MySQL 8.0 (Containerizado via Docker Compose)
* **Documentação & Observabilidade:** Springdoc OpenAPI (Swagger UI)
* **Persistência & Logs:** Auditoria completa de ações (CRUD / Importações) via `LogSistema`

---

## ✨ Funcionalidades Principais

* 📊 **Dashboard Interativo:** Visualização de totalizadores com tooltip individual por unidade (HC3 x HC4), Health Check de servidor (Uptime e Memória RAM) e atalho dinâmico para filtragem de base.
* 🔍 **Filtragem Avançada:** Sistema de busca híbrido (Busca Geral + Painel de Filtro Detalhado multicritério por Unidade, Netname, Modelo, Local e Andar).
* ↕️ **Ordenação em Tempo Real:** Ordenação crescente/decrescente (A-Z/Z-A) direto nos cabeçalhos da tabela com suporte a *sticky header*.
* 📂 **Importação/Exportação:** Processamento de planilhas Excel (`.xlsx`) e colagem direta de texto do Excel com mesclagem inteligente de dados; exportação respeitando estritamente o estado filtrado na tela.
* 🔐 **Segurança e Auditoria:** Controle de acesso por perfil (`ADMIN`, `MASTER`), gestão de usuários e página de registros de logs exclusiva para perfil `MASTER`.

---

## 🛠️ Como Iniciar o Sistema (Passo a Passo)

### 📌 Pré-requisitos

Antes de iniciar, certifique-se de ter instalado em sua máquina:
1. **[Docker Desktop](https://www.docker.com/products/docker-desktop/)** (aberto e com o status *Engine Running* em verde).
2. **Git** (para clonar e atualizar o repositório).

---
```powershell
### 1️⃣ Clonar o Repositório

Abra o terminal (PowerShell ou Prompt de Comando) e execute:


git clone [https://github.com/thiagorbessa/inventario-hc3-hc4.git](https://github.com/thiagorbessa/inventario-hc3-hc4.git)
cd inventario-hc3-hc4

###2️⃣ Configurar as Variáveis de Ambiente (.env)
O projeto utiliza um arquivo .env para manter as credenciais do banco de dados seguras e fora do controle de versão.

Na raiz do projeto, copie o arquivo de exemplo .env.example para .env:
PowerShell
copy .env.example .env

(Opcional) Abra o arquivo .env gerado e defina suas credenciais locais:

Snippet de código
MYSQL_ROOT_PASSWORD=sua_senha_root_aqui
MYSQL_DATABASE=inventariodb
MYSQL_USER=inventario_user
MYSQL_PASSWORD=sua_senha_usuario_aqui

###3️⃣ Compilar o Pacote Executável (.jar)
Para que o container Java execute a versão mais recente da aplicação:

PowerShell
.\mvnw clean package -DskipTests

###4️⃣ Executar a Aplicação
🟢 Opção A: Execução Automática via Script (iniciar-sistema.bat)
Na pasta do projeto, dê duplo clique no arquivo iniciar-sistema.bat (ou execute no terminal):

DOS
.\iniciar-sistema.bat
O script realiza automaticamente:

Inicia o container do banco de dados MySQL no Docker.

Sobe a aplicação Spring Boot em um container Java 17 isolado.

Aguarda os serviços inicializarem.

Abre o navegador padrão no endereço http://localhost:8080.

🐳 Opção B: Execução via Terminal Docker Direct
Se preferir subir manualmente pelo Docker Compose:

PowerShell
docker-compose up -d --build
Após a inicialização, acesse http://localhost:8080 no seu navegador.

###🌐 Acesso Remoto e Externo via ngrok
Caso precise liberar o acesso temporário ao sistema pela internet (para testes em celulares ou acesso fora da rede local) sem abrir portas no roteador, utilize o ngrok.

1. Configuração Inicial do ngrok
Baixe o executável em ngrok.com/download e extraia na raiz do projeto.

Crie uma conta gratuita no ngrok e autentique o seu token no terminal:

PowerShell
.\ngrok config add-authtoken SEU_AUTHTOKEN_AQUI
2. Iniciar o Túnel Público
Com a aplicação rodando no Docker na porta 8080, execute:

PowerShell
.\ngrok http 8080
O ngrok exibirá a URL pública gerada no terminal:

Plaintext
Forwarding     [https://xxxx-xxx-xxx.ngrok-free.app](https://xxxx-xxx-xxx.ngrok-free.app) -> http://localhost:8080
Copie a URL gerada (https://...ngrok-free.app) e compartilhe para acesso externo.

Ao acessar pela primeira vez, clique no botão "Visit Site" na tela de aviso do ngrok para carregar o sistema.

🌐 Acesso Remoto e Externo via ngrokO ngrok permite gerar um link público temporário (https://...ngrok-free.app) para acessar o sistema de qualquer lugar pela internet ou por dispositivos móveis (como celulares).

1. Instalação do ngrokVia Microsoft Store (Recomendado): Procure por ngrok na Microsoft Store e instale.
Ele será adicionado automaticamente às variáveis de ambiente (PATH) do Windows.
Via Download direto: Baixe em ngrok.com/download e extraia na pasta do projeto.

2. Autenticação (Apenas na primeira vez)Crie uma conta gratuita no site do ngrok, copie o seu token e execute no PowerShell/CMD:
DOSngrok config add-authtoken SEU_AUTHTOKEN_AQUI

3. Iniciar o Túnel RemotoCom o Docker Desktop rodando com o sistema na porta 8080, abra o PowerShell ou CMD e digite:
DOSngrok http 8080
Atenção: O ngrok é uma ferramenta de linha de comando (CLI). Não dê dois cliques no ngrok.exe, execute-o sempre pelo terminal.
O terminal exibirá a URL pública gerada:   PlaintextForwarding     [https://xxxx-xxx-xxx.ngrok-free.app](https://xxxx-xxx-xxx.ngrok-free.app) -> http://localhost:8080
Copie o link https://...ngrok-free.app e acesse de qualquer lugar.
Na primeira visita, clique em "Visit Site" na tela de aviso do ngrok para acessar a tela de login do inventário.

Passo a Passo para criar o túnel público:
Certifique-se de que a aplicação está rodando no Docker:
No PowerShell, confirme que os containers estão ativos rodando:

PowerShell
docker ps
(A aplicação deve estar rodando na porta 8080).

Gerar o novo link público do Cloudflare:
No terminal do PowerShell (ou CMD), execute o comando para forçar o protocolo HTTP/2 (evitando bloqueios de rede/firewall):

PowerShell
cloudflared tunnel --protocol http2 --url http://localhost:8080
Copiar a nova URL gerada:
Após alguns segundos, o terminal exibirá uma caixa no formato:

Plaintext
+-----------------------------------------------------------------------------------+
| Your quick Tunnel has been created! Visit it at:                                 |
| https://xxxxxxxx-xxxx-xxxx.trycloudflare.com                                      |
+------------------------------------------------------------------

🔑 Acesso Inicial ao Sistema
Ao acessar a aplicação pela primeira vez, utilize as credenciais padrão de administrador:

URL Local: http://localhost:8080

Usuário: admin

Senha: admin
