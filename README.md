# Faye_Bassirou_M1_API_Ecommerce

## Objectif
API REST professionnelle pour une plateforme e-commerce multi-boutiques. Architecture propre, securite, documentation Swagger, tests et migrations BD.

## Technologies
- Spring Boot 3.2
- Java 17
- MySQL 8
- Spring Security + JWT (access + refresh)
- Spring Data JPA
- Liquibase
- Swagger / OpenAPI 3
- JUnit 5 + JaCoCo

## Fonctionnalites (exigences)
- Authentification : inscription, login, refresh, logout
- Roles : CLIENT, STORE_OWNER, ADMIN
- Catalogue produits : CRUD + recherche avancee (texte, prix, categorie, stock, tri, pagination)
- Upload images produits
- Panier client
- Commandes + changement de statut par proprietaire
- Paiement (simulation) : initie, confirme, echoue, rembourse
- Expedition : statut + tracking + mode de livraison
- Profil utilisateur + adresses + moyens de paiement (mock)
- Stocks : decrement auto + alerte stock faible
- Marketing : coupons/remises
- Analytics boutique + export CSV/Excel
- Rate limiting simple

## Architecture
- Separation des couches : Controller / Service / Repository / DTO / Entities
- Validation Jakarta Bean Validation
- Gestion des erreurs centralisee
- Logs structures (event=...)

Arborescence principale :
```
src/main/java/com/ecommerce/mulboutique/
  config/
  controller/
  dto/
  entity/
  exception/
  repository/
  security/
  service/
```

## Securite
- JWT + roles
- BCrypt pour hashage mots de passe
- Rate limiting configurable
- Sanitization des champs texte (anti-XSS)

## Acces par roles (endpoints autorises)
Note : les anciennes routes `/api/...` redirigent automatiquement vers `/api/v1/...`.
### PUBLIC (non authentifie)
- Auth: POST `/api/v1/auth/register`, POST `/api/v1/auth/login`, POST `/api/v1/auth/refresh`, POST `/api/v1/auth/logout`
- Produits: GET `/api/v1/products/{id}`, GET `/api/v1/products/store/{storeId}`, GET `/api/v1/products/category/{categoryId}`, GET `/api/v1/products/search?...`
- Boutiques: GET `/api/v1/stores`, GET `/api/v1/stores/{id}`
- Uploads: GET `/api/v1/uploads/{filename}`
- Coupons: GET `/api/v1/coupons/validate?code=&storeId=&cartTotal=`

### CLIENT
- Panier: GET `/api/v1/clients/cart?storeId=`, POST `/api/v1/clients/cart/items`, PUT `/api/v1/clients/cart/items/{itemId}`, DELETE `/api/v1/clients/cart/items/{itemId}`, DELETE `/api/v1/clients/cart/clear?storeId=`
- Commandes: POST `/api/v1/clients/orders`, GET `/api/v1/clients/orders`
- Paiements: POST `/api/v1/payments/initiate`, POST `/api/v1/payments/confirm`, POST `/api/v1/payments/fail`, POST `/api/v1/payments/refund`
- Profil: GET `/api/v1/users/me`, PUT `/api/v1/users/me`
- Adresses: GET/POST/PUT/DELETE `/api/v1/users/me/addresses`
- Moyens de paiement: GET/POST/PUT/DELETE `/api/v1/users/me/payment-methods`

### STORE_OWNER
- Boutiques: POST/PUT/DELETE `/api/v1/stores`, GET `/api/v1/stores/my-stores`
- Categories: POST/PUT/DELETE `/api/v1/categories`, GET `/api/v1/categories/store/{storeId}`
- Produits: POST/PUT/DELETE `/api/v1/products`
- Uploads: POST `/api/v1/uploads`
- Coupons: POST/PUT/DELETE/GET `/api/v1/store-owners/coupons`
- Commandes boutique: GET `/api/v1/store-owners/orders?storeId=`, PUT `/api/v1/store-owners/orders/{orderId}/status`, PUT `/api/v1/store-owners/orders/{orderId}/shipping`
- Stocks: GET `/api/v1/store-owners/stock/low?storeId=`
- Analytics: GET `/api/v1/store-owners/analytics/store/{storeId}`, GET `/api/v1/store-owners/analytics/store/{storeId}/export`
- Analytics Excel: GET `/api/v1/store-owners/analytics/store/{storeId}/export/xlsx`

