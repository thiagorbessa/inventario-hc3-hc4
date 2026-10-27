📦 Sistema de Inventário TI — HC3 e HC4
Sistema web desenvolvido em Spring Boot (Java 17), Thymeleaf, Bootstrap 5 e MySQL para gestão, mesclagem e consulta do inventário de computadores e monitores das unidades HC3 e HC4.

🚀 Como Iniciar o Sistema (Passo a Passo)
📌 Pré-requisitos
Antes de iniciar, certifique-se de ter instalado em sua máquina:

Docker Desktop (aberto e com o serviço Engine Running em verde).

Git (para clonar e atualizar o repositório).

1️⃣ Clonar o Repositório
Abra o terminal (PowerShell ou Prompt de Comando) e clone o projeto:

PowerShell
git clone https://github.com/thiagorbessa/inventario-hc3-hc4.git
cd inventario-hc3-hc4
2️⃣ Configurar as Variáveis de Ambiente (.env)
O projeto utiliza um arquivo .env para manter as credenciais do banco de dados seguras fora do repositório público.

Na raiz do projeto, copie o arquivo de exemplo .env.example para .env:

No PowerShell:

PowerShell
copy .env.example .env
Caso queira alterar as senhas padrão do banco MySQL local, abra o arquivo .env gerado e defina suas credenciais:

Snippet de código
MYSQL_ROOT_PASSWORD=sua_senha_root_aqui
MYSQL_DATABASE=inventariodb
MYSQL_USER=inventario_user
MYSQL_PASSWORD=sua_senha_usuario_aqui
3️⃣ Compilar o Projeto (.jar)
Para que o container do Spring Boot execute o código atualizado, gere o pacote executável .jar:

No Windows (PowerShell):

PowerShell
.\mvnw clean package -DskipTests
4️⃣ Executar o Sistema
🟢 Opção A: Via Script Automático (Mais Prático no Windows)
Na pasta do projeto, dê duplo clique no arquivo iniciar-sistema.bat (ou execute no terminal):

DOS
.\iniciar-sistema.bat
O que o script faz automaticamente:

Liga o container do MySQL no Docker.

Sobe a aplicação Spring Boot em um container isolado.

Aguarda os serviços iniciarem.

Abre o navegador na tela inicial em http://localhost:8080.

🐳 Opção B: Via Terminal Docker Direct
Se preferir subir manualmente pelo Docker Compose:

PowerShell
docker-compose up -d --build
Após subir os containers, acesse http://localhost:8080 no seu navegador.

🔑 Acesso Inicial ao Sistema
Ao abrir a aplicação no navegador pela primeira vez, utilize as credenciais padrão de administrador:

URL: http://localhost:8080

Usuário: admin

Senha: admin

🛠️ Comandos Úteis e Manutenção
Verificar o status dos containers em execução:

PowerShell
docker ps
Parar a aplicação sem perder os dados do banco:

PowerShell
docker-compose stop
Reiniciar alterando senhas do .env (limpa e reconstrói o banco do zero):

PowerShell
docker-compose down -v
docker-compose up -d --build
💻 Tecnologias Utilizadas
Java 17 / Spring Boot 3 (Spring Security, Spring Data JPA, OpenPOI)

MySQL 8.0 (Containerizado via Docker)

Thymeleaf + Bootstrap 5 (Frontend Responsivo)

Docker & Docker Compose

📥 Atualizar as alterações no GitHub
Para salvar este novo README.md atualizado no seu GitHub, basta rodar no terminal:

PowerShell
git add README.md
git commit -m "docs: adiciona instrucoes passo a passo de inicializacao no README.md"
git push origin main
