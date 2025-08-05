# P6-PayMyBuddy

PayMyBuddy est une application de gestion d'argent.
Elle offre une plateforme simple et intuitive pour les utilisateurs souhaitant transférer de l'argent de manière efficace. 
Les utilisateurs peuvent ajouter des contacts et effectuer des paiements instantanés, en toute sécurité. 
Le système de PayMyBuddy garantit une traçabilité complète des transactions, offrant ainsi une visibilité totale sur l'historique de vos paiements.

#Lancement de l'application:

Il faut créer une variable d'environement dans Windows:
 - Chercher "Variables d'environnement".
 - Cliquer sur "Variables d'environnement..."
 - Dans "Variables système, ajouter:
 - > Nom de variable : spring.satasource.password
 - > Valeur : admin
 - Lancer via l'invite de commande, aller dans le repertoir du projet.
 - Executer la commande : mvn spring-boot:run
 - Via le navigateur, se rendre sur "http://localhost:8080/"

#Schéma MPD:

![Schéma MPD](https://github.com/eMaxOCR/P6-PayMyBuddy/blob/dev/resources/MPD.png?raw=true)