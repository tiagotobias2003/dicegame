# Análise Detalhada do Projeto - Jogo de Dados

## Visão Geral do Projeto

Este documento apresenta uma análise detalhada de cada fase do desenvolvimento do projeto "Jogo de Dados", uma aplicação web desenvolvida com Spring Boot que demonstra conceitos fundamentais de desenvolvimento Java para iniciantes.

---

## 📋 Tópico 1: Criação das Classes de Implementação dos Models
**Data:** 11/11/2025

### Descrição da Fase

Nesta primeira fase do projeto, foi estabelecida a estrutura fundamental da aplicação através da criação das classes de domínio (Models) e suas respectivas camadas de acesso a dados e serviços.

### Entidades de Domínio Criadas

#### 1. AbstractEntity<ID>
**Localização:** `com.lunaltas.dicegame.domain.AbstractEntity`

Classe abstrata genérica que serve como base para todas as entidades do sistema, implementando funcionalidades comuns:

- **Gerenciamento de ID:** Utiliza `@Id` e `@GeneratedValue(strategy = GenerationType.IDENTITY)` para geração automática de identificadores
- **Métodos fundamentais:** Implementa `hashCode()`, `equals()` e `toString()` baseados no ID
- **Serialização:** Implementa `Serializable` para suportar persistência e transmissão de dados

**Características técnicas:**
- Utiliza `@MappedSuperclass` para permitir herança sem criar tabela própria
- Tipo genérico `ID extends Serializable` permite flexibilidade no tipo de chave primária

#### 2. User (Usuário)
**Localização:** `com.lunaltas.dicegame.domain.User`

Entidade que representa os usuários do sistema:

**Atributos principais:**
- `name` (String): Nome do usuário (obrigatório, máximo 60 caracteres)
- `email` (String): Email do usuário (obrigatório, validado com `@Email`, máximo 60 caracteres)
- `password` (String): Senha do usuário (obrigatória, será criptografada posteriormente)
- `role` (String): Papel do usuário no sistema (padrão: "USER")

**Validações implementadas:**
- `@NotBlank` em todos os campos obrigatórios
- `@Email` para validação de formato de email
- Mensagens de erro personalizadas em português

**Relacionamentos:**
- `@OneToMany` com `Bet`: Um usuário pode ter múltiplas apostas

#### 3. Bet (Aposta)
**Localização:** `com.lunaltas.dicegame.domain.Bet`

Entidade que representa as apostas realizadas no jogo:

**Atributos principais:**
- `name` (String): Nome da aposta (obrigatório, máximo 60 caracteres)

**Relacionamentos:**
- `@ManyToOne` com `User`: Múltiplas apostas pertencem a um usuário

### Camada de Acesso a Dados (DAO)

#### AbstractDao<T, PK>
**Localização:** `com.lunaltas.dicegame.dao.AbstractDao`

Classe abstrata genérica que implementa operações CRUD básicas usando JPA:

**Métodos implementados:**
- `save(T entity)`: Persiste uma nova entidade
- `update(T entity)`: Atualiza uma entidade existente
- `delete(PK id)`: Remove uma entidade pelo ID
- `findById(PK id)`: Busca uma entidade pelo ID
- `findAll()`: Retorna todas as entidades do tipo
- `createQuery(String jpql, Object... params)`: Método auxiliar para consultas JPQL customizadas

**Características técnicas:**
- Utiliza `EntityManager` injetado via `@PersistenceContext`
- Usa reflexão para determinar a classe da entidade automaticamente
- Suporta consultas parametrizadas para segurança

#### Implementações Específicas

**UserDao e UserDaoImpl:**
- Interface: `com.lunaltas.dicegame.dao.UserDao`
- Implementação: `com.lunaltas.dicegame.dao.UserDaoImpl`
- Estende `AbstractDao<User, Long>`

**BetDao e BetDaoImpl:**
- Interface: `com.lunaltas.dicegame.dao.BetDao`
- Implementação: `com.lunaltas.dicegame.dao.BetDaoImpl`
- Estende `AbstractDao<Bet, Long>`

### Camada de Serviços (Service)

#### Interfaces de Serviço

**UserService:**
- Interface que define operações de negócio para usuários
- Estende `UserDetailsService` para integração com Spring Security
- Métodos: `save()`, `update()`, `delete()`, `findById()`, `findAll()`, `hasBets()`

**BetService:**
- Interface que define operações de negócio para apostas
- Métodos: `save()`, `update()`, `delete()`, `findById()`, `findAll()`

