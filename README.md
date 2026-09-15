# API de adoção de animais

API REST em Java 21 e Spring Boot para cadastrar e gerenciar animais disponíveis para adoção. Os dados são persistidos em um banco H2 local no diretório `data`.

## Executar no IntelliJ IDEA

Abra o diretório como projeto Maven, aguarde a importação das dependências e execute `br.com.adocao.AdocaoApplication`. A aplicação inicia em `http://localhost:8081`.

Também é possível executar pelo terminal:

```bash
mvn spring-boot:run
```

Para executar os testes:

```bash
mvn test
```

## API

### Cadastrar animal

`POST /animais` retorna `201 Created`, o animal cadastrado e o cabeçalho `Location` com o endereço do recurso.

```powershell
$body = @{
    nome = "Luna"
    especie = "Cachorro"
    raca = "Vira-lata"
    idade = 3
    sexo = "FEMEA"
    porte = "MEDIO"
    descricao = "Dócil e vacinada"
    disponivelParaAdocao = $true
} | ConvertTo-Json

$animal = Invoke-RestMethod -Uri "http://localhost:8081/animais" -Method Post -ContentType "application/json" -Body $body
$animal
$id = $animal.id
```

### Consultar todos os animais

`GET /animais`

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/animais" -Method Get
```

### Consultar um animal

`GET /animais/{id}`

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/animais/$id" -Method Get
```

### Atualizar um animal

`PUT /animais/{id}` recebe no corpo os mesmos campos do cadastro.

```powershell
$animal.nome = "Luna Atualizada"
$body = $animal | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8081/animais/$id" -Method Put -ContentType "application/json" -Body $body
```

### Atualizar campos específicos

`PATCH /animais/{id}` altera somente as propriedades enviadas; as demais permanecem iguais.

```powershell
$alteracao = @{
    idade = 4
    disponivelParaAdocao = $false
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8081/animais/$id" -Method Patch -ContentType "application/json" -Body $alteracao
```

### Excluir um animal

`DELETE /animais/{id}` retorna `204 No Content`.

```powershell
Invoke-WebRequest -Uri "http://localhost:8081/animais/$id" -Method Delete
```

IDs inexistentes retornam `404 Not Found`; dados inválidos retornam `400 Bad Request` com os campos que precisam de correção. `sexo` e `porte` são obrigatórios, mas a especificação recebida não define uma lista fechada de opções.
