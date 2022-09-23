English version [HERE](README-EN.md)

# Test Technique myCANAL Android

Ce test se déroulera en 3 parties:
1. Afficher la page détail d'un programme
2. Lire un flux video en clair
3. Faire des retours sur le code


Prenez le temps que vous souhaitez pour réaliser ce test technique et n'hésitez pas à nous contacter pour toute question.

En résultat il faudra partager les sources de l'application via un projet Git

Lors de la review je porterai attention à:
- l'utilisation de git
- le respect de l'architecture
- l'implémentation des fonctionnalités
- Si vous avez envie d'aller plus loin faites vous plaisir dans des commits dédiés

Let's go! :)

## 0 - Présentation de l'application

### Fonctionnellement
- Elle affiche au démarrage une liste de programme (émissions de télé et films)

### Techniquement
- Elle a été développée en architecture MVVM en suivant les principes de clean architecture
- La première page répond à l'api https://static.canal-plus.net/exoplayer/api/programmes.json
- Les apis sont normalement fonctionnelles (contactez nous si ce n'est pas le cas)
- La navigation dans l'application:
   - Elle utilise un nav graph (nav_graph.xml)
   - La gestion du clic sur les programmes n'est pas faite, ce sera a vous de trouver une solution pour ouvrir les différentes pages
   - Pour savoir quelle page ouvrir vous pourrez utiliser l'objet domain NavigateTo inclus dans ProgramUi

## 1 - Afficher une page détail
Lorsqu’on clique sur un programme ayant le champ `NavigateTo` de type `DetailPage` il faudra afficher une nouvelle page qui correspond à la page détail. 

Pour cela il faudra appeler l'url contenu dans le champ `urlPage` puis afficher :
- le titre (`title`), 
- le sous-titre (`subtitle`), 
- l'image (`URLImage`),
- le résumé (`summary`)
- un bouton play

## 2 - Lire un programme de la liste avec ExoPlayer

Lorsqu’on clique sur un programme ayant le champ `NavigateTo` de type `QuickTime` il faudra directement lancer la lecture du flux video avec Exoplayer SANS afficher la page détail. Les programmes de type quicktime correspondent à des videos en clair (non cryptées) au format HLS

Pour récupérer le flux video à lire il faudra appeler l’url fournie par le champ `urlMedias` inclus dans l'objet `QuickTime` qui retournera un champ `videoUrl` avec une url d’un fichier en m3u8 et le champs `encryption` égal à `clear`.

**Un peu d’aide :**
- Lire une video avec ExoPlayer: http://google.github.io/ExoPlayer/guide.html

## 3 - Retours du test (Vous pouvez etre succint dans les réponses)

- Y a t'il une erreur dans la classe `ProgramView` ? Si oui quel bug fonctionnel cela produit ?
- Quel risque courent les mappers OnClickMapper et ProgramMapper ? Comment pourrait-on les améliorer ?
- Voyez vous d'autres améliorations que l'on aurait pu faire ?
- Avez vous rencontré des difficultés dans le développement / compréhension du projet ?
- Y a t-il des améliorations de votre code qui aurait pu etre faites ?
- Combien de temps avez vous mis pour le faire ?