#### Implementações de Serviço

**UserServiceImpl:**
- Implementa `UserService`
- Injeta `UserDao` e `UserRepository`
- Gerencia criptografia de senhas usando `PasswordEncoder`
- Implementa `loadUserByUsername()` para autenticação

**BetServiceImpl:**
- Implementa `BetService`
- Injeta `BetDao`
- Delega operações CRUD para a camada DAO

### Resultado da Fase

Ao final desta fase, o projeto possuía:
- ✅ Estrutura de entidades bem definida
- ✅ Camada de acesso a dados genérica e reutilizável
- ✅ Camada de serviços preparada para regras de negócio
- ✅ Separação clara de responsabilidades (Domain, DAO, Service)
- ✅ Base sólida para integração com banco de dados

---

## 📋 Tópico 2: Integração dos Models com Banco de Dados
**Data:** 12/11/2025

### Descrição da Fase

Nesta segunda fase, foi realizada a integração completa entre as entidades JPA e o banco de dados PostgreSQL, configurando todas as dependências e mapeamentos necessários.

### Configurações Realizadas

#### 1. Dependências Maven (pom.xml)

**Spring Data JPA:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

**Driver PostgreSQL:**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### 2. Configuração do Banco de Dados

**Arquivo:** `src/main/resources/application.properties`

**Configurações de DataSource:**
```properties
spring.datasource.url = jdbc:postgresql://localhost:5432/dice_game
spring.datasource.username = TOKA
spring.datasource.password = 123
```

**Configurações JPA/Hibernate:**
```properties
# Exibe as consultas SQL no console para debug
spring.jpa.show-sql = true

# Mantém a sessão JPA aberta durante o processamento de requisições
spring.jpa.open-in-view = true
```

### Mapeamentos JPA Implementados

#### Entidade User

**Anotações utilizadas:**
- `@Entity`: Marca a classe como entidade JPA
- `@Table(name = "users")`: Define o nome da tabela no banco
- `@Column`: Especifica detalhes das colunas (nullable, length)
- `@OneToMany(mappedBy = "user")`: Define relacionamento um-para-muitos com Bet

**Estrutura da tabela `users`:**
- `id` (BIGSERIAL PRIMARY KEY): Gerado automaticamente
- `name` (VARCHAR(60) NOT NULL): Nome do usuário
- `email` (VARCHAR(60) NOT NULL): Email único
- `password` (VARCHAR NOT NULL): Senha criptografada
- `role` (VARCHAR NOT NULL): Papel do usuário (padrão: "USER")

#### Entidade Bet

**Anotações utilizadas:**
- `@Entity`: Marca a classe como entidade JPA
- `@Table(name = "bet")`: Define o nome da tabela
- `@ManyToOne`: Define relacionamento muitos-para-um com User
- `@JoinColumn(name = "user_id")`: Especifica a coluna de chave estrangeira

**Estrutura da tabela `bet`:**
- `id` (BIGSERIAL PRIMARY KEY): Gerado automaticamente
- `name` (VARCHAR(60) NOT NULL): Nome da aposta
- `user_id` (BIGINT FOREIGN KEY): Referência ao usuário

### Implementação do EntityManager

**Classe:** `AbstractDao`

O `EntityManager` é injetado via `@PersistenceContext` e utilizado para:

1. **Persistência:**
   ```java
   entityManager.persist(entity);
   ```

2. **Atualização:**
   ```java
   entityManager.merge(entity);
   ```

3. **Remoção:**
   ```java
   entityManager.remove(entityManager.getReference(entityClass, id));
   ```

4. **Consultas:**
   ```java
   entityManager.find(entityClass, id);
   entityManager.createQuery("from " + entityClass.getSimpleName(), entityClass);
   ```

### Gerenciamento de Transações

**Anotações utilizadas nos serviços:**
- `@Transactional(readOnly = false)`: Para operações de escrita (padrão)
- `@Transactional(readOnly = true)`: Para operações de leitura (otimização)

**Exemplo em UserServiceImpl:**
```java
@Service @Transactional(readOnly = false)
public class UserServiceImpl implements UserService {
    // Operações de escrita usam transação completa
    
    @Override @Transactional(readOnly = true)
    public User findById(Long id) {
        return dao.findById(id);
    }
}
```

### Validações de Integridade

**Validações Bean Validation:**
- `@NotBlank`: Campos obrigatórios não podem ser vazios
- `@Email`: Validação de formato de email
- Mensagens personalizadas em português

