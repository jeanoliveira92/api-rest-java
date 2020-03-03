# API REST JAVA

API Rest desenvolvida na linguagem Java. 

Para mais informações sobre Criar uma API em Java, consultar o item 1 da bibliografia, ou se, a duvida for sobre autentiação, vide o item 2.

## Tecnologias
* Java
* Glassfish/Tomcat
* Jersey
* GSON
* JJWT
* Mysql
* Oracle

## Dependencias
Este projeto requer:
* Java 7 Development Kit (JDK)
* IDE Netbeans / Eclipse
* Servidor Glassfish / Tomcat

## Banco de dados
Todas as tabelas foram criadas de uma forma simples, podendo ser melhoradas.
### Tabela Usuarios
Corresponde aos dados dos usuarios de acesso as informações
```mysql
CREATE TABLE 'usuarios' (
  'cod' int(11) NOT NULL,
  'usuario' varchar(100) NOT NULL,
  'senha' varchar(100) NOT NULL,
  'nivelDeAcesso' varchar(30) NOT NULL
)
```
### Tabela Permissoes
Tabela que possui o nivel de permissoes das rotas
```mysql
CREATE TABLE 'permissoes' (
  'area' varchar(100) NOT NULL,
  'nivel' varchar(30) NOT NULL
) 
```
### Tabela Log
Tabela que armazena os passos dos usuarios nas requisições
```mysql
CREATE TABLE 'log' (
  'data' timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  'usuario' varchar(200) NOT NULL,
  'area' varchar(200) NOT NULL
)
```

## Funções especiais
Todas as seguintes funções se encontram dentro do arquivo DAO.java
Um grave problema que acontece ao se acessar bancos de dados é o esquecimento ao fechar a conexão ao realizar consultas, e para resolver esse problema algumas funções foram criadas. Outro problema era o tipo de dado que poderia ser usado para levar a informação até a saida da rota, e para isso, em cada caso, foi usado:
* **Hashmap**: Para consultas com varias linhas e colunas
* **List**: quando quando deseja apenas as informações sem a informação do cabeçalho
* **String**: Retorno de um único valor para a consulta

## Funções de consulta
* **createStatement**: Executa uma consulta sem parâmetros e retorna uma matriz resultado
* **createPrepareStatement**: Executa uma consulta com parâmetros e retorna uma matriz resultado
* **createListStatement**: Executa uma consulta sem parâmetros e retorna uma lista resultado
* **createListPrepareStatement**: Executa uma consulta com parâmetros e retorna uma lista resultado
* **createValuePrepareStatement**: Executa uma consulta com parâmetros e retorna apenas uma coluna/valor
* **createInsertOrUpdatePrepareStatement**: Executa um Insert ou Update com parâmetros

Funções de conversão: Para que as informações trafeguem rota a fora da API é necessário que todas as informações estejam no formato String(texto) para assim serem convertidos em JSON. As seguintes funções foram criadas justamente para realizar tais transformações.
* **getInt**: Retorna um valor inteiro de um objeto enviado
* **getFloat**: Retorna um valor flutuante de um objeto enviado
* **getBigDecimal**: Retorna um valor bigDecimal de um objeto enviado
* **getString**: Retorna um valor String de um objeto enviado
* **getDateHour**: Returna a data completa com horas e minutos em String de um objeto tipo data;
* **getDate**: Returna a data completa em String de um objeto tipo data;
* **mapToString**: Retorna uma Map em formato String
* **mapMapToString**: Retorna uma Map de Map em formato String
* **listToString**: Retorna uma lista em formato String

## Bibliografia
[1. Criando um serviço RESTful com Java](https://www.devmedia.com.br/curso/criando-um-servico-restful-com-java/1465)

[2. Autenticação baseada em token em uma aplicação REST](https://www.linkedin.com/pulse/autentica%C3%A7%C3%A3o-baseada-em-token-uma-aplica%C3%A7%C3%A3o-rest-tarcisio-carvalho/)

## Agradecimento
[Tarcisio Carvalho](https://github.com/tarcCar) - Auxilando nas dúvidas quanto a autenticação na API.

## Licença
Este projeto está licenciado sob a licença MIT - consulte o arquivo [LICENSE.md] (LICENSE.md) para obter detalhes.
