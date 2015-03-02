package ga.nurupeaches.katou.network.manager;


/**

Packet Structure:

 	Generic Structure:
 		---------------------------------
 		| Length | ID of Packet |  Data |
 		---------------------------------

 	Data can be:
		Version:
			----------------------------
			| Version-Length | Version |
			----------------------------

		Metadata:
			------------------------------------------
			| ID of File | Name-Length | Name | Size |
			------------------------------------------

 */