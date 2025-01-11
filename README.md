# Silver Octo Guide: 


## WIP

![ChatApp Icon](src/main/resources/icon.png)

Esta aplicação de chat simples busca integrar serviços de IA para ajudar você a manter o fluxo de contexto, como uma assistente que funciona em todas as suas ferramentas, evitando a necessidade de pagar por várias licenças de IA em diferentes produtos.

## 🛠️ Tecnologias Utilizadas

- **Java 17** (ou versão compatível)
- **Swing** para a interface gráfica
- **HttpServer** para o servidor HTTP embutido
- **Maven** para gerenciamento de dependências

## 🚀 Funcionalidades

- Interface gráfica para criação e exclusão de chats.
- Chegada de novos textos entre contextos já definidos por você.

## 📂 Estrutura do Projeto

```plaintext
.
├── initialization_file.sh        # Script para inicialização
├── logs
│   └── application.log           # Arquivo de log do aplicativo
├── pom.xml                       # Arquivo de configuração do Maven
├── README.md                     # Este arquivo
├── src
│   ├── main
│   │   ├── java
│   │   │   └── example
│   │   │       └── ChatApp.java  # Código principal do aplicativo
│   │   └── resources
│   │       └── icon.png          # Ícone da aplicação
│   └── test
│       └── java                  # Diretório para testes
└── target
    └── ChatApp-1.0-SNAPSHOT.jar  # Arquivo JAR gerado
```

## ⚙️ Pré-requisitos

- **Java 8** ou superior instalado no sistema.
- **Maven** configurado corretamente.

## 📖 Como Executar

### Clonar o Repositório

```bash
git clone https://github.com/AugustoSavi/silver-octo-guide.git
cd silver-octo-guide
```

### Construir o Projeto

```bash
mvn clean package
```

### Executar a Aplicação

```bash
java -jar target/ChatApp-1.0-SNAPSHOT.jar
```

## 📝 Logs

Os logs da aplicação são armazenados no arquivo:

```plaintext
logs/application.log
```

### Configuração Adicional

Você deve configurar um atalho para a aplicação apontando para o arquivo `initialization_file.sh`.

## 🛠️ Contribuindo

Sinta-se à vontade para contribuir com melhorias ao projeto. Siga os passos abaixo para começar:

1. **Fork** o repositório.
2. Crie uma nova branch: `git checkout -b minha-feature`.
3. Faça suas alterações e commit: `git commit -m 'Minha nova feature'`.
4. Envie suas alterações para o repositório remoto: `git push origin minha-feature`.
5. Abra um **Pull Request**.


## ✉️ Contato

Caso tenha dúvidas ou sugestões, entre em contato:

- **Autor**: Augusto Savi
- **Repositório**: [Silver Octo Guide](https://github.com/AugustoSavi/silver-octo-guide)
```
