# LiveCoached_Oz
magician of Oz version of the LiveCoached watch App for WearOS

Cette application est à utiliser avec l'application [LiveCoaching_Oz](https://github.com/AlexGreau/LiveCoaching_Oz) pour tablettes Android.
Cette paire d'application servant à conduire l'expérience cherchant à prouver nos hypothèses. Voici le schéma avec captures d'écran de la logique de la paire d'applications :

![schemaOz](https://github.com/AlexGreau/LiveCoached_Oz/blob/master/readmeImages/flow.PNG)

## Main activity

Le rôle de la montre est de recevoir les ordres, les decoder et interagir avec son utilisateur pour les transmettre.
La MainActivity initialise l'Interface et le Server.
 Elle décode ensuite les messages via la fonction `public String decodeMessage(String rep)` qui vérifie si le message est conforme au formt atendu, le split et passe les différentes parties aux fonctions ` private void decodeInteraction(String interactionString)` et `private void decodeDirection(String i)`. Une fois l'interaction récupérée, les fonctions `public void showHapticScreen()` ou `public void showOnScreen(String order)` ainsi que la fonction `public void vibrate(int pat)` sont appelée selon l'interaction.

 La fonction vibrate prend le code de l'ordre reçu, puis le transmet à la fonction `private void setVibroValues(int style)` qui modifie les paramètres du feedback haptique en conséquence.

## Communications

Ne sachant pas exactement quand elle va recevoir un ordre, la montre lance un thread qui sera à l'écoute de la tablette dans la classe "Server" instanciée par la MainActivity.