**Validações de Relacionamento:**
- Integridade referencial entre User e Bet
- Prevenção de exclusão de usuários com apostas associadas

### Resultado da Fase

Ao final desta fase, o projeto possuía:
- ✅ Banco de dados PostgreSQL configurado e conectado
- ✅ Entidades mapeadas corretamente para tabelas
- ✅ Relacionamentos JPA funcionando (OneToMany, ManyToOne)
- ✅ Operações CRUD funcionais através do EntityManager
- ✅ Gerenciamento de transações implementado
- ✅ Validações de dados ativas
- ✅ Consultas SQL visíveis para debug

---

## 📋 Tópico 3: Integração dos Controllers com as Views e Models
**Data:** 18/11/2025 - 19/11/2025

### Descrição da Fase

Nesta terceira fase, foi implementada a camada de apresentação completa, conectando os controllers Spring MVC às views Thymeleaf e integrando tudo com os models e serviços já criados.

### Controllers Implementados

#### 1. UsersController
**Localização:** `com.lunaltas.dicegame.Controller.UsersController`
**Rota base:** `/users`

**Endpoints implementados:**

| Método HTTP | Rota | Descrição |
|------------|------|-----------|
| GET | `/users/index` | Lista todos os usuários |
| GET | `/users/new` | Exibe formulário de criação |
| POST | `/users/create` | Salva novo usuário |
| GET | `/users/show/{id}` | Exibe detalhes do usuário |
| GET | `/users/edit/{id}` | Exibe formulário de edição |
| PUT | `/users/update/{id}` | Atualiza usuário existente |
| DELETE | `/users/delete/{id}` | Remove usuário |

**Funcionalidades especiais:**

1. **Validação de Senha Customizada:**
   ```java
   if (user.getPassword() != null && 
       (user.getPassword().length() < 8 || 
        user.getPassword().length() > 20)) {
       result.rejectValue("password", "error.password", 
           "A senha deve ter entre 8 e 20 caracteres.");
   }
   ```

2. **Proteção contra Exclusão:**
   ```java
   if (userService.hasBets(id)) {
       redirectAttributes.addFlashAttribute("error", 
           "Usuário não pode ser deletado porque tem Bets associadas");
       return "redirect:/users/show/" + id;
   }
   ```

3. **Mensagens de Feedback:**
   - Uso de `RedirectAttributes` para mensagens de sucesso/erro
   - Flash attributes para persistência entre redirecionamentos

#### 2. BetsController
**Localização:** `com.lunaltas.dicegame.Controller.BetsController`
**Rota base:** `/bets`

**Endpoints implementados:**

| Método HTTP | Rota | Descrição |
|------------|------|-----------|
| GET | `/bets/index` | Lista todas as apostas |
| GET | `/bets/new` | Exibe formulário de criação |
| POST | `/bets/create` | Salva nova aposta |
| GET | `/bets/show/{id}` | Exibe detalhes da aposta |
| GET | `/bets/edit/{id}` | Exibe formulário de edição |
| PUT | `/bets/update/{id}` | Atualiza aposta existente |
| DELETE | `/bets/delete/{id}` | Remove aposta |

**Funcionalidades especiais:**
- Injeção de lista de usuários para seleção em formulários
- Validação de dados usando Bean Validation
- Tratamento de erros de validação com retorno ao formulário

### Camada de Serviços - Funcionalidades Avançadas

#### Criptografia de Senhas

**UserServiceImpl - Método save():**
```java
@Override
public void save(User user) {
    // Criptografa a senha antes de salvar
    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    dao.save(user);
}
```

**UserServiceImpl - Método update():**
```java
@Override
public void update(User user) {
    User existingUser = dao.findById(user.getId());
    if (existingUser != null && user.getPassword() != null && 
        !user.getPassword().isEmpty() && 
        !user.getPassword().equals(existingUser.getPassword())) {
        // Verifica se a senha já está criptografada
        if (!user.getPassword().startsWith("$2a$") && 
            !user.getPassword().startsWith("$2b$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }
    dao.update(user);
}
```

**Características:**
- Usa BCrypt para criptografia (identificado por prefixo `$2a$` ou `$2b$`)
- Evita re-criptografar senhas já criptografadas
- Aplicado automaticamente em criação e atualização

#### Validação de Regras de Negócio

**Método hasBets():**
```java
@Override
public boolean hasBets(Long id) {
    User user = findById(id);
    if (user == null) {
        return false;
    }
    return user.getBets().size() > 0;
}
```

