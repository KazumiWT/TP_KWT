# TP_KWT – Application REST de calcul IMC

> Projet d’API REST en Python avec deux clients : une interface web en Flask (Python) et une application mobile Android en Java.

---

##  Présentation du projet

Ce projet consiste à créer une application de calcul IMC avec une architecture modulaire, séparée en plusieurs services :

- **new_BMI_Calculator_dao** : Service DAO (accès direct à la base de données MySQL)
- **new_BMI_Calculator_service** : Service principal (authentification, calcul IMC, communication avec DAO)
- **new_BMI_Calculator_client** : Interface web utilisant Flask + Jinja2
- **New_BMI_Calculator_client_mobile** : Application Android développée en Java (Android Studio)

## L'application permet :
- l’inscription et la connexion sécurisée via JWT
- le calcul automatique de l’IMC avec classification standard
- l’évaluation du tour de taille pour une indication plus précise du niveau d’obésité
- la gestion d’un historique des mesures
- la suppression d’entrées individuelles

---

##  Structure du projet

TP_KWT/
├── new_BMI_Calculator_dao/
├── new_BMI_Calculator_service/
├── new_BMI_Calculator_client/
├── New_BMI_Calculator_client_mobile/
├── .gitignore
├── README.md


---

##  Technologies utilisées

| Composant        | Technologie                  |
|------------------|------------------------------|
| Backend API       | Python (Flask)               |
| Base de données   | MySQL                        |
| Authentification  | JWT + Bcrypt                 |
| Interface Web     | HTML, Jinja2, Bootstrap      |
| Application mobile| Java (Android Studio)        |
| IDE utilisés      | Android Studio, PyCharm      |

---

##  Lancement du projet

###  Prérequis

- Python 3.10+
- MySQL ou MariaDB
- Android Studio
- Navigateur Web (pour le client web)
- Environnement virtuel Python (optionnel mais recommandé)

###  Démarrage des services

#### 1. Service DAO

cd new_BMI_Calculator_dao
python app.py

#### 2. Service principal (API)

cd new_BMI_Calculator_service
python app.py

#### 3. Client web

cd new_BMI_Calculator_client
python app.py

#### 4. Client Android
Ouvrir New_BMI_Calculator_client_mobile dans Android Studio

Exécuter l’application sur un émulateur ou un appareil réel


## Fonctionnalités principales

 - Authentification sécurisée avec JWT

 - Calcul de l’IMC et affichage de la classification
 
 - Analyse complémentaire avec le tour de taille (ventre) pour détecter les risques liés à l’obésité abdominale

 - Historique des mesures par utilisateur

 - Suppression d’une entrée historique

 - Utilisation depuis le web ou un mobile
 
 
## Auteure
Kazumi Watanabe Target
Étudiante en AEC Analyste Programmeuse
Collège de Bois-de-Boulogne - 2025

##  Licence
Ce projet a été développé dans un but pédagogique dans le cadre d’un travail de fin d’études.
Toute reproduction à usage commercial est interdite.