### ADMIN
- Tous les endpoints STORE_OWNER + CLIENT
- Endpoints `/api/v1/admin/**` (reserve)

## Base de donnees
- Migrations Liquibase : `src/main/resources/db/changelog`
- Activation par defaut : `spring.liquibase.enabled=true`

### Schema BD (ERD simplifie)
```
users (1) --< stores (N)
users (1) --< orders (N)
users (1) --< shopping_carts (N)
users (1) --< addresses (N)
users (1) --< payment_methods (N)

stores (1) --< products (N)
stores (1) --< categories (N)
stores (1) --< coupons (N)

categories (1) --< products (N)
shopping_carts (1) --< cart_items (N)
orders (1) --< order_items (N)
products (1) --< order_items (N)
products (1) --< cart_items (N)
products (1) --< reviews (N)
orders (1) --< reviews (N)
```

Schema BD (PNG) : `docs/schema-bd.png`

### Tables principales
- users
- stores
- categories
- products
- shopping_carts
- cart_items
- orders
- order_items
- coupons
- reviews
- addresses
- payment_methods

## Configuration
Creer la base :
```sql
CREATE DATABASE ecommerce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Configurer `src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

## Lancement
```bash
mvn spring-boot:run
```
Swagger : `http://localhost:8080/swagger-ui/index.html`

## Déploiement Docker (recommandé)
Pré-requis : Docker + Docker Compose.

1) Démarrer API + MySQL :
```bash
docker compose up --build
```

2) L'API est disponible sur `http://localhost:8080`

Swagger : `http://localhost:8080/swagger-ui/index.html`

Notes :
- La base MySQL est dans le service `mysql` (pas `localhost`).
- Profil Spring utilisé : `docker` (fichier `application-docker.properties`).
- Uploads persistés dans le volume `uploads`.

## CI/CD (GitHub Actions + Docker Hub)
### CI (tests automatiques)
Le workflow `CI` lance les tests à chaque `push`/`pull_request`.

Verification :
1. GitHub → onglet **Actions**
2. Cliquer sur **CI**
3. Ouvrir la dernière exécution pour voir les logs

### CD (build + push image Docker)
Le workflow `CD` construit et pousse l’image Docker à chaque `push` sur `main`.

Pre-requis (secrets GitHub) :
- `DOCKER_USERNAME`
- `DOCKER_PASSWORD` (token Docker Hub)

Comment obtenir le token Docker Hub :
1. Se connecter  sur Docker Hub
2. Profil → **Account Settings** → **Security**
3. **Access Tokens** → **New Access Token**
4. Nommer le token (ex: `github-actions`) + droits `read/write`
5. **Generate** puis copier le token

Configuration des secrets GitHub :
1. GitHub → Settings → **Secrets and variables** → **Actions**
2. Ajouter `DOCKER_USERNAME` = votre username Docker Hub
3. Ajouter `DOCKER_PASSWORD` = le token Docker Hub (Access Token)

Verification :
1. GitHub → **Actions** → **CD**
2. Verifier le statut "Success"
3. Docker Hub : image `bassirou02/api_ecommerce:latest`

Pull manuel de l’image :
```bash
docker pull bassirou02/api_ecommerce:latest
```

Lancer l’image seule (sans MySQL) :
```bash
docker run -p 8080:8080 bassirou02/api_ecommerce:latest
```

## Comptes de demo (si DataInitializer actif)
- admin / password
- boutiquier1 / password
- client1 / password

## Donnees d'exemple (insertion auto)
Utilisateurs (ids):
- 1: admin / password / ADMIN
- 2: boutiquier1 / password / STORE_OWNER
- 3: client1 / password / CLIENT

Boutiques:
- 1: Tech Dakar (owner_id=2)
- 2: Boutique Paris Mode (owner_id=2)

Categories:
- store 1: Telephones (id=1), Ordinateurs (id=2)
- store 2: Vetements Homme (id=3), Vetements Femme (id=4)

