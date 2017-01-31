# AI-gomoku-search
An Artificial Intelligence project which plays an informed game of gomoku against an opponent.

[v.031616.1]

Jacob Ochs

jochs@westmont.edu

Artificial Intelligence -- Westmont College



------------------------- GOMOKU-SEARCH README --------------------------


I. Acknowledgments:

	Some of the code within the GomokuSearch java class has been inspired by/adapted from Dr. Wayne Iba's
Racket Gomoku Client and Server provided in the our deliverable specifications.  In addition, some of the concepts
behind the code was brainstormed in joint collaboration with Chris Betsill and Hunter McGusheon.


II. Running Instructions:

	You must run the java GomokuSearch as one of two clients in order to play Gomoku over Dr. Iba's Gomoku Server.
Simply compile the server and both run the server first, then the client to be x's, and finally the client to be o's.
Compile the client using javac, and run the java file.
For example:
			javac GomokuSearch.java

would compile the client, which is then ready to run over the Racket server.
In order to interract the GomokuSearch client with the game board server
you now have open, be sure that your portnumber [GOMOKUPORT] used in GomokuAgent is 17033, and 
that it is running on "localhost" (unless you want to connect the program to another machine).



III. Artificial Intelligence Strategies:

	For this GomokuSearch client program, the primary method of play resolution is implementation of an AlphaBeta Search
ALgorithm.
