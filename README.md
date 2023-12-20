# JawaChat

JawaChat is a user chat plugin that impliments an entier chat system for minecraft. This includes chat formatting, private chatting, discord integration, and cross server chatting.

## Getting Started

1. Clone the repo:
```
git clone https://github.com/arthurbulin/JawaChat
```
2. Either open the project in Netbeans and build with the Maven plugin or build from the CLI. NOTE: I never build from the CLI because I'm lazy.

## Prerequisites

You will need 
    - Maven to build the plugin
    - Java 17
    - ElasticSearch (7.15.0 is the current version)
    - Minecraft server running on Paper/Spigot (Paper is prefered and I will probably not support Spigot by the end of 1.20 support)

## Installing

1. Place the JawaChat-1.20.X-#.#.jar into the server's plugin folder and start the server. Needed configuration files will be created.
2. Shutdown your server
2. Edit the config.yml. See the Configuration Paramaters section below.
4. Restart your server. If you have any malformed config files the server will let you know, although I cannot guarantee how enlightening what it tells you will be.

### Configuration Parameters
debug: \<true/false\>
* chat-settings:
    * format: \<Arbitrary string\>
        - A combination of replacements for formatting. These are {WORLD}, {TAG}, {DISPLAYNAME}, and {MESSAGE}
* crosslink:
    * enable-crosslink: \<true/false\> 
        - If server chat crosslinking is enabled
    * server-role: \<controller/node\>
        - Controllor or node. When set to controllor this server will be the central router for chat messages and the chat authority. For a node the 
    * server-port: \<integer\>
        - The port to communicate with the controllor or client on
    * server-host: \<hostname or IP\>
        - Hostname/IP of the controller, if this is a controller this is not used
    * server-uuid: \<UUID\>
        - A compliant UUID. This is not present to start out. On the first run, when CrossLink is enabled, this UUID will be generated.
    * authorized-nodes:
        - \<Arbitrarty String:UUID\>
            - This is used by the controller to authenticate a node. It must follow this format "<name>:<UUID>"

[!WARNING] I HAVE NOT finished the Discord link! DO NOT USE IT unless you want to generate bug reports for me. It won't work the way you want it to!!

## Commands and Permissions
### Commands and root permission nodes
These are the commands available within JawaChat, their descriptions, and relevent command usage permissions nodes.

setnick:
description: Used to set the player's nick name
usage: /setnick <playername> [player nick name]
permission: jawachat.setnick

settag:
description: Used to give a player a tag.
usage: /settag <playername> [tag]
permission: jawachat.settag

setstar:
description: Used to give a player a red or green star preceeding their name.
usage: /setstar <playername> [r or g]
permission: jawachat.setstar

pm:
description: Send a private message to a player
usage: /pm <player> <Your message>
permission: jawachat.pm

mute:
description: "Allows an admin to mute a player"
usage: "/mute <player>"
permission: jawachat.mute

discordlink:
description: "Generates a linking code to be used with the FoxelBot discord bot"
usage: "/discordlink"
permission: jawachat.discordlink

breakcommand:
permission: breakcommand

### Permissions
Some of these permissions are root permissions (which entitle a user to run a command), but some are additional features of a command.

jawachat.discordlink:
description: "Allows linking of an account to discord"
default: true

jawachat.mute:
description: "Allows access to the /mute command"
default: op

jawachat.pm:
description: "Allows a player to private message others"
default: true

jawachat.setstar:
description: "Allows an admin to set a star on the player specified"
default: op

jawachat.settag:
description: "Allows an admin to set a tag on the player specified"
default: op

jawachat.setnick:
description: "Allows an admin to set the nickname of the player specified"
default: op

## Built With

* [NetBeans](https://netbeans.org/) - The IDE used
* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Arthur Bulin aka Jawamaster** - [Arthur Bulin](https://github.com/arthurbulin)

## License

This project is licensed under the MIT License. Just don't be a jerk about it.