**Uso no Controller:**
- Previne exclusão de usuários com apostas associadas
- Mantém integridade referencial no nível de aplicação

### Views Thymeleaf Implementadas

#### Estrutura de Templates

```
templates/
├── users/
│   ├── index.html      # Listagem de usuários
│   ├── new.html        # Formulário de criação
│   ├── edit.html       # Formulário de edição
│   └── show.html       # Detalhes do usuário
├── bets/
│   ├── index.html      # Listagem de apostas
│   ├── new.html        # Formulário de criação
│   ├── edit.html       # Formulário de edição
│   └── show.html       # Detalhes da aposta
├── fragments/
│   ├── dashboard.html  # Fragmento de dashboard
│   ├── forms.html      # Fragmento de formulários
│   └── validation.html  # Fragmento de validação
├── home.html           # Página inicial
├── login.html          # Página de login
└── error.html          # Página de erro
```

#### Funcionalidades das Views

1. **Fragmentos Reutilizáveis:**
   - `dashboard.html`: Componentes de navegação e layout
   - `forms.html`: Formulários padronizados
   - `validation.html`: Exibição de erros de validação

2. **Integração com Controllers:**
   - Uso de `th:object` para binding de objetos
   - `th:field` para campos de formulário
   - `th:each` para iteração em listas
   - `th:if` para renderização condicional

3. **Validação Visual:**
   - Exibição de mensagens de erro
   - Feedback visual de sucesso/erro
   - Validação client-side e server-side

### Configurações Thymeleaf

**application.properties:**
```properties
# Desativa o cache do Thymeleaf para desenvolvimento
spring.thymeleaf.cache= false
```

**Habilitar métodos HTTP (PUT/DELETE):**
```properties
spring.mvc.hiddenmethod.filter.enabled=true
```

### Fluxo de Dados Completo

```
1. Requisição HTTP → Controller
2. Controller → Service (regras de negócio)
3. Service → DAO (acesso a dados)
4. DAO → EntityManager → Banco de Dados
5. Resposta ← ModelMap (dados para view)
6. View Thymeleaf renderiza HTML
7. Resposta HTTP com HTML
```

### Resultado da Fase

Ao final desta fase, o projeto possuía:
- ✅ CRUD completo funcional para Users e Bets
- ✅ Validação de dados em múltiplas camadas
- ✅ Criptografia de senhas implementada
- ✅ Interface web completa e responsiva
- ✅ Mensagens de feedback para o usuário
- ✅ Proteção de integridade referencial
- ✅ Fragmentos reutilizáveis de templates
- ✅ Tratamento de erros de validação
- ✅ Suporte a métodos HTTP (GET, POST, PUT, DELETE)

---

## 📋 Tópico 4: Finalização do Projeto
**Data:** 25/11/2025 - 26/11/2025

### Descrição da Fase

Nesta fase final, foram implementadas funcionalidades de segurança, tratamento de erros avançado, e realizados os ajustes finais para tornar a aplicação completa e pronta para uso.

### Segurança Implementada

#### 1. Dependências de Segurança

**pom.xml:**
```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Integração Thymeleaf + Spring Security -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

#### 2. SecurityConfig

**Localização:** `com.lunaltas.dicegame.config.SecurityConfig`

**Funcionalidades implementadas:**
- Configuração de autenticação
- Definição de regras de autorização
- Proteção de rotas
- Integração com sistema de login

#### 3. Integração UserService com Spring Security

**UserService:**
- Estende `UserDetailsService` do Spring Security
- Implementa método `loadUserByUsername()`

**UserServiceImpl - loadUserByUsername():**
```java
@Override
@Transactional(readOnly = true)
public UserDetails loadUserByUsername(String username) 
    throws UsernameNotFoundException {
    User user = repository.findByEmail(username);
    
    if (user == null) {
        throw new UsernameNotFoundException(
            "Usuário não encontrado: " + username);
    }
    
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        AuthorityUtils.createAuthorityList("ROLE_" + user.getRole())
    );
}
```

**Características:**
- Busca usuário por email
- Converte entidade `User` para `UserDetails`
- Mapeia role do usuário para authorities do Spring Security
- Lança exceção apropriada se usuário não encontrado

#### 4. Sistema de Roles

**Roles implementadas:**
- `USER`: Papel padrão para usuários comuns
- Suporte para múltiplos roles (extensível)

**Uso em User:**
```java
@Column(name = "role", nullable = false)
private String role = "USER";
```

### Tratamento de Erros

#### 1. MyErrorView

**Localização:** `com.lunaltas.dicegame.error.MyErrorView`

**Funcionalidades:**
- Página de erro customizada
- Tratamento de diferentes tipos de exceções
- Mensagens de erro amigáveis ao usuário

#### 2. error.html

**Localização:** `src/main/resources/templates/error.html`

**Características:**
- Template Thymeleaf para exibição de erros
- Design consistente com o resto da aplicação
- Informações úteis para debug (em desenvolvimento)

### Recursos Adicionais Implementados

#### 1. HomeController

**Localização:** `com.lunaltas.dicegame.Controller.HomeController`

**Funcionalidades:**
- Página inicial da aplicação
- Dashboard com informações do sistema
- Navegação principal

#### 2. Recursos Estáticos

**Arquivo:** `src/main/resources/static/rolling-dice.gif`

- Imagem animada para interface
- Melhora a experiência visual do jogo

#### 3. UserRepository

**Localização:** `com.lunaltas.dicegame.repository.UserRepository`

**Interface Spring Data JPA:**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
```

