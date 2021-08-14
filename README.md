# JChat
JChat is a self-hosted Java chat application.
## Using the client
Download the latest release [here](https://github.com/DenDen747/JChat/raw/main/builds/2.2/JChatClient.jar).

To use it, simply launch the application.
## Setting up a server
Download the latest release [here](https://github.com/DenDen747/JChat/raw/main/builds/2.2/JChatServer.jar).

To run the server, simply run the jar file by double clicking on it. For further configuration, stop the server by doing "/stop", and then change whatever you like.
For commands, type "/help" once the server starts.

By default, servers have nicknames, but you can make it so that users have to use accounts to get in by changing ``use-accounts`` in ``config.properties`` from ``false`` to ``true``. You can also make it so that users can create accounts by changing ``allow-account-creation`` to ``true``. Created accounts are stored in a directory called ``accounts``.
