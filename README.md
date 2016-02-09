# Gestionnaire de tâches partagées
## Protocole et implémentation client/serveur en TCP
## DA2I 2015/2016 - Fevre Rémy - Ferro Thomas

# Protocole

## Déclaration d'un utilisateur

| Type de requête | Paramètres | Exemple |
| - | - | - |
| Requête Client -> Serveur | **CREATE:USER:'login'** | **CREATE:USER:fevrer** |
| Réponse Serveur -> Client | **CREATE:USER:'statut'** | **CREATE:USER:OK** ou **CREATE:USER:KO** |

## Création d'une tâche

| Type de requête | Paramètres | Exemple |
| - | - | - |
| Requête Client -> Serveur | **CREATE:TASK:'description'** | **CREATE:TASK:finir le gestionnaire de tâches** |
| Réponse Serveur -> Client | **CREATE:TASK:'id'** | **CREATE:TASK:1123** |

Retourne l'id de la tâche si créée ou **KO:'code'**.

### Codes :

| Code | Description |
| - | - |
| OK | tâche créée avec succès |
| CONFLICT | tâche déjà existante |
| UNAUTHORIZED | utilisateur non existant |

## Déstruction d'une tâche

| Type de requête | Paramètres | Exemple |
| - | - | - |
| Requête Client -> Serveur | **DELETE:TASK:'id'** | **DELETE:TASK:1123** |
| Réponse Serveur -> Client | **DELETE:TASK:'code'** | **DELETE:TASK:OK** ou **DELETE:TASK:KO** |

## Attribution d'une tâche à un utilisateur

| Type de requête | Paramètres | Exemple |
| - | - | - |
| Requête Client -> Serveur | **GIVE:'idTache':TO:'utilisateur'** | **GIVE:1123:TO:fevrer** |
| Réponse Serveur -> Client | **GIVE:'code'** | **GIVE:OK** |

### Codes :

| Code | Description |
| - | - |
| OK | tâche attribuée avec succès |
| NOTFOUND | tâche non existante |
| UNAUTHORIZED | utilisateur non existant |

## Changement de l'êtat d'une tâche

| Type de requête | Paramètres | Exemple |
| - | - | - |
| Requête Client -> Serveur | **CHANGE:'idTache':TO:'etat'** | **CHANGE:1123:TO:TODO** |
| Réponse Serveur -> Client | **CHANGE:'code' | **CHANGE:OK** |

### Codes :

| Code | Description |
| - | - |
| OK | tâche modifiée avec succès |
| NOTFOUND | tâche non existante |
| UNAUTHORIZED | utilisateur non existant |
| NOTMODIFIED | tâche non modifiée |

## Notification des utilisateurs...

### D'une tâche créée

| Type de requête | Paramètres | Exemple |
| - | - | - |
| Requête Serveur -> Client | **CREATED:'idTache':'createur'** | **CREATED:1123:fevrer** |
| Réponse Client -> Serveur | **CREATED:OK** | **CREATED:OK** |

### D'une tâche modifiée

| Type de requête | Paramètres | Exemple |
| - | - | - |
| Requête Serveur -> Client | **MODIFIED:'idTache':'etat':'createur':'executant'** | **MODIFIED:1123:TODO:fevrer:ferrot** |
| Réponse Client -> Serveur | **MODIFIED:OK** | **MODIFIED:OK** |

## Récupération des tâches d'un utilisateur

| Type de requête | Paramètres | Exemple |
| - | - | - |
| Requête Client -> Serveur | **GET:TASKS:'utilisateur'** | **GET:TASKS:fevrer**  |
| Réponse Serveur -> Client | **GET:TASKS:'liste des taches'** | **GET:TASKS:1123:1574:2546**  |

Si l'utilisateur est vide, on renvoi les tâches de l'utilisateur courant.

## Récupération des informations d'une tâche

| Type de requête | Paramètres | Exemple |
| - | - | - |
| Requête Client -> Serveur | **GET:TASK:'idTache'** | **GET:TASKS:1123**  |
| Réponse Serveur -> Client | **GET:TASK:'etat':'createur':'executant':'description'** | **GET:TASK:TODO:fevrer:ferrot:finir le gestionnaire de tâches**  |