**Vantagens:**
- Consultas automáticas pelo Spring Data JPA
- Método customizado `findByEmail()` gerado automaticamente
- Integração simplificada com Spring Security

### Configurações Finais

#### 1. Thymeleaf (Desenvolvimento)

```properties
spring.thymeleaf.cache= false
```

**Benefícios:**
- Alterações em templates refletidas imediatamente
- Facilita desenvolvimento e debug
- **Nota:** Deve ser `true` em produção

#### 2. Métodos HTTP Ocultos

```properties
spring.mvc.hiddenmethod.filter.enabled=true
```

**Funcionalidade:**
- Permite uso de PUT e DELETE via formulários HTML
- Necessário porque navegadores não suportam PUT/DELETE nativamente
- Usa campo hidden `_method` nos formulários

#### 3. Porta da Aplicação

```properties
server.port=8081
```

**Configuração:**
- Aplicação roda na porta 8081
- Evita conflitos com outras aplicações na porta padrão 8080

### Funcionalidades de Segurança Adicionais

#### 1. Criptografia de Senhas

- **Algoritmo:** BCrypt
- **Aplicação:** Automática em criação e atualização
- **Detecção:** Identifica senhas já criptografadas pelo prefixo

#### 2. Validação de Senhas

- **Tamanho mínimo:** 8 caracteres
- **Tamanho máximo:** 20 caracteres
- **Validação:** Client-side e server-side

#### 3. Proteção de Dados

- Senhas nunca expostas em logs
- Criptografia antes de persistência
- Validação de integridade referencial

### Estrutura Final do Projeto

```
dicegame/
├── src/main/java/com/lunaltas/dicegame/
│   ├── config/
│   │   └── SecurityConfig.java          # Configuração de segurança
│   ├── Controller/
│   │   ├── BetsController.java          # Controller de apostas
│   │   ├── HomeController.java          # Controller da home
│   │   └── UsersController.java         # Controller de usuários
│   ├── dao/
│   │   ├── AbstractDao.java             # DAO genérico
│   │   ├── BetDao.java                  # Interface DAO de apostas
│   │   ├── BetDaoImpl.java              # Implementação DAO de apostas
│   │   ├── UserDao.java                 # Interface DAO de usuários
│   │   └── UserDaoImpl.java             # Implementação DAO de usuários
│   ├── domain/
│   │   ├── AbstractEntity.java          # Entidade abstrata base
│   │   ├── Bet.java                     # Entidade de aposta
│   │   └── User.java                    # Entidade de usuário
│   ├── error/
│   │   └── MyErrorView.java             # Tratamento de erros
│   ├── repository/
│   │   └── UserRepository.java          # Repository Spring Data JPA
│   └── service/
│       ├── BetService.java              # Interface serviço de apostas
│       ├── BetServiceImpl.java          # Implementação serviço de apostas
│       ├── UserService.java             # Interface serviço de usuários
│       └── UserServiceImpl.java         # Implementação serviço de usuários
└── src/main/resources/
    ├── application.properties            # Configurações da aplicação
    ├── static/
    │   └── rolling-dice.gif              # Recurso estático
    └── templates/
        ├── bets/                         # Templates de apostas
        ├── users/                        # Templates de usuários
        ├── fragments/                    # Fragmentos reutilizáveis
        ├── error.html                    # Template de erro
        ├── home.html                     # Página inicial
        └── login.html                    # Página de login
```