Produits:
- store 1: Telephone Itel A70 (id=1), Samsung Galaxy A15 (id=2), HP EliteBook 840 (id=3)
- store 2: Boubou Traditionnel (id=4), Robe Parisienne (id=5)

## Guide de test (Swagger + ligne de commande)
### 1) Authentification JWT dans Swagger
Le schema securite est `bearer` (HTTP). Dans Swagger UI:
1. Execute `POST /api/v1/auth/login` avec le JSON ci-dessous.
2. Copie uniquement `accessToken` (pas `refreshToken`).
3. Clique sur **Authorize** et colle **seulement le token** (sans `Bearer `).
Swagger ajoute `Bearer` automatiquement.

Login JSON:
```json
{
  "usernameOrEmail": "boutiquier1",
  "password": "password"
}
```

### 2) Ordre conseille des tests (par role)
Store Owner / Admin:
1. Creer une boutique (`POST /api/v1/stores?ownerId=...`).
2. Creer une categorie pour la boutique.
3. Creer des produits (necessite `storeId` + `categoryId`).
4. Tester stocks, coupons, analytics.

Client:
1. Lister produits / categories.
2. Ajouter au panier.
3. Creer une commande.
4. Tester paiements / suivi.

### 3) Comment obtenir `ownerId`
`ownerId` est l'ID de l'utilisateur proprietaire connecte.
Recuperer via `GET /api/v1/users/me` apres login.

### 4) Exemples JSON par etape
Creer boutique (STORE_OWNER/ADMIN):
```json
{
  "name": "Boutique Dakar",
  "description": "Boutique de test",
  "contactEmail": "contact@boutiquier.sn",
  "contactPhone": "221770000000",
  "address": "1 avenue Test",
  "city": "Dakar",
  "country": "Senegal",
  "postalCode": "24000"
}
```

Creer categorie (STORE_OWNER/ADMIN):
```json
{
  "name": "Informatique",
  "description": "Produits informatiques"
}
```

Creer produit (STORE_OWNER/ADMIN):
```json
{
  "name": "Clavier Mecanique",
  "description": "Clavier gaming",
  "price": 79.90,
  "stockQuantity": 50,
  "imageUrl": "./image/clavier.png",
  "sku": "KB-001",
  "active": true
}
```

Uploader une image (STORE_OWNER/ADMIN):
- Swagger: choisir `multipart/form-data`, champ `file` = fichier a televerser.
- Curl (Windows PowerShell):
```powershell
curl -X POST "http://localhost:8080/api/v1/uploads" `
  -H "Authorization: Bearer <TOKEN>" `
  -F "file=@C:\chemin\vers\itelA70.jpg"
```
- Curl (Linux/Mac):
```bash
curl -X POST "http://localhost:8080/api/v1/uploads" \
  -H "Authorization: Bearer <TOKEN>" \
  -F "file=@/chemin/vers/itelA70.jpg"
```

Ajouter au panier (CLIENT):
```json
{
  "storeId": 1,
  "productId": 1,
  "quantity": 2
}
```

Creer commande (CLIENT):
```json
{
  "storeId": 1,
  "shippingAddress": "10 rue Exemple, Dakar, 11000, Senegal",
  "billingAddress": "10 rue Exemple, Dakar, 11000, Senegal",
  "paymentMethod": "CARD",
  "shippingMethod": "STANDARD"
}
```

Mettre a jour statut commande (STORE_OWNER/ADMIN):
```json
{ "status": "SHIPPED" }
```

Mettre a jour livraison (STORE_OWNER/ADMIN):
```json
{
  "shippingProvider": "DHL",
  "trackingNumber": "SN123456789"
}
```

### 5) Exemple ligne de commande (PowerShell)
Login + appel securise:
```powershell
$resp = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/v1/auth/login -ContentType "application/json" -Body '{"usernameOrEmail":"boutiquier1","password":"password"}'
$token = $resp.accessToken
Invoke-RestMethod -Method Get -Uri http://localhost:8080/api/v1/stores/my-stores -Headers @{ Authorization = "Bearer $token" }
```

## Tests
```bash
mvn test
mvn test jacoco:report
```

## Livrables
- Code source complet (dossier : Faye_Bassirou_M1_API_Ecommerce)
- Documentation Swagger + README + schema BD
- Tests

