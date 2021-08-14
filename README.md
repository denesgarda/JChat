# JChat
JChat is a self-hosted Java chat application.
## Using the client
Download the latest release [here](https://github.com/DenDen747/JChat/raw/main/builds/2.2/JChatClient.jar).

To use it, simply launch the application.
## Setting up a server
Download the latest release [here](https://github.com/DenDen747/JChat/raw/main/builds/2.2/JChatServer.jar).

Put the server in its own dedicated directory. For example, I put it in the a folder called ``server``.

![server_jar_empty](https://github.com/DenDen747/JChat/blob/main/assets/image/server_jar_empty.png?raw=true)

Then, to set it up, double click the jar file. This will launch the server, as well as create other files next to it that can be used for configuration.

![server_jar_nicknames](https://github.com/DenDen747/JChat/blob/main/assets/image/server_jar_nicknames.png?raw=true)

By default, servers use a nickname system. This means that to join, the client will have to enter a nickname. This can be changed to accounts. You can do this by changing ``use-accounts`` in ``config.properties`` from ``false`` to ``true``. If you stop the server, change the value, then restart it, you should see a new folder called ``accounts``. This is where information about accounts are stored. Each account is a ``.properties`` file. The username is the name of the properties file. By default, an account named ``default`` will be automatically creatied to showcase how accounts work. You can delete this account. There, however, should always be at least one existent account. 

![server_jar_accounts](https://github.com/DenDen747/JChat/blob/main/assets/image/server_jar_accounts.png?raw=true)

Users cannot create accounts by default. This can be allowed by changing ``allow-account-creation`` in ``config.properties`` from ``false`` to ``true``.

Server logs are stored in the ``logs`` dircotry. Every instance of the server running has its own separate log file.

For further information / help, run the ``/help`` command on the server console.
