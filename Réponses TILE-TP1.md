# TP TILE - Introduction à la recherche d'information

## 1 Indexation des films

* A quoi sert le champ TextField.TYPE_STORED ?  
	Le champ TextField.TYPE_STORED décrit un type de champ (Field) indexé, tokenizé et stocké.
	
* Que veut dire RAMDirectory ?  
	C'est une implémentation de la classe Directory résidant en mémoire.

	
## 4 Question Bonus

* Comment tenir compte de la popularité ?  
	Le fichier ratings.dat contient un champ `Rating` qui correspond à la note du film par l'utilisateur identifié par le champ `UserID`.  
	On peut tenir compte de la popularité des films en exploitant les valeurs du champ `Rating`.

* Comment calculer la popularité ?  
	On peut calculer la popularité d'un film en faisant la moyenne des notes attribués par les utilisateurs ou en faisant la somme des notes supérieures à 3/5.