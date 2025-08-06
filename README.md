# <center>Trabalho Modelagem 2025</center>

### Alunos
- Augusto Benedito
- Quezia Emanuelly

### Sobre
- O projeto consiste em um sistema onde pacientes poderão agendar suas consultas ao psicólogo. Além disso, o psicólogo poderá cadastrar os horários na qual estará realizando os atendimentos e também visualizar sua agenda.

### Tecnologias Utilizadas
- Linguagem Usada: Java - SDK 17
- Front-End: JavaFx
- SGBD: MySql

## <center>Manual de Uso para Ambiente de Execução</center>

### Ferramentas Necessárias:
- IDE que rode bem o Java. Recomentado: `Intellij`, `Eclipse` ou `NetBeans`.
- MySql Workbench
- SDK 17 do Java

### Passos
- Baixe e configure as ferramentas citadas acima
- Clone este repositório na sua máquina
- No MySql Workbench crie uma nova conexão, pode ser o próprio root
- Ainda no Workbench, crie um novo banco de dados chamado `agendamentoconsulta`
- Importe as tabelas do banco que estão dentro da pasta `sql` aqui no repositório
- Abra os arquivos do projeto na IDE
- Na classe `util/Conexao.java` edite as variáveis `PASSWORD` e `USER` para as suas informações do mysql workbench.
- Feito tudo isso, rode o projeto.