### Tecnologias Utilizadas

| Tecnologia | Versão/Purpose | Uso no Projeto |
|-----------|----------------|----------------|
| **Spring Boot** | 3.5.7 | Framework principal |
| **Spring Security** | - | Autenticação e autorização |
| **Spring Data JPA** | - | Abstração de acesso a dados |
| **Hibernate** | - | ORM (via JPA) |
| **PostgreSQL** | - | Banco de dados relacional |
| **Thymeleaf** | - | Engine de templates |
| **Maven** | - | Gerenciamento de dependências |
| **Java** | 25 | Linguagem de programação |
| **Bean Validation** | - | Validação de dados |
| **BCrypt** | - | Criptografia de senhas |

### Padrões de Arquitetura Aplicados

1. **MVC (Model-View-Controller):**
   - Separação clara entre camadas
   - Controllers gerenciam requisições
   - Models representam dados
   - Views renderizam interface

2. **DAO (Data Access Object):**
   - Abstração de acesso a dados
   - Implementação genérica reutilizável
   - Isolamento de lógica de persistência

3. **Service Layer:**
   - Regras de negócio centralizadas
   - Transações gerenciadas
   - Integração entre camadas

4. **Repository Pattern:**
   - Spring Data JPA para consultas simples
   - DAO para operações complexas
   - Combinação de ambos conforme necessidade

### Resultado Final da Fase

Ao final desta fase, o projeto possuía:
- ✅ Sistema de autenticação e autorização completo
- ✅ Criptografia de senhas implementada
- ✅ Tratamento de erros robusto
- ✅ Interface web completa e funcional
- ✅ Validações em múltiplas camadas
- ✅ Proteção de integridade de dados
- ✅ Código organizado e manutenível
- ✅ Documentação através de código limpo
- ✅ Aplicação pronta para uso e demonstração

---

## 📊 Resumo Geral do Projeto

### Objetivos Alcançados

✅ **Estrutura de Entidades:** Modelagem completa com relacionamentos JPA  
✅ **Persistência de Dados:** Integração funcional com PostgreSQL  
✅ **Interface Web:** CRUD completo com Thymeleaf  
✅ **Segurança:** Autenticação e autorização com Spring Security  
✅ **Validações:** Múltiplas camadas de validação  
✅ **Tratamento de Erros:** Páginas customizadas e tratamento adequado  
✅ **Arquitetura:** Padrões de design bem aplicados  
✅ **Código Limpo:** Organização e manutenibilidade  

### Conceitos Demonstrados

1. **Spring Boot:** Configuração e uso do framework
2. **JPA/Hibernate:** Mapeamento objeto-relacional
3. **Spring MVC:** Controllers e gerenciamento de requisições
4. **Thymeleaf:** Templates e renderização de views
5. **Spring Security:** Autenticação e autorização
6. **Validação:** Bean Validation e validações customizadas
7. **Transações:** Gerenciamento de transações JPA
8. **Arquitetura em Camadas:** Separação de responsabilidades

### Aprendizados para Iniciantes

Este projeto demonstra de forma didática:
- Como estruturar uma aplicação Spring Boot
- Como integrar banco de dados relacional
- Como criar interfaces web funcionais
- Como implementar segurança básica
- Como aplicar validações
- Como organizar código em camadas
- Como usar padrões de design comuns

### Próximos Passos Sugeridos

Para evoluir o projeto:
1. Implementar testes unitários e de integração
2. Adicionar funcionalidades do jogo de dados propriamente dito
3. Implementar API REST além da interface web
4. Adicionar logging estruturado
5. Implementar cache para melhor performance
6. Adicionar documentação Swagger/OpenAPI
7. Implementar testes end-to-end
8. Configurar CI/CD

---

## 📝 Conclusão

O projeto "Jogo de Dados" foi desenvolvido seguindo uma metodologia estruturada em quatro fases principais, cada uma construindo sobre a anterior. O resultado é uma aplicação web completa que demonstra os conceitos fundamentais do desenvolvimento Java com Spring Boot, servindo como excelente material didático para iniciantes e base sólida para projetos mais complexos.

A arquitetura implementada segue boas práticas da indústria, com separação clara de responsabilidades, código organizado e manutenível, e integração adequada de tecnologias modernas do ecossistema Spring.

---

**Documento gerado em:** 2025  
**Projeto:** Dice Game - Jogo de Dados  
**Tecnologia:** Spring Boot 3.5.7 + Java 25 + PostgreSQL + Thymeleaf